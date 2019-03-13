/**
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.util;


import com.github.dozermapper.core.functional_tests.runner.ProxyDataObjectInstantiator;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Test;


public class DefaultProxyResolverTest {
    private DefaultProxyResolver resolver;

    @Test
    public void testUnenhanceObject() {
        Object obj = new Object();
        Object result = resolver.unenhanceObject(obj);
        Assert.assertSame(obj, result);
    }

    @Test
    public void testGetRealClass() {
        Object proxy = ProxyDataObjectInstantiator.INSTANCE.newInstance(Calendar.class);
        Assert.assertFalse(proxy.getClass().equals(Calendar.class));
        Class<?> realClass = resolver.getRealClass(proxy.getClass());
        Assert.assertTrue(realClass.equals(Calendar.class));
    }
}

