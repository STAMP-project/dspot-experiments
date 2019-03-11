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


import java.util.Collections;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.apache.ambari.server.controller.predicate.CategoryIsEmptyPredicate;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PredicateHelper;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;


/**
 * Tests for SimplifyingPredicateVisitor
 */
public class SimplifyingPredicateVisitorTest {
    private static final String PROPERTY_A = PropertyHelper.getPropertyId("category", "A");

    private static final String PROPERTY_B = PropertyHelper.getPropertyId("category", "B");

    private static final String PROPERTY_C = PropertyHelper.getPropertyId("category", "C");

    private static final String PROPERTY_D = PropertyHelper.getPropertyId("category", "D");

    private static final Predicate PREDICATE_1 = new PredicateBuilder().property(SimplifyingPredicateVisitorTest.PROPERTY_A).equals("Monkey").toPredicate();

    private static final Predicate PREDICATE_2 = new PredicateBuilder().property(SimplifyingPredicateVisitorTest.PROPERTY_B).equals("Runner").toPredicate();

    private static final Predicate PREDICATE_3 = new org.apache.ambari.server.controller.predicate.AndPredicate(SimplifyingPredicateVisitorTest.PREDICATE_1, SimplifyingPredicateVisitorTest.PREDICATE_2);

    private static final Predicate PREDICATE_4 = new org.apache.ambari.server.controller.predicate.OrPredicate(SimplifyingPredicateVisitorTest.PREDICATE_1, SimplifyingPredicateVisitorTest.PREDICATE_2);

    private static final Predicate PREDICATE_5 = new PredicateBuilder().property(SimplifyingPredicateVisitorTest.PROPERTY_C).equals("Racer").toPredicate();

    private static final Predicate PREDICATE_6 = new org.apache.ambari.server.controller.predicate.OrPredicate(SimplifyingPredicateVisitorTest.PREDICATE_5, SimplifyingPredicateVisitorTest.PREDICATE_4);

    private static final Predicate PREDICATE_7 = new PredicateBuilder().property(SimplifyingPredicateVisitorTest.PROPERTY_C).equals("Power").toPredicate();

    private static final Predicate PREDICATE_8 = new org.apache.ambari.server.controller.predicate.OrPredicate(SimplifyingPredicateVisitorTest.PREDICATE_6, SimplifyingPredicateVisitorTest.PREDICATE_7);

    private static final Predicate PREDICATE_9 = new org.apache.ambari.server.controller.predicate.AndPredicate(SimplifyingPredicateVisitorTest.PREDICATE_1, SimplifyingPredicateVisitorTest.PREDICATE_8);

    private static final Predicate PREDICATE_10 = new org.apache.ambari.server.controller.predicate.OrPredicate(SimplifyingPredicateVisitorTest.PREDICATE_3, SimplifyingPredicateVisitorTest.PREDICATE_5);

    private static final Predicate PREDICATE_11 = new org.apache.ambari.server.controller.predicate.AndPredicate(SimplifyingPredicateVisitorTest.PREDICATE_4, SimplifyingPredicateVisitorTest.PREDICATE_10);

    private static final Predicate PREDICATE_12 = new PredicateBuilder().property(SimplifyingPredicateVisitorTest.PROPERTY_D).equals("Installer").toPredicate();

    private static final Predicate PREDICATE_13 = new org.apache.ambari.server.controller.predicate.AndPredicate(SimplifyingPredicateVisitorTest.PREDICATE_1, SimplifyingPredicateVisitorTest.PREDICATE_12);

    private static final Predicate PREDICATE_14 = new PredicateBuilder().property(SimplifyingPredicateVisitorTest.PROPERTY_D).greaterThan(12).toPredicate();

    private static final Predicate PREDICATE_15 = new org.apache.ambari.server.controller.predicate.AndPredicate(SimplifyingPredicateVisitorTest.PREDICATE_1, SimplifyingPredicateVisitorTest.PREDICATE_14);

    private static final Predicate PREDICATE_16 = new CategoryIsEmptyPredicate("cat1");

    @Test
    public void testVisit() {
        ResourceProvider provider = createStrictMock(ResourceProvider.class);
        Capture<Set<String>> propertiesCapture = EasyMock.newCapture();
        SimplifyingPredicateVisitor visitor = new SimplifyingPredicateVisitor(provider);
        // expectations
        expect(provider.checkPropertyIds(capture(propertiesCapture))).andReturn(Collections.emptySet()).anyTimes();
        replay(provider);
        PredicateHelper.visit(SimplifyingPredicateVisitorTest.PREDICATE_1, visitor);
        List<Predicate> simplifiedPredicates = visitor.getSimplifiedPredicates();
        Assert.assertEquals(1, simplifiedPredicates.size());
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_1, simplifiedPredicates.get(0));
        Set<String> setProps = propertiesCapture.getValue();
        Assert.assertEquals(1, setProps.size());
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PROPERTY_A, setProps.iterator().next());
        // ---
        PredicateHelper.visit(SimplifyingPredicateVisitorTest.PREDICATE_3, visitor);
        simplifiedPredicates = visitor.getSimplifiedPredicates();
        Assert.assertEquals(1, simplifiedPredicates.size());
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_3, simplifiedPredicates.get(0));
        // ---
        PredicateHelper.visit(SimplifyingPredicateVisitorTest.PREDICATE_4, visitor);
        simplifiedPredicates = visitor.getSimplifiedPredicates();
        Assert.assertEquals(2, simplifiedPredicates.size());
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_1, simplifiedPredicates.get(0));
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_2, simplifiedPredicates.get(1));
        // ---
        PredicateHelper.visit(SimplifyingPredicateVisitorTest.PREDICATE_6, visitor);
        simplifiedPredicates = visitor.getSimplifiedPredicates();
        Assert.assertEquals(3, simplifiedPredicates.size());
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_5, simplifiedPredicates.get(0));
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_1, simplifiedPredicates.get(1));
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_2, simplifiedPredicates.get(2));
        // ---
        PredicateHelper.visit(SimplifyingPredicateVisitorTest.PREDICATE_8, visitor);
        simplifiedPredicates = visitor.getSimplifiedPredicates();
        Assert.assertEquals(4, simplifiedPredicates.size());
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_5, simplifiedPredicates.get(0));
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_1, simplifiedPredicates.get(1));
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_2, simplifiedPredicates.get(2));
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_7, simplifiedPredicates.get(3));
        // ---
        PredicateHelper.visit(SimplifyingPredicateVisitorTest.PREDICATE_9, visitor);
        simplifiedPredicates = visitor.getSimplifiedPredicates();
        Assert.assertEquals(4, simplifiedPredicates.size());
        // Assert.assertEquals(???, simplifiedPredicates.get(0));
        // ---
        PredicateHelper.visit(SimplifyingPredicateVisitorTest.PREDICATE_11, visitor);
        simplifiedPredicates = visitor.getSimplifiedPredicates();
        Assert.assertEquals(4, simplifiedPredicates.size());
        // Assert.assertEquals(???, simplifiedPredicates.get(0));
        // ---
        PredicateHelper.visit(SimplifyingPredicateVisitorTest.PREDICATE_16, visitor);
        simplifiedPredicates = visitor.getSimplifiedPredicates();
        Assert.assertEquals(1, simplifiedPredicates.size());
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_16, simplifiedPredicates.get(0));
        // reset assertions.  For property D, indicate that it is not supported.
        verify(provider);
        reset(provider);
        expect(provider.checkPropertyIds(capture(propertiesCapture))).andReturn(Collections.emptySet());
        expect(provider.checkPropertyIds(capture(propertiesCapture))).andReturn(Collections.singleton(SimplifyingPredicateVisitorTest.PROPERTY_D));
        replay(provider);
        // ---
        PredicateHelper.visit(SimplifyingPredicateVisitorTest.PREDICATE_13, visitor);
        simplifiedPredicates = visitor.getSimplifiedPredicates();
        Assert.assertEquals(1, simplifiedPredicates.size());
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_1, simplifiedPredicates.get(0));
        verify(provider);
        reset(provider);
        expect(provider.checkPropertyIds(capture(propertiesCapture))).andReturn(Collections.emptySet()).anyTimes();
        replay(provider);
        // ---
        PredicateHelper.visit(SimplifyingPredicateVisitorTest.PREDICATE_15, visitor);
        simplifiedPredicates = visitor.getSimplifiedPredicates();
        Assert.assertEquals(1, simplifiedPredicates.size());
        Assert.assertEquals(SimplifyingPredicateVisitorTest.PREDICATE_1, simplifiedPredicates.get(0));
        verify(provider);
    }
}

