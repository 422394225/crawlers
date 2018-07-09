package core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * properties配置文件工具类
 * 
 */
public class PropertiesUtil {

	private static Logger logger= LoggerFactory.getLogger(PropertiesUtil.class);
	public static Properties properties = new Properties();

	static {
		init();
	}

	private static void init() {
		String path = "./";
		try {
//			path = Thread.currentThread().getContextClassLoader().getResource("").getFile();
			path = PropertiesUtil.class.getClassLoader().getResource("").getFile();
		} catch (NullPointerException e) {
			File file = new File(path);
			logger.error("未获取到配置文件将从" + file.getAbsolutePath() + "目录获取", e);
		}

		try {
			path = URLDecoder.decode(path, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			throw new RuntimeException("转换" + path + "配置文件编码时候出错");
		} // 防止路径中出现中文或者空格
		File[] files = new File(path).listFiles();
		for (File file : files) {
			if (file.getName().endsWith(".properties") && !file.getName().startsWith("log4j")) {
				try {
					properties.load(new FileInputStream(file));
				} catch (Exception e) {
					throw new RuntimeException("加载" + file.getName() + "配置文件时候出错");
				}
			}
		}
	}

	private static String getValue(String key) {
		return properties.getProperty(key);
	}

	private static void setValue(String key, String value) {
		properties.setProperty(key, value);
	}

	public static String getStr(String name, String defaultValue) {
		String valueString = getValue(name);
		if (valueString == null){
			logger.info("未找到配置项:"+name+",将使用默认值"+defaultValue);
			return defaultValue;
		}
		return String.valueOf(valueString);
	}

	public static int getInt(String name, int defaultValue) {
		String valueString = getValue(name);
		if (valueString == null){
			logger.info("未找到配置项:"+name+",将使用默认值"+defaultValue);
			return defaultValue;
		}
		return Integer.parseInt(valueString);
	}

	public static long getLong(String name, long defaultValue) {
		String valueString = getValue(name);
		if (valueString == null){
			logger.info("未找到配置项:"+name+",将使用默认值"+defaultValue);
			return defaultValue;
		}
		return Long.parseLong(valueString);
	}
}
