/**
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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.janusgraph.graphdb.predicate;


import Cmp.EQUAL;
import Text.PREFIX;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author David Clement (david.clement90@laposte.net)
 */
public class OrJanusPredicateTest extends ConnectiveJanusPredicateTest {
    @Test
    public void testIsQNF() {
        Assert.assertTrue(getPredicate(Arrays.asList(PREFIX, EQUAL)).isQNF());
        Assert.assertTrue(getPredicate(Arrays.asList(PREFIX, EQUAL, new OrJanusPredicate(Arrays.asList(PREFIX, EQUAL)))).isQNF());
        Assert.assertFalse(getPredicate(Arrays.asList(PREFIX, EQUAL, new AndJanusPredicate(Arrays.asList(PREFIX, EQUAL)))).isQNF());
    }
}

