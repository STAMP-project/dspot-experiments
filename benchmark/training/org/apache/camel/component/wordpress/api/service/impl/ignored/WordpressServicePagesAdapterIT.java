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
package org.apache.camel.component.wordpress.api.service.impl.ignored;


import java.util.List;
import org.apache.camel.component.wordpress.api.model.Page;
import org.apache.camel.component.wordpress.api.model.PageSearchCriteria;
import org.apache.camel.component.wordpress.api.service.WordpressServicePages;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Not implemented yet")
public class WordpressServicePagesAdapterIT {
    private static WordpressServicePages servicePages;

    @Test
    public void testRetrieve() {
        final Page page = WordpressServicePagesAdapterIT.servicePages.retrieve(2, null, null);
        Assert.assertThat(page, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(page.getId(), CoreMatchers.is(2));
    }

    @Test
    public void testList() {
        final PageSearchCriteria criteria = new PageSearchCriteria();
        criteria.setPage(1);
        criteria.setPerPage(5);
        final List<Page> posts = WordpressServicePagesAdapterIT.servicePages.list(criteria);
        Assert.assertThat(posts, CoreMatchers.is(CoreMatchers.not(Matchers.emptyCollectionOf(Page.class))));
        Assert.assertThat(posts.size(), CoreMatchers.is(5));
    }
}

