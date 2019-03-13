/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.integration.impl;


import TestTypes.VISIBILITY;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.integration.TraitRequirement;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.StoreTrait;
import uk.gov.gchq.gaffer.user.User;


public class VisibilityIT extends AbstractStoreIT {
    private static final User USER_VIS_1 = new User.Builder().dataAuth("vis1").build();

    private static final User USER_VIS_2 = new User.Builder().dataAuth("vis2").build();

    @Test
    @TraitRequirement(StoreTrait.VISIBILITY)
    public void shouldAccessMissingVisibilityGroups() throws OperationException {
        final Set<Element> elements = new HashSet<>();
        final Entity entity1 = new Entity(TestGroups.ENTITY, "A");
        // Do NOT add an explicit visibility property
        // entity1.putProperty(AccumuloPropertyNames.VISIBILITY, "");
        elements.add(entity1);
        final AddElements addElements = new AddElements.Builder().input(elements).build();
        AbstractStoreIT.graph.execute(addElements, getUser());
        final GetElements get = new GetElements.Builder().input(new EntitySeed("A")).build();
        final CloseableIterable<? extends Element> iterable = AbstractStoreIT.graph.execute(get, getUser());
        final List<Element> results = Lists.newArrayList(iterable);
        // Check for all entities which should be visible
        Assert.assertThat("Results do not contain all expected entities.", results, hasSize(1));
        for (final Element e : results) {
            // Check that all visible entities contain the visibility property
            Assert.assertTrue("Visibility property should be visible.", e.getProperties().containsKey(VISIBILITY));
            Assert.assertThat("Visibility property should contain an empty String.", e.getProperties().get(VISIBILITY).toString(), Matchers.isEmptyString());
        }
        iterable.close();
    }

    @Test
    @TraitRequirement(StoreTrait.VISIBILITY)
    public void shouldAccessMissingVisibilityGroupsWithNoVisibilityPropertyInSchema() throws OperationException {
        AbstractStoreIT.graph = createGraphWithNoVisibility();
        final Set<Element> elements = new HashSet<>();
        final Entity entity1 = new Entity(TestGroups.ENTITY, "A");
        elements.add(entity1);
        final AddElements addElements = new AddElements.Builder().input(elements).build();
        AbstractStoreIT.graph.execute(addElements, getUser());
        final GetElements get = new GetElements.Builder().input(new EntitySeed("A")).build();
        final CloseableIterable<? extends Element> iterable = AbstractStoreIT.graph.execute(get, getUser());
        final List<Element> results = Lists.newArrayList(iterable);
        // Check for all entities which should be visible
        Assert.assertThat("Results do not contain all expected entities.", results, hasSize(1));
        for (final Element e : results) {
            // Check that all visible entities do not contain the visibility property
            Assert.assertFalse("Visibility property should not be visible.", e.getProperties().containsKey(VISIBILITY));
        }
        iterable.close();
    }

    @Test
    @TraitRequirement(StoreTrait.VISIBILITY)
    public void shouldAccessEmptyVisibilityGroups() throws OperationException {
        final Set<Element> elements = new HashSet<>();
        final Entity entity1 = new Entity(TestGroups.ENTITY, "A");
        entity1.putProperty(VISIBILITY, "");
        elements.add(entity1);
        final AddElements addElements = new AddElements.Builder().input(elements).build();
        AbstractStoreIT.graph.execute(addElements, getUser());
        final GetElements get = new GetElements.Builder().input(new EntitySeed("A")).build();
        final CloseableIterable<? extends Element> iterable = AbstractStoreIT.graph.execute(get, getUser());
        final List<Element> results = Lists.newArrayList(iterable);
        // Check for all entities which should be visible
        Assert.assertThat("Results do not contain all expected entities.", results, hasSize(1));
        for (final Element e : results) {
            // Check that all visible entities contain the visibility property
            Assert.assertTrue("Visibility property should be visible.", e.getProperties().containsKey(VISIBILITY));
            Assert.assertThat("Visibility property should contain an empty String.", e.getProperties().get(VISIBILITY).toString(), Matchers.isEmptyString());
        }
        iterable.close();
    }

    @Test
    @TraitRequirement(StoreTrait.VISIBILITY)
    public void shouldAccessNullVisibilityGroups() throws OperationException {
        final Set<Element> elements = new HashSet<>();
        final Entity entity1 = new Entity(TestGroups.ENTITY, "A");
        entity1.putProperty(VISIBILITY, null);
        elements.add(entity1);
        final AddElements addElements = new AddElements.Builder().input(elements).build();
        AbstractStoreIT.graph.execute(addElements, getUser());
        final GetElements get = new GetElements.Builder().input(new EntitySeed("A"), new EntitySeed("B")).build();
        final CloseableIterable<? extends Element> iterable = AbstractStoreIT.graph.execute(get, getUser());
        final List<Element> results = Lists.newArrayList(iterable);
        // Check for all entities which should be visible
        Assert.assertThat("Results do not contain all expected entities.", results, hasSize(1));
        for (final Element e : results) {
            // Check that all visible entities contain the visibility property
            Assert.assertTrue("Visibility property should be visible.", e.getProperties().containsKey(VISIBILITY));
            Assert.assertThat("Visibility property should contain an empty String.", e.getProperties().get(VISIBILITY).toString(), Matchers.isEmptyString());
        }
        iterable.close();
    }

    @Test
    @TraitRequirement(StoreTrait.VISIBILITY)
    public void shouldAccessSingleVisibilityGroup() throws OperationException {
        final Set<Element> elements = new HashSet<>();
        final Entity entity1 = new Entity(TestGroups.ENTITY, "A");
        entity1.putProperty(VISIBILITY, "vis1");
        final Entity entity2 = new Entity(TestGroups.ENTITY, "B");
        entity2.putProperty(VISIBILITY, "vis1");
        elements.add(entity1);
        elements.add(entity2);
        final AddElements addElements = new AddElements.Builder().input(elements).build();
        AbstractStoreIT.graph.execute(addElements, VisibilityIT.USER_VIS_1);
        final GetElements get = new GetElements.Builder().input(new EntitySeed("A"), new EntitySeed("B")).build();
        final CloseableIterable<? extends Element> userVis1Iterable = AbstractStoreIT.graph.execute(get, VisibilityIT.USER_VIS_1);
        final CloseableIterable<? extends Element> userVis2Iterable = AbstractStoreIT.graph.execute(get, VisibilityIT.USER_VIS_2);
        final List<Element> userVis1Results = Lists.newArrayList(userVis1Iterable);
        final List<Element> userVis2Results = Lists.newArrayList(userVis2Iterable);
        Assert.assertThat(userVis1Results, hasSize(2));
        Assert.assertThat(userVis2Results, Is.is(Matchers.empty()));
        for (final Element e : userVis1Results) {
            // Check that all visible entities contain the visibility property
            Assert.assertTrue("Missing visibility property.", e.getProperties().containsKey(VISIBILITY));
            // Check that the visibility key contai
            // ns the correct value
            Assert.assertEquals("Visibility property should be \"vis1\"", e.getProperties().get(VISIBILITY).toString(), "vis1");
        }
        userVis1Iterable.close();
        userVis2Iterable.close();
    }

    @Test
    @TraitRequirement(StoreTrait.VISIBILITY)
    public void shouldAccessMultipleVisibilityGroups_and() throws OperationException {
        final Set<Element> elements = new HashSet<>();
        final Entity entity1 = new Entity(TestGroups.ENTITY, "B");
        entity1.putProperty(VISIBILITY, "vis1&vis2");
        elements.add(entity1);
        final AddElements addElements = new AddElements.Builder().input(elements).build();
        AbstractStoreIT.graph.execute(addElements, new User());
        final GetElements get = new GetElements.Builder().input(new EntitySeed("B")).build();
        final CloseableIterable<? extends Element> iterable = AbstractStoreIT.graph.execute(get, new User(User.UNKNOWN_USER_ID, Sets.newHashSet("vis1", "vis2")));
        final List<Element> results = Lists.newArrayList(iterable);
        Assert.assertThat("Results do not contain all expected Elements.", results, hasSize(1));
        for (final Element e : iterable) {
            Assert.assertTrue(e.getProperties().containsKey(VISIBILITY));
        }
        iterable.close();
    }

    @Test
    @TraitRequirement(StoreTrait.VISIBILITY)
    public void shouldAccessMultipleVisibilityGroups_or() throws OperationException {
        final Set<Element> elements = new HashSet<>();
        final Entity entity1 = new Entity(TestGroups.ENTITY, "B");
        entity1.putProperty(VISIBILITY, "vis1|vis2");
        elements.add(entity1);
        final AddElements addElements = new AddElements.Builder().input(elements).build();
        AbstractStoreIT.graph.execute(addElements, new User());
        final GetElements get = new GetElements.Builder().input(new EntitySeed("B")).build();
        final CloseableIterable<? extends Element> iterable = AbstractStoreIT.graph.execute(get, new User(User.UNKNOWN_USER_ID, Sets.newHashSet("vis1")));
        final List<Element> results = Lists.newArrayList(iterable);
        Assert.assertThat("Results do not contain all expected Elements.", results, hasSize(1));
        for (final Element e : results) {
            Assert.assertTrue(e.getProperties().containsKey(VISIBILITY));
        }
        iterable.close();
    }
}

