/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.test;


import org.apache.zookeeper.TestableZooKeeper;
import org.apache.zookeeper.server.command.FourLetterCommands;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FourLetterWordsWhiteListTest extends ClientBase {
    protected static final Logger LOG = LoggerFactory.getLogger(FourLetterWordsWhiteListTest.class);

    /* ZOOKEEPER-2693: test white list of four letter words.
    For 3.5.x default white list is empty. Verify that is
    the case (except 'stat' command which is enabled in ClientBase
    which other tests depend on.).
     */
    @Test(timeout = 30000)
    public void testFourLetterWordsAllDisabledByDefault() throws Exception {
        stopServer();
        FourLetterCommands.resetWhiteList();
        System.setProperty("zookeeper.4lw.commands.whitelist", "stat");
        startServer();
        // Default white list for 3.5.x is empty, so all command should fail.
        verifyAllCommandsFail();
        TestableZooKeeper zk = createClient();
        verifyAllCommandsFail();
        getData("/", true, null);
        verifyAllCommandsFail();
        close();
        verifyFuzzyMatch("stat", "Outstanding");
        verifyAllCommandsFail();
    }

    @Test(timeout = 30000)
    public void testFourLetterWordsEnableSomeCommands() throws Exception {
        stopServer();
        FourLetterCommands.resetWhiteList();
        System.setProperty("zookeeper.4lw.commands.whitelist", "stat, ruok, isro");
        startServer();
        // stat, ruok and isro are white listed.
        verifyFuzzyMatch("stat", "Outstanding");
        verifyExactMatch("ruok", "imok");
        verifyExactMatch("isro", "rw");
        // Rest of commands fail.
        verifyExactMatch("conf", generateExpectedMessage("conf"));
        verifyExactMatch("cons", generateExpectedMessage("cons"));
        verifyExactMatch("crst", generateExpectedMessage("crst"));
        verifyExactMatch("dirs", generateExpectedMessage("dirs"));
        verifyExactMatch("dump", generateExpectedMessage("dump"));
        verifyExactMatch("envi", generateExpectedMessage("envi"));
        verifyExactMatch("gtmk", generateExpectedMessage("gtmk"));
        verifyExactMatch("stmk", generateExpectedMessage("stmk"));
        verifyExactMatch("srst", generateExpectedMessage("srst"));
        verifyExactMatch("wchc", generateExpectedMessage("wchc"));
        verifyExactMatch("wchp", generateExpectedMessage("wchp"));
        verifyExactMatch("wchs", generateExpectedMessage("wchs"));
        verifyExactMatch("mntr", generateExpectedMessage("mntr"));
    }

    @Test(timeout = 30000)
    public void testISROEnabledWhenReadOnlyModeEnabled() throws Exception {
        stopServer();
        FourLetterCommands.resetWhiteList();
        System.setProperty("zookeeper.4lw.commands.whitelist", "stat");
        System.setProperty("readonlymode.enabled", "true");
        startServer();
        verifyExactMatch("isro", "rw");
        System.clearProperty("readonlymode.enabled");
    }

    @Test(timeout = 30000)
    public void testFourLetterWordsInvalidConfiguration() throws Exception {
        stopServer();
        FourLetterCommands.resetWhiteList();
        System.setProperty("zookeeper.4lw.commands.whitelist", ("foo bar" + (" foo,,, " + "bar :.,@#$%^&*() , , , , bar, bar, stat,        ")));
        startServer();
        // Just make sure we are good when admin made some mistakes in config file.
        verifyAllCommandsFail();
        // But still, what's valid in white list will get through.
        verifyFuzzyMatch("stat", "Outstanding");
    }

    @Test(timeout = 30000)
    public void testFourLetterWordsEnableAllCommandsThroughAsterisk() throws Exception {
        stopServer();
        FourLetterCommands.resetWhiteList();
        System.setProperty("zookeeper.4lw.commands.whitelist", "*");
        startServer();
        verifyAllCommandsSuccess();
    }

    @Test(timeout = 30000)
    public void testFourLetterWordsEnableAllCommandsThroughExplicitList() throws Exception {
        stopServer();
        FourLetterCommands.resetWhiteList();
        System.setProperty("zookeeper.4lw.commands.whitelist", ("ruok, envi, conf, stat, srvr, cons, dump," + ("wchs, wchp, wchc, srst, crst, " + "dirs, mntr, gtmk, isro, stmk")));
        startServer();
        verifyAllCommandsSuccess();
    }
}

