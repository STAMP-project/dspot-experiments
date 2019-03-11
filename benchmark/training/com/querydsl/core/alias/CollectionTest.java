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


import com.querydsl.core.types.EntityPath;
import org.junit.Assert;
import org.junit.Test;


public class CollectionTest {
    @Test
    public void collectionUsage() {
        DomainType domainType = Alias.alias(DomainType.class);
        Assert.assertEquals("any(domainType.collection) = domainType", Alias.$(domainType.getCollection()).any().eq(domainType).toString());
        Assert.assertEquals("any(domainType.set) = domainType", Alias.$(domainType.getSet()).any().eq(domainType).toString());
        Assert.assertEquals("any(domainType.list) = domainType", Alias.$(domainType.getList()).any().eq(domainType).toString());
        Assert.assertEquals("domainType.list.get(0) = domainType", Alias.$(domainType.getList().get(0)).eq(domainType).toString());
        Assert.assertEquals("domainType.list.get(0) = domainType", Alias.$(domainType.getList()).get(0).eq(domainType).toString());
        Assert.assertEquals("domainType.map.get(key) = domainType", Alias.$(domainType.getMap()).get("key").eq(domainType).toString());
        EntityPath<DomainType> domainTypePath = Alias.$(domainType);
        Assert.assertEquals("domainType in domainType.collection", Alias.$(domainType.getCollection()).contains(domainTypePath).toString());
    }

    @Test
    public void collectionUsage_types() {
        DomainType domainType = Alias.alias(DomainType.class);
        Assert.assertEquals(DomainType.class, Alias.$(domainType.getCollection()).any().getType());
        Assert.assertEquals(DomainType.class, Alias.$(domainType.getSet()).any().getType());
        Assert.assertEquals(DomainType.class, Alias.$(domainType.getList()).any().getType());
        Assert.assertEquals(DomainType.class, Alias.$(domainType.getMap()).get("key").getType());
    }
}

