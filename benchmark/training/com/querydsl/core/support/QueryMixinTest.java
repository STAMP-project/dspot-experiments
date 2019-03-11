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
package com.querydsl.core.support;


import com.querydsl.core.JoinExpression;
import com.querydsl.core.alias.Alias;
import com.querydsl.core.domain.QCommonPersistence;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Assert;
import org.junit.Test;


public class QueryMixinTest {
    private QueryMixin<?> mixin = new QueryMixin<Void>();

    private QCommonPersistence entity = new QCommonPersistence(PathMetadataFactory.forVariable("entity"));

    @Test
    public void where_null() {
        mixin.where(((Predicate) (null)));
    }

    @Test
    public void getJoins_with_condition() {
        mixin.innerJoin(entity);
        mixin.on(entity.version.isNull(), entity.version.isNotNull());
        Assert.assertEquals(1, mixin.getMetadata().getJoins().size());
        JoinExpression je = mixin.getMetadata().getJoins().get(0);
        Assert.assertEquals(entity, je.getTarget());
        Assert.assertEquals(Expressions.allOf(entity.version.isNull(), entity.version.isNotNull()), je.getCondition());
    }

    @Test
    public void getJoins_no_condition() {
        mixin.innerJoin(entity);
        Assert.assertEquals(1, mixin.getMetadata().getJoins().size());
        JoinExpression je = mixin.getMetadata().getJoins().get(0);
        Assert.assertEquals(entity, je.getTarget());
        Assert.assertNull(je.getCondition());
    }

    @Test
    public void innerJoin() {
        DummyEntity e = Alias.alias(DummyEntity.class);
        DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
        DummyEntity e3 = Alias.alias(DummyEntity.class, "e3");
        DummyEntity e4 = Alias.alias(DummyEntity.class, "e4");
        // inner join
        mixin.innerJoin(Alias.$(e));
        mixin.innerJoin(Alias.$(e.getOther()), Alias.$(e2));
        mixin.innerJoin(Alias.$(e.getList()), Alias.$(e3));
        mixin.innerJoin(Alias.$(e.getList()));
        mixin.innerJoin(Alias.$(e.getMap()), Alias.$(e4));
        mixin.innerJoin(Alias.$(e.getMap()));
        Assert.assertEquals(6, mixin.getMetadata().getJoins().size());
    }

    @Test
    public void join() {
        DummyEntity e = Alias.alias(DummyEntity.class);
        DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
        DummyEntity e3 = Alias.alias(DummyEntity.class, "e3");
        DummyEntity e4 = Alias.alias(DummyEntity.class, "e4");
        // inner join
        mixin.innerJoin(Alias.$(e));
        mixin.innerJoin(Alias.$(e.getOther()), Alias.$(e2));
        mixin.innerJoin(Alias.$(e.getList()), Alias.$(e3));
        mixin.innerJoin(Alias.$(e.getList()));
        mixin.innerJoin(Alias.$(e.getMap()), Alias.$(e4));
        mixin.innerJoin(Alias.$(e.getMap()));
        Assert.assertEquals(6, mixin.getMetadata().getJoins().size());
    }

    @Test
    public void joins() {
        DummyEntity e = Alias.alias(DummyEntity.class);
        DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
        mixin.join(Alias.$(e));
        mixin.on(Alias.$(e).isNotNull());
        mixin.join(Alias.$(e.getOther()), Alias.$(e2));
        mixin.on(Alias.$(e).isNotNull());
        Assert.assertEquals(2, mixin.getMetadata().getJoins().size());
    }

    @Test
    public void leftJoin() {
        DummyEntity e = Alias.alias(DummyEntity.class);
        DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
        DummyEntity e3 = Alias.alias(DummyEntity.class, "e3");
        DummyEntity e4 = Alias.alias(DummyEntity.class, "e4");
        // left join
        mixin.leftJoin(Alias.$(e));
        mixin.leftJoin(Alias.$(e.getOther()), Alias.$(e2));
        mixin.leftJoin(Alias.$(e.getList()), Alias.$(e3));
        mixin.leftJoin(Alias.$(e.getList()));
        mixin.leftJoin(Alias.$(e.getMap()), Alias.$(e4));
        mixin.leftJoin(Alias.$(e.getMap()));
        Assert.assertEquals(6, mixin.getMetadata().getJoins().size());
    }

    @Test
    public void rightJoin() {
        DummyEntity e = Alias.alias(DummyEntity.class);
        DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
        DummyEntity e3 = Alias.alias(DummyEntity.class, "e3");
        DummyEntity e4 = Alias.alias(DummyEntity.class, "e4");
        // right join
        mixin.rightJoin(Alias.$(e));
        mixin.rightJoin(Alias.$(e.getOther()), Alias.$(e2));
        mixin.rightJoin(Alias.$(e.getList()), Alias.$(e3));
        mixin.rightJoin(Alias.$(e.getList()));
        mixin.rightJoin(Alias.$(e.getMap()), Alias.$(e4));
        mixin.rightJoin(Alias.$(e.getMap()));
        Assert.assertEquals(6, mixin.getMetadata().getJoins().size());
    }

    @Test
    public void fullJoin() {
        DummyEntity e = Alias.alias(DummyEntity.class);
        DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
        DummyEntity e3 = Alias.alias(DummyEntity.class, "e3");
        DummyEntity e4 = Alias.alias(DummyEntity.class, "e4");
        // full join
        mixin.fullJoin(Alias.$(e));
        mixin.fullJoin(Alias.$(e.getOther()), Alias.$(e2));
        mixin.fullJoin(Alias.$(e.getList()), Alias.$(e3));
        mixin.fullJoin(Alias.$(e.getList()));
        mixin.fullJoin(Alias.$(e.getMap()), Alias.$(e4));
        mixin.fullJoin(Alias.$(e.getMap()));
        Assert.assertEquals(6, mixin.getMetadata().getJoins().size());
    }
}

