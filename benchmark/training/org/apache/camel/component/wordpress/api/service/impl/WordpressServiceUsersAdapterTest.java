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


import java.util.List;
import org.apache.camel.component.wordpress.api.model.User;
import org.apache.camel.component.wordpress.api.model.UserSearchCriteria;
import org.apache.camel.component.wordpress.api.service.WordpressServiceUsers;
import org.apache.camel.component.wordpress.api.test.WordpressMockServerTestSupport;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class WordpressServiceUsersAdapterTest extends WordpressMockServerTestSupport {
    private static WordpressServiceUsers serviceUsers;

    @Test
    public void testRetrieveUser() {
        final User user = WordpressServiceUsersAdapterTest.serviceUsers.retrieve(1);
        Assert.assertThat(user, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(user.getId(), CoreMatchers.is(Matchers.greaterThan(0)));
    }

    @Test
    public void testCreateUser() {
        final User entity = new User();
        entity.setEmail("bill.denbrough@derry.com");
        entity.setFirstName("Bill");
        entity.setLastName("Denbrough");
        entity.setNickname("Big Bill");
        entity.setUsername("bdenbrough");
        final User user = WordpressServiceUsersAdapterTest.serviceUsers.create(entity);
        Assert.assertThat(user, CoreMatchers.not(CoreMatchers.nullValue()));
        Assert.assertThat(user.getId(), CoreMatchers.is(3));
    }

    @Test
    public void testListUsers() {
        final UserSearchCriteria criteria = new UserSearchCriteria();
        criteria.setPage(1);
        criteria.setPerPage(10);
        final List<User> users = WordpressServiceUsersAdapterTest.serviceUsers.list(criteria);
        Assert.assertThat(users, CoreMatchers.is(CoreMatchers.not(Matchers.emptyCollectionOf(User.class))));
        Assert.assertThat(users.size(), CoreMatchers.is(3));
    }
}

