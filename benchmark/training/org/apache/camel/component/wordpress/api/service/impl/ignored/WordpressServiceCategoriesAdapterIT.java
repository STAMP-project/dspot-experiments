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
import org.apache.camel.component.wordpress.api.model.Category;
import org.apache.camel.component.wordpress.api.model.CategorySearchCriteria;
import org.apache.camel.component.wordpress.api.service.WordpressServiceCategories;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Not implemented yet")
public class WordpressServiceCategoriesAdapterIT {
    private static WordpressServiceCategories serviceCategories;

    @Test
    public void testRetrieve() {
        final Category cat = WordpressServiceCategoriesAdapterIT.serviceCategories.retrieve(1, null);
        Assert.assertThat(cat, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(cat.getId(), CoreMatchers.is(1));
        Assert.assertThat(cat.getName(), CoreMatchers.not(Matchers.isEmptyOrNullString()));
    }

    @Test
    public void testList() {
        final CategorySearchCriteria criteria = new CategorySearchCriteria();
        criteria.setPage(1);
        criteria.setPerPage(2);
        final List<Category> revisions = WordpressServiceCategoriesAdapterIT.serviceCategories.list(criteria);
        Assert.assertThat(revisions, CoreMatchers.is(CoreMatchers.not(Matchers.emptyCollectionOf(Category.class))));
        Assert.assertThat(revisions.size(), CoreMatchers.is(2));
    }
}

