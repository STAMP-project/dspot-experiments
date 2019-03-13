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


import org.apache.hadoop.io.erasurecode.ECSchema;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test ErasureCodingPolicy.
 */
public class TestErasureCodingPolicy {
    private static final ECSchema SCHEMA_1 = new ECSchema("one", 1, 2, null);

    private static final ECSchema SCHEMA_2 = new ECSchema("two", 1, 2, null);

    @Test
    public void testInvalid() {
        try {
            new ErasureCodingPolicy(null, TestErasureCodingPolicy.SCHEMA_1, 123, ((byte) (-1)));
            Assert.fail("Instantiated invalid ErasureCodingPolicy");
        } catch (NullPointerException e) {
        }
        try {
            new ErasureCodingPolicy("policy", null, 123, ((byte) (-1)));
            Assert.fail("Instantiated invalid ErasureCodingPolicy");
        } catch (NullPointerException e) {
        }
        try {
            new ErasureCodingPolicy("policy", TestErasureCodingPolicy.SCHEMA_1, (-1), ((byte) (-1)));
            Assert.fail("Instantiated invalid ErasureCodingPolicy");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("cellSize", e);
        }
        try {
            new ErasureCodingPolicy(null, 1024, ((byte) (-1)));
            Assert.fail("Instantiated invalid ErasureCodingPolicy");
        } catch (NullPointerException e) {
        }
        try {
            new ErasureCodingPolicy(TestErasureCodingPolicy.SCHEMA_1, (-1), ((byte) (-1)));
            Assert.fail("Instantiated invalid ErasureCodingPolicy");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("cellSize", e);
        }
        try {
            new ErasureCodingPolicy(null, 1024);
            Assert.fail("Instantiated invalid ErasureCodingPolicy");
        } catch (NullPointerException e) {
        }
        try {
            new ErasureCodingPolicy(TestErasureCodingPolicy.SCHEMA_1, (-1));
            Assert.fail("Instantiated invalid ErasureCodingPolicy");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("cellSize", e);
        }
    }

    @Test
    public void testEqualsAndHashCode() {
        ErasureCodingPolicy[] policies = new ErasureCodingPolicy[]{ new ErasureCodingPolicy("one", TestErasureCodingPolicy.SCHEMA_1, 1024, ((byte) (1))), new ErasureCodingPolicy("two", TestErasureCodingPolicy.SCHEMA_1, 1024, ((byte) (1))), new ErasureCodingPolicy("one", TestErasureCodingPolicy.SCHEMA_2, 1024, ((byte) (1))), new ErasureCodingPolicy("one", TestErasureCodingPolicy.SCHEMA_1, 2048, ((byte) (1))), new ErasureCodingPolicy("one", TestErasureCodingPolicy.SCHEMA_1, 1024, ((byte) (3))) };
        for (int i = 0; i < (policies.length); i++) {
            final ErasureCodingPolicy ei = policies[i];
            // Check identity
            ErasureCodingPolicy temp = new ErasureCodingPolicy(ei.getName(), ei.getSchema(), ei.getCellSize(), ei.getId());
            Assert.assertEquals(ei, temp);
            Assert.assertEquals(ei.hashCode(), temp.hashCode());
            // Check against other policies
            for (int j = 0; j < (policies.length); j++) {
                final ErasureCodingPolicy ej = policies[j];
                if (i == j) {
                    Assert.assertEquals(ei, ej);
                    Assert.assertEquals(ei.hashCode(), ej.hashCode());
                } else {
                    Assert.assertNotEquals(ei, ej);
                    Assert.assertNotEquals(ei, ej.hashCode());
                }
            }
        }
    }
}

