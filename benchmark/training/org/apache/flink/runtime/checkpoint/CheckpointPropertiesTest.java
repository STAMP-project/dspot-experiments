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
package org.apache.flink.runtime.checkpoint;


import CheckpointRetentionPolicy.RETAIN_ON_CANCELLATION;
import CheckpointRetentionPolicy.RETAIN_ON_FAILURE;
import org.apache.flink.util.InstantiationUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the default checkpoint properties.
 */
public class CheckpointPropertiesTest {
    /**
     * Tests the external checkpoints properties.
     */
    @Test
    public void testCheckpointProperties() {
        CheckpointProperties props = CheckpointProperties.forCheckpoint(RETAIN_ON_FAILURE);
        Assert.assertFalse(props.forceCheckpoint());
        Assert.assertTrue(props.discardOnSubsumed());
        Assert.assertTrue(props.discardOnJobFinished());
        Assert.assertTrue(props.discardOnJobCancelled());
        Assert.assertFalse(props.discardOnJobFailed());
        Assert.assertTrue(props.discardOnJobSuspended());
        props = CheckpointProperties.forCheckpoint(RETAIN_ON_CANCELLATION);
        Assert.assertFalse(props.forceCheckpoint());
        Assert.assertTrue(props.discardOnSubsumed());
        Assert.assertTrue(props.discardOnJobFinished());
        Assert.assertFalse(props.discardOnJobCancelled());
        Assert.assertFalse(props.discardOnJobFailed());
        Assert.assertFalse(props.discardOnJobSuspended());
    }

    /**
     * Tests the default (manually triggered) savepoint properties.
     */
    @Test
    public void testSavepointProperties() {
        CheckpointProperties props = CheckpointProperties.forSavepoint();
        Assert.assertTrue(props.forceCheckpoint());
        Assert.assertFalse(props.discardOnSubsumed());
        Assert.assertFalse(props.discardOnJobFinished());
        Assert.assertFalse(props.discardOnJobCancelled());
        Assert.assertFalse(props.discardOnJobFailed());
        Assert.assertFalse(props.discardOnJobSuspended());
    }

    /**
     * Tests the isSavepoint utility works as expected.
     */
    @Test
    public void testIsSavepoint() throws Exception {
        {
            CheckpointProperties props = CheckpointProperties.forCheckpoint(RETAIN_ON_FAILURE);
            Assert.assertFalse(props.isSavepoint());
        }
        {
            CheckpointProperties props = CheckpointProperties.forCheckpoint(RETAIN_ON_CANCELLATION);
            Assert.assertFalse(props.isSavepoint());
        }
        {
            CheckpointProperties props = CheckpointProperties.forSavepoint();
            Assert.assertTrue(props.isSavepoint());
            CheckpointProperties deserializedCheckpointProperties = InstantiationUtil.deserializeObject(InstantiationUtil.serializeObject(props), getClass().getClassLoader());
            Assert.assertTrue(deserializedCheckpointProperties.isSavepoint());
        }
    }
}

