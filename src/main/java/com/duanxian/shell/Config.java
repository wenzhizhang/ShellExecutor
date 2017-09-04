package com.duanxian.shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;


public class Config {
    private static final Logger LOG = LogManager.getLogger(Config.class);
    static final String FILE_MARKER = System.getProperty("file.separator");
    private static Map<String, String> configuration = new HashMap();
    private static Timer timer = new Timer(true);
    private static boolean isConfigLoded = false;
    private static final String CONF_FILE = "/opt/oss/NSN-ne3sws_core/install/conf/cnn_config";
    private static String placeholderPrefix = "${";
    private static String placeholderSuffix = "}";
    private static boolean ignoreUnresolvablePlaceholders = false;

    public Config() {
    }

    public static Map<String, String> getAllConfigurations() {
        return configuration;
    }

    protected static synchronized void loadProperties() {
        Properties allConfig = new Properties();
        Properties properties = new Properties();
        Object fis = null;
        FileInputStream propfis = null;
        FileOutputStream fout = null;

        try {
            File confFile = new File(CONF_FILE);
            if(!confFile.exists()) {
                LOG.warn("Configuration file does not exist.");
                return;
            }
            propfis = new FileInputStream(confFile);
            properties.load(propfis);
            LOG.info("Loaded the properties from " + confFile);
            Iterator i$1 = properties.keySet().iterator();

            while(i$1.hasNext()) {
                Object k = i$1.next();
                String value1 = "";
                if(null != properties.get(k)) {
                    value1 = parseStringValue((String)properties.get(k), properties, (Set)null);
                }

                if(value1.contains("$")) {
                    value1 = decodeEnvVar(value1);
                    LOG.debug("Decoded Value : " + value1);
                }

                allConfig.put(k.toString().trim(), value1.trim());
                LOG.debug("Property " + k + " : " + value1);
                configuration.put(k.toString().trim(), value1.trim());
            }

        } catch (IOException var37) {
            LOG.fatal("Error while reading the preferences", var37);
        } finally {
            try {
                if(null != fis) {
                    ((FileInputStream)fis).close();
                }
            } catch (IOException var36) {
                LOG.warn(var36.getMessage());
            }

            try {
                if(null != propfis) {
                    propfis.close();
                }
            } catch (IOException var35) {
                LOG.warn(var35.getMessage());
            }

            try {
                if(null != fout) {
                    fout.close();
                }
            } catch (IOException var34) {
                LOG.warn(var34.getMessage());
            }

        }

        isConfigLoded = true;
    }

    private static String decodeEnvVar(String inString) {
        String retValue = null;
        String[] paths = inString.split("/");
        if(paths.length > 1) {
            StringBuffer sb = new StringBuffer();
            String[] arr$ = paths;
            int len$ = paths.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String path = arr$[i$];
                if(path.contains("$")) {
                    String sysVar = System.getenv(path.substring(1));
                    path = sysVar != null?sysVar:path;
                }

                sb.append(path + FILE_MARKER);
            }

            retValue = sb.toString();
        }

        LOG.debug("Decoded value : " + retValue);
        return retValue;
    }

    private static String parseStringValue(String strVal, Properties properties, Set<String> visitedPlaceholders) {
        if(null == visitedPlaceholders) {
            visitedPlaceholders = new HashSet();
        }

        StringBuffer buf = new StringBuffer(strVal);
        int startIndex = strVal.indexOf(placeholderPrefix);

        while(startIndex != -1) {
            int endIndex = buf.toString().indexOf(placeholderSuffix, startIndex + placeholderPrefix.length());
            if(endIndex != -1) {
                String placeholder = buf.substring(startIndex + placeholderPrefix.length(), endIndex);
                if(!((Set)visitedPlaceholders).add(placeholder)) {
                    throw new RuntimeException("Circular placeholder reference \'" + placeholder + "\' in property definitions");
                }

                String propVal = get(placeholder, (String)null);
                if(null == propVal) {
                    propVal = properties.getProperty(placeholder);
                }

                if(null != propVal) {
                    propVal = parseStringValue(propVal, properties, (Set)visitedPlaceholders);
                    buf.replace(startIndex, endIndex + placeholderSuffix.length(), propVal);
                    LOG.debug("Resolved placeholder \'" + placeholder + "\'");
                    startIndex = buf.toString().indexOf(placeholderPrefix, startIndex + propVal.length());
                } else {
                    if(!ignoreUnresolvablePlaceholders) {
                        throw new RuntimeException("Could not resolve placeholder \'" + placeholder + "\'");
                    }

                    LOG.error("Not able to resolve the placeholder :" + placeholder);
                    startIndex = buf.toString().indexOf(placeholderPrefix, endIndex + placeholderSuffix.length());
                }

                ((Set)visitedPlaceholders).remove(placeholder);
            } else {
                startIndex = -1;
            }
        }

        return buf.toString();
    }

    public static String get(String key, String def) {
        String value = (String)configuration.get(key.trim());
        if(!isEmpty(value)) {
            def = value.trim();
        }

        return def;
    }

    private static boolean isEmpty(String value) {
        return null == value || value.trim().length() == 0;
    }

    public static boolean getBoolean(String key, boolean def) {
        String value = (String)configuration.get(key);
        if(!isEmpty(value)) {
            def = value.trim().equalsIgnoreCase("true");
        }

        return def;
    }

    public static double getDouble(String key, double def) {
        String value = (String)configuration.get(key);
        if(!isEmpty(value)) {
            try {
                def = Double.parseDouble(value.trim());
            } catch (NumberFormatException var5) {
                LOG.error("Exception thrown while parsing the config for " + key, var5);
            }
        }

        return def;
    }

    public static float getFloat(String key, float def) {
        String value = (String)configuration.get(key);
        if(!isEmpty(value)) {
            try {
                def = Float.parseFloat(value.trim());
            } catch (NumberFormatException var4) {
                LOG.error("Exception thrown while parsing the config for " + key, var4);
            }
        }

        return def;
    }

    public static int getInt(String key, int def) {
        String value = (String)configuration.get(key);
        if(!isEmpty(value)) {
            try {
                def = Integer.parseInt(value.trim());
            } catch (NumberFormatException var4) {
                LOG.error("Exception thrown while parsing the config for " + key, var4);
            }
        }

        return def;
    }

    public static long getLong(String key, long def) {
        String value = (String)configuration.get(key);
        if(!isEmpty(value)) {
            try {
                def = Long.parseLong(value.trim());
            } catch (NumberFormatException var5) {
                LOG.error("Exception thrown while parsing the config for " + key, var5);
            }
        }

        return def;
    }

}
