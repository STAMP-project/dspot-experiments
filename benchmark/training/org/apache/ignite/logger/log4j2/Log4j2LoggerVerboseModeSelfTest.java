/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.logger.log4j2;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.ignite.Ignite;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.logging.log4j.Level;
import org.junit.Assert;
import org.junit.Test;


/**
 * Grid Log4j2 SPI test.
 */
public class Log4j2LoggerVerboseModeSelfTest {
    /**
     *
     */
    private static final String LOG_PATH_VERBOSE_TEST = "modules/core/src/test/config/log4j2-verbose-test.xml";

    /**
     * Test works fine after other tests. Please do not forget to call Log4J2Logger.cleanup()
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testVerboseMode() throws Exception {
        final PrintStream backupSysOut = System.out;
        final PrintStream backupSysErr = System.err;
        final ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        final ByteArrayOutputStream testErr = new ByteArrayOutputStream();
        String consoleOut = "Empty";
        String consoleErr = "Empty";
        String testMsg = "******* Hello Tester! ******* ";
        try {
            System.setOut(new PrintStream(testOut));
            System.setErr(new PrintStream(testErr));
            System.setProperty("IGNITE_QUIET", "false");
            try (Ignite ignite = G.start(Log4j2LoggerVerboseModeSelfTest.getConfiguration("verboseLogGrid", Log4j2LoggerVerboseModeSelfTest.LOG_PATH_VERBOSE_TEST))) {
                ignite.log().error((testMsg + (Level.ERROR)));
                ignite.log().warning((testMsg + (Level.WARN)));
                ignite.log().info((testMsg + (Level.INFO)));
                ignite.log().debug((testMsg + (Level.DEBUG)));
                ignite.log().trace((testMsg + (Level.TRACE)));
            }
        } finally {
            System.setProperty("IGNITE_QUIET", "true");
            System.setOut(backupSysOut);
            System.setErr(backupSysErr);
        }
        testOut.flush();
        testErr.flush();
        consoleOut = testOut.toString();
        consoleErr = testErr.toString();
        System.out.println("**************** Out Console content ***************");
        System.out.println(consoleOut);
        System.out.println("**************** Err Console content ***************");
        System.out.println(consoleErr);
        Assert.assertTrue(consoleOut.contains((testMsg + (Level.INFO))));
        Assert.assertTrue(consoleOut.contains((testMsg + (Level.DEBUG))));
        Assert.assertTrue(consoleOut.contains((testMsg + (Level.TRACE))));
        Assert.assertTrue(consoleOut.contains((testMsg + (Level.ERROR))));
        Assert.assertTrue(consoleOut.contains((testMsg + (Level.WARN))));
        Assert.assertTrue(consoleErr.contains((testMsg + (Level.ERROR))));
        Assert.assertTrue(consoleErr.contains((testMsg + (Level.WARN))));
        Assert.assertTrue((!(consoleErr.contains((testMsg + (Level.INFO))))));
        Assert.assertTrue(consoleErr.contains((testMsg + (Level.DEBUG))));
        Assert.assertTrue(consoleErr.contains((testMsg + (Level.TRACE))));
    }
}

