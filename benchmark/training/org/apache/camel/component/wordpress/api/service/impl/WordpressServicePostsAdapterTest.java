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
package org.apache.camel.component.wordpress.api.service.impl;


import Format.standard;
import java.util.List;
import org.apache.camel.component.wordpress.api.model.Content;
import org.apache.camel.component.wordpress.api.model.Post;
import org.apache.camel.component.wordpress.api.model.PostSearchCriteria;
import org.apache.camel.component.wordpress.api.service.WordpressServicePosts;
import org.apache.camel.component.wordpress.api.test.WordpressMockServerTestSupport;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class WordpressServicePostsAdapterTest extends WordpressMockServerTestSupport {
    private static WordpressServicePosts servicePosts;

    @Test
    public void testRetrievePost() {
        final Post post = WordpressServicePostsAdapterTest.servicePosts.retrieve(1);
        Assert.assertThat(post, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(post.getId(), CoreMatchers.is(Matchers.greaterThan(0)));
    }

    @Test
    public void testCreatePost() {
        final Post entity = new Post();
        entity.setAuthor(2);
        entity.setTitle(new Content("hello from postman 2"));
        entity.setContent(new Content("hello world 2"));
        entity.setFormat(standard);
        final Post post = WordpressServicePostsAdapterTest.servicePosts.create(entity);
        Assert.assertThat(post, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(post.getId(), CoreMatchers.is(9));
    }

    @Test
    public void testListPosts() {
        final PostSearchCriteria criteria = new PostSearchCriteria();
        criteria.setPage(1);
        criteria.setPerPage(10);
        final List<Post> posts = WordpressServicePostsAdapterTest.servicePosts.list(criteria);
        Assert.assertThat(posts, CoreMatchers.is(CoreMatchers.not(Matchers.emptyCollectionOf(Post.class))));
        Assert.assertThat(posts.size(), CoreMatchers.is(10));
    }
}

