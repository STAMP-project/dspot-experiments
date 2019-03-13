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


import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.ProxyUtil;
import java.io.Serializable;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tina Tian
 */
public class EntityCacheImplTest {
    @Test
    public void testNotifyPortalCacheRemovedPortalCacheName() {
        EntityCacheImpl entityCacheImpl = new EntityCacheImpl();
        entityCacheImpl.setMultiVMPool(((MultiVMPool) (ProxyUtil.newProxyInstance(_classLoader, new Class<?>[]{ MultiVMPool.class }, new MultiVMPoolInvocationHandler(_classLoader, true)))));
        entityCacheImpl.setProps(_props);
        entityCacheImpl.activate();
        PortalCache<?, ?> portalCache = entityCacheImpl.getPortalCache(EntityCacheImplTest.class);
        Map<String, PortalCache<Serializable, Serializable>> portalCaches = ReflectionTestUtil.getFieldValue(entityCacheImpl, "_portalCaches");
        Assert.assertEquals(portalCaches.toString(), 1, portalCaches.size());
        Assert.assertSame(portalCache, portalCaches.get(EntityCacheImplTest.class.getName()));
        entityCacheImpl.notifyPortalCacheRemoved(portalCache.getPortalCacheName());
        Assert.assertTrue(portalCaches.toString(), portalCaches.isEmpty());
    }

    @Test
    public void testPutAndGetNullModel() throws Exception {
        _testPutAndGetNullModel(false);
        _testPutAndGetNullModel(true);
    }

    private ClassLoader _classLoader;

    private Serializable _nullModel;

    private Props _props;
}

