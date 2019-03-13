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
package org.apache.ambari.server.controller.predicate;


import Resource.Type;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.junit.Test;


/**
 *
 */
public class OrPredicateTest {
    @Test
    public void testApply() {
        Resource resource = new org.apache.ambari.server.controller.internal.ResourceImpl(Type.HostComponent);
        String propertyId1 = PropertyHelper.getPropertyId("category1", "property1");
        String propertyId2 = PropertyHelper.getPropertyId("category1", "property2");
        String propertyId3 = PropertyHelper.getPropertyId("category1", "property3");
        EqualsPredicate predicate1 = new EqualsPredicate(propertyId1, "v1");
        EqualsPredicate predicate2 = new EqualsPredicate(propertyId2, "v2");
        EqualsPredicate predicate3 = new EqualsPredicate(propertyId3, "v3");
        OrPredicate orPredicate = new OrPredicate(predicate1, predicate2, predicate3);
        resource.setProperty(propertyId1, "big");
        resource.setProperty(propertyId2, "monkey");
        resource.setProperty(propertyId3, "runner");
        Assert.assertFalse(orPredicate.evaluate(resource));
        resource.setProperty(propertyId2, "v2");
        Assert.assertTrue(orPredicate.evaluate(resource));
    }

    @Test
    public void testGetProperties() {
        String propertyId1 = PropertyHelper.getPropertyId("category1", "property1");
        String propertyId2 = PropertyHelper.getPropertyId("category1", "property2");
        String propertyId3 = PropertyHelper.getPropertyId("category1", "property3");
        EqualsPredicate predicate1 = new EqualsPredicate(propertyId1, "v1");
        EqualsPredicate predicate2 = new EqualsPredicate(propertyId2, "v2");
        EqualsPredicate predicate3 = new EqualsPredicate(propertyId3, "v3");
        OrPredicate orPredicate = new OrPredicate(predicate1, predicate2, predicate3);
        Set<String> ids = orPredicate.getPropertyIds();
        Assert.assertEquals(3, ids.size());
        Assert.assertTrue(ids.contains(propertyId1));
        Assert.assertTrue(ids.contains(propertyId2));
        Assert.assertTrue(ids.contains(propertyId3));
    }
}

