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
package org.springframework.boot.autoconfigure.jdbc;


import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Tests for {@link EmbeddedDataSourceConfiguration}.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class EmbeddedDataSourceConfigurationTests {
    private AnnotationConfigApplicationContext context;

    @Test
    public void defaultEmbeddedDatabase() {
        this.context = load();
        assertThat(this.context.getBean(DataSource.class)).isNotNull();
    }

    @Test
    public void generateUniqueName() throws Exception {
        this.context = load("spring.datasource.generate-unique-name=true");
        try (AnnotationConfigApplicationContext context2 = load("spring.datasource.generate-unique-name=true")) {
            DataSource dataSource = this.context.getBean(DataSource.class);
            DataSource dataSource2 = context2.getBean(DataSource.class);
            assertThat(getDatabaseName(dataSource)).isNotEqualTo(getDatabaseName(dataSource2));
        }
    }
}

