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
package com.querydsl.jpa;


import QAnimal.animal;
import QAnimal.animal.birthdate;
import QAnimal.animal.bodyWeight;
import com.google.common.collect.Maps;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Date;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class JPAQueryFactoryTest {
    private EntityManagerFactory factoryMock;

    private EntityManager mock;

    private JPAQueryFactory queryFactory;

    private JPQLQueryFactory queryFactory2;

    private JPAQueryFactory queryFactory3;

    private Map<String, Object> properties = Maps.newHashMap();

    @Test
    public void query() {
        Assert.assertNotNull(queryFactory.query());
    }

    @Test
    public void query2() {
        queryFactory2.query().from(animal);
    }

    @Test
    public void query3() {
        EasyMock.expect(mock.getEntityManagerFactory()).andReturn(factoryMock);
        EasyMock.expect(factoryMock.getProperties()).andReturn(properties);
        EasyMock.expect(mock.getDelegate()).andReturn(mock).atLeastOnce();
        EasyMock.replay(mock, factoryMock);
        queryFactory3.query().from(animal);
        EasyMock.verify(mock, factoryMock);
    }

    @Test
    public void from() {
        Assert.assertNotNull(queryFactory.from(animal));
    }

    @Test
    public void delete() {
        Assert.assertNotNull(queryFactory.delete(animal));
    }

    @Test
    public void delete2() {
        queryFactory2.delete(animal).where(bodyWeight.gt(0));
    }

    @Test
    public void delete3() {
        EasyMock.expect(mock.getEntityManagerFactory()).andReturn(factoryMock);
        EasyMock.expect(factoryMock.getProperties()).andReturn(properties);
        EasyMock.expect(mock.getDelegate()).andReturn(mock).atLeastOnce();
        EasyMock.replay(mock, factoryMock);
        Assert.assertNotNull(queryFactory3.delete(animal));
        EasyMock.verify(mock, factoryMock);
    }

    @Test
    public void update() {
        Assert.assertNotNull(queryFactory.update(animal));
    }

    @Test
    public void update2() {
        queryFactory2.update(animal).set(birthdate, new Date()).where(birthdate.isNull());
    }

    @Test
    public void update3() {
        EasyMock.expect(mock.getEntityManagerFactory()).andReturn(factoryMock);
        EasyMock.expect(factoryMock.getProperties()).andReturn(properties);
        EasyMock.expect(mock.getDelegate()).andReturn(mock).atLeastOnce();
        EasyMock.replay(mock, factoryMock);
        Assert.assertNotNull(queryFactory3.update(animal));
        EasyMock.verify(mock, factoryMock);
    }
}

