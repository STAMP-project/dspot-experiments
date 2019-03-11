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


import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.binder.AnnotatedBindingBuilder;
import java.util.Collection;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.env.Environment;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.EventBusAware;
import org.apache.shiro.event.Subscribe;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Destroyable;
import org.junit.Assert;
import org.junit.Test;


public class ShiroModuleTest {
    @Test
    public void basicInstantiation() {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        Injector injector = Guice.createInjector(new ShiroModule() {
            @Override
            protected void configureShiro() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }
        });
        SecurityManager securityManager = injector.getInstance(SecurityManager.class);
        Assert.assertNotNull(securityManager);
    }

    @Test
    public void testConfigure() {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        AuthenticationToken authToken = createMock(AuthenticationToken.class);
        AuthenticationInfo info = new SimpleAuthenticationInfo("mockUser", "password", "mockRealm");
        expect(mockRealm.supports(authToken)).andReturn(true);
        expect(mockRealm.getAuthenticationInfo(authToken)).andReturn(info);
        replay(mockRealm);
        Injector injector = Guice.createInjector(new ShiroModule() {
            @Override
            protected void configureShiro() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }
        });
        SecurityManager securityManager = injector.getInstance(SecurityManager.class);
        Assert.assertNotNull(securityManager);
        SecurityUtils.setSecurityManager(securityManager);
        final Subject subject = new Subject.Builder(securityManager).buildSubject();
        securityManager.login(subject, authToken);
        verify(mockRealm);
    }

    @Test
    public void testBindSecurityManager() {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        Injector injector = Guice.createInjector(new ShiroModule() {
            @Override
            protected void configureShiro() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }

            @Override
            protected void bindSecurityManager(AnnotatedBindingBuilder<? super SecurityManager> bind) {
                bind.to(ShiroModuleTest.MyDefaultSecurityManager.class);
            }
        });
        SecurityManager securityManager = injector.getInstance(SecurityManager.class);
        Assert.assertNotNull(securityManager);
        Assert.assertTrue((securityManager instanceof ShiroModuleTest.MyDefaultSecurityManager));
    }

    @Test
    public void testBindSessionManager() {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        Injector injector = Guice.createInjector(new ShiroModule() {
            @Override
            protected void configureShiro() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }

            @Override
            protected void bindSessionManager(AnnotatedBindingBuilder<SessionManager> bind) {
                bind.to(ShiroModuleTest.MyDefaultSessionManager.class);
            }
        });
        DefaultSecurityManager securityManager = ((DefaultSecurityManager) (injector.getInstance(SecurityManager.class)));
        Assert.assertNotNull(securityManager);
        Assert.assertNotNull(securityManager.getSessionManager());
        Assert.assertTrue(((securityManager.getSessionManager()) instanceof ShiroModuleTest.MyDefaultSessionManager));
    }

    @Test
    public void testBindEnvironment() {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        Injector injector = Guice.createInjector(new ShiroModule() {
            @Override
            protected void configureShiro() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
                expose(Environment.class);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }

            @Override
            protected void bindEnvironment(AnnotatedBindingBuilder<Environment> bind) {
                bind.to(ShiroModuleTest.MyEnvironment.class);
            }
        });
        Environment environment = injector.getInstance(Environment.class);
        Assert.assertNotNull(environment);
        Assert.assertTrue((environment instanceof ShiroModuleTest.MyEnvironment));
    }

    @Test
    public void testDestroy() throws Exception {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        final ShiroModuleTest.MyDestroyable myDestroyable = createMock(ShiroModuleTest.MyDestroyable.class);
        destroy();
        replay(myDestroyable);
        final ShiroModule shiroModule = new ShiroModule() {
            @Override
            protected void configureShiro() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
                bind(ShiroModuleTest.MyDestroyable.class).toInstance(myDestroyable);
                expose(ShiroModuleTest.MyDestroyable.class);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }
        };
        Injector injector = Guice.createInjector(shiroModule);
        injector.getInstance(ShiroModuleTest.MyDestroyable.class);
        shiroModule.destroy();
        verify(myDestroyable);
    }

    /**
     *
     *
     * @since 1.4
     * @throws Exception
     * 		
     */
    @Test
    public void testEventListener() throws Exception {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        final EventBus eventBus = createMock(EventBus.class);
        // expect both objects to be registered
        eventBus.register(anyObject(ShiroModuleTest.MockEventListener1.class));
        eventBus.register(anyObject(ShiroModuleTest.MockEventListener2.class));
        replay(eventBus);
        final ShiroModule shiroModule = new ShiroModule() {
            @Override
            protected void configureShiro() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
                // bind our event listeners
                bind(ShiroModuleTest.MockEventListener1.class).asEagerSingleton();
                bind(ShiroModuleTest.MockEventListener2.class).asEagerSingleton();
            }

            @Override
            protected void bindEventBus(AnnotatedBindingBuilder<EventBus> bind) {
                bind.toInstance(eventBus);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }
        };
        Guice.createInjector(shiroModule);
        verify(eventBus);
    }

    /**
     *
     *
     * @since 1.4
     * @throws Exception
     * 		
     */
    @Test
    public void testEventBusAware() throws Exception {
        final ShiroModuleTest.MockRealm mockRealm = createMock(ShiroModuleTest.MockRealm.class);
        final ShiroModule shiroModule = new ShiroModule() {
            @Override
            protected void configureShiro() {
                bindRealm().to(ShiroModuleTest.MockRealm.class);
                bind(ShiroModuleTest.MockEventBusAware.class).asEagerSingleton();
                expose(ShiroModuleTest.MockEventBusAware.class);
            }

            @Provides
            public ShiroModuleTest.MockRealm createRealm() {
                return mockRealm;
            }
        };
        Injector injector = Guice.createInjector(shiroModule);
        EventBus eventBus = injector.getInstance(EventBus.class);
        SecurityManager securityManager = injector.getInstance(SecurityManager.class);
        ShiroModuleTest.MockEventBusAware eventBusAware = injector.getInstance(ShiroModuleTest.MockEventBusAware.class);
        Assert.assertSame(eventBus, eventBusAware.eventBus);
        Assert.assertSame(eventBus, getEventBus());
    }

    public static interface MockRealm extends Realm {}

    public static class MyDefaultSecurityManager extends DefaultSecurityManager {
        @Inject
        public MyDefaultSecurityManager(Collection<Realm> realms) {
            super(realms);
        }
    }

    public static class MyDefaultSessionManager extends DefaultSessionManager {}

    public static class MyEnvironment extends GuiceEnvironment {
        @Inject
        public MyEnvironment(SecurityManager securityManager) {
            super(securityManager);
        }
    }

    public static interface MyDestroyable extends Destroyable {}

    public static class MockEventListener1 {
        @Subscribe
        public void listenToAllAndDoNothing(Object o) {
        }
    }

    public static class MockEventListener2 {
        @Subscribe
        public void listenToAllAndDoNothing(Object o) {
        }
    }

    public static class MockEventBusAware implements EventBusAware {
        private EventBus eventBus;

        public EventBus getEventBus() {
            return eventBus;
        }

        @Override
        public void setEventBus(EventBus eventBus) {
            this.eventBus = eventBus;
        }
    }
}

