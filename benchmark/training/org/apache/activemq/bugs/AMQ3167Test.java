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
package org.apache.activemq.bugs;


import ActiveMQDestination.QUEUE_TYPE;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the loss of messages detected during testing with ActiveMQ 5.4.1 and 5.4.2.
 * <p/>
 * Symptoms: - 1 record is lost "early" in the stream. - no more records lost.
 * <p/>
 * Test Configuration: - Broker Settings: - Destination Policy - Occurs with "Destination Policy" using Store Cursor and
 * a memory limit - Not reproduced without "Destination Policy" defined - Persistence Adapter - Memory: Does not occur.
 * - KahaDB: Occurs. - Messages - Occurs with TextMessage and BinaryMessage - Persistent messages.
 * <p/>
 * Notes: - Lower memory limits increase the rate of occurrence. - Higher memory limits may prevent the problem
 * (probably because memory limits not reached). - Producers sending a number of messages before consumers come online
 * increases rate of occurrence.
 */
public class AMQ3167Test {
    protected BrokerService embeddedBroker;

    protected static final int MEMORY_LIMIT = 16 * 1024;

    protected static boolean Debug_f = false;

    protected long Producer_stop_time = 0;

    protected long Consumer_stop_time = 0;

    protected long Consumer_startup_delay_ms = 2000;

    protected boolean Stop_after_error = true;

    protected Connection JMS_conn;

    protected long Num_error = 0;

    // // ////
    // // TEST ////
    // // ////
    @Test
    public void testQueueLostMessage() throws Exception {
        Destination dest;
        dest = ActiveMQDestination.createDestination("lostmsgtest.queue", QUEUE_TYPE);
        // 10 seconds from now
        Producer_stop_time = (System.nanoTime()) + (10L * 1000000000L);
        // 15 seconds from now
        Consumer_stop_time = (Producer_stop_time) + (5L * 1000000000L);
        runLostMsgTest(dest, 1000000, 1, 1, false);
        // Make sure failures in the threads are thoroughly reported in the JUnit framework.
        Assert.assertTrue(((Num_error) == 0));
    }

    // // ////
    // // INTERNAL CLASSES ////
    // // ////
    /**
     * Producer thread - runs a single producer until the maximum number of messages is sent, the producer stop time is
     * reached, or a test error is detected.
     */
    protected class producerThread extends Thread {
        protected Session msgSess;

        protected MessageProducer msgProd;

        protected String producerTag;

        protected int numMsg;

        protected int numPerSess;

        protected long producer_stop_time;

        producerThread(Session sess, MessageProducer prod, String tag, int num_msg, int sess_size) {
            super();
            producer_stop_time = 0;
            msgSess = sess;
            msgProd = prod;
            producerTag = tag;
            numMsg = num_msg;
            numPerSess = sess_size;
        }

        public void execTest() throws Exception {
            Message msg;
            int sess_start;
            int cur;
            sess_start = 0;
            cur = 0;
            while (((cur < (numMsg)) && (!(didTimeOut()))) && ((!(Stop_after_error)) || ((Num_error) == 0))) {
                msg = msgSess.createTextMessage(("test message from " + (producerTag)));
                msg.setStringProperty("testprodtag", producerTag);
                msg.setIntProperty("seq", cur);
                if (msg instanceof ActiveMQMessage) {
                    setResponseRequired(true);
                }
                // 
                // Send the message.
                // 
                msgProd.send(msg);
                cur++;
                // 
                // Commit if the number of messages per session has been reached, and
                // transactions are being used (only when > 1 msg per sess).
                // 
                if (((numPerSess) > 1) && ((cur - sess_start) >= (numPerSess))) {
                    msgSess.commit();
                    sess_start = cur;
                }
            } 
            // Make sure to send the final commit, if there were sends since the last commit.
            if (((numPerSess) > 1) && ((cur - sess_start) > 0))
                msgSess.commit();

            if (cur < (numMsg))
                AMQ3167Test.log((((((("* Producer " + (producerTag)) + " timed out at ") + (System.nanoTime())) + " (stop time ") + (producer_stop_time)) + ")"));

        }

        /**
         * Check whether it is time for the producer to terminate.
         */
        protected boolean didTimeOut() {
            if (((Producer_stop_time) > 0) && ((System.nanoTime()) >= (Producer_stop_time)))
                return true;

            return false;
        }

        /**
         * Run the producer.
         */
        @Override
        public void run() {
            try {
                AMQ3167Test.log(("- running producer " + (producerTag)));
                execTest();
                AMQ3167Test.log(("- finished running producer " + (producerTag)));
            } catch (Throwable thrown) {
                (Num_error)++;
                Assert.fail(((("producer " + (producerTag)) + " failed: ") + (thrown.getMessage())));
                throw new Error((("producer " + (producerTag)) + " failed"), thrown);
            }
        }

        @Override
        public String toString() {
            return producerTag;
        }
    }

    /**
     * Producer thread - runs a single consumer until the maximum number of messages is received, the consumer stop time
     * is reached, or a test error is detected.
     */
    protected class consumerThread extends Thread {
        protected Session msgSess;

        protected MessageConsumer msgCons;

        protected String consumerTag;

        protected int numMsg;

        protected int numPerSess;

        consumerThread(Session sess, MessageConsumer cons, String tag, int num_msg, int sess_size) {
            super();
            msgSess = sess;
            msgCons = cons;
            consumerTag = tag;
            numMsg = num_msg;
            numPerSess = sess_size;
        }

        public void execTest() throws Exception {
            Message msg;
            int sess_start;
            int cur;
            msg = null;
            sess_start = 0;
            cur = 0;
            while (((cur < (numMsg)) && (!(didTimeOut()))) && ((!(Stop_after_error)) || ((Num_error) == 0))) {
                // 
                // Use a timeout of 1 second to periodically check the consumer timeout.
                // 
                msg = msgCons.receive(1000);
                if (msg != null) {
                    checkMessage(msg, cur);
                    cur++;
                    if (((numPerSess) > 1) && ((cur - sess_start) >= (numPerSess))) {
                        msg.acknowledge();
                        sess_start = cur;
                    }
                }
            } 
            // Acknowledge the last messages, if they were not yet acknowledged.
            if (((numPerSess) > 1) && ((cur - sess_start) > 0))
                msg.acknowledge();

            if (cur < (numMsg))
                AMQ3167Test.log((("* Consumer " + (consumerTag)) + " timed out"));

        }

        /**
         * Check whether it is time for the consumer to terminate.
         */
        protected boolean didTimeOut() {
            if (((Consumer_stop_time) > 0) && ((System.nanoTime()) >= (Consumer_stop_time)))
                return true;

            return false;
        }

        /**
         * Verify the message received. Sequence numbers are checked and are expected to exactly match the message
         * number (starting at 0).
         */
        protected void checkMessage(Message msg, int exp_seq) throws JMSException {
            int seq;
            seq = msg.getIntProperty("seq");
            if (exp_seq != seq) {
                (Num_error)++;
                Assert.fail(((((("*** Consumer " + (consumerTag)) + " expected seq ") + exp_seq) + "; received ") + seq));
            }
        }

        /**
         * Run the consumer.
         */
        @Override
        public void run() {
            try {
                AMQ3167Test.log(("- running consumer " + (consumerTag)));
                execTest();
                AMQ3167Test.log(("- running consumer " + (consumerTag)));
            } catch (Throwable thrown) {
                (Num_error)++;
                Assert.fail(((("consumer " + (consumerTag)) + " failed: ") + (thrown.getMessage())));
                throw new Error((("consumer " + (consumerTag)) + " failed"), thrown);
            }
        }

        @Override
        public String toString() {
            return consumerTag;
        }
    }
}

