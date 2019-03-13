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


import Operator.TYPE.NOT_EQUAL;
import org.junit.Assert;
import org.junit.Test;


/**
 * NOT EQUALS operator test.
 */
public class NotEqualsOperatorTest {
    @Test
    public void testGetName() {
        Assert.assertEquals("NotEqualsOperator", new NotEqualsOperator().getName());
    }

    @Test
    public void testToPredicate() {
        Assert.assertEquals(new org.apache.ambari.server.controller.predicate.NotPredicate(new org.apache.ambari.server.controller.predicate.EqualsPredicate("prop", "val")), new NotEqualsOperator().toPredicate("prop", "val"));
    }

    @Test
    public void testGetType() {
        Assert.assertSame(NOT_EQUAL, new NotEqualsOperator().getType());
    }

    @Test
    public void testGetBasePrecedence() {
        Assert.assertEquals((-1), new NotEqualsOperator().getBasePrecedence());
    }

    @Test
    public void testGetPrecedence() {
        Assert.assertEquals((-1), new NotEqualsOperator().getPrecedence());
    }
}

