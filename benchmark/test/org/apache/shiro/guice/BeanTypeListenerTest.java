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
package org.apache.shiro.guice;


import BeanTypeListener.MAP_KEY;
import BeanTypeListener.MATCHER;
import com.google.inject.name.Names;
import com.google.inject.spi.Message;
import com.google.inject.spi.TypeEncounter;
import java.util.Collections;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.aop.DefaultAnnotationResolver;
import org.apache.shiro.crypto.BlowfishCipherService;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test Cases::
 * Test package matching
 * injects on classes in shiro package and sub packages
 * excludes classes in shiro-guice package and sub packages
 * Test that properties are set properly
 * ensure optional
 * ensure property names are correct
 * ensure "named" properties require a name, and unnamed do not
 */
public class BeanTypeListenerTest {
    @Test
    public void testUnmatchedPackage() throws Exception {
        Assert.assertFalse(MATCHER.matches(TypeLiteral.get(GuiceEnvironment.class)));
        Assert.assertFalse(MATCHER.matches(TypeLiteral.get(ShiroWebModule.class)));
        Assert.assertFalse(MATCHER.matches(TypeLiteral.get(ShiroAopModule.class)));
    }

    @Test
    public void testMatchedPackage() throws Exception {
        Assert.assertTrue(MATCHER.matches(TypeLiteral.get(SecurityUtils.class)));
        Assert.assertTrue(MATCHER.matches(TypeLiteral.get(DefaultAnnotationResolver.class)));
        Assert.assertTrue(MATCHER.matches(TypeLiteral.get(BlowfishCipherService.class)));
    }

    @Test
    public void testPropertySetting() throws Exception {
        IMocksControl control = createControl();
        TypeEncounter<BeanTypeListenerTest.SomeInjectableBean> encounter = control.createMock(TypeEncounter.class);
        Provider<Injector> injectorProvider = control.createMock(Provider.class);
        Injector injector = control.createMock(Injector.class);
        expect(encounter.getProvider(Injector.class)).andReturn(injectorProvider);
        expect(injectorProvider.get()).andReturn(injector).anyTimes();
        Capture<MembersInjector<BeanTypeListenerTest.SomeInjectableBean>> capture = new Capture<MembersInjector<BeanTypeListenerTest.SomeInjectableBean>>();
        encounter.register(and(anyObject(MembersInjector.class), capture(capture)));
        SecurityManager securityManager = control.createMock(SecurityManager.class);
        String property = "myPropertyValue";
        expect(injector.getInstance(Key.get(SecurityManager.class))).andReturn(securityManager);
        expect(injector.getInstance(Key.get(String.class, Names.named("shiro.myProperty")))).andReturn(property);
        expect(injector.getInstance(Key.get(String.class, Names.named("shiro.unavailableProperty")))).andThrow(new ConfigurationException(Collections.singleton(new Message("Not Available!"))));
        expect(((java.util.Map) (injector.getInstance(MAP_KEY)))).andReturn(Collections.EMPTY_MAP).anyTimes();
        control.replay();
        BeanTypeListener underTest = new BeanTypeListener();
        underTest.hear(TypeLiteral.get(BeanTypeListenerTest.SomeInjectableBean.class), encounter);
        BeanTypeListenerTest.SomeInjectableBean bean = new BeanTypeListenerTest.SomeInjectableBean();
        capture.getValue().injectMembers(bean);
        Assert.assertSame(securityManager, bean.securityManager);
        Assert.assertSame(property, bean.myProperty);
        Assert.assertNull(bean.unavailableProperty);
        control.verify();
    }

    public static class SomeInjectableBean {
        private SecurityManager securityManager;

        private String myProperty;

        private String unavailableProperty;

        public void setSecurityManager(SecurityManager securityManager) {
            this.securityManager = securityManager;
        }

        public void setMyProperty(String myProperty) {
            this.myProperty = myProperty;
        }

        public void setUnavailableProperty(String unavailableProperty) {
            this.unavailableProperty = unavailableProperty;
        }
    }
}

