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
package org.apache.ambari.server.collections;


import junit.framework.Assert;
import org.junit.Test;


public class PredicateUtilsTest {
    @Test
    public void toMap() throws Exception {
        // Test null predicate safely returns null
        Assert.assertNull(PredicateUtils.toMap(null));
        Assert.assertEquals(createMap(), PredicateUtils.toMap(createPredicate()));
    }

    @Test
    public void fromMap() throws Exception {
        verifyPredicate(PredicateUtils.fromMap(createMap()));
    }

    @Test
    public void toJSON() throws Exception {
        // Test null predicate safely returns null
        Assert.assertNull(PredicateUtils.toJSON(null));
        Assert.assertEquals(createJSON(), PredicateUtils.toJSON(createPredicate()));
    }

    @Test
    public void fromJSON() throws Exception {
        verifyPredicate(PredicateUtils.fromJSON(createJSON()));
    }
}

