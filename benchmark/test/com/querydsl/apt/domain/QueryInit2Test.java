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


import QQueryInit2Test_Categorization.categorization.event;
import QQueryInit2Test_Categorization.categorization.event.account.owner;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryInit;
import org.junit.Assert;
import org.junit.Test;


public class QueryInit2Test {
    @QueryEntity
    public static class Categorization {
        @QueryInit("account.owner")
        QueryInit2Test.Event event;
    }

    @QueryEntity
    public static class Event {
        QueryInit2Test.Account account;
    }

    @QueryEntity
    public static class Activation extends QueryInit2Test.Event {}

    @QueryEntity
    public static class Account {
        QueryInit2Test.Owner owner;
    }

    @QueryEntity
    public static class Owner {}

    @Test
    public void long_path() {
        Assert.assertNotNull(owner);
        Assert.assertNotNull(event.as(QQueryInit2Test_Activation.class).account.owner);
    }
}

