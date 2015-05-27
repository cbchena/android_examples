package com.example.myAppLog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    private String s;
    private static Logger logger = LoggerFactory
            .getLogger(MyActivity.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        _configureLogbackByFilePath();



        // 制作异常
//        System.out.println(s.equals("any string"));
    }

    public void OnRecord(View view) {
        String path = "/sdcard/andy/crash/";
        getFileDir(path);
    }

    public List<String> getFileDir(String filePath) {
        File[] files = new File(filePath).listFiles();
        List<String> lstNames = new ArrayList<String>();
        if (files != null) {
            for (File file : files) {
//            File mm = new File(file.getParent() + "/" + file.getName().replace("n", "y"));
//            boolean result = file.renameTo(mm); // 修改文件名

                logger.debug("File path: {}", file.getPath());
                logger.info("====================File path: {}====================", file.getPath());
                lstNames.add(file.getName());
            }
        }

        return lstNames;
    }

    static final String LOGBACK_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "\n" +
            "<configuration>\n" +
            "\t<!-- 系统程序日志配置 -->\n" +
            "\t<!-- 将日志打印到控制台 -->\n" +
            "\t<appender name=\"consoleAppender\" class=\"ch.qos.logback.core.ConsoleAppender\">\n" +
            "\t\t<encoder>\n" +
            "\t\t\t<pattern>%d{HH:mm:ss,SSS} %-5p %m%n</pattern>\n" +
            "\t\t</encoder>\n" +
            "\t</appender>\n" +
            "\t\n" +
            "\t<!-- 将日志打印到文件 -->\n" +
            "\t<appender name=\"fileAppender\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">\n" +
            "\t\t<File>/sdcard/andy/logs2/andy.log</File>\n" +
            "        <append>true</append>\n" +
            "\t\t<encoder>\n" +
            "\t\t\t<charset>UTF-8</charset>\n" +
            "\t\t\t<pattern>%d %-5p %m%n</pattern>\n" +
            "\t\t</encoder>\n" +
            "\n" +
            "        <!-- 路径：../logs/fandy/andy.log  时间：{yyyy-MM-dd-HH}，以小时分隔  -->\n" +
            "\t\t<rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">\n" +
            "\t\t\t<fileNamePattern>/sdcard/andy/logs2/andy.log.%d{yyyy-MM-dd}\n" +
            "\t\t\t</fileNamePattern>\n" +
            "\t\t</rollingPolicy>\n" +
            "\t</appender>\n" +
            "\t\n" +
            "\t<logger name=\"myAppLog\" level=\"INFO\">\n" +
            "\t\t<appender-ref ref=\"consoleAppender\" />\n" +
            "\t\t<appender-ref ref=\"fileAppender\" />\n" +
            "\t</logger>\n" +
            "\t\n" +
            "\t<root level=\"INFO\"> <!-- 日志级别 -->\n" +
            "\t\t<appender-ref ref=\"consoleAppender\" />\n" +
            "\t\t<appender-ref ref=\"fileAppender\" />\n" +
            "\t</root>\n" +
            "\n" +
            "</configuration>";

    private void _configureLogbackByFilePath() {
        LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
        lc.reset();

        JoranConfigurator config = new JoranConfigurator();
        config.setContext(lc);

        InputStream stream = new ByteArrayInputStream(LOGBACK_XML.getBytes());
        try {
            config.doConfigure(stream);
        } catch (JoranException e) {
            e.printStackTrace();
        }
    }
}
