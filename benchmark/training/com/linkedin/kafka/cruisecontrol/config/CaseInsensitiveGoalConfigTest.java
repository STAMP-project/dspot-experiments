/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.config;


import java.util.Properties;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit test for testing goals with excluded topics under fixed cluster properties.
 */
@RunWith(Parameterized.class)
public class CaseInsensitiveGoalConfigTest {
    private static final Logger LOG = LoggerFactory.getLogger(CaseInsensitiveGoalConfigTest.class);

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private Properties _properties;

    private Class<Throwable> _exceptionClass;

    public CaseInsensitiveGoalConfigTest(Properties properties, Class<Throwable> exceptionClass) {
        _properties = properties;
        _exceptionClass = exceptionClass;
    }

    @Test
    public void test() {
        CaseInsensitiveGoalConfigTest.LOG.debug("Testing case insensitive goal configuration: {}.", _properties);
        if ((_exceptionClass) != null) {
            expected.expect(_exceptionClass);
        }
        new KafkaCruiseControlConfig(_properties);
        Assert.assertTrue("Failed to detect case insensitive goal configs.", true);
    }
}

