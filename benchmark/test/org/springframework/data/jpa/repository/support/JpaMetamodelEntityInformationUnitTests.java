/**
 * Copyright 2012-2019 the original author or authors.
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


import java.io.Serializable;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.domain.sample.PersistableWithIdClass;


/**
 * Unit tests for {@link JpaMetamodelEntityInformation}.
 *
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class JpaMetamodelEntityInformationUnitTests {
    @Mock
    Metamodel metamodel;

    @Mock
    IdentifiableType<PersistableWithIdClass> type;

    @Mock
    SingularAttribute<PersistableWithIdClass, ?> first;

    @Mock
    SingularAttribute<PersistableWithIdClass, ?> second;

    @Mock
    @SuppressWarnings("rawtypes")
    Type idType;

    // DATAJPA-50
    @Test
    public void doesNotCreateIdIfAllPartialAttributesAreNull() {
        JpaMetamodelEntityInformation<PersistableWithIdClass, Serializable> information = new JpaMetamodelEntityInformation<PersistableWithIdClass, Serializable>(PersistableWithIdClass.class, metamodel);
        PersistableWithIdClass entity = new PersistableWithIdClass(null, null);
        Assert.assertThat(information.getId(entity), is(nullValue()));
        entity = new PersistableWithIdClass(2L, null);
        Assert.assertThat(information.getId(entity), is(notNullValue()));
    }
}

