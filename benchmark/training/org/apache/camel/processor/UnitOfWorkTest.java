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


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.TestSupport;
import org.apache.camel.spi.Synchronization;
import org.junit.Assert;
import org.junit.Test;


public class UnitOfWorkTest extends ContextTestSupport {
    protected Synchronization synchronization;

    protected Exchange completed;

    protected Exchange failed;

    protected String uri = "direct:foo";

    protected CountDownLatch doneLatch = new CountDownLatch(1);

    protected Object foo;

    protected Object baz;

    @Test
    public void testSuccess() throws Exception {
        sendMessage();
        Assert.assertTrue("Exchange did not complete.", doneLatch.await(5, TimeUnit.SECONDS));
        Assert.assertNull("Should not have failed", failed);
        Assert.assertNotNull("Should have received completed notification", completed);
        Assert.assertEquals("Should have propagated the header inside the Synchronization.onComplete() callback", "bar", foo);
        Assert.assertNull("The Synchronization.onFailure() callback should have not been invoked", baz);
        log.info(("Received completed: " + (completed)));
    }

    @Test
    public void testFail() throws Exception {
        sendMessage();
        Assert.assertTrue("Exchange did not complete.", doneLatch.await(5, TimeUnit.SECONDS));
        Assert.assertNull("Should not have completed", completed);
        Assert.assertNotNull("Should have received failed notification", failed);
        Assert.assertEquals("Should have propagated the header inside the Synchronization.onFailure() callback", "bat", baz);
        Assert.assertNull("The Synchronization.onComplete() callback should have not been invoked", foo);
        log.info(("Received fail: " + (failed)));
    }

    @Test
    public void testException() throws Exception {
        sendMessage();
        Assert.assertTrue("Exchange did not complete.", doneLatch.await(5, TimeUnit.SECONDS));
        Assert.assertNull("Should not have completed", completed);
        Assert.assertNotNull("Should have received failed notification", failed);
        Assert.assertEquals("Should have propagated the header inside the Synchronization.onFailure() callback", "bat", baz);
        Assert.assertNull("The Synchronization.onComplete() callback should have not been invoked", foo);
        log.info(("Received fail: " + (failed)));
    }
}

