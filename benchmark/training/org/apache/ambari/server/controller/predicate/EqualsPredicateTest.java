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
 * Equals predicate tests.
 */
public class EqualsPredicateTest {
    @Test
    public void testApply() {
        Resource resource = new org.apache.ambari.server.controller.internal.ResourceImpl(Type.HostComponent);
        String propertyId = PropertyHelper.getPropertyId("category1", "foo");
        Predicate predicate = new EqualsPredicate(propertyId, "bar");
        resource.setProperty(propertyId, "monkey");
        Assert.assertFalse(predicate.evaluate(resource));
        resource.setProperty(propertyId, "bar");
        Assert.assertTrue(predicate.evaluate(resource));
        propertyId = PropertyHelper.getPropertyId("category1", "fun");
        predicate = new EqualsPredicate(propertyId, "bar");
        Assert.assertFalse(predicate.evaluate(resource));
    }

    @Test
    public void testApplyNullValue() {
        Resource resource = new org.apache.ambari.server.controller.internal.ResourceImpl(Type.HostComponent);
        String propertyId = PropertyHelper.getPropertyId("category1", "foo");
        Predicate predicate = new EqualsPredicate<String>(propertyId, null);
        resource.setProperty(propertyId, "monkey");
        Assert.assertFalse(predicate.evaluate(resource));
        resource.setProperty(propertyId, null);
        Assert.assertTrue(predicate.evaluate(resource));
    }

    @Test
    public void testGetProperties() {
        String propertyId = PropertyHelper.getPropertyId("category1", "foo");
        EqualsPredicate predicate = new EqualsPredicate(propertyId, "bar");
        Set<String> ids = predicate.getPropertyIds();
        Assert.assertEquals(1, ids.size());
        Assert.assertTrue(ids.contains(propertyId));
    }

    @Test
    public void testApplyNumberValues() throws Exception {
        Resource resource = new org.apache.ambari.server.controller.internal.ResourceImpl(Type.HostComponent);
        String propertyId = PropertyHelper.getPropertyId("propertyId1", "1");
        Predicate predicate = new EqualsPredicate(propertyId, "1");
        resource.setProperty(propertyId, "1");
        Assert.assertTrue(predicate.evaluate(resource));
        resource.setProperty(propertyId, "2");
        Assert.assertFalse(predicate.evaluate(resource));
        resource.setProperty(propertyId, "5");
        Assert.assertFalse(predicate.evaluate(resource));
    }
}

