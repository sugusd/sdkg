/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.utils.JacksonUtils;

@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
public abstract class AbstractAdVancedTestNGSpringContextTest extends
        AbstractTestNGSpringContextTests {
    protected final Logger log = LogManager.getLogger(this.getClass());

    protected Properties propParams = new Properties();

    @BeforeClass
    @Parameters({
            "envName"
    })
    protected void initParam(@Optional("unitTest") String envName) {
        try {
            InputStream inParam =
                    this.getClass().getResourceAsStream((("/testdata/" + envName) + ".properties"));
            propParams.load(inParam);
            inParam.close();
        } catch (IOException e) {
            log.error("load config for error:{}", e);
        }
    }

    @DataProvider(name = "loadParam")
    public Object[][] loadParam(Method method) {
        Set<String> keys = propParams.stringPropertyNames();
        List<String[]> parameters = new ArrayList<String[]>();
        for (String key : keys) {
            if (key.startsWith(((this.getClass().getSimpleName() + ".") + (method.getName() + ".")))) {
                String value = propParams.getProperty(key);
                parameters.add(new String[] {value});
            }
        }
        Object[][] object = new Object[parameters.size()][];
        for (int i = 0; (i < object.length); i++) {
            String[] str = parameters.get(i);
            object[i] = new String[str.length];
            System.arraycopy(str, 0, object[i], 0, str.length);
        }
        return object;
    }

    protected JsonNode generateParamJson(String reqParamsJsonStrAndAssert) {
        String[] reqParamsJsonStrAndAssertArray = reqParamsJsonStrAndAssert.split(",assert=");
        String reqParamsJsonStr = reqParamsJsonStrAndAssertArray[0];
        log.debug(reqParamsJsonStr);
        return JacksonUtils.toJsonNode(reqParamsJsonStr);
    }

}
