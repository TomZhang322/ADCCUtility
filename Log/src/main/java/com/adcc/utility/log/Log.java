package com.adcc.utility.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志记录类
 */
public final class Log {

    // logger
    public static Logger logger = LoggerFactory.getLogger(Log.class.getName());

    /**
     * 设置LoggerName
     * @param name
     */
    public static void setLoggerName(String name){
        logger = LoggerFactory.getLogger(name);
    }

    /**
     * INFO日志
     * @param log
     */
    public static void info(String log){
        try{
            logger.info(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * INFO日志
     * @param name
     * @param log
     */
    public static void info(String name,String log){
        try{
            LoggerFactory.getLogger(name).info(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * INFO日志
     * @param clazz
     * @param log
     */
    public static void info(Class clazz,String log){
        try{
            LoggerFactory.getLogger(clazz).info(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * DEBUG日志
     * @param log
     */
    public static void debug(String log){
        try{
            logger.debug(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * DEBUG日志
     * @param log
     * @param ex
     */
    public static void debug(String log,Exception ex){
        try{
            logger.debug(log, ex);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * DEBUG日志
     * @param name
     * @param log
     */
    public static void debug(String name,String log){
        try{
            LoggerFactory.getLogger(name).debug(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * DEBUG日志
     * @param name
     * @param log
     * @param ex
     */
    public static void debug(String name,String log,Exception ex){
        try{
            LoggerFactory.getLogger(name).debug(log, ex);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * DEBUG日志
     * @param clazz
     * @param log
     */
    public static void debug(Class clazz,String log){
        try{
            LoggerFactory.getLogger(clazz).debug(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * DEBUG日志
     * @param clazz
     * @param log
     * @param ex
     */
    public static void debug(Class clazz,String log,Exception ex){
        try{
            LoggerFactory.getLogger(clazz).debug(log, ex);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * WARN日志
     * @param log
     */
    public static void warn(String log){
        try{
            logger.warn(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * WARN日志
     * @param log
     * @param ex
     */
    public static void warn(String log,Exception ex){
        try{
            logger.warn(log, ex);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * WARN日志
     * @param name
     * @param log
     */
    public static void warn(String name,String log){
        try{
            LoggerFactory.getLogger(name).warn(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * WARN日志
     * @param clazz
     * @param log
     */
    public static void warn(Class clazz,String log){
        try{
            LoggerFactory.getLogger(clazz).warn(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * WARN日志
     * @param name
     * @param log
     * @param ex
     */
    public static void warn(String name,String log,Exception ex){
        try {
            LoggerFactory.getLogger(name).warn(log, ex);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * WARN日志
     * @param clazz
     * @param log
     * @param ex
     */
    public static void warn(Class clazz,String log,Exception ex){
        try {
            LoggerFactory.getLogger(clazz).warn(log, ex);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * ERROR日志
     * @param log
     */
    public static void error(String log){
        try{
            logger.error(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * ERROR日志
     * @param log
     * @param ex
     */
    public static void error(String log,Exception ex){
        try{
            logger.error(log, ex);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * ERROR日志
     * @param name
     * @param log
     */
    public static void error(String name,String log){
        try{
            LoggerFactory.getLogger(name).error(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * ERROR日志
     * @param clazz
     * @param log
     */
    public static void error(Class clazz,String log){
        try{
            LoggerFactory.getLogger(clazz).error(log);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * ERROR日志
     * @param name
     * @param log
     * @param ex
     */
    public static void error(String name,String log,Exception ex){
        try {
            LoggerFactory.getLogger(name).error(log, ex);
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * ERROR日志
     * @param clazz
     * @param log
     * @param ex
     */
    public static void error(Class clazz,String log,Exception ex){
        try {
            LoggerFactory.getLogger(clazz).error(log, ex);
        }catch (Exception e){
            throw e;
        }
    }
}
