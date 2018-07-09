package core.task.blockPets;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import core.utils.CommonUtils;
import core.utils.HttpClientUtils;
import core.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BlockPetsWalkTask {
    private static Logger logger = LoggerFactory.getLogger(BlockPetsWalkTask.class);
    private static final String LOG_PREFIX = "【Block Pets Walk】";
    //登录API
    private static final String URL_LOGIN = "http://www.bgoo.cc/api/v1/api/login";
    //账户API
    private static final String URL_ACCOUNT = "http://www.bgoo.cc/api/v1/api/my-account";
    //宠物列表API
    private static final String URL_PETS = "http://www.bgoo.cc/api/v1/api/my-pets";
    //宠物详情API
    private static final String URL_DETAIL = "http://www.bgoo.cc/api/v1/api/ignore/pet-detail";
    //宠物游走API
    private static final String URL_WALK = "http://www.bgoo.cc/api/v1/api/pet-walk";
    private static final long ALL_SUCCESS_SLEEP = PropertiesUtil.getLong("walk.all.success.sleep",1200000);
    private static final long NOT_ALL_SUCCESS_SLEEP = PropertiesUtil.getLong("walk.not.all.success.sleep",60000);

    /**
     * 程序入口
     */
    public static void  excute() {
        long sleep = NOT_ALL_SUCCESS_SLEEP;
        try {
            logger.info(LOG_PREFIX + "Task Start");
            String loginName = PropertiesUtil.getStr("loginName", "");
            String pass = PropertiesUtil.getStr("pass", "");
            JSONObject callback = doLogin(loginName, pass);
            if (callback.getInteger("code") == 200) {
                JSONObject data = callback.getJSONObject("data");
                String token = data.getString("token");
                logger.info(LOG_PREFIX + "已登录账号[" + loginName + "],当前token:" + token);
                JSONArray pets = getPets(token);
                printAllPetInfo(pets);
                Map<String, Integer> walkMap = petsAllWalk(token, pets);
                Integer successCount = walkMap.get("successCount");
                Integer totalCount = walkMap.get("totalCount");
                if (successCount != null && totalCount != null && successCount == totalCount) {
                    logger.info(LOG_PREFIX + "END (全部)游走成功,等待" + ALL_SUCCESS_SLEEP*1.0 / 60000 + "分钟后重新开启");
                    sleep = ALL_SUCCESS_SLEEP;
                } else {
                    logger.info(LOG_PREFIX + "END (" + successCount + "/" + totalCount + ")游走成功,等待" + NOT_ALL_SUCCESS_SLEEP*1.0 / 60000 + "分钟后重新开启");
                    sleep = NOT_ALL_SUCCESS_SLEEP;
                }
                Double left = checkLeft(token);
                if (left != null) {
                    logger.info(LOG_PREFIX + "账号[" + loginName + "]余额 " + left + "BGO");
                } else {
                    logger.info(LOG_PREFIX + "账号[" + loginName + "]无法获取到余额");
                }
            } else {
                logger.error(LOG_PREFIX + "登录失败,返回JSON:\n" + callback.toJSONString());
            }
        }catch (Exception e){
            logger.error(LOG_PREFIX,e);
        }
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            logger.error(LOG_PREFIX, e);
        }
    }

    /**
     * 登录
     * @param loginName
     * @param pass
     * @return
     */
    private static JSONObject doLogin(String loginName, String pass) {
        JSONObject params = new JSONObject();
        params.put("loginName", loginName);
        params.put("pass", pass);
        JSONObject result = HttpClientUtils.doPost2JSON(URL_LOGIN + "?loginName=" + loginName + "&pass=" + pass, params);
        try {
            return result;
        } catch (Exception e) {
            logger.error(LOG_PREFIX + "登陆出错", e);
            return new JSONObject();
        }
    }

    /**
     * 检查剩余BGO
     * @param token
     * @return
     */
    private static Double checkLeft(String token) {
        Double left = null;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("token", token);
        try {
            JSONObject result = HttpClientUtils.doGet2JSON(URL_ACCOUNT, headers);
            if (result.getInteger("code") == 200) {
                left = result.getJSONObject("data").getDouble("account");
            }
        }catch (Exception e){
            logger.error(LOG_PREFIX,e);
        }
        return left;
    }

    /**
     * 取通过token取所有宠物信息
     * @param token
     * @return
     */
    private static JSONArray getPets(String token) {
        JSONArray pets = new JSONArray();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("token", token);
        Boolean last = false;
        int size = 9;
        int offset = 0;
        while (!last) {
            try {
                JSONObject result = HttpClientUtils.doGet2JSON(URL_PETS + "?size=" + size + "&offset=" + offset, headers);
                if (result.getInteger("code") == 200) {
                    JSONObject data = result.getJSONObject("data");
                    pets.addAll(data.getJSONArray("content"));
                    last = data.getBoolean("last");
                }
                offset++;
                Thread.sleep(500);
            } catch (Exception e) {
                logger.error(LOG_PREFIX, e);
                break;
            }
        }
        return pets;
    }

    /**
     * 通过token取单个宠物信息
     * @param token
     * @param petCode
     */
    private void postPetInfo(String token, String petCode) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("token", token);
        JSONObject result = HttpClientUtils.doGet2JSON(URL_DETAIL + "?petCode=" + petCode, headers);
        if (result.getInteger("code") == 200) {
            JSONObject data = result.getJSONObject("data");
            logger.info(LOG_PREFIX + getPetInfo(data));
        } else {
            logger.info(LOG_PREFIX + result.toJSONString());
        }
    }


    /**
     * 取宠物基本信息
     * @param pet
     * @return
     */
    private static String getPetMainInfo(JSONObject pet) {
        String name = pet.getString("name");
        String petCode = pet.getString("petCode");
        String generation = pet.getString("generation");
        JSONObject attributeGradeResponse = pet.getJSONObject("attributeGradeResponse");
        String grade = attributeGradeResponse.getString("grade");
        Double action = attributeGradeResponse.getDouble("avgAction")+attributeGradeResponse.getDouble("incAction");
        Double interactive = attributeGradeResponse.getDouble("avgInteractive")+attributeGradeResponse.getDouble("incInteractive");
        Double lucky = attributeGradeResponse.getDouble("avgLucky")+attributeGradeResponse.getDouble("incLucky");
        StringBuilder sb = new StringBuilder("");
        JSONArray characterArray = pet.getJSONArray("attributeResponse");
        for (int j = 0; j < characterArray.size(); j++) {
            JSONObject characterJson = characterArray.getJSONObject(j);
            if (j != 0) {
                sb.append(",");
            }
            sb.append(characterJson.getString("name"));
        }
        String character = sb.toString();
        String mainInfo = "[" + petCode + "]\n[" + name + "][" + generation + "代" + grade + "星]["+"行动"+ CommonUtils.getRounding(action)+"交互"+CommonUtils.getRounding(interactive)+"幸运"+CommonUtils.getRounding(lucky)+"][" + character + "]";
        return mainInfo;
    }

    /**
     * 取宠物完整信息
     * @param pet
     * @return
     */
    private static String getPetInfo(JSONObject pet) {
        String infoStr = getPetMainInfo(pet) + "----" + getPetActionInfo(pet);
        return infoStr;
    }

    private static String getPetActionInfo(JSONObject pet){
        String actionInfo = "";
        String status = pet.getString("petStatus");
        actionInfo += PropertiesUtil.getStr(status,status) ;
        Integer leftTime = pet.getInteger("eventEndTime");
        if(leftTime!=null && leftTime!=0){
            actionInfo+=",剩余"+leftTime+"秒";
        }
        return actionInfo;
    }

    /**
     * 打印所有宠物完整信息
     * @param pets
     */
    private static void printAllPetInfo(JSONArray pets) {
        if (pets != null) {
            for (int i = 0; i < pets.size(); i++) {
                JSONObject pet = pets.getJSONObject(i);
                logger.info(LOG_PREFIX + getPetInfo(pet));
            }
        }

    }

    /**
     * 宠物左右接口
     * @param token
     * @param pet
     * @return
     */
    private static Integer petWalk(String token, JSONObject pet) {
        String petStatus = pet.getString("petStatus");
        String message = "";
        Integer code;
        if ("WALK".equalsIgnoreCase(petStatus)) {
            code = -99;//已在游走不发请求
        }else if("MATING".equalsIgnoreCase(petStatus)){
            code = -98;//已在交配不发起游走
        }else if("MATINGMARKET".equalsIgnoreCase(petStatus)){
            code = -97;//已在交配不发起游走
        }else {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("token", token);
            String petCode = pet.getString("petCode");
            JSONObject params = new JSONObject();
            params.put("petCode", petCode);
            JSONObject result = HttpClientUtils.doPost2JSON(URL_WALK + "?petCode=" + petCode, params, headers);
            message = result.getString("message");
            code = result.getInteger("code");
            logger.info(LOG_PREFIX + getPetMainInfo(pet) + "----" + message);
        }
        return code;
    }

    /**
     * 发起所有宠物游走
     * @param token
     * @param pets
     * @return
     */
    private static Map<String, Integer> petsAllWalk(String token, JSONArray pets) {
        Map<String, Integer> resultMap = new HashMap<>();
        if (pets != null) {
            int successCount = 0;
            for (int i = 0; i < pets.size(); i++) {
                JSONObject pet = pets.getJSONObject(i);
                if(true/*&&!"vIONs4MrbV9wXy5bjAFgvdfFfpNdT0l1".equalsIgnoreCase(pet.getString("petCode"))*/){
                    Integer code = petWalk(token, pet);
                    if (code >=0) {
                        try {
                            Thread.sleep((long) (Math.random() * 2000));
                        } catch (InterruptedException e) {
                            logger.error(LOG_PREFIX, e);
                        }
                    }
                    if (code <0 || code == 200) {//如果已经在游走视为成功
                        successCount++;
                    }
                }else{
                    logger.info(LOG_PREFIX+"跳过"+getPetMainInfo(pet));
                    successCount++;
                }
            }
            resultMap.put("successCount", successCount);
            resultMap.put("totalCount", pets.size());
        }
        return resultMap;
    }
}
