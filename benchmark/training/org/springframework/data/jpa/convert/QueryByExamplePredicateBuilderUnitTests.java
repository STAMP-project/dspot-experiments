/**
 * Copyright 2016-2019 the original author or authors.
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
package org.springframework.data.jpa.convert;


import PersistentAttributeType.BASIC;
import PersistentAttributeType.EMBEDDED;
import java.lang.reflect.Member;
import java.util.Set;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable.BindableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.util.ObjectUtils;

import static BindableType.SINGULAR_ATTRIBUTE;


/**
 * Unit tests for {@link QueryByExamplePredicateBuilder}.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.Silent.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class QueryByExamplePredicateBuilderUnitTests {
    @Mock
    CriteriaBuilder cb;

    @Mock
    Root root;

    @Mock
    EntityType<QueryByExamplePredicateBuilderUnitTests.Person> personEntityType;

    @Mock
    Expression expressionMock;

    @Mock
    Predicate truePredicate;

    @Mock
    Predicate dummyPredicate;

    @Mock
    Predicate andPredicate;

    @Mock
    Predicate orPredicate;

    @Mock
    Path dummyPath;

    Set<SingularAttribute<? super QueryByExamplePredicateBuilderUnitTests.Person, ?>> personEntityAttribtues;

    SingularAttribute<? super QueryByExamplePredicateBuilderUnitTests.Person, Long> personIdAttribute;

    SingularAttribute<? super QueryByExamplePredicateBuilderUnitTests.Person, String> personFirstnameAttribute;

    SingularAttribute<? super QueryByExamplePredicateBuilderUnitTests.Person, Long> personAgeAttribute;

    SingularAttribute<? super QueryByExamplePredicateBuilderUnitTests.Person, QueryByExamplePredicateBuilderUnitTests.Person> personFatherAttribute;

    SingularAttribute<? super QueryByExamplePredicateBuilderUnitTests.Person, QueryByExamplePredicateBuilderUnitTests.Skill> personSkillAttribute;

    SingularAttribute<? super QueryByExamplePredicateBuilderUnitTests.Person, QueryByExamplePredicateBuilderUnitTests.Address> personAddressAttribute;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    // DATAJPA-218
    @Test(expected = IllegalArgumentException.class)
    public void getPredicateShouldThrowExceptionOnNullRoot() {
        QueryByExamplePredicateBuilder.getPredicate(null, cb, of(new QueryByExamplePredicateBuilderUnitTests.Person()));
    }

    // DATAJPA-218
    @Test(expected = IllegalArgumentException.class)
    public void getPredicateShouldThrowExceptionOnNullCriteriaBuilder() {
        QueryByExamplePredicateBuilder.getPredicate(root, null, of(new QueryByExamplePredicateBuilderUnitTests.Person()));
    }

    // DATAJPA-218
    @Test(expected = IllegalArgumentException.class)
    public void getPredicateShouldThrowExceptionOnNullExample() {
        QueryByExamplePredicateBuilder.getPredicate(root, null, null);
    }

    // DATAJPA-218
    @Test
    public void emptyCriteriaListShouldResultTruePredicate() {
        Assert.assertThat(QueryByExamplePredicateBuilder.getPredicate(root, cb, of(new QueryByExamplePredicateBuilderUnitTests.Person())), IsEqual.equalTo(truePredicate));
    }

    // DATAJPA-218
    @Test
    public void singleElementCriteriaShouldJustReturnIt() {
        QueryByExamplePredicateBuilderUnitTests.Person p = new QueryByExamplePredicateBuilderUnitTests.Person();
        p.firstname = "foo";
        Assert.assertThat(QueryByExamplePredicateBuilder.getPredicate(root, cb, of(p)), IsEqual.equalTo(dummyPredicate));
        Mockito.verify(cb, Mockito.times(1)).equal(ArgumentMatchers.any(Expression.class), ArgumentMatchers.eq("foo"));
    }

    // DATAJPA-937
    @Test
    public void unresolvableNestedAssociatedPathShouldFail() {
        QueryByExamplePredicateBuilderUnitTests.Person p = new QueryByExamplePredicateBuilderUnitTests.Person();
        QueryByExamplePredicateBuilderUnitTests.Person father = new QueryByExamplePredicateBuilderUnitTests.Person();
        father.father = new QueryByExamplePredicateBuilderUnitTests.Person();
        p.father = father;
        exception.expectCause(IsInstanceOf.<Throwable>instanceOf(IllegalArgumentException.class));
        exception.expectMessage("Unexpected path type");
        QueryByExamplePredicateBuilder.getPredicate(root, cb, of(p));
    }

    // DATAJPA-218
    @Test
    public void multiPredicateCriteriaShouldReturnCombinedOnes() {
        QueryByExamplePredicateBuilderUnitTests.Person p = new QueryByExamplePredicateBuilderUnitTests.Person();
        p.firstname = "foo";
        p.age = 2L;
        Assert.assertThat(QueryByExamplePredicateBuilder.getPredicate(root, cb, of(p)), IsEqual.equalTo(andPredicate));
        Mockito.verify(cb, Mockito.times(1)).equal(ArgumentMatchers.any(Expression.class), ArgumentMatchers.eq("foo"));
        Mockito.verify(cb, Mockito.times(1)).equal(ArgumentMatchers.any(Expression.class), ArgumentMatchers.eq(2L));
    }

    // DATAJPA-879
    @Test
    public void orConcatenatesPredicatesIfMatcherSpecifies() {
        QueryByExamplePredicateBuilderUnitTests.Person person = new QueryByExamplePredicateBuilderUnitTests.Person();
        person.firstname = "foo";
        person.age = 2L;
        Example<QueryByExamplePredicateBuilderUnitTests.Person> example = of(person, ExampleMatcher.matchingAny());
        Assert.assertThat(QueryByExamplePredicateBuilder.getPredicate(root, cb, example), IsEqual.equalTo(orPredicate));
        Mockito.verify(cb, Mockito.times(1)).or(ArgumentMatchers.any());
    }

    static class Person {
        @Id
        Long id;

        String firstname;

        Long age;

        QueryByExamplePredicateBuilderUnitTests.Person father;

        QueryByExamplePredicateBuilderUnitTests.Address address;

        QueryByExamplePredicateBuilderUnitTests.Skill skill;
    }

    static class Address {
        String city;

        String country;
    }

    static class Skill {
        @Id
        Long id;

        String name;
    }

    static class SingluarAttributeStub<X, T> implements SingularAttribute<X, T> {
        private String name;

        private PersistentAttributeType attributeType;

        private Class<T> javaType;

        private Type<T> type;

        public SingluarAttributeStub(String name, PersistentAttributeType attributeType, Class<T> javaType) {
            this(name, attributeType, javaType, null);
        }

        public SingluarAttributeStub(String name, PersistentAttributeType attributeType, Class<T> javaType, Type<T> type) {
            this.name = name;
            this.attributeType = attributeType;
            this.javaType = javaType;
            this.type = type;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public PersistentAttributeType getPersistentAttributeType() {
            return attributeType;
        }

        @Override
        public ManagedType<X> getDeclaringType() {
            return null;
        }

        @Override
        public Class<T> getJavaType() {
            return javaType;
        }

        @Override
        public Member getJavaMember() {
            return null;
        }

        @Override
        public boolean isAssociation() {
            return (!(attributeType.equals(BASIC))) && (!(attributeType.equals(EMBEDDED)));
        }

        @Override
        public boolean isCollection() {
            return false;
        }

        @Override
        public BindableType getBindableType() {
            return SINGULAR_ATTRIBUTE;
        }

        @Override
        public Class<T> getBindableJavaType() {
            return javaType;
        }

        @Override
        public boolean isId() {
            return ObjectUtils.nullSafeEquals(name, "id");
        }

        @Override
        public boolean isVersion() {
            return false;
        }

        @Override
        public boolean isOptional() {
            return false;
        }

        @Override
        public Type<T> getType() {
            return type;
        }
    }
}

