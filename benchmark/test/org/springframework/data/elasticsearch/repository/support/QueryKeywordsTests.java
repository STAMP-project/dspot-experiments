/**
 * Copyright 2016-2019 the original author or authors.
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
package org.springframework.data.elasticsearch.repository.support;


import java.util.Arrays;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repositories.query.ProductRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Artur Konczak
 * @author Christoph Strobl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/repository-query-support.xml")
public class QueryKeywordsTests {
    @Autowired
    private ProductRepository repository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void shouldSupportAND() {
        // given
        // when
        // then
        Assert.assertThat(repository.findByNameAndText("Sugar", "Cane sugar").size(), Is.is(2));
        Assert.assertThat(repository.findByNameAndPrice("Sugar", 1.1F).size(), Is.is(1));
    }

    @Test
    public void shouldSupportOR() {
        // given
        // when
        // then
        Assert.assertThat(repository.findByNameOrPrice("Sugar", 1.9F).size(), Is.is(4));
        Assert.assertThat(repository.findByNameOrText("Salt", "Beet sugar").size(), Is.is(3));
    }

    @Test
    public void shouldSupportTrueAndFalse() {
        // given
        // when
        // then
        Assert.assertThat(repository.findByAvailableTrue().size(), Is.is(3));
        Assert.assertThat(repository.findByAvailableFalse().size(), Is.is(2));
    }

    @Test
    public void shouldSupportInAndNotInAndNot() {
        // given
        // when
        // then
        Assert.assertThat(repository.findByPriceIn(Arrays.asList(1.2F, 1.1F)).size(), Is.is(2));
        Assert.assertThat(repository.findByPriceNotIn(Arrays.asList(1.2F, 1.1F)).size(), Is.is(3));
        Assert.assertThat(repository.findByPriceNot(1.2F).size(), Is.is(4));
    }

    /* DATAES-171 */
    @Test
    public void shouldWorkWithNotIn() {
        // given
        // when
        // then
        Assert.assertThat(repository.findByIdNotIn(Arrays.asList("2", "3")).size(), Is.is(3));
    }

    @Test
    public void shouldSupportBetween() {
        // given
        // when
        // then
        Assert.assertThat(repository.findByPriceBetween(1.0F, 2.0F).size(), Is.is(4));
    }

    @Test
    public void shouldSupportLessThanAndGreaterThan() {
        // given
        // when
        // then
        Assert.assertThat(repository.findByPriceLessThan(1.1F).size(), Is.is(1));
        Assert.assertThat(repository.findByPriceLessThanEqual(1.1F).size(), Is.is(2));
        Assert.assertThat(repository.findByPriceGreaterThan(1.9F).size(), Is.is(1));
        Assert.assertThat(repository.findByPriceGreaterThanEqual(1.9F).size(), Is.is(2));
    }
}

