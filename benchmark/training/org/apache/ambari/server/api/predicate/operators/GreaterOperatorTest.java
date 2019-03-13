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
package org.apache.ambari.server.api.predicate.operators;


import Operator.TYPE.GREATER;
import org.junit.Assert;
import org.junit.Test;


/**
 * GREATER operator test.
 */
public class GreaterOperatorTest {
    @Test
    public void testGetName() {
        Assert.assertEquals("GreaterOperator", new GreaterOperator().getName());
    }

    @Test
    public void testToPredicate() {
        Assert.assertEquals(new org.apache.ambari.server.controller.predicate.GreaterPredicate("1", "2"), new GreaterOperator().toPredicate("1", "2"));
    }

    @Test
    public void testGetType() {
        Assert.assertSame(GREATER, new GreaterOperator().getType());
    }

    @Test
    public void testGetBasePrecedence() {
        Assert.assertEquals((-1), new GreaterOperator().getBasePrecedence());
    }

    @Test
    public void testGetPrecedence() {
        Assert.assertEquals((-1), new GreaterOperator().getPrecedence());
    }
}

