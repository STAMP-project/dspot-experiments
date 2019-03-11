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
package org.apache.nifi.processors.email;


import ConsumeIMAP.FOLDER;
import ConsumeIMAP.HOST;
import ConsumeIMAP.PASSWORD;
import ConsumeIMAP.PORT;
import ConsumeIMAP.REL_SUCCESS;
import ConsumeIMAP.USER;
import ConsumeIMAP.USE_SSL;
import ServerSetupTest.IMAP;
import ServerSetupTest.POP3;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.GreenMail;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.integration.mail.AbstractMailReceiver;


public class TestConsumeEmail {
    private GreenMail mockIMAP4Server;

    private GreenMail mockPOP3Server;

    private GreenMailUser imapUser;

    private GreenMailUser popUser;

    // Start the testing units
    @Test
    public void testConsumeIMAP4() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new ConsumeIMAP());
        runner.setProperty(HOST, IMAP.getBindAddress());
        runner.setProperty(PORT, String.valueOf(IMAP.getPort()));
        runner.setProperty(USER, "nifiUserImap");
        runner.setProperty(PASSWORD, "nifiPassword");
        runner.setProperty(FOLDER, "INBOX");
        runner.setProperty(USE_SSL, "false");
        addMessage("testConsumeImap1", imapUser);
        addMessage("testConsumeImap2", imapUser);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 2);
        final List<MockFlowFile> messages = runner.getFlowFilesForRelationship(REL_SUCCESS);
        String result = new String(runner.getContentAsByteArray(messages.get(0)));
        // Verify body
        Assert.assertTrue(result.contains("test test test chocolate"));
        // Verify sender
        Assert.assertTrue(result.contains("alice@nifi.org"));
        // Verify subject
        Assert.assertTrue(result.contains("testConsumeImap1"));
    }

    @Test
    public void testConsumePOP3() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new ConsumePOP3());
        runner.setProperty(HOST, POP3.getBindAddress());
        runner.setProperty(PORT, String.valueOf(POP3.getPort()));
        runner.setProperty(USER, "nifiUserPop");
        runner.setProperty(PASSWORD, "nifiPassword");
        runner.setProperty(FOLDER, "INBOX");
        runner.setProperty(USE_SSL, "false");
        addMessage("testConsumePop1", popUser);
        addMessage("testConsumePop2", popUser);
        runner.run();
        runner.assertTransferCount(ConsumePOP3.REL_SUCCESS, 2);
        final List<MockFlowFile> messages = runner.getFlowFilesForRelationship(ConsumePOP3.REL_SUCCESS);
        String result = new String(runner.getContentAsByteArray(messages.get(0)));
        // Verify body
        Assert.assertTrue(result.contains("test test test chocolate"));
        // Verify sender
        Assert.assertTrue(result.contains("alice@nifi.org"));
        // Verify subject
        Assert.assertTrue(result.contains("Pop1"));
    }

    @Test
    public void validateProtocol() {
        AbstractEmailProcessor<? extends AbstractMailReceiver> consume = new ConsumeIMAP();
        TestRunner runner = TestRunners.newTestRunner(consume);
        runner.setProperty(USE_SSL, "false");
        Assert.assertEquals("imap", consume.getProtocol(runner.getProcessContext()));
        runner = TestRunners.newTestRunner(consume);
        runner.setProperty(USE_SSL, "true");
        Assert.assertEquals("imaps", consume.getProtocol(runner.getProcessContext()));
        consume = new ConsumePOP3();
        Assert.assertEquals("pop3", consume.getProtocol(runner.getProcessContext()));
    }

    @Test
    public void validateUrl() throws Exception {
        Field displayUrlField = AbstractEmailProcessor.class.getDeclaredField("displayUrl");
        displayUrlField.setAccessible(true);
        AbstractEmailProcessor<? extends AbstractMailReceiver> consume = new ConsumeIMAP();
        TestRunner runner = TestRunners.newTestRunner(consume);
        runner.setProperty(HOST, "foo.bar.com");
        runner.setProperty(PORT, "1234");
        runner.setProperty(USER, "jon");
        runner.setProperty(PASSWORD, "qhgwjgehr");
        runner.setProperty(FOLDER, "MYBOX");
        runner.setProperty(USE_SSL, "false");
        Assert.assertEquals("imap://jon:qhgwjgehr@foo.bar.com:1234/MYBOX", consume.buildUrl(runner.getProcessContext()));
        Assert.assertEquals("imap://jon:[password]@foo.bar.com:1234/MYBOX", displayUrlField.get(consume));
    }
}

