/**
 * Copyright 2011-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.support;


import java.util.Collections;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit tests for {@link AbstractJpaEntityInformation}.
 *
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class JpaEntityInformationSupportUnitTests {
    @Mock
    EntityManager em;

    @Mock
    Metamodel metaModel;

    @Test
    public void usesSimpleClassNameIfNoEntityNameGiven() throws Exception {
        JpaEntityInformation<JpaEntityInformationSupportUnitTests.User, Long> information = new JpaEntityInformationSupportUnitTests.DummyJpaEntityInformation<JpaEntityInformationSupportUnitTests.User, Long>(JpaEntityInformationSupportUnitTests.User.class);
        Assert.assertEquals("User", information.getEntityName());
        JpaEntityInformation<JpaEntityInformationSupportUnitTests.NamedUser, ?> second = new JpaEntityInformationSupportUnitTests.DummyJpaEntityInformation<JpaEntityInformationSupportUnitTests.NamedUser, java.io.Serializable>(JpaEntityInformationSupportUnitTests.NamedUser.class);
        Assert.assertEquals("AnotherNamedUser", second.getEntityName());
    }

    // DATAJPA-93
    @Test(expected = IllegalArgumentException.class)
    public void rejectsClassNotBeingFoundInMetamodel() {
        Mockito.when(em.getMetamodel()).thenReturn(metaModel);
        JpaEntityInformationSupport.getEntityInformation(JpaEntityInformationSupportUnitTests.User.class, em);
    }

    static class User {}

    @Entity(name = "AnotherNamedUser")
    public class NamedUser {}

    static class DummyJpaEntityInformation<T, ID> extends JpaEntityInformationSupport<T, ID> {
        public DummyJpaEntityInformation(Class<T> domainClass) {
            super(domainClass);
        }

        @Override
        public SingularAttribute<? super T, ?> getIdAttribute() {
            return null;
        }

        @Override
        public ID getId(T entity) {
            return null;
        }

        @Override
        public Class<ID> getIdType() {
            return null;
        }

        @Override
        public Iterable<String> getIdAttributeNames() {
            return Collections.emptySet();
        }

        @Override
        public boolean hasCompositeId() {
            return false;
        }

        @Override
        public Object getCompositeIdAttributeValue(Object id, String idAttribute) {
            return null;
        }
    }
}

