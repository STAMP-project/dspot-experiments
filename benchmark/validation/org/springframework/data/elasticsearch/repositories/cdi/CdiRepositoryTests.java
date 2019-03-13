/**
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.elasticsearch.repositories.cdi;


import java.util.Optional;
import java.util.function.Consumer;
import org.apache.webbeans.cditest.CdiTestContainer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.elasticsearch.entities.Product;


/**
 *
 *
 * @author Mohsin Husen
 * @author Mark Paluch
 * @author Christoph Strobl
 */
public class CdiRepositoryTests {
    private static CdiTestContainer cdiContainer;

    private CdiProductRepository repository;

    private SamplePersonRepository personRepository;

    private QualifiedProductRepository qualifiedProductRepository;

    @Test
    public void testCdiRepository() {
        Assert.assertNotNull(repository);
        Product bean = new Product();
        setId("id-1");
        setName("cidContainerTest-1");
        save(bean);
        Assert.assertTrue(repository.existsById(getId()));
        Optional<Product> retrieved = repository.findById(getId());
        Assert.assertTrue(retrieved.isPresent());
        retrieved.ifPresent(( product) -> {
            Assert.assertEquals(getId(), getId());
            Assert.assertEquals(getName(), getName());
        });
        Assert.assertEquals(1, count());
        Assert.assertTrue(repository.existsById(getId()));
        delete(bean);
        Assert.assertEquals(0, count());
        retrieved = repository.findById(getId());
        Assert.assertFalse(retrieved.isPresent());
    }

    /**
     *
     *
     * @see DATAES-234
     */
    @Test
    public void testQualifiedCdiRepository() {
        Assert.assertNotNull(qualifiedProductRepository);
        Product bean = new Product();
        setId("id-1");
        setName("cidContainerTest-1");
        save(bean);
        Assert.assertTrue(qualifiedProductRepository.existsById(getId()));
        Optional<Product> retrieved = qualifiedProductRepository.findById(getId());
        Assert.assertTrue(retrieved.isPresent());
        retrieved.ifPresent(( product) -> {
            Assert.assertEquals(getId(), getId());
            Assert.assertEquals(getName(), getName());
        });
        Assert.assertEquals(1, count());
        Assert.assertTrue(qualifiedProductRepository.existsById(getId()));
        delete(bean);
        Assert.assertEquals(0, count());
        retrieved = qualifiedProductRepository.findById(getId());
        Assert.assertFalse(retrieved.isPresent());
    }

    /**
     *
     *
     * @see DATAES-113
     */
    @Test
    public void returnOneFromCustomImpl() {
        Assert.assertThat(personRepository.returnOne(), CoreMatchers.is(1));
    }
}

