package core.starter;

import core.utils.PropertiesUtil;
import core.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starter {
    private static Logger logger = LoggerFactory.getLogger(Starter.class);
    private static final String MAIN_CLASS = PropertiesUtil.getStr("crawlers.enter.method",null);

    public static void main(String[] args) {
        logger.info("------------------CRAWLERS INIT START------------------");
        logger.info("------------------CRAWLERS INIT END--------------------");
        ReflectUtils.invokeMethod(MAIN_CLASS);
    }
}