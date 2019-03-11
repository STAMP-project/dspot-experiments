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
package org.apache.rocketmq.logging;


import InnerLoggerFactory.InnerLogger;
import InternalLoggerFactory.LOGGER_INNER;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Assert;
import org.junit.Test;


public class InnerLoggerFactoryTest extends BasicLoggerTest {
    private ByteArrayOutputStream byteArrayOutputStream;

    public static final String LOGGER = "ConsoleLogger";

    private PrintStream out;

    @Test
    public void testInnerLoggerFactory() {
        InternalLoggerFactory.setCurrentLoggerType(LOGGER_INNER);
        InternalLogger logger1 = InnerLoggerFactory.getLogger(InnerLoggerFactoryTest.LOGGER);
        InternalLogger logger = InternalLoggerFactory.getLogger(InnerLoggerFactoryTest.LOGGER);
        Assert.assertTrue(logger.getName().equals(logger1.getName()));
        InternalLogger logger2 = InnerLoggerFactory.getLogger(InnerLoggerFactoryTest.class);
        InnerLoggerFactory.InnerLogger logger3 = ((InnerLoggerFactory.InnerLogger) (logger2));
        logger.info("innerLogger inner info Message");
        logger.error("innerLogger inner error Message", new RuntimeException());
        logger.debug("innerLogger inner debug message");
        logger3.info("innerLogger info message");
        logger3.error("logback error message");
        logger3.info("info {}", "hahahah");
        logger3.warn("warn {}", "hahahah");
        logger3.warn("logger3 warn");
        logger3.error("error {}", "hahahah");
        logger3.debug("debug {}", "hahahah");
        String content = new String(byteArrayOutputStream.toByteArray());
        System.out.println(content);
        Assert.assertTrue(content.contains("InnerLoggerFactoryTest"));
        Assert.assertTrue(content.contains("info"));
        Assert.assertTrue(content.contains("RuntimeException"));
        Assert.assertTrue((!(content.contains("debug"))));
    }
}

