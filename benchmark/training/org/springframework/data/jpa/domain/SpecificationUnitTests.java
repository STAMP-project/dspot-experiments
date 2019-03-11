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


import java.io.Serializable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit tests for {@link Specification}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Sebastian Staudt
 */
@SuppressWarnings("serial")
@RunWith(MockitoJUnitRunner.class)
public class SpecificationUnitTests implements Serializable {
    Specification.Specification<Object> spec;

    @Mock(extraInterfaces = Serializable.class)
    Root<Object> root;

    @Mock(extraInterfaces = Serializable.class)
    CriteriaQuery<?> query;

    @Mock(extraInterfaces = Serializable.class)
    CriteriaBuilder builder;

    @Mock(extraInterfaces = Serializable.class)
    Predicate predicate;

    // DATAJPA-300, DATAJPA-1170
    @Test
    public void createsSpecificationsFromNull() {
        Specification.Specification<Object> specification = where(null);
        Assert.assertThat(specification, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(specification.toPredicate(root, query, builder), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    // DATAJPA-300, DATAJPA-1170
    @Test
    public void negatesNullSpecToNull() {
        Specification.Specification<Object> specification = CoreMatchers.not(null);
        Assert.assertThat(specification, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(specification.toPredicate(root, query, builder), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    // DATAJPA-300, DATAJPA-1170
    @Test
    public void andConcatenatesSpecToNullSpec() {
        Specification.Specification<Object> specification = where(null);
        specification = specification.and(spec);
        Assert.assertThat(specification, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(specification.toPredicate(root, query, builder), CoreMatchers.is(predicate));
    }

    // DATAJPA-300, DATAJPA-1170
    @Test
    public void andConcatenatesNullSpecToSpec() {
        Specification.Specification<Object> specification = spec.and(null);
        Assert.assertThat(specification, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(specification.toPredicate(root, query, builder), CoreMatchers.is(predicate));
    }

    // DATAJPA-300, DATAJPA-1170
    @Test
    public void orConcatenatesSpecToNullSpec() {
        Specification.Specification<Object> specification = where(null);
        specification = specification.or(spec);
        Assert.assertThat(specification, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(specification.toPredicate(root, query, builder), CoreMatchers.is(predicate));
    }

    // DATAJPA-300, DATAJPA-1170
    @Test
    public void orConcatenatesNullSpecToSpec() {
        Specification.Specification<Object> specification = spec.or(null);
        Assert.assertThat(specification, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(specification.toPredicate(root, query, builder), CoreMatchers.is(predicate));
    }

    // DATAJPA-523
    @Test
    public void specificationsShouldBeSerializable() {
        Specification.Specification<Object> serializableSpec = new SpecificationUnitTests.SerializableSpecification();
        Specification.Specification<Object> specification = serializableSpec.and(serializableSpec);
        Assert.assertThat(specification, CoreMatchers.is(CoreMatchers.notNullValue()));
        @SuppressWarnings("unchecked")
        Specification.Specification<Object> transferredSpecification = ((Specification.Specification<Object>) (deserialize(serialize(specification))));
        Assert.assertThat(transferredSpecification, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    // DATAJPA-523
    @Test
    public void complexSpecificationsShouldBeSerializable() {
        SpecificationUnitTests.SerializableSpecification serializableSpec = new SpecificationUnitTests.SerializableSpecification();
        Specification.Specification<Object> specification = Specification.Specification.not(and(serializableSpec).or(serializableSpec));
        Assert.assertThat(specification, CoreMatchers.is(CoreMatchers.notNullValue()));
        @SuppressWarnings("unchecked")
        Specification.Specification<Object> transferredSpecification = ((Specification.Specification<Object>) (deserialize(serialize(specification))));
        Assert.assertThat(transferredSpecification, CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    public class SerializableSpecification implements Serializable , Specification.Specification<Object> {
        @Override
        public Predicate toPredicate(Root<Object> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return null;
        }
    }
}

