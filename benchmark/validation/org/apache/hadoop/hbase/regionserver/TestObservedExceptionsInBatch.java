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
package org.apache.hadoop.hbase.regionserver;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.regionserver.HRegion.ObservedExceptionsInBatch;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test class for {@link ObservedExceptionsInBatch}.
 */
@Category(SmallTests.class)
public class TestObservedExceptionsInBatch {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestObservedExceptionsInBatch.class);

    private ObservedExceptionsInBatch observedExceptions;

    @Test
    public void testNoObservationsOnCreation() {
        Assert.assertFalse(observedExceptions.hasSeenFailedSanityCheck());
        Assert.assertFalse(observedExceptions.hasSeenNoSuchFamily());
        Assert.assertFalse(observedExceptions.hasSeenWrongRegion());
    }

    @Test
    public void testObservedAfterRecording() {
        observedExceptions.sawFailedSanityCheck();
        Assert.assertTrue(observedExceptions.hasSeenFailedSanityCheck());
        observedExceptions.sawNoSuchFamily();
        Assert.assertTrue(observedExceptions.hasSeenNoSuchFamily());
        observedExceptions.sawWrongRegion();
        Assert.assertTrue(observedExceptions.hasSeenWrongRegion());
    }
}

