/**
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.database.builder;


import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.item.database.JpaItemWriter;


/**
 *
 *
 * @author Mahmoud Ben Hassine
 */
public class JpaItemWriterBuilderTests {
    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    @Test
    public void testConfiguration() throws Exception {
        JpaItemWriter<String> itemWriter = new JpaItemWriterBuilder<String>().entityManagerFactory(this.entityManagerFactory).build();
        itemWriter.afterPropertiesSet();
        List<String> items = Arrays.asList("foo", "bar");
        itemWriter.write(items);
        Mockito.verify(this.entityManager).merge(items.get(0));
        Mockito.verify(this.entityManager).merge(items.get(1));
    }

    @Test
    public void testValidation() {
        try {
            new JpaItemWriterBuilder<String>().build();
            Assert.fail("Should fail if no EntityManagerFactory is provided");
        } catch (IllegalStateException ise) {
            Assert.assertEquals("Incorrect message", "EntityManagerFactory must be provided", ise.getMessage());
        }
    }
}

