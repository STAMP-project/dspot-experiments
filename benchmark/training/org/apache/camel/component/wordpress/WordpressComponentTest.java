/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.wordpress;


import PostOrderBy.author;
import org.apache.camel.CamelContext;
import org.apache.camel.component.wordpress.api.model.PostSearchCriteria;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WordpressComponentTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordpressComponentTest.class);

    @Test
    public void testParseUriPropertiesCriteria() throws Exception {
        final WordpressComponent component = new WordpressComponent(Mockito.mock(CamelContext.class));
        final WordpressEndpoint endpoint = ((WordpressEndpoint) (component.createEndpoint("wordpress:post?apiVersion=2&url=http://mysite.com/&criteria.search=test&criteria.page=1&criteria.perPage=10&criteria.orderBy=author&criteria.categories=camel,dozer,json")));
        Assert.assertThat(endpoint.getConfig().getSearchCriteria(), CoreMatchers.instanceOf(PostSearchCriteria.class));
        Assert.assertNotNull(endpoint.getConfig().getSearchCriteria());
        Assert.assertThat(endpoint.getConfig().getSearchCriteria().getPage(), CoreMatchers.is(1));
        Assert.assertThat(endpoint.getConfig().getSearchCriteria().getPerPage(), CoreMatchers.is(10));
        Assert.assertThat(endpoint.getConfig().getSearchCriteria().getSearch(), CoreMatchers.is("test"));
        Assert.assertThat(getOrderBy(), CoreMatchers.is(author));
        Assert.assertThat(getCategories(), CoreMatchers.notNullValue());
        Assert.assertThat(getCategories(), Matchers.not(Matchers.emptyCollectionOf(String.class)));
        WordpressComponentTest.LOGGER.info("Categories are {}", getCategories());
    }
}

