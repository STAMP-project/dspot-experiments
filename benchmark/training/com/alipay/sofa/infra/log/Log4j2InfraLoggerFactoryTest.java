/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.infra.log;


import com.alipay.sofa.common.log.env.LogEnvUtils;
import com.alipay.sofa.infra.log.base.AbstractTestBase;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;


/**
 * Log4jInfrastructureHealthCheckLoggerFactory Tester.
 *
 * @author <guanchao.ygc>
 * @version 1.0
 * @since <pre>1? 20, 2018</pre>
 */
public class Log4j2InfraLoggerFactoryTest extends AbstractTestBase {
    /**
     * Method: getLogger(String name)
     */
    @Test
    public void testDebugGetLogger() throws Exception {
        // ?????Debug
        LogEnvUtils.processGlobalSystemLogProperties().put(AbstractTestBase.restLogLevel, "DEBUG");
        try {
            String name = "com.test.name";
            Logger logger = InfraHealthCheckLoggerFactory.getLogger(name);
            Assert.assertNotNull(logger);
            Assert.assertFalse(logger.isTraceEnabled());
            TestCase.assertTrue(logger.isDebugEnabled());
        } finally {
            LogEnvUtils.processGlobalSystemLogProperties().remove(AbstractTestBase.restLogLevel);
        }
    }

    /**
     * Method: getLogger(String name)
     */
    @Test
    public void testInfoGetLogger() {
        LogEnvUtils.processGlobalSystemLogProperties().put(AbstractTestBase.restLogLevel, "INFO");
        try {
            String name = "com.test.name";
            Logger logger = InfraHealthCheckLoggerFactory.getLogger(name);
            Assert.assertNotNull(logger);
            TestCase.assertTrue(logger.isInfoEnabled());
            Assert.assertFalse(logger.isDebugEnabled());
        } finally {
            LogEnvUtils.processGlobalSystemLogProperties().remove(AbstractTestBase.restLogLevel);
        }
    }

    @Test
    public void testWarnGetLogger() {
        LogEnvUtils.processGlobalSystemLogProperties().put(AbstractTestBase.restLogLevel, "WARN");
        try {
            String name1 = "com.test.name";
            Logger logger = InfraHealthCheckLoggerFactory.getLogger(name1);
            Assert.assertNotNull(logger);
            Assert.assertFalse(logger.isInfoEnabled());
            TestCase.assertTrue(logger.isWarnEnabled());
        } finally {
            LogEnvUtils.processGlobalSystemLogProperties().remove(AbstractTestBase.restLogLevel);
        }
    }

    @Test
    public void testErrorGetLogger() {
        LogEnvUtils.processGlobalSystemLogProperties().put(AbstractTestBase.restLogLevel, "ERROR");
        try {
            String name1 = "com.test.name";
            Logger logger = InfraHealthCheckLoggerFactory.getLogger(name1);
            Assert.assertFalse(logger.isWarnEnabled());
            TestCase.assertTrue(logger.isErrorEnabled());
        } finally {
            LogEnvUtils.processGlobalSystemLogProperties().remove(AbstractTestBase.restLogLevel);
        }
    }
}

