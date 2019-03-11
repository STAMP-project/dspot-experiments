/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.cache.internal.dao.orm;


import PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD;
import com.liferay.portal.cache.key.HashCodeHexStringCacheKeyGenerator;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Preston Crary
 */
public class FinderCacheImplTest {
    @Test
    public void testNotifyPortalCacheRemovedPortalCacheName() {
        FinderCacheImpl finderCacheImpl = new FinderCacheImpl();
        finderCacheImpl.setMultiVMPool(((MultiVMPool) (ProxyUtil.newProxyInstance(FinderCacheImplTest._classLoader, new Class<?>[]{ MultiVMPool.class }, new MultiVMPoolInvocationHandler(FinderCacheImplTest._classLoader, true)))));
        finderCacheImpl.setProps(PropsTestUtil.setProps(FinderCacheImplTest._properties));
        finderCacheImpl.activate();
        PortalCache<Serializable, Serializable> portalCache = ReflectionTestUtil.invoke(finderCacheImpl, "_getPortalCache", new Class<?>[]{ String.class }, FinderCacheImplTest.class.getName());
        Map<String, PortalCache<Serializable, Serializable>> portalCaches = ReflectionTestUtil.getFieldValue(finderCacheImpl, "_portalCaches");
        Assert.assertEquals(portalCaches.toString(), 1, portalCaches.size());
        Assert.assertSame(portalCache, portalCaches.get(FinderCacheImplTest.class.getName()));
        finderCacheImpl.notifyPortalCacheRemoved(portalCache.getPortalCacheName());
        Assert.assertTrue(portalCaches.toString(), portalCaches.isEmpty());
    }

    @Test
    public void testPutEmptyListInvalid() {
        _assertPutEmptyListInvalid(FinderCacheImplTest._notSerializedMultiVMPool);
        _assertPutEmptyListInvalid(FinderCacheImplTest._serializedMultiVMPool);
    }

    @Test
    public void testPutEmptyListValid() {
        _assertPutEmptyListValid(FinderCacheImplTest._notSerializedMultiVMPool);
        _assertPutEmptyListValid(FinderCacheImplTest._serializedMultiVMPool);
    }

    @Test
    public void testTestKeysCollide() {
        Assert.assertEquals(FinderCacheImplTest._cacheKeyGenerator.getCacheKey(FinderCacheImplTest._KEY1), FinderCacheImplTest._cacheKeyGenerator.getCacheKey(FinderCacheImplTest._KEY2));
    }

    @Test
    public void testThreshold() {
        FinderCacheImplTest._properties.put(VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD, "2");
        FinderCache finderCache = _activateFinderCache(FinderCacheImplTest._notSerializedMultiVMPool);
        List<Serializable> values = new ArrayList<>();
        values.add("a");
        values.add("b");
        finderCache.putResult(_finderPath, FinderCacheImplTest._KEY1, values, true);
        Object result = finderCache.getResult(_finderPath, FinderCacheImplTest._KEY1, new FinderCacheImplTest.TestBasePersistence(new HashSet<>(values)));
        Assert.assertEquals(values, result);
        values.add("c");
        finderCache.putResult(_finderPath, FinderCacheImplTest._KEY1, values, true);
        result = finderCache.getResult(_finderPath, FinderCacheImplTest._KEY1, new FinderCacheImplTest.TestBasePersistence(null));
        Assert.assertNull(result);
    }

    private static final String[] _KEY1 = new String[]{ "home" };

    private static final String[] _KEY2 = new String[]{ "j1me" };

    private static final CacheKeyGenerator _cacheKeyGenerator = new HashCodeHexStringCacheKeyGenerator();

    private static final ClassLoader _classLoader = FinderCacheImplTest.class.getClassLoader();

    private static MultiVMPool _notSerializedMultiVMPool;

    private static Map<String, Object> _properties;

    private static MultiVMPool _serializedMultiVMPool;

    private FinderPath _finderPath;

    private static class TestBasePersistence<T extends BaseModel<T>> extends BasePersistenceImpl<T> {
        @Override
        public Map<Serializable, T> fetchByPrimaryKeys(Set<Serializable> primaryKeys) {
            Assert.assertNotNull(_keys);
            Assert.assertEquals(_keys, primaryKeys);
            Map map = new HashMap();
            for (Object key : _keys) {
                map.put(key, key);
            }
            return map;
        }

        private TestBasePersistence(Set<?> keys) {
            _keys = keys;
        }

        private final Set<?> _keys;
    }
}

