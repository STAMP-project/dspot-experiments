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
package org.apache.ambari.server.controller.internal;


import java.util.Map;
import junit.framework.Assert;
import org.apache.ambari.server.controller.predicate.CategoryIsEmptyPredicate;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PredicateHelper;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.junit.Test;


/**
 * Tests for the property predicate visitor.
 */
public class PropertyPredicateVisitorTest {
    private static final String PROPERTY_A = PropertyHelper.getPropertyId("category", "A");

    private static final String PROPERTY_B = PropertyHelper.getPropertyId("category", "B");

    private static final Predicate PREDICATE_1 = new PredicateBuilder().property(PropertyPredicateVisitorTest.PROPERTY_A).equals("Monkey").toPredicate();

    private static final Predicate PREDICATE_2 = new PredicateBuilder().property(PropertyPredicateVisitorTest.PROPERTY_B).equals("Runner").toPredicate();

    private static final Predicate PREDICATE_3 = new org.apache.ambari.server.controller.predicate.AndPredicate(PropertyPredicateVisitorTest.PREDICATE_1, PropertyPredicateVisitorTest.PREDICATE_2);

    private static final Predicate PREDICATE_4 = new org.apache.ambari.server.controller.predicate.OrPredicate(PropertyPredicateVisitorTest.PREDICATE_1, PropertyPredicateVisitorTest.PREDICATE_2);

    private static final Predicate PREDICATE_5 = new CategoryIsEmptyPredicate("cat1");

    @Test
    public void testVisit() {
        PropertyPredicateVisitor visitor = new PropertyPredicateVisitor();
        PredicateHelper.visit(PropertyPredicateVisitorTest.PREDICATE_1, visitor);
        Map<String, Object> properties = visitor.getProperties();
        Assert.assertEquals(1, properties.size());
        Assert.assertEquals("Monkey", properties.get(PropertyPredicateVisitorTest.PROPERTY_A));
        visitor = new PropertyPredicateVisitor();
        PredicateHelper.visit(PropertyPredicateVisitorTest.PREDICATE_3, visitor);
        properties = visitor.getProperties();
        Assert.assertEquals(2, properties.size());
        Assert.assertEquals("Monkey", properties.get(PropertyPredicateVisitorTest.PROPERTY_A));
        Assert.assertEquals("Runner", properties.get(PropertyPredicateVisitorTest.PROPERTY_B));
        visitor = new PropertyPredicateVisitor();
        PredicateHelper.visit(PropertyPredicateVisitorTest.PREDICATE_4, visitor);
        properties = visitor.getProperties();
        Assert.assertEquals(2, properties.size());
        Assert.assertEquals("Monkey", properties.get(PropertyPredicateVisitorTest.PROPERTY_A));
        Assert.assertEquals("Runner", properties.get(PropertyPredicateVisitorTest.PROPERTY_B));
        visitor = new PropertyPredicateVisitor();
        PredicateHelper.visit(PropertyPredicateVisitorTest.PREDICATE_5, visitor);
        properties = visitor.getProperties();
        Assert.assertTrue(properties.isEmpty());
    }
}

