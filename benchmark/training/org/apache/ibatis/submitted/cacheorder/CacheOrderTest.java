/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.submitted.cacheorder;


import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;


public class CacheOrderTest {
    private static SqlSessionFactory sqlSessionFactory;

    @Test
    public void shouldResolveACacheRefNotYetRead() {
        MappedStatement ms = CacheOrderTest.sqlSessionFactory.getConfiguration().getMappedStatement("getUser");
        Cache cache = ms.getCache();
        Assert.assertEquals("org.apache.ibatis.submitted.cacheorder.Mapper2", cache.getId());
    }
}

