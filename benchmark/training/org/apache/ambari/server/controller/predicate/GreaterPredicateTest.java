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
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.junit.Test;


/**
 *
 */
public class GreaterPredicateTest {
    @Test
    public void testApply() {
        Resource resource = new org.apache.ambari.server.controller.internal.ResourceImpl(Type.HostComponent);
        String propertyId = PropertyHelper.getPropertyId("category1", "foo");
        Predicate predicate = new GreaterPredicate(propertyId, 10);
        resource.setProperty(propertyId, 1);
        Assert.assertFalse(predicate.evaluate(resource));
        resource.setProperty(propertyId, 100);
        Assert.assertTrue(predicate.evaluate(resource));
        resource.setProperty(propertyId, 10);
        Assert.assertFalse(predicate.evaluate(resource));
    }

    @Test
    public void testNullValue() {
        try {
            new GreaterPredicate<Integer>("category/foo", null);
            Assert.fail("Expected IllegalArgumentException for null value.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testGetProperties() {
        String propertyId = PropertyHelper.getPropertyId("category1", "foo");
        GreaterPredicate predicate = new GreaterPredicate(propertyId, 10);
        Set<String> ids = predicate.getPropertyIds();
        Assert.assertEquals(1, ids.size());
        Assert.assertTrue(ids.contains(propertyId));
    }

    @Test
    public void testApplyWithSmallFloats() {
        Resource resource = new org.apache.ambari.server.controller.internal.ResourceImpl(Type.HostComponent);
        String propertyId = PropertyHelper.getPropertyId("category1", "foo");
        Predicate predicate = new GreaterPredicate(propertyId, 0.1);
        resource.setProperty(propertyId, 1.3);
        Assert.assertTrue(predicate.evaluate(resource));
        resource.setProperty(propertyId, 0.06);
        Assert.assertFalse(predicate.evaluate(resource));
        resource.setProperty(propertyId, 100.3);
        Assert.assertTrue(predicate.evaluate(resource));
    }
}

