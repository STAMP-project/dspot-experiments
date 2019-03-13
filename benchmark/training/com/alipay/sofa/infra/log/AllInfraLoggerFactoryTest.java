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
 * Created by yangguanchao on 18/01/04.
 */
public class AllInfraLoggerFactoryTest extends AbstractTestBase {
    @Test
    public void testGetLogger() {
        Logger logger = InfraHealthCheckLoggerFactory.getLogger(this.getClass());
        Assert.assertNotNull(logger);
        logger.info("ok testGetLogger by class");
        Assert.assertTrue(logger.isErrorEnabled());
    }

    /**
     * ???????????
     * <p/>
     * ?????????: sofa-rest-log/src/test/log4j.xml ??
     * Method: getLogger(String name)
     */
    @Test
    public void testInfoGetLogger() {
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
    }
}

