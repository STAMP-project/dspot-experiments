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
package com.querydsl.mongodb.domain;


import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mongodb.morphia.Morphia;


public class UserTest {
    private static final Morphia morphia = new Morphia().map(User.class);

    @Test
    public void map() {
        City tampere = new City("Tampere", 61.3, 23.5);
        User user = new User();
        user.setAge(12);
        user.setFirstName("Jaakko");
        user.addAddress("Aakatu", "00300", tampere);
        Assert.assertNotNull(UserTest.morphia.toDBObject(user));
    }

    @Test
    public void friend() {
        User friend = new User();
        friend.setId(ObjectId.createFromLegacyFormat(1, 2, 3));
        User user = new User();
        user.setFriend(friend);
        Assert.assertNotNull(UserTest.morphia.toDBObject(user));
    }

    @Test
    public void friends() {
        User friend = new User();
        friend.setId(ObjectId.createFromLegacyFormat(1, 2, 3));
        User user = new User();
        user.addFriend(friend);
        Assert.assertNotNull(UserTest.morphia.toDBObject(user));
    }
}

