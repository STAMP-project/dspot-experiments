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


import QQueryEmbedded5Test_User.user.collection;
import QQueryEmbedded5Test_User.user.list;
import QQueryEmbedded5Test_User.user.map;
import QQueryEmbedded5Test_User.user.rawList;
import QQueryEmbedded5Test_User.user.rawMap1;
import QQueryEmbedded5Test_User.user.rawMap2;
import QQueryEmbedded5Test_User.user.rawMap3;
import QQueryEmbedded5Test_User.user.set;
import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryEntity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class QueryEmbedded5Test {
    @QueryEntity
    public static class User {
        @QueryEmbedded
        List<QueryEmbedded5Test.Complex<?>> rawList;

        @QueryEmbedded
        List<QueryEmbedded5Test.Complex<String>> list;

        @QueryEmbedded
        Set<QueryEmbedded5Test.Complex<String>> set;

        @QueryEmbedded
        Collection<QueryEmbedded5Test.Complex<String>> collection;

        @QueryEmbedded
        Map<String, QueryEmbedded5Test.Complex<String>> map;

        @QueryEmbedded
        Map<String, QueryEmbedded5Test.Complex<String>> rawMap1;

        @QueryEmbedded
        Map<String, QueryEmbedded5Test.Complex<?>> rawMap2;

        @QueryEmbedded
        Map<?, QueryEmbedded5Test.Complex<String>> rawMap3;
    }

    public static class Complex<T extends Comparable<T>> implements Comparable<QueryEmbedded5Test.Complex<T>> {
        T a;

        @Override
        public int compareTo(QueryEmbedded5Test.Complex<T> arg0) {
            return 0;
        }

        public boolean equals(Object o) {
            return o == (this);
        }
    }

    @Test
    public void user_rawList() {
        Assert.assertEquals(QQueryEmbedded5Test_Complex.class, rawList.any().getClass());
    }

    @Test
    public void user_list() {
        Assert.assertEquals(QQueryEmbedded5Test_Complex.class, list.any().getClass());
    }

    @Test
    public void user_set() {
        Assert.assertEquals(QQueryEmbedded5Test_Complex.class, set.any().getClass());
    }

    @Test
    public void user_collection() {
        Assert.assertEquals(QQueryEmbedded5Test_Complex.class, collection.any().getClass());
    }

    @Test
    public void user_map() {
        Assert.assertEquals(QQueryEmbedded5Test_Complex.class, map.get("XXX").getClass());
    }

    @Test
    public void user_rawMap1() {
        Assert.assertEquals(QQueryEmbedded5Test_Complex.class, rawMap1.get("XXX").getClass());
    }

    @Test
    public void user_rawMap2() {
        Assert.assertEquals(QQueryEmbedded5Test_Complex.class, rawMap2.get("XXX").getClass());
    }

    @Test
    public void user_rawMap3() {
        Assert.assertEquals(QQueryEmbedded5Test_Complex.class, rawMap3.get("XXX").getClass());
    }
}

