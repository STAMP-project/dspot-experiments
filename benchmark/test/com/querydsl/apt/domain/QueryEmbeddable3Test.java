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


import QQueryEmbeddable3Test_User.user.collection;
import QQueryEmbeddable3Test_User.user.list;
import QQueryEmbeddable3Test_User.user.map;
import QQueryEmbeddable3Test_User.user.rawList;
import QQueryEmbeddable3Test_User.user.rawMap1;
import QQueryEmbeddable3Test_User.user.rawMap2;
import QQueryEmbeddable3Test_User.user.set;
import com.querydsl.core.annotations.QueryEmbeddable;
import com.querydsl.core.annotations.QueryEntity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class QueryEmbeddable3Test {
    @QueryEntity
    public static class User {
        List<QueryEmbeddable3Test.Complex<?>> rawList;

        List<QueryEmbeddable3Test.Complex<String>> list;

        Set<QueryEmbeddable3Test.Complex<String>> set;

        Collection<QueryEmbeddable3Test.Complex<String>> collection;

        Map<String, QueryEmbeddable3Test.Complex<String>> map;

        Map<String, QueryEmbeddable3Test.Complex<String>> rawMap1;

        Map<String, QueryEmbeddable3Test.Complex<?>> rawMap2;

        Map<?, QueryEmbeddable3Test.Complex<String>> rawMap3;
    }

    @QueryEmbeddable
    public static class Complex<T extends Comparable<T>> implements Comparable<QueryEmbeddable3Test.Complex<T>> {
        T a;

        @Override
        public int compareTo(QueryEmbeddable3Test.Complex<T> arg0) {
            return 0;
        }

        public boolean equals(Object o) {
            return o == (this);
        }
    }

    @Test
    public void user_rawList() {
        Assert.assertEquals(QQueryEmbeddable3Test_Complex.class, rawList.any().getClass());
    }

    @Test
    public void user_list() {
        Assert.assertEquals(QQueryEmbeddable3Test_Complex.class, list.any().getClass());
    }

    @Test
    public void user_set() {
        Assert.assertEquals(QQueryEmbeddable3Test_Complex.class, set.any().getClass());
    }

    @Test
    public void user_collection() {
        Assert.assertEquals(QQueryEmbeddable3Test_Complex.class, collection.any().getClass());
    }

    @Test
    public void user_map() {
        Assert.assertEquals(QQueryEmbeddable3Test_Complex.class, map.get("XXX").getClass());
    }

    @Test
    public void user_rawMap1() {
        Assert.assertEquals(QQueryEmbeddable3Test_Complex.class, rawMap1.get("XXX").getClass());
    }

    @Test
    public void user_rawMap2() {
        Assert.assertEquals(QQueryEmbeddable3Test_Complex.class, rawMap2.get("XXX").getClass());
    }
}

