/**
 * *****************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package org.apache.storm.eventhubs.spout;


import org.junit.Assert;
import org.junit.Test;


public class TestPartitionManager {
    @Test
    public void testPartitionManagerNoFail() {
        PartitionManagerCallerMock mock = new PartitionManagerCallerMock("1");
        String result = mock.execute("r,r,r,a0,a1,a2,r");
        Assert.assertEquals("0,1,2,3", result);
    }

    @Test
    public void testPartitionManagerResend() {
        PartitionManagerCallerMock mock = new PartitionManagerCallerMock("1");
        String result = mock.execute("r,a0,r,r,r,f3,r,f2,f1,r,r,a1,a2,a3,r");
        Assert.assertEquals("0,1,2,3,3,1,2,4", result);
    }

    @Test
    public void testPMCheckpointWithPending() {
        PartitionManagerCallerMock mock = new PartitionManagerCallerMock("1");
        mock.execute("r,r,r");
        // no ack, so return the first of pending list
        Assert.assertEquals("0", mock.checkpoint());
        mock.execute("a0,a2");
        // still need to return the first of pending list
        Assert.assertEquals("1", mock.checkpoint());
    }

    @Test
    public void testPMCheckpointWithResend() {
        PartitionManagerCallerMock mock = new PartitionManagerCallerMock("1");
        mock.execute("r,r,r,f2,f1,f0");
        // pending is empty, return the smallest in toResend
        Assert.assertEquals("0", mock.checkpoint());
        mock.execute("r,a0");
        // pending is still empty
        Assert.assertEquals("1", mock.checkpoint());
    }

    @Test
    public void testPMCheckpointWithPendingAndResend() {
        PartitionManagerCallerMock mock = new PartitionManagerCallerMock("1");
        mock.execute("r,r,r,f2,f1");
        // return the smaller of pending and toResend
        Assert.assertEquals("0", mock.checkpoint());
        mock.execute("a0,r");
        // now pending: [3], toResend: [1,2]
        Assert.assertEquals("1", mock.checkpoint());
    }

    @Test
    public void testPMCheckpointWithNoPendingAndNoResend() {
        PartitionManagerCallerMock mock = new PartitionManagerCallerMock("1");
        // if no event sent, no checkpoint shall be created
        Assert.assertEquals(null, mock.checkpoint());
        mock.execute("r,r,r,f2,f1,r,r,a2,a1,a0");
        // all events are sent successfully, return last sent offset
        Assert.assertEquals("2", mock.checkpoint());
    }

    @Test
    public void testPartitionManagerMaxPendingMessages() {
        PartitionManagerCallerMock mock = new PartitionManagerCallerMock("1");
        String result = mock.execute("r1024");
        // any receive call after exceeding max pending messages results in null
        result = mock.execute("r2");
        Assert.assertEquals("null,null", result);
        result = mock.execute("a0,a1,r2");
        Assert.assertEquals("1024,1025", result);
    }

    @Test
    public void testPartitionManagerEnqueueTimeFilter() {
        PartitionManagerCallerMock mock = new PartitionManagerCallerMock("1", 123456);
        String result = mock.execute("r2");
        Assert.assertEquals("123457,123458", result);
    }
}

