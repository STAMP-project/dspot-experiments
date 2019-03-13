/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.web.filter.mgt;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.filter.authz.PortFilter;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.filter.authz.SslFilter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for the {@link SimpleNamedFilterList} implementation.
 *
 * @since 1.0
 */
public class SimpleNamedFilterListTest {
    @Test
    public void testNewInstance() {
        @SuppressWarnings({ "MismatchedQueryAndUpdateOfCollection" })
        SimpleNamedFilterList list = new SimpleNamedFilterList("test");
        Assert.assertNotNull(list.getName());
        Assert.assertEquals("test", list.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewInstanceNameless() {
        new SimpleNamedFilterList(null);
    }

    @Test
    public void testNewInstanceBackingList() {
        new SimpleNamedFilterList("test", new ArrayList<Filter>());
    }

    @Test(expected = NullPointerException.class)
    public void testNewInstanceNullBackingList() {
        new SimpleNamedFilterList("test", null);
    }

    /**
     * Exists mainly to increase code coverage as the SimpleNamedFilterList
     * implementation is a direct pass through.
     */
    @Test
    public void testListMethods() {
        FilterChain mock = createNiceMock(FilterChain.class);
        Filter filter = createNiceMock(Filter.class);
        NamedFilterList list = new SimpleNamedFilterList("test");
        list.add(filter);
        FilterChain chain = list.proxy(mock);
        Assert.assertNotNull(chain);
        Assert.assertNotSame(mock, chain);
        Filter singleFilter = new SslFilter();
        List<? extends Filter> multipleFilters = CollectionUtils.asList(new PortFilter(), new UserFilter());
        list.add(0, singleFilter);
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(((list.get(0)) instanceof SslFilter));
        Assert.assertTrue(Arrays.equals(list.toArray(), new Object[]{ singleFilter, filter }));
        list.addAll(multipleFilters);
        Assert.assertEquals(4, list.size());
        Assert.assertTrue(((list.get(2)) instanceof PortFilter));
        Assert.assertTrue(((list.get(3)) instanceof UserFilter));
        list.addAll(0, CollectionUtils.asList(new PermissionsAuthorizationFilter(), new RolesAuthorizationFilter()));
        Assert.assertEquals(6, list.size());
        Assert.assertTrue(((list.get(0)) instanceof PermissionsAuthorizationFilter));
        Assert.assertTrue(((list.get(1)) instanceof RolesAuthorizationFilter));
        Assert.assertEquals(2, list.indexOf(singleFilter));
        Assert.assertEquals(multipleFilters, list.subList(4, list.size()));
        Assert.assertTrue(list.contains(singleFilter));
        Assert.assertTrue(list.containsAll(multipleFilters));
        Assert.assertFalse(list.isEmpty());
        list.clear();
        Assert.assertTrue(list.isEmpty());
        list.add(singleFilter);
        Iterator i = list.iterator();
        Assert.assertTrue(i.hasNext());
        Assert.assertEquals(i.next(), singleFilter);
        ListIterator li = list.listIterator();
        Assert.assertTrue(li.hasNext());
        Assert.assertEquals(li.next(), singleFilter);
        li = list.listIterator(0);
        Assert.assertTrue(li.hasNext());
        Assert.assertEquals(li.next(), singleFilter);
        list.set(0, singleFilter);
        Assert.assertEquals(list.get(0), singleFilter);
        Filter[] filters = new Filter[list.size()];
        filters = list.toArray(filters);
        Assert.assertEquals(1, filters.length);
        Assert.assertEquals(filters[0], singleFilter);
        Assert.assertEquals(0, list.lastIndexOf(singleFilter));
        list.remove(singleFilter);
        Assert.assertTrue(list.isEmpty());
        list.add(singleFilter);
        list.remove(0);
        Assert.assertTrue(list.isEmpty());
        list.add(singleFilter);
        list.addAll(multipleFilters);
        Assert.assertEquals(3, list.size());
        list.removeAll(multipleFilters);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(list.get(0), singleFilter);
        list.addAll(multipleFilters);
        Assert.assertEquals(3, list.size());
        list.retainAll(multipleFilters);
        Assert.assertEquals(2, list.size());
        // noinspection unchecked
        Assert.assertEquals(new ArrayList(list), multipleFilters);
    }
}

