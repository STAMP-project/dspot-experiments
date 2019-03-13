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
package org.apache.camel.processor.resequencer;


import org.junit.Assert;
import org.junit.Test;


public class SequenceTest extends Assert {
    private TestObject e1;

    private TestObject e2;

    private TestObject e3;

    private Sequence<TestObject> set;

    @Test
    public void testPredecessor() {
        Assert.assertEquals(e1, set.predecessor(e2));
        Assert.assertEquals(null, set.predecessor(e1));
        Assert.assertEquals(null, set.predecessor(e3));
    }

    @Test
    public void testSuccessor() {
        Assert.assertEquals(e2, set.successor(e1));
        Assert.assertEquals(null, set.successor(e2));
        Assert.assertEquals(null, set.successor(e3));
    }
}

