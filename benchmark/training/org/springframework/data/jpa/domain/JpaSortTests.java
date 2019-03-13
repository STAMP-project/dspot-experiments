/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.domain;


import Address_.streetName;
import MailSender_.name;
import User_.lastname;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.JpaSort.JpaOrder;
import org.springframework.data.jpa.domain.JpaSort.Path;
import org.springframework.data.jpa.domain.sample.User_;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static Direction.DESC;


/**
 * Integration tests for {@link JpaSort}. This has to be an integration test due to the design of the statically
 * generated meta-model classes. The properties cannot be referred to statically (quite a surprise, as they're static)
 * but only after they've been enhanced by the persistence provider. This requires an {@link EntityManagerFactory} to be
 * bootstrapped.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 * @author Christoph Strobl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:infrastructure.xml")
public class JpaSortTests {
    @Nullable
    private static final Attribute<?, ?> NULL_ATTRIBUTE = null;

    private static final Attribute<?, ?>[] EMPTY_ATTRIBUTES = new Attribute<?, ?>[0];

    @Nullable
    private static final PluralAttribute<?, ?, ?> NULL_PLURAL_ATTRIBUTE = null;

    private static final PluralAttribute<?, ?, ?>[] EMPTY_PLURAL_ATTRIBUTES = new PluralAttribute<?, ?, ?>[0];

    // DATAJPA-12
    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullAttribute() {
        new JpaSort.JpaSort(JpaSortTests.NULL_ATTRIBUTE);
    }

    // DATAJPA-12
    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyAttributes() {
        new JpaSort.JpaSort(JpaSortTests.EMPTY_ATTRIBUTES);
    }

    // DATAJPA-12
    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullPluralAttribute() {
        new JpaSort.JpaSort(JpaSortTests.NULL_PLURAL_ATTRIBUTE);
    }

    // DATAJPA-12
    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyPluralAttributes() {
        new JpaSort.JpaSort(JpaSortTests.EMPTY_PLURAL_ATTRIBUTES);
    }

    // DATAJPA-12
    @Test
    public void sortBySinglePropertyWithDefaultSortDirection() {
        Assert.assertThat(new JpaSort.JpaSort(path(User_.firstname)), hasItems(new Sort.Order("firstname")));
    }

    // DATAJPA-12
    @Test
    public void sortByMultiplePropertiesWithDefaultSortDirection() {
        Assert.assertThat(new JpaSort.JpaSort(User_.firstname, User_.lastname), hasItems(new Order("firstname"), new Order("lastname")));
    }

    // DATAJPA-12
    @Test
    public void sortByMultiplePropertiesWithDescSortDirection() {
        Assert.assertThat(new JpaSort.JpaSort(DESC, User_.firstname, User_.lastname), hasItems(new Order(DESC, "firstname"), new Order(DESC, "lastname")));
    }

    // DATAJPA-12
    @Test
    public void combiningSortByMultipleProperties() {
        Assert.assertThat(and(new JpaSort.JpaSort(User_.lastname)), hasItems(new Order("firstname"), new Order("lastname")));
    }

    // DATAJPA-12
    @Test
    public void combiningSortByMultiplePropertiesWithDifferentSort() {
        Assert.assertThat(and(new JpaSort.JpaSort(DESC, User_.lastname)), hasItems(new Order("firstname"), new Order(DESC, "lastname")));
    }

    // DATAJPA-12
    @Test
    public void combiningSortByNestedEmbeddedProperty() {
        Assert.assertThat(new JpaSort.JpaSort(path(User_.address).dot(streetName)), hasItems(new Order("address.streetName")));
    }

    // DATAJPA-12
    @Test
    public void buildJpaSortFromJpaMetaModelSingleAttribute() {
        // 
        Assert.assertThat(new JpaSort.JpaSort(ASC, path(User_.firstname)), hasItems(new Order("firstname")));
    }

    // DATAJPA-12
    @Test
    public void buildJpaSortFromJpaMetaModelNestedAttribute() {
        // 
        Assert.assertThat(new JpaSort.JpaSort(ASC, path(MailMessage_.mailSender).dot(name)), hasItems(new Order("mailSender.name")));
    }

    // DATAJPA-702
    @Test
    public void combiningSortByMultiplePropertiesWithDifferentSortUsingSimpleAnd() {
        Assert.assertThat(new JpaSort.JpaSort(User_.firstname).and(DESC, lastname), contains(new Order("firstname"), new Order(DESC, "lastname")));
    }

    // DATAJPA-702
    @Test
    public void combiningSortByMultiplePathsWithDifferentSortUsingSimpleAnd() {
        Assert.assertThat(new JpaSort.JpaSort(User_.firstname).and(DESC, path(MailMessage_.mailSender).dot(name)), contains(new Order("firstname"), new Order(DESC, "mailSender.name")));
    }

    // DATAJPA-702
    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullAttributesForCombiningCriterias() {
        new JpaSort.JpaSort(User_.firstname).and(DESC, ((Attribute<?, ?>[]) (null)));
    }

    // DATAJPA-702
    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullPathsForCombiningCriterias() {
        new JpaSort.JpaSort(User_.firstname).and(DESC, ((Path<?, ?>[]) (null)));
    }

    // DATAJPA-702
    @Test
    public void buildsUpPathForPluralAttributesCorrectly() {
        // assertThat(new JpaSort(JpaSort.path(User_.colleagues).dot(User_.roles).dot(Role_.name)), //
        // hasItem(new Order(ASC, "colleagues.roles.name")));
    }

    // DATAJPA-965
    @Test
    public void createsUnsafeSortCorrectly() {
        JpaSort.JpaSort sort = JpaSort.JpaSort.unsafe(DESC, "foo.bar");
        Assert.assertThat(sort, hasItem(new Order(DESC, "foo.bar")));
        Assert.assertThat(sort.getOrderFor("foo.bar"), is(instanceOf(JpaOrder.class)));
    }

    // DATAJPA-965
    @Test
    public void createsUnsafeSortWithMultiplePropertiesCorrectly() {
        JpaSort.JpaSort sort = JpaSort.JpaSort.unsafe(DESC, "foo.bar", "spring.data");
        Assert.assertThat(sort, hasItems(new Order(DESC, "foo.bar"), new Order(DESC, "spring.data")));
        Assert.assertThat(sort.getOrderFor("foo.bar"), is(instanceOf(JpaOrder.class)));
        Assert.assertThat(sort.getOrderFor("spring.data"), is(instanceOf(JpaOrder.class)));
    }

    // DATAJPA-965
    @Test
    public void combinesSafeAndUnsafeSortCorrectly() {
        // JpaSort sort = new JpaSort(path(User_.colleagues).dot(User_.roles).dot(Role_.name)).andUnsafe(DESC, "foo.bar");
        // 
        // assertThat(sort, hasItems(new Order(ASC, "colleagues.roles.name"), new Order(DESC, "foo.bar")));
        // assertThat(sort.getOrderFor("colleagues.roles.name"), is(not(instanceOf(JpaOrder.class))));
        // assertThat(sort.getOrderFor("foo.bar"), is(instanceOf(JpaOrder.class)));
    }

    // DATAJPA-965
    @Test
    public void combinesUnsafeAndSafeSortCorrectly() {
        // Sort sort = JpaSort.unsafe(DESC, "foo.bar").and(ASC, path(User_.colleagues).dot(User_.roles).dot(Role_.name));
        // 
        // assertThat(sort, hasItems(new Order(ASC, "colleagues.roles.name"), new Order(DESC, "foo.bar")));
        // assertThat(sort.getOrderFor("colleagues.roles.name"), is(not(instanceOf(JpaOrder.class))));
        // assertThat(sort.getOrderFor("foo.bar"), is(instanceOf(JpaOrder.class)));
    }
}

