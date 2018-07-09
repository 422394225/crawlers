package core.utils;

import java.math.BigDecimal;

public class CommonUtils {

    public static String getStr(String str){
        if(str==null){
            return "";
        }else{
            return str;
        }
    }

    public static int getInt(Integer num){
        if(num==null){
            return 0;
        }else{
            return num;
        }
    }
    /**
     * 四舍五入，这里取两位
     * @param num
     * @return
     */
    public static Double getRounding(Double num){
        return getRounding(num,2);
    }

    /**
     * 四舍五入
     * @param num
     * @param scale 位数
     * @return
     */
    public static Double getRounding(Double num,int scale){
        return new BigDecimal(num).setScale(scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
