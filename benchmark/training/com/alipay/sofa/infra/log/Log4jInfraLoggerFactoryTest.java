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


import com.alipay.sofa.infra.log.base.AbstractTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;


/**
 * Log4jInfrastructureHealthCheckLoggerFactory Tester.
 *
 * @author <guanchao.ygc>
 * @version 1.0
 * @since <pre>?? 20, 2016</pre>
 */
public class Log4jInfraLoggerFactoryTest extends AbstractTestBase {
    /**
     * Method: getLogger(String name)
     */
    @Test
    public void testDebugGetLogger() throws Exception {
        // TODO: Test goes here...
        // ?????Debug
        System.getProperties().put(AbstractTestBase.restLogLevel, "DEBUG");
        try {
            String name1 = "com.test.1";
            Logger logger = InfraHealthCheckLoggerFactory.getLogger(name1);
            Assert.assertNotNull(logger);
            logger.debug("test1 debug ok");
            String name2 = "com.test.2";
            Logger logger2 = InfraHealthCheckLoggerFactory.getLogger(name2);
            logger2.debug("test2 debug ok");
            Assert.assertFalse(logger.isTraceEnabled());
            Assert.assertTrue(logger.isInfoEnabled());
            Assert.assertTrue(logger.isDebugEnabled());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method: getLogger(String name)
     */
    @Test
    public void testInfoGetLogger() throws Exception {
        // TODO: Test goes here...
        System.getProperties().put(AbstractTestBase.restLogLevel, "INFO");
        try {
            String name1 = "com.test.3";
            Logger logger = InfraHealthCheckLoggerFactory.getLogger(name1);
            System.err.println(((("\nLoggerName1 : " + (logger.getName())) + " ,logger1:") + logger));
            Assert.assertNotNull(logger);
            logger.info("test1 info ok");
            String name2 = "com.test.4";
            Logger logger2 = InfraHealthCheckLoggerFactory.getLogger(name2);
            System.err.println(((("\nLoggerName2 : " + (logger2.getName())) + " ,logger2:") + logger2));
            logger2.info("test2 info ok");
            Assert.assertTrue(logger.isInfoEnabled());
            Assert.assertFalse(logger.isDebugEnabled());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testWarnGetLogger() throws Exception {
        // TODO: Test goes here...
        System.getProperties().put(AbstractTestBase.restLogLevel, "WARN");
        try {
            String name1 = "com.test.5";
            Logger logger = InfraHealthCheckLoggerFactory.getLogger(name1);
            Assert.assertNotNull(logger);
            logger.warn("test1 warn ok");
            String name2 = "com.test.6";
            Logger logger2 = InfraHealthCheckLoggerFactory.getLogger(name2);
            System.err.println(((("\nLoggerName2 : " + (logger2.getName())) + " ,logger2:") + logger2));
            logger2.warn("test2 warn ok");
            Assert.assertFalse(logger.isInfoEnabled());
            Assert.assertFalse(logger.isDebugEnabled());
            Assert.assertTrue(logger.isWarnEnabled());
            Assert.assertTrue(logger.isErrorEnabled());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testErrorGetLogger() throws Exception {
        // TODO: Test goes here...
        System.getProperties().put(AbstractTestBase.restLogLevel, "ERROR");
        try {
            String name1 = "com.test.7";
            Logger logger = InfraHealthCheckLoggerFactory.getLogger(name1);
            System.err.println(((("\nLoggerName1 : " + (logger.getName())) + " ,logger1:") + logger));
            Assert.assertNotNull(logger);
            logger.error("test1 error ok");
            String name2 = "com.test.8";
            Logger logger2 = InfraHealthCheckLoggerFactory.getLogger(name2);
            System.err.println(((("\nLoggerName2 : " + (logger2.getName())) + " ,logger2:") + logger2));
            logger2.error("test2 error ok");
            Assert.assertFalse(logger.isInfoEnabled());
            Assert.assertFalse(logger.isDebugEnabled());
            Assert.assertFalse(logger.isWarnEnabled());
            Assert.assertTrue(logger.isErrorEnabled());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

