package com.stevekung.skyblockcatia.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerIN
{
    private static final Logger LOG = LogManager.getLogger("SkyBlockcatia");
    private static File logFile;
    private static PrintWriter logWriter;

    public static void info(String message)
    {
        LoggerIN.LOG.info(message);
    }

    public static void error(String message)
    {
        LoggerIN.LOG.error(message);
    }

    public static void warning(String message)
    {
        LoggerIN.LOG.warn(message);
    }

    public static void info(String message, Object... obj)
    {
        LoggerIN.LOG.info(message, obj);
    }

    public static void error(String message, Object... obj)
    {
        LoggerIN.LOG.error(message, obj);
    }

    public static void warning(String message, Object... obj)
    {
        LoggerIN.LOG.warn(message, obj);
    }

    public static void logToast(Object object)
    {
        String message = object == null ? "null" : object.toString();
        String preLine = new SimpleDateFormat("[HH:mm:ss]").format(new Date()) + " [" + Level.DEBUG.name() + "] ";

        for (String line : message.split("\\n"))
        {
            LoggerIN.LOG.log(Level.DEBUG, line);
            logWriter.println(preLine + line);
        }
        logWriter.flush();
    }

    public static void setup()
    {
        File logDirectory = new File("./logs/skyblockcatia/" + GameProfileUtils.getUUID().toString() + "/");
        logDirectory.mkdirs();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        logFile = new File(logDirectory, dtf.format(now) + "-toast.log");

        try
        {
            logFile.createNewFile();
            logWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}