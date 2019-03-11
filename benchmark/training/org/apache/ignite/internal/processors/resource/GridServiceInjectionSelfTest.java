/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.resource;


import java.io.Serializable;
import java.util.concurrent.Callable;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.resources.ServiceResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for injected service.
 */
public class GridServiceInjectionSelfTest extends GridCommonAbstractTest implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 0L;

    /**
     * Service name.
     */
    private static final String SERVICE_NAME1 = "testService1";

    /**
     * Service name.
     */
    private static final String SERVICE_NAME2 = "testService2";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureField() throws Exception {
        grid(0).compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @ServiceResource(serviceName = GridServiceInjectionSelfTest.SERVICE_NAME1)
            private GridServiceInjectionSelfTest.DummyService svc;

            @Override
            public Object call() throws Exception {
                assertNotNull(svc);
                assertTrue(((svc) instanceof Service));
                svc.noop();
                return null;
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureFieldProxy() throws Exception {
        grid(0).compute(grid(0).cluster().forRemotes()).call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @ServiceResource(serviceName = GridServiceInjectionSelfTest.SERVICE_NAME2, proxyInterface = GridServiceInjectionSelfTest.DummyService.class)
            private GridServiceInjectionSelfTest.DummyService svc;

            @Override
            public Object call() throws Exception {
                assertNotNull(svc);
                // Ensure proxy instance.
                assertFalse(((svc) instanceof Service));
                svc.noop();
                return null;
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureFieldLocalProxy() throws Exception {
        grid(0).compute(grid(0).cluster().forRemotes()).call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @ServiceResource(serviceName = GridServiceInjectionSelfTest.SERVICE_NAME1, proxyInterface = GridServiceInjectionSelfTest.DummyService.class)
            private GridServiceInjectionSelfTest.DummyService svc;

            @Override
            public Object call() throws Exception {
                assertNotNull(svc);
                // Ensure proxy instance.
                assertTrue(((svc) instanceof Service));
                svc.noop();
                return null;
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureFieldWithIncorrectType() throws Exception {
        GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
            @Override
            public Void call() {
                grid(0).compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
                    @ServiceResource(serviceName = GridServiceInjectionSelfTest.SERVICE_NAME1)
                    private String svcName;

                    @Override
                    public Object call() throws Exception {
                        fail();
                        return null;
                    }
                });
                return null;
            }
        }, IgniteCheckedException.class, "Resource field is not assignable from the resource");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureMethod() throws Exception {
        grid(0).compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            private GridServiceInjectionSelfTest.DummyService svc;

            @ServiceResource(serviceName = GridServiceInjectionSelfTest.SERVICE_NAME1)
            private void service(GridServiceInjectionSelfTest.DummyService svc) {
                assertNotNull(svc);
                assertTrue((svc instanceof Service));
                this.svc = svc;
            }

            @Override
            public Object call() throws Exception {
                svc.noop();
                return null;
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureMethodProxy() throws Exception {
        grid(0).compute(grid(0).cluster().forRemotes()).call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            private GridServiceInjectionSelfTest.DummyService svc;

            @ServiceResource(serviceName = GridServiceInjectionSelfTest.SERVICE_NAME2, proxyInterface = GridServiceInjectionSelfTest.DummyService.class)
            private void service(GridServiceInjectionSelfTest.DummyService svc) {
                assertNotNull(svc);
                // Ensure proxy instance.
                assertFalse((svc instanceof Service));
                this.svc = svc;
            }

            @Override
            public Object call() throws Exception {
                svc.noop();
                return null;
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureMethodLocalProxy() throws Exception {
        grid(0).compute(grid(0).cluster().forRemotes()).call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            private GridServiceInjectionSelfTest.DummyService svc;

            @ServiceResource(serviceName = GridServiceInjectionSelfTest.SERVICE_NAME1, proxyInterface = GridServiceInjectionSelfTest.DummyService.class)
            private void service(GridServiceInjectionSelfTest.DummyService svc) {
                assertNotNull(svc);
                // Ensure proxy instance.
                assertTrue((svc instanceof Service));
                this.svc = svc;
            }

            @Override
            public Object call() throws Exception {
                svc.noop();
                return null;
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureMethodWithIncorrectType() throws Exception {
        GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
            @Override
            public Void call() {
                grid(0).compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
                    @ServiceResource(serviceName = GridServiceInjectionSelfTest.SERVICE_NAME1)
                    private void service(String svcs) {
                        fail();
                    }

                    @Override
                    public Object call() throws Exception {
                        return null;
                    }
                });
                return null;
            }
        }, IgniteCheckedException.class, "Setter does not have single parameter of required type");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureFieldWithNonExistentService() throws Exception {
        grid(0).compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @ServiceResource(serviceName = "nonExistentService")
            private GridServiceInjectionSelfTest.DummyService svc;

            @Override
            public Object call() throws Exception {
                assertNull(svc);
                return null;
            }
        });
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureMethodWithNonExistentService() throws Exception {
        grid(0).compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @ServiceResource(serviceName = "nonExistentService")
            private void service(GridServiceInjectionSelfTest.DummyService svc) {
                assertNull(svc);
            }

            @Override
            public Object call() throws Exception {
                return null;
            }
        });
    }

    /**
     * Dummy Service.
     */
    public interface DummyService {
        public void noop();
    }

    /**
     * No-op test service.
     */
    public static class DummyServiceImpl implements GridServiceInjectionSelfTest.DummyService , Service {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         * {@inheritDoc }
         */
        @Override
        public void noop() {
            // No-op.
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void cancel(ServiceContext ctx) {
            System.out.println(("Cancelling service: " + (ctx.name())));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void init(ServiceContext ctx) throws Exception {
            System.out.println(("Initializing service: " + (ctx.name())));
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void execute(ServiceContext ctx) {
            System.out.println(("Executing service: " + (ctx.name())));
        }
    }
}

