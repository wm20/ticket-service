package com.myproject.ticketing.service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Util class to load properties from config file
 * 
 * @author WM
 *
 */

public class PropertiesUtil {

	private static Properties appProps;

	private static PropertiesUtil propertiesUtilInstance = null;

	private static final String APP_FILE = "/app.properties";

	private static final Logger logger = LogManager.getLogger(PropertiesUtil.class);

	private PropertiesUtil() {

		appProps = new Properties();
		try (InputStream inputStream = getClass().getResourceAsStream(APP_FILE);) {
			appProps.load(inputStream);
		} catch (IOException e) {
			logger.error("Error loading props file", e);
		}
	}

	public static synchronized PropertiesUtil getInstance() {
		if (null == propertiesUtilInstance) {
			propertiesUtilInstance = new PropertiesUtil();
		}

		return propertiesUtilInstance;
	}

	public static String getAllProperties() {
		StringWriter writer = new StringWriter();
		appProps.list(new PrintWriter(writer));
		return writer.getBuffer().toString();
	}

	/**
	 * read an int configuration item
	 * 
	 * @param key          The key of the configuration item
	 * @param defaultValue The defaultValue of the configuration item if none is
	 *                     found in the config file
	 * @param minValue     The min value that the configured property should have
	 * @param maxValue     The max value that the configured property should have
	 * @return The config value as an int, or default value if the property could
	 *         not be read or outside specified bounds.
	 */
	public int getIntProperty(String key, int defaultValue, int minValue, int maxValue) {
		int result = defaultValue;
		String value = appProps.getProperty(key, Integer.toString(defaultValue));
		try {
			result = Integer.parseInt(value);
			if (result < minValue || result > maxValue) { // outside bounds
				logger.error("Configured int value exceeds bounds, reverting to defaultValue of: " + defaultValue);
			}
		} catch (NumberFormatException e) {
			logger.error("getIntProperty: Unable to parse key: " + key + ", value: " + value
					+ ", reverting to defaultValue: " + result);
		}

		return result;
	}

	/**
	 * Update an int property in the properties file
	 * 
	 * @param key   The key of the config item
	 * @param value The value to set for the config item
	 */
	public void setProperty(String key, String value) {

		URL url = null;
		File file = null;
		FileOutputStream out = null;

		try {
			url = getClass().getResource(APP_FILE);
			file = new File(url.toURI());
			out = new FileOutputStream(file.getAbsolutePath());
			appProps.setProperty(key, String.valueOf(value));
			appProps.store(out, null);
		} catch (FileNotFoundException e) {
			logger.error("Error setting properties", e);
		} catch (IOException e) {
			logger.error("Error setting properties", e);
		} catch (URISyntaxException e) {
			logger.error("Error setting properties", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {

				}
			}
		}
	}

	/**
	 * read a configuration item of type long
	 * 
	 * @param key          The key of the configuration item
	 * @param defaultValue The defaultValue of the configuration item if none is
	 *                     found in the config file
	 * @param minValue     The min value that the configured property should have
	 * @param maxValue     The max value that the configured property should have
	 * @return The config value as an int, or default value if the property could
	 *         not be read or outside specified bounds.
	 */
	public long getLongProperty(String key, long defaultValue, long minValue, long maxValue) {
		long result = defaultValue;
		String value = appProps.getProperty(key, Long.toString(defaultValue));
		try {
			result = Long.parseLong(value);
			if (result < minValue || result > maxValue) { // outside bounds
				logger.error("Configured long value exceeds bounds, reverting to defaultValue of: " + defaultValue);
			}
		} catch (NumberFormatException e) {
			logger.error("getIntProperty: Unable to parse key: " + key + ", value: " + value
					+ ", reverting to defaultValue: " + result);
		}

		return result;
	}

}
