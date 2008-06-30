package com.custardsource.cache.util;

import org.apache.commons.logging.Log;

public class LogUtils {
    /**
     * Convenience method to log a message to a {@link Log} at debug level. Takes a printf-style
     * {@link String} (see {@link Format}) and a vararg parameter list. The resulting string is
     * ONLY evaluated if debug logging is enabled; therefore, this has next to no overhead, and
     * makes for cleaner code than a lot of:
     * 
     * <pre>
     * if (LOG.isDebugEnabled()) {
     * </pre>
     */
    public static void debug(Log log, String message, Object... args) {
        if (!log.isDebugEnabled()) {
            return;
        }
        log.debug(getFormattedMessage(message, args));
    }
    public static void info(Log log, String message, Object... args) {
        if (!log.isInfoEnabled()) {
            return;
        }
        log.info(getFormattedMessage(message, args));
    }
    
    public static void warn(Log log, String message, Object... args) {
        if (!log.isWarnEnabled()) {
            return;
        }
        log.warn(getFormattedMessage(message, args));
    }
    
    public static void error(Log log, String message, Object... args) {
        if (!log.isErrorEnabled()) {
            return;
        }
        log.error(getFormattedMessage(message, args));
    }

    public static void fatal(Log log, String message, Object... args) {
        if (!log.isFatalEnabled()) {
            return;
        }
        log.fatal(getFormattedMessage(message, args));
    }
    
    public static void trace(Log log, String message, Object... args) {
        if (!log.isTraceEnabled()) {
            return;
        }
        log.trace(getFormattedMessage(message, args));
    }

    private static String getFormattedMessage(String message, Object... args) {
        String formattedMessage = message;
        if (args != null && args.length > 0) {
            formattedMessage = String.format(message, args);
        }
        return formattedMessage;
    }
}
