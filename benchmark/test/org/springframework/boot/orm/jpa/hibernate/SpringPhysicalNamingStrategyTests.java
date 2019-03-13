/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.orm.jpa.hibernate;


import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.mapping.PersistentClass;
import org.junit.Test;


/**
 * Tests for {@link SpringPhysicalNamingStrategy}.
 *
 * @author Phillip Webb
 * @author Madhura Bhave
 */
public class SpringPhysicalNamingStrategyTests {
    private Metadata metadata;

    private MetadataSources metadataSources;

    @Test
    public void tableNameShouldBeLowercaseUnderscore() {
        PersistentClass binding = this.metadata.getEntityBinding(TelephoneNumber.class.getName());
        assertThat(binding.getTable().getQuotedName()).isEqualTo("telephone_number");
    }

    @Test
    public void tableNameShouldNotBeLowerCaseIfCaseSensitive() {
        this.metadata = this.metadataSources.getMetadataBuilder().applyPhysicalNamingStrategy(new SpringPhysicalNamingStrategyTests.TestSpringPhysicalNamingStrategy()).build();
        PersistentClass binding = this.metadata.getEntityBinding(TelephoneNumber.class.getName());
        assertThat(binding.getTable().getQuotedName()).isEqualTo("Telephone_Number");
    }

    private class TestSpringPhysicalNamingStrategy extends SpringPhysicalNamingStrategy {
        @Override
        protected boolean isCaseInsensitive(JdbcEnvironment jdbcEnvironment) {
            return false;
        }
    }
}

