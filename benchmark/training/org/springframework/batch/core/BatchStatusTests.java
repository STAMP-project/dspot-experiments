/**
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core;


import BatchStatus.ABANDONED;
import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import BatchStatus.STARTED;
import BatchStatus.STARTING;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.batch.runtime.BatchStatus.STOPPED;
import javax.batch.runtime.BatchStatus.STOPPING;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dave Syer
 */
public class BatchStatusTests {
    /**
     * Test method for
     * {@link org.springframework.batch.core.BatchStatus#toString()}.
     */
    @Test
    public void testToString() {
        Assert.assertEquals("ABANDONED", ABANDONED.toString());
    }

    @Test
    public void testMaxStatus() {
        Assert.assertEquals(FAILED, BatchStatus.max(FAILED, COMPLETED));
        Assert.assertEquals(FAILED, BatchStatus.max(COMPLETED, FAILED));
        Assert.assertEquals(FAILED, BatchStatus.max(FAILED, FAILED));
        Assert.assertEquals(STARTED, BatchStatus.max(STARTED, STARTING));
        Assert.assertEquals(STARTED, BatchStatus.max(COMPLETED, STARTED));
    }

    @Test
    public void testUpgradeStatusFinished() {
        Assert.assertEquals(FAILED, FAILED.upgradeTo(COMPLETED));
        Assert.assertEquals(FAILED, COMPLETED.upgradeTo(FAILED));
    }

    @Test
    public void testUpgradeStatusUnfinished() {
        Assert.assertEquals(COMPLETED, STARTING.upgradeTo(COMPLETED));
        Assert.assertEquals(COMPLETED, COMPLETED.upgradeTo(STARTING));
        Assert.assertEquals(STARTED, STARTING.upgradeTo(STARTED));
        Assert.assertEquals(STARTED, STARTED.upgradeTo(STARTING));
    }

    @Test
    public void testIsRunning() {
        Assert.assertFalse(FAILED.isRunning());
        Assert.assertFalse(COMPLETED.isRunning());
        Assert.assertTrue(STARTED.isRunning());
        Assert.assertTrue(STARTING.isRunning());
    }

    @Test
    public void testIsUnsuccessful() {
        Assert.assertTrue(FAILED.isUnsuccessful());
        Assert.assertFalse(COMPLETED.isUnsuccessful());
        Assert.assertFalse(STARTED.isUnsuccessful());
        Assert.assertFalse(STARTING.isUnsuccessful());
    }

    @Test
    public void testGetStatus() {
        Assert.assertEquals(FAILED, BatchStatus.valueOf(FAILED.toString()));
    }

    @Test
    public void testGetStatusWrongCode() {
        try {
            BatchStatus.valueOf("foo");
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test(expected = NullPointerException.class)
    public void testGetStatusNullCode() {
        Assert.assertNull(BatchStatus.valueOf(null));
    }

    @Test
    public void testSerialization() throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(COMPLETED);
        out.flush();
        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);
        BatchStatus status = ((BatchStatus) (in.readObject()));
        Assert.assertEquals(COMPLETED, status);
    }

    @Test
    public void testJsrConversion() {
        Assert.assertEquals(javax.batch.runtime.BatchStatus.ABANDONED, ABANDONED.getBatchStatus());
        Assert.assertEquals(javax.batch.runtime.BatchStatus.COMPLETED, COMPLETED.getBatchStatus());
        Assert.assertEquals(javax.batch.runtime.BatchStatus.STARTED, STARTED.getBatchStatus());
        Assert.assertEquals(javax.batch.runtime.BatchStatus.STARTING, STARTING.getBatchStatus());
        Assert.assertEquals(STOPPED, BatchStatus.STOPPED.getBatchStatus());
        Assert.assertEquals(STOPPING, BatchStatus.STOPPING.getBatchStatus());
        Assert.assertEquals(javax.batch.runtime.BatchStatus.FAILED, FAILED.getBatchStatus());
    }
}

