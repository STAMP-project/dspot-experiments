/**
 * Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.springframework.batch.item.data.builder;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.data.repository.CrudRepository;


/**
 *
 *
 * @author Glenn Renfro
 */
public class RepositoryItemWriterBuilderTests {
    @Mock
    private RepositoryItemWriterBuilderTests.TestRepository repository;

    @Test
    public void testNullRepository() throws Exception {
        try {
            new RepositoryItemWriterBuilder<String>().methodName("save").build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "repository is required.", iae.getMessage());
        }
    }

    @Test
    public void testEmptyMethodName() throws Exception {
        try {
            new RepositoryItemWriterBuilder<String>().repository(this.repository).build();
            Assert.fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException iae) {
            Assert.assertEquals("IllegalArgumentException message did not match the expected result.", "methodName is required.", iae.getMessage());
        }
    }

    @Test
    public void testWriteItems() throws Exception {
        RepositoryItemWriter<String> writer = new RepositoryItemWriterBuilder<String>().methodName("save").repository(this.repository).build();
        List<String> items = Collections.singletonList("foo");
        writer.write(items);
        save("foo");
    }

    @Test
    public void testWriteItemsTestRepository() throws Exception {
        RepositoryItemWriter<String> writer = new RepositoryItemWriterBuilder<String>().methodName("foo").repository(this.repository).build();
        List<String> items = Collections.singletonList("foo");
        writer.write(items);
        Mockito.verify(this.repository).foo("foo");
    }

    @Test
    public void testWriteItemsTestRepositoryMethodIs() throws Exception {
        RepositoryItemWriterBuilder.RepositoryMethodReference<RepositoryItemWriterBuilderTests.TestRepository> repositoryMethodReference = new RepositoryItemWriterBuilder.RepositoryMethodReference<>(this.repository);
        repositoryMethodReference.methodIs().foo(null);
        RepositoryItemWriter<String> writer = new RepositoryItemWriterBuilder<String>().methodName("foo").repository(repositoryMethodReference).build();
        List<String> items = Collections.singletonList("foo");
        writer.write(items);
        Mockito.verify(this.repository).foo("foo");
    }

    public interface TestRepository extends CrudRepository<String, Serializable> {
        Object foo(String arg1);
    }
}

