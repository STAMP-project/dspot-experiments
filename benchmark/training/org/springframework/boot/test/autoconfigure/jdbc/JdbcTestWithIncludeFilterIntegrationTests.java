/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.test.autoconfigure.jdbc;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * Integration test with custom include filter for {@link JdbcTest}.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringRunner.class)
@JdbcTest(includeFilters = @Filter(Repository.class))
@TestPropertySource(properties = "spring.datasource.schema=classpath:org/springframework/boot/test/autoconfigure/jdbc/schema.sql")
public class JdbcTestWithIncludeFilterIntegrationTests {
    @Autowired
    private ExampleRepository repository;

    @Test
    public void testRepository() {
        this.repository.save(new ExampleEntity(42, "Smith"));
        ExampleEntity entity = this.repository.findById(42);
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Smith");
    }
}

