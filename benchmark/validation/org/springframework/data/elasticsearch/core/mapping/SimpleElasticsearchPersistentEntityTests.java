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
package org.springframework.data.elasticsearch.core.mapping;


import java.beans.IntrospectionException;
import org.junit.Test;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Score;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;


/**
 *
 *
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Mark Paluch
 * @author Oliver Gierke
 */
public class SimpleElasticsearchPersistentEntityTests {
    @Test(expected = MappingException.class)
    public void shouldThrowExceptionGivenVersionPropertyIsNotLong() throws IntrospectionException, NoSuchFieldException {
        // given
        TypeInformation typeInformation = ClassTypeInformation.from(SimpleElasticsearchPersistentEntityTests.EntityWithWrongVersionType.class);
        SimpleElasticsearchPersistentEntity<SimpleElasticsearchPersistentEntityTests.EntityWithWrongVersionType> entity = new SimpleElasticsearchPersistentEntity(typeInformation);
        SimpleElasticsearchPersistentProperty persistentProperty = SimpleElasticsearchPersistentEntityTests.createProperty(entity, "version");
        // when
        entity.addPersistentProperty(persistentProperty);
    }

    @Test(expected = MappingException.class)
    public void shouldThrowExceptionGivenMultipleVersionPropertiesArePresent() throws IntrospectionException, NoSuchFieldException {
        // given
        TypeInformation typeInformation = ClassTypeInformation.from(SimpleElasticsearchPersistentEntityTests.EntityWithMultipleVersionField.class);
        SimpleElasticsearchPersistentEntity<SimpleElasticsearchPersistentEntityTests.EntityWithMultipleVersionField> entity = new SimpleElasticsearchPersistentEntity(typeInformation);
        SimpleElasticsearchPersistentProperty persistentProperty1 = SimpleElasticsearchPersistentEntityTests.createProperty(entity, "version1");
        SimpleElasticsearchPersistentProperty persistentProperty2 = SimpleElasticsearchPersistentEntityTests.createProperty(entity, "version2");
        entity.addPersistentProperty(persistentProperty1);
        // when
        entity.addPersistentProperty(persistentProperty2);
    }

    // DATAES-462
    @Test
    public void rejectsMultipleScoreProperties() {
        SimpleElasticsearchMappingContext context = new SimpleElasticsearchMappingContext();
        // 
        // 
        // 
        assertThatExceptionOfType(MappingException.class).isThrownBy(() -> context.getRequiredPersistentEntity(.class)).withMessageContaining("first").withMessageContaining("second");
    }

    private class EntityWithWrongVersionType {
        @Version
        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    private class EntityWithMultipleVersionField {
        @Version
        private Long version1;

        @Version
        private Long version2;

        public Long getVersion1() {
            return version1;
        }

        public void setVersion1(Long version1) {
            this.version1 = version1;
        }

        public Long getVersion2() {
            return version2;
        }

        public void setVersion2(Long version2) {
            this.version2 = version2;
        }
    }

    // DATAES-462
    static class TwoScoreProperties {
        @Score
        float first;

        @Score
        float second;
    }
}

