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


import MethodType.GETTER;
import MethodType.GET_MAPPED_PATH;
import MethodType.HASH_CODE;
import MethodType.LIST_ACCESS;
import MethodType.MAP_ACCESS;
import MethodType.SIZE;
import MethodType.TO_STRING;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MethodTypeTest {
    @Test
    public void get() throws NoSuchMethodException, SecurityException {
        Method getVal = MethodTypeTest.class.getMethod("getVal");
        Method hashCode = Object.class.getMethod("hashCode");
        Method size = Collection.class.getMethod("size");
        Method toString = Object.class.getMethod("toString");
        Assert.assertEquals(GET_MAPPED_PATH, MethodType.get(ManagedObject.class.getMethod("__mappedPath")));
        Assert.assertEquals(GETTER, MethodType.get(getVal));
        Assert.assertEquals(HASH_CODE, MethodType.get(hashCode));
        Assert.assertEquals(LIST_ACCESS, MethodType.get(List.class.getMethod("get", int.class)));
        Assert.assertEquals(MAP_ACCESS, MethodType.get(Map.class.getMethod("get", Object.class)));
        Assert.assertEquals(SIZE, MethodType.get(size));
        Assert.assertEquals(TO_STRING, MethodType.get(toString));
    }
}

