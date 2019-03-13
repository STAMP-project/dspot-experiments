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
package org.apache.camel.processor;


import Exchange.FILE_NAME_PRODUCED;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.TestSupport;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.util.FileUtil;
import org.junit.Assert;
import org.junit.Test;


public class FileRollbackOnCompletionTest extends ContextTestSupport {
    private static final CountDownLatch LATCH = new CountDownLatch(1);

    public static final class FileRollback implements Synchronization {
        public void onComplete(Exchange exchange) {
            // this method is invoked when the Exchange completed with no failure
        }

        public void onFailure(Exchange exchange) {
            // delete the file
            String name = exchange.getIn().getHeader(FILE_NAME_PRODUCED, String.class);
            FileUtil.deleteFile(new File(name));
            // signal we have deleted the file
            FileRollbackOnCompletionTest.LATCH.countDown();
        }
    }

    public static final class OrderService {
        public String createMail(String order) throws Exception {
            return "Order confirmed: " + order;
        }

        public void sendMail(String body, @Header("to")
        String to) {
            // simulate fatal error if we refer to a special no
            if (to.equals("FATAL")) {
                throw new IllegalArgumentException("Simulated fatal error");
            }
        }
    }

    @Test
    public void testOk() throws Exception {
        template.sendBodyAndHeader("direct:confirm", "bumper", "to", "someone@somewhere.org");
        File file = new File("target/data/mail/backup/");
        String[] files = file.list();
        Assert.assertEquals("There should be one file", 1, files.length);
    }

    @Test
    public void testRollback() throws Exception {
        try {
            template.sendBodyAndHeader("direct:confirm", "bumper", "to", "FATAL");
            Assert.fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            TestSupport.assertIsInstanceOf(IllegalArgumentException.class, e.getCause());
            Assert.assertEquals("Simulated fatal error", e.getCause().getMessage());
        }
        oneExchangeDone.matchesMockWaitTime();
        // onCompletion is async so we gotta wait a bit for the file to be deleted
        Assert.assertTrue("Should countdown the latch", FileRollbackOnCompletionTest.LATCH.await(5, TimeUnit.SECONDS));
        File file = new File("target/data/mail/backup/");
        String[] files = file.list();
        Assert.assertEquals("There should be no files", 0, files.length);
    }
}

