/**
 * Copyright 2014-2019 the original author or authors.
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
package org.springframework.data.elasticsearch;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.entities.Author;
import org.springframework.data.elasticsearch.entities.Book;
import org.springframework.data.elasticsearch.repositories.book.SampleElasticSearchBookRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Mohsin Husen
 * @author Christoph Strobl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/repository-test-nested-object-books.xml")
public class InnerObjectTests {
    @Autowired
    private SampleElasticSearchBookRepository bookRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldIndexInnerObject() {
        // given
        String id = randomAlphanumeric(5);
        Book book = new Book();
        setId(id);
        setName("xyz");
        Author author = new Author();
        author.setId("1");
        author.setName("ABC");
        setAuthor(author);
        // when
        save(book);
        // then
        Assert.assertThat(findById(id), is(notNullValue()));
    }
}

