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


import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.core.EntityInformation;


/**
 * Unit tests for {@link JpaPersistableEntityInformation}.
 *
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaPersistableEntityInformationUnitTests {
    @Mock
    Metamodel metamodel;

    @Mock
    EntityType<JpaPersistableEntityInformationUnitTests.Foo> type;

    @Mock
    @SuppressWarnings("rawtypes")
    Type idType;

    @Test
    public void usesPersistableMethodsForIsNewAndGetId() {
        EntityInformation<JpaPersistableEntityInformationUnitTests.Foo, Long> entityInformation = new JpaPersistableEntityInformation<JpaPersistableEntityInformationUnitTests.Foo, Long>(JpaPersistableEntityInformationUnitTests.Foo.class, metamodel);
        JpaPersistableEntityInformationUnitTests.Foo foo = new JpaPersistableEntityInformationUnitTests.Foo();
        Assert.assertThat(entityInformation.isNew(foo), CoreMatchers.is(false));
        Assert.assertThat(entityInformation.getId(foo), CoreMatchers.is(CoreMatchers.nullValue()));
        foo.id = 1L;
        Assert.assertThat(entityInformation.isNew(foo), CoreMatchers.is(true));
        Assert.assertThat(entityInformation.getId(foo), CoreMatchers.is(1L));
    }

    @SuppressWarnings("serial")
    class Foo implements Persistable<Long> {
        Long id;

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public boolean isNew() {
            return (id) != null;
        }
    }
}

