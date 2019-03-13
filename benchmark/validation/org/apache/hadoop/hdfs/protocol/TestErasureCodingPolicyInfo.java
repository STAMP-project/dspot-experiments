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
package org.apache.hadoop.hdfs.protocol;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test {@link ErasureCodingPolicyInfo}.
 */
public class TestErasureCodingPolicyInfo {
    @Test
    public void testPolicyAndStateCantBeNull() {
        try {
            new ErasureCodingPolicyInfo(null);
            Assert.fail("Null policy should fail");
        } catch (NullPointerException expected) {
        }
        try {
            new ErasureCodingPolicyInfo(SystemErasureCodingPolicies.getByID(SystemErasureCodingPolicies.RS_6_3_POLICY_ID), null);
            Assert.fail("Null policy should fail");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testStates() {
        ErasureCodingPolicyInfo info = new ErasureCodingPolicyInfo(SystemErasureCodingPolicies.getByID(SystemErasureCodingPolicies.RS_6_3_POLICY_ID));
        info.setState(ErasureCodingPolicyState.ENABLED);
        Assert.assertFalse(info.isDisabled());
        Assert.assertTrue(info.isEnabled());
        Assert.assertFalse(info.isRemoved());
        info.setState(ErasureCodingPolicyState.REMOVED);
        Assert.assertFalse(info.isDisabled());
        Assert.assertFalse(info.isEnabled());
        Assert.assertTrue(info.isRemoved());
        info.setState(ErasureCodingPolicyState.DISABLED);
        Assert.assertTrue(info.isDisabled());
        Assert.assertFalse(info.isEnabled());
        Assert.assertFalse(info.isRemoved());
    }
}

