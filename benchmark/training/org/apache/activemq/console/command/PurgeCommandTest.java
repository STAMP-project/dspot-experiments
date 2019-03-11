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
package org.apache.activemq.console.command;


import java.util.ArrayList;
import java.util.List;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import junit.framework.TestCase;
import org.apache.activemq.console.CommandContext;
import org.apache.activemq.console.formatter.CommandShellOutputFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;


public class PurgeCommandTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(PurgeCommandTest.class);

    protected static final int MESSAGE_COUNT = 10;

    protected static final String PROPERTY_NAME = "XTestProperty";

    protected static final String PROPERTY_VALUE = "1:1";

    // check for existence of property
    protected static final String MSG_SEL_WITH_PROPERTY = (PurgeCommandTest.PROPERTY_NAME) + " is not null";

    // check for non-existence of property
    protected static final String MSG_SEL_WITHOUT_PROPERTY = (PurgeCommandTest.PROPERTY_NAME) + " is null";

    // complex message selector query using XTestProperty and JMSPriority
    protected static final String MSG_SEL_COMPLEX = (((PurgeCommandTest.PROPERTY_NAME) + "='") + "1:1") + "' AND JMSPriority>3";

    // complex message selector query using XTestProperty AND JMSPriority
    // but in SQL-92 syntax
    protected static final String MSG_SEL_COMPLEX_SQL_AND = ((("(" + (PurgeCommandTest.PROPERTY_NAME)) + "='") + "1:1") + "') AND (JMSPriority>3)";

    // complex message selector query using XTestProperty OR JMSPriority
    // but in SQL-92 syntax
    protected static final String MSG_SEL_COMPLEX_SQL_OR = ((("(" + (PurgeCommandTest.PROPERTY_NAME)) + "='") + "1:1") + "') OR (JMSPriority>3)";

    protected static final String QUEUE_NAME = "org.apache.activemq.network.jms.QueueBridgeTest";

    protected AbstractApplicationContext context;

    protected QueueConnection localConnection;

    protected QueueRequestor requestor;

    protected QueueSession requestServerSession;

    protected MessageConsumer requestServerConsumer;

    protected MessageProducer requestServerProducer;

    protected Queue theQueue;

    public void testPurgeCommandSimpleSelector() throws Exception {
        try {
            PurgeCommand purgeCommand = new PurgeCommand();
            CommandContext context = new CommandContext();
            context.setFormatter(new CommandShellOutputFormatter(System.out));
            purgeCommand.setCommandContext(context);
            purgeCommand.setJmxUseLocal(true);
            List<String> tokens = new ArrayList<String>();
            tokens.add("--msgsel");
            tokens.add(PurgeCommandTest.MSG_SEL_WITH_PROPERTY);
            addMessages();
            validateCounts(PurgeCommandTest.MESSAGE_COUNT, PurgeCommandTest.MESSAGE_COUNT, ((PurgeCommandTest.MESSAGE_COUNT) * 2));
            purgeCommand.execute(tokens);
            validateCounts(0, PurgeCommandTest.MESSAGE_COUNT, PurgeCommandTest.MESSAGE_COUNT);
        } finally {
            purgeAllMessages();
        }
    }

    public void testPurgeCommandComplexSelector() throws Exception {
        try {
            PurgeCommand purgeCommand = new PurgeCommand();
            CommandContext context = new CommandContext();
            context.setFormatter(new CommandShellOutputFormatter(System.out));
            purgeCommand.setCommandContext(context);
            purgeCommand.setJmxUseLocal(true);
            List<String> tokens = new ArrayList<String>();
            tokens.add("--msgsel");
            tokens.add(PurgeCommandTest.MSG_SEL_COMPLEX);
            addMessages();
            validateCounts(PurgeCommandTest.MESSAGE_COUNT, PurgeCommandTest.MESSAGE_COUNT, ((PurgeCommandTest.MESSAGE_COUNT) * 2));
            purgeCommand.execute(tokens);
            QueueBrowser withPropertyBrowser = requestServerSession.createBrowser(theQueue, PurgeCommandTest.MSG_SEL_COMPLEX);
            QueueBrowser allBrowser = requestServerSession.createBrowser(theQueue);
            int withCount = getMessageCount(withPropertyBrowser, "withProperty ");
            int allCount = getMessageCount(allBrowser, "allMessages ");
            withPropertyBrowser.close();
            allBrowser.close();
            TestCase.assertEquals((("Expected withCount to be " + ("0" + " was ")) + withCount), 0, withCount);
            TestCase.assertEquals(((("Expected allCount to be " + (PurgeCommandTest.MESSAGE_COUNT)) + " was ") + allCount), PurgeCommandTest.MESSAGE_COUNT, allCount);
            PurgeCommandTest.LOG.info(((((("withCount = " + withCount) + "\n allCount = ") + allCount) + "\n  = ") + "\n"));
        } finally {
            purgeAllMessages();
        }
    }

    public void testPurgeCommandComplexSQLSelector_AND() throws Exception {
        try {
            String one = "ID:mac.fritz.box:1213242.3231.1:1:1:100";
            String two = "\\*:100";
            try {
                if (one.matches(two))
                    PurgeCommandTest.LOG.info("String matches.");
                else
                    PurgeCommandTest.LOG.info("string does not match.");

            } catch (Exception ex) {
                PurgeCommandTest.LOG.error(ex.getMessage());
            }
            PurgeCommand purgeCommand = new PurgeCommand();
            CommandContext context = new CommandContext();
            context.setFormatter(new CommandShellOutputFormatter(System.out));
            purgeCommand.setCommandContext(context);
            purgeCommand.setJmxUseLocal(true);
            List<String> tokens = new ArrayList<String>();
            tokens.add("--msgsel");
            tokens.add(PurgeCommandTest.MSG_SEL_COMPLEX_SQL_AND);
            addMessages();
            validateCounts(PurgeCommandTest.MESSAGE_COUNT, PurgeCommandTest.MESSAGE_COUNT, ((PurgeCommandTest.MESSAGE_COUNT) * 2));
            purgeCommand.execute(tokens);
            QueueBrowser withPropertyBrowser = requestServerSession.createBrowser(theQueue, PurgeCommandTest.MSG_SEL_COMPLEX_SQL_AND);
            QueueBrowser allBrowser = requestServerSession.createBrowser(theQueue);
            int withCount = getMessageCount(withPropertyBrowser, "withProperty ");
            int allCount = getMessageCount(allBrowser, "allMessages ");
            withPropertyBrowser.close();
            allBrowser.close();
            TestCase.assertEquals((("Expected withCount to be " + ("0" + " was ")) + withCount), 0, withCount);
            TestCase.assertEquals(((("Expected allCount to be " + (PurgeCommandTest.MESSAGE_COUNT)) + " was ") + allCount), PurgeCommandTest.MESSAGE_COUNT, allCount);
            PurgeCommandTest.LOG.info(((((("withCount = " + withCount) + "\n allCount = ") + allCount) + "\n  = ") + "\n"));
        } finally {
            purgeAllMessages();
        }
    }

    public void testPurgeCommandComplexSQLSelector_OR() throws Exception {
        try {
            PurgeCommand purgeCommand = new PurgeCommand();
            CommandContext context = new CommandContext();
            context.setFormatter(new CommandShellOutputFormatter(System.out));
            purgeCommand.setCommandContext(context);
            purgeCommand.setJmxUseLocal(true);
            List<String> tokens = new ArrayList<String>();
            tokens.add("--msgsel");
            tokens.add(PurgeCommandTest.MSG_SEL_COMPLEX_SQL_OR);
            addMessages();
            validateCounts(PurgeCommandTest.MESSAGE_COUNT, PurgeCommandTest.MESSAGE_COUNT, ((PurgeCommandTest.MESSAGE_COUNT) * 2));
            purgeCommand.execute(tokens);
            QueueBrowser withPropertyBrowser = requestServerSession.createBrowser(theQueue, PurgeCommandTest.MSG_SEL_COMPLEX_SQL_OR);
            QueueBrowser allBrowser = requestServerSession.createBrowser(theQueue);
            int withCount = getMessageCount(withPropertyBrowser, "withProperty ");
            int allCount = getMessageCount(allBrowser, "allMessages ");
            withPropertyBrowser.close();
            allBrowser.close();
            TestCase.assertEquals(("Expected withCount to be 0 but was " + withCount), 0, withCount);
            TestCase.assertEquals(("Expected allCount to be 0 but was " + allCount), 0, allCount);
            PurgeCommandTest.LOG.info(((((("withCount = " + withCount) + "\n allCount = ") + allCount) + "\n  = ") + "\n"));
        } finally {
            purgeAllMessages();
        }
    }

    public void testDummy() throws Exception {
        try {
            String one = "ID:mac.fritz.box:1213242.3231.1:1:1:100";
            String two = "ID*:100";
            try {
                if (one.matches(two))
                    PurgeCommandTest.LOG.info("String matches.");
                else
                    PurgeCommandTest.LOG.info("string does not match.");

            } catch (Exception ex) {
                PurgeCommandTest.LOG.error(ex.getMessage());
            }
            PurgeCommand purgeCommand = new PurgeCommand();
            CommandContext context = new CommandContext();
            context.setFormatter(new CommandShellOutputFormatter(System.out));
            purgeCommand.setCommandContext(context);
            purgeCommand.setJmxUseLocal(true);
            List<String> tokens = new ArrayList<String>();
            tokens.add("--msgsel");
            tokens.add("(XTestProperty LIKE '1:*') AND (JMSPriority>3)");
            addMessages();
            purgeCommand.execute(tokens);
            /* QueueBrowser withPropertyBrowser = requestServerSession.createBrowser(
            theQueue, MSG_SEL_COMPLEX_SQL_AND);
            QueueBrowser allBrowser = requestServerSession.createBrowser(theQueue);

            int withCount = getMessageCount(withPropertyBrowser, "withProperty ");
            int allCount = getMessageCount(allBrowser, "allMessages ");

            withPropertyBrowser.close();
            allBrowser.close();

            assertEquals("Expected withCount to be " + "0" + " was "
            + withCount, 0, withCount);
            assertEquals("Expected allCount to be " + MESSAGE_COUNT + " was "
            + allCount, MESSAGE_COUNT, allCount);
            LOG.info("withCount = " + withCount + "\n allCount = " +
            allCount + "\n  = " + "\n");
             */
        } finally {
            purgeAllMessages();
        }
    }
}

