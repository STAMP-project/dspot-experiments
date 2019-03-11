/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.apt.domain;


import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QuerySupertype;
import org.junit.Assert;
import org.junit.Test;

import static QDelegateTest_SimpleUser.simpleUser;
import static QDelegateTest_SimpleUser2.simpleUser2;
import static QDelegateTest_User.user;


public class DelegateTest {
    @QuerySupertype
    public static class Identifiable {
        long id;
    }

    @QueryEntity
    public static class User extends DelegateTest.Identifiable {
        String name;

        DelegateTest.User managedBy;
    }

    @QueryEntity
    public static class SimpleUser extends DelegateTest.User {}

    @QueryEntity
    public static class SimpleUser2 extends DelegateTest.SimpleUser {}

    @Test
    public void user() {
        QDelegateTest_User user = user;
        Assert.assertNotNull(user.isManagedBy(new DelegateTest.User()));
        Assert.assertNotNull(user.isManagedBy(user));
        Assert.assertNotNull(user.simpleMethod());
        Assert.assertEquals(user.name, user.getName());
    }

    @Test
    public void simpleUser() {
        QDelegateTest_SimpleUser user = simpleUser;
        Assert.assertNotNull(user.isManagedBy(new DelegateTest.User()));
        Assert.assertNotNull(user.isManagedBy(user._super));
        Assert.assertEquals(user.name, user.getName());
    }

    @Test
    public void simpleUser2() {
        QDelegateTest_SimpleUser2 user = simpleUser2;
        Assert.assertNotNull(user.isManagedBy(new DelegateTest.User()));
        Assert.assertNotNull(user.isManagedBy(user._super._super));
        Assert.assertEquals(user.name, user.getName());
    }
}

