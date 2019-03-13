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
package com.querydsl.core.alias;


import com.querydsl.core.types.Path;
import org.junit.Assert;
import org.junit.Test;


public class AliasTest {
    @Test
    public void alias() {
        DomainType domainType = Alias.alias(DomainType.class);
        Alias.alias(DomainType.class, Alias.$(domainType.getCollection()).any());
    }

    @Test
    public void comparableEntity() {
        ComparableEntity entity = Alias.alias(ComparableEntity.class);
        Path<ComparableEntity> path = Alias.$(entity);
        Assert.assertEquals(ComparableEntity.class, path.getType());
    }

    @Test
    public void comparableEntity_property() {
        ComparableEntity entity = Alias.alias(ComparableEntity.class);
        Path<String> propertyPath = Alias.$(entity.getProperty());
        Assert.assertEquals(String.class, propertyPath.getType());
        Assert.assertEquals("property", propertyPath.getMetadata().getName());
    }

    @Test
    public void basicUsage() {
        DomainType domainType = Alias.alias(DomainType.class);
        Assert.assertEquals("lower(domainType.firstName)", Alias.$(domainType.getFirstName()).lower().toString());
        Assert.assertEquals("domainType.age", Alias.$(domainType.getAge()).toString());
        Assert.assertEquals("domainType.map.get(a)", Alias.$(domainType.getMap().get("a")).toString());
        Assert.assertEquals("domainType.list.get(0)", Alias.$(domainType.getList().get(0)).toString());
        Assert.assertEquals("domainType.bigDecimal", Alias.$(domainType.getBigDecimal()).toString());
        Assert.assertEquals("domainType.bigInteger", Alias.$(domainType.getBigInteger()).toString());
        Assert.assertEquals("domainType.byte", Alias.$(domainType.getByte()).toString());
        Assert.assertEquals("domainType.collection", Alias.$(domainType.getCollection()).toString());
        Assert.assertEquals("domainType.double", Alias.$(domainType.getDouble()).toString());
        Assert.assertEquals("domainType.float", Alias.$(domainType.getFloat()).toString());
        Assert.assertEquals("domainType.date", Alias.$(domainType.getDate()).toString());
        Assert.assertEquals("domainType.date2", Alias.$(domainType.getDate2()).toString());
        Assert.assertEquals("domainType.set", Alias.$(domainType.getSet()).toString());
        Assert.assertEquals("domainType.short", Alias.$(domainType.getShort()).toString());
        Assert.assertEquals("domainType.time", Alias.$(domainType.getTime()).toString());
        Assert.assertEquals("domainType.timestamp", Alias.$(domainType.getTimestamp()).toString());
        Assert.assertEquals("domainType.gender", Alias.$(domainType.getGender()).toString());
    }

    @Test
    public void getAny() {
        DomainType domainType = Alias.alias(DomainType.class);
        Assert.assertEquals(DomainType.class, Alias.getAny(domainType).getType());
        Assert.assertEquals(String.class, Alias.getAny(domainType.getFirstName()).getType());
    }

    @Test
    public void otherMethods() {
        DomainType domainType = Alias.alias(DomainType.class);
        Assert.assertEquals("domainType", domainType.toString());
    }

    @Test
    public void var() {
        Assert.assertEquals("it", Alias.var().toString());
        Assert.assertEquals("varInteger1", Alias.var(1).toString());
        Assert.assertEquals("X", Alias.var("X").toString());
        Assert.assertEquals("varMALE", Alias.var(Gender.MALE).toString());
        Assert.assertEquals("varAliasTest_XXX", Alias.var(new AliasTest()).toString());
    }
}

