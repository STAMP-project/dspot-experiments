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


import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteSpring;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.resources.SpringResource;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Tests for injected resource.
 */
@SuppressWarnings("unused")
public class GridSpringResourceInjectionSelfTest extends GridCommonAbstractTest {
    /**
     * Bean name.
     */
    private static final String DUMMY_BEAN = "dummyResourceBean";

    /**
     * Test grid with Spring context.
     */
    private static Ignite grid;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureFieldByResourceName() throws Exception {
        GridSpringResourceInjectionSelfTest.grid.compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @SpringResource(resourceName = GridSpringResourceInjectionSelfTest.DUMMY_BEAN)
            private transient GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @Override
            public Object call() throws Exception {
                assertNotNull(dummyRsrcBean);
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
    public void testClosureFieldByResourceClass() throws Exception {
        GridSpringResourceInjectionSelfTest.grid.compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @SpringResource(resourceClass = GridSpringResourceInjectionSelfTest.DummyResourceBean.class)
            private transient GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @Override
            public Object call() throws Exception {
                assertNotNull(dummyRsrcBean);
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
    public void testClosureFieldByResourceClassWithMultipleBeans() throws Exception {
        IgniteConfiguration anotherCfg = new IgniteConfiguration();
        anotherCfg.setIgniteInstanceName("anotherGrid");
        Ignite anotherGrid = IgniteSpring.start(anotherCfg, new ClassPathXmlApplicationContext("/org/apache/ignite/internal/processors/resource/spring-resource-with-duplicate-beans.xml"));
        assertError(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @SpringResource(resourceClass = GridSpringResourceInjectionSelfTest.DummyResourceBean.class)
            private transient GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @Override
            public Object call() throws Exception {
                assertNotNull(dummyRsrcBean);
                return null;
            }
        }, anotherGrid, NoUniqueBeanDefinitionException.class, ("No qualifying bean of type " + ("'org.apache.ignite.internal.processors.resource.GridSpringResourceInjectionSelfTest$DummyResourceBean'" + " available: expected single matching bean but found 2:")));
        G.stop("anotherGrid", false);
    }

    /**
     * Resource injection with non-existing resource name.
     */
    @Test
    public void testClosureFieldWithWrongResourceName() {
        assertError(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @SpringResource(resourceName = "nonExistentResource")
            private transient GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @Override
            public Object call() throws Exception {
                assertNull(dummyRsrcBean);
                return null;
            }
        }, GridSpringResourceInjectionSelfTest.grid, NoSuchBeanDefinitionException.class, "No bean named 'nonExistentResource' available");
    }

    /**
     * Resource injection with non-existing resource class.
     */
    @Test
    public void testClosureFieldWithWrongResourceClass() {
        assertError(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @SpringResource(resourceClass = GridSpringResourceInjectionSelfTest.AnotherDummyResourceBean.class)
            private transient GridSpringResourceInjectionSelfTest.AnotherDummyResourceBean dummyRsrcBean;

            @Override
            public Object call() throws Exception {
                assertNull(dummyRsrcBean);
                return null;
            }
        }, GridSpringResourceInjectionSelfTest.grid, NoSuchBeanDefinitionException.class, ("No qualifying bean of type 'org.apache.ignite.internal.processors.resource." + "GridSpringResourceInjectionSelfTest$AnotherDummyResourceBean' available"));
    }

    /**
     * Resource injection with both resource and class set (ambiguity).
     */
    @Test
    public void testClosureFieldByResourceClassAndName() {
        assertError(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @SpringResource(resourceClass = GridSpringResourceInjectionSelfTest.DummyResourceBean.class, resourceName = GridSpringResourceInjectionSelfTest.DUMMY_BEAN)
            private transient GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @Override
            public Object call() throws Exception {
                assertNull(dummyRsrcBean);
                return null;
            }
        }, GridSpringResourceInjectionSelfTest.grid, IgniteException.class, "Either bean name or its class must be specified in @SpringResource, but not both");
    }

    /**
     * Resource injection with no name and class set.
     */
    @Test
    public void testClosureFieldWithNoParams() {
        assertError(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @SpringResource
            private transient GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @Override
            public Object call() throws Exception {
                assertNull(dummyRsrcBean);
                return null;
            }
        }, GridSpringResourceInjectionSelfTest.grid, IgniteException.class, "Either bean name or its class must be specified in @SpringResource, but not both");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testClosureMethodWithResourceName() throws Exception {
        GridSpringResourceInjectionSelfTest.grid.compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            private GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @SpringResource(resourceName = GridSpringResourceInjectionSelfTest.DUMMY_BEAN)
            private void setDummyResourceBean(GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean) {
                assertNotNull(dummyRsrcBean);
                this.dummyRsrcBean = dummyRsrcBean;
            }

            @Override
            public Object call() throws Exception {
                assertNotNull(dummyRsrcBean);
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
    public void testClosureMethodWithResourceClass() throws Exception {
        GridSpringResourceInjectionSelfTest.grid.compute().call(new org.apache.ignite.lang.IgniteCallable<Object>() {
            private GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @SpringResource(resourceClass = GridSpringResourceInjectionSelfTest.DummyResourceBean.class)
            private void setDummyResourceBean(GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean) {
                assertNotNull(dummyRsrcBean);
                this.dummyRsrcBean = dummyRsrcBean;
            }

            @Override
            public Object call() throws Exception {
                assertNotNull(dummyRsrcBean);
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
    public void testClosureMethodWithResourceClassWithMultipleBeans() throws Exception {
        IgniteConfiguration anotherCfg = new IgniteConfiguration();
        anotherCfg.setIgniteInstanceName("anotherGrid");
        Ignite anotherGrid = IgniteSpring.start(anotherCfg, new ClassPathXmlApplicationContext("/org/apache/ignite/internal/processors/resource/spring-resource-with-duplicate-beans.xml"));
        try {
            assertError(new org.apache.ignite.lang.IgniteCallable<Object>() {
                private GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

                @SpringResource(resourceClass = GridSpringResourceInjectionSelfTest.DummyResourceBean.class)
                private void setDummyResourceBean(GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean) {
                    assertNotNull(dummyRsrcBean);
                    this.dummyRsrcBean = dummyRsrcBean;
                }

                @Override
                public Object call() throws Exception {
                    assertNotNull(dummyRsrcBean);
                    return null;
                }
            }, anotherGrid, NoUniqueBeanDefinitionException.class, ("No qualifying bean of type " + ("'org.apache.ignite.internal.processors.resource.GridSpringResourceInjectionSelfTest$DummyResourceBean'" + " available: expected single matching bean but found 2:")));
        } finally {
            G.stop("anotherGrid", false);
        }
    }

    /**
     * Resource injection with non-existing resource name.
     */
    @Test
    public void testClosureMethodWithWrongResourceName() {
        assertError(new org.apache.ignite.lang.IgniteCallable<Object>() {
            private GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @SpringResource(resourceName = "nonExistentResource")
            private void setDummyResourceBean(GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean) {
                // No-op.
            }

            @Override
            public Object call() throws Exception {
                assertNull(dummyRsrcBean);
                return null;
            }
        }, GridSpringResourceInjectionSelfTest.grid, NoSuchBeanDefinitionException.class, "No bean named 'nonExistentResource' available");
    }

    /**
     * Resource injection with non-existing resource class.
     */
    @Test
    public void testClosureMethodWithWrongResourceClass() {
        assertError(new org.apache.ignite.lang.IgniteCallable<Object>() {
            private GridSpringResourceInjectionSelfTest.AnotherDummyResourceBean dummyRsrcBean;

            @SpringResource(resourceClass = GridSpringResourceInjectionSelfTest.AnotherDummyResourceBean.class)
            private void setDummyResourceBean(GridSpringResourceInjectionSelfTest.AnotherDummyResourceBean dummyRsrcBean) {
                // No-op.
            }

            @Override
            public Object call() throws Exception {
                assertNull(dummyRsrcBean);
                return null;
            }
        }, GridSpringResourceInjectionSelfTest.grid, NoSuchBeanDefinitionException.class, ("No qualifying bean of type 'org.apache.ignite.internal.processors.resource" + ".GridSpringResourceInjectionSelfTest$AnotherDummyResourceBean' available"));
    }

    /**
     * Resource injection with both resource and class set (ambiguity).
     */
    @Test
    public void testClosureMethodByResourceClassAndName() {
        assertError(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @SpringResource(resourceClass = GridSpringResourceInjectionSelfTest.DummyResourceBean.class, resourceName = GridSpringResourceInjectionSelfTest.DUMMY_BEAN)
            private transient GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @Override
            public Object call() throws Exception {
                assertNull(dummyRsrcBean);
                return null;
            }
        }, GridSpringResourceInjectionSelfTest.grid, IgniteException.class, "Either bean name or its class must be specified in @SpringResource, but not both");
    }

    /**
     * Resource injection with no params.
     */
    @Test
    public void testClosureMethodWithNoParams() {
        assertError(new org.apache.ignite.lang.IgniteCallable<Object>() {
            @SpringResource
            private transient GridSpringResourceInjectionSelfTest.DummyResourceBean dummyRsrcBean;

            @Override
            public Object call() throws Exception {
                assertNull(dummyRsrcBean);
                return null;
            }
        }, GridSpringResourceInjectionSelfTest.grid, IgniteException.class, "Either bean name or its class must be specified in @SpringResource, but not both");
    }

    /**
     * Dummy resource bean.
     */
    public static class DummyResourceBean {
        /**
         *
         */
        public DummyResourceBean() {
            // No-op.
        }
    }

    /**
     * Another dummy resource bean.
     */
    private static class AnotherDummyResourceBean {
        /**
         *
         */
        public AnotherDummyResourceBean() {
            // No-op.
        }
    }
}

