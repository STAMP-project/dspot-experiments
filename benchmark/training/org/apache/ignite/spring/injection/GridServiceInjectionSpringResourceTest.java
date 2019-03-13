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
package org.apache.ignite.spring.injection;


import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import org.apache.ignite.Ignite;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.ServiceResource;
import org.apache.ignite.resources.SpringResource;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;


/**
 * Tests for injected service.
 */
public class GridServiceInjectionSpringResourceTest extends GridCommonAbstractTest {
    /**
     * Service name.
     */
    private static final String SERVICE_NAME = "testService";

    /**
     * Bean name.
     */
    private static final String DUMMY_BEAN = "dummyResourceBean";

    /**
     *
     */
    private static final int NODES = 8;

    /**
     *
     */
    private static final int TEST_ITERATIONS = 5;

    /**
     *
     */
    private static FileSystem FS = FileSystems.getDefault();

    /**
     *
     */
    private static final String springCfgFileTemplate = "spring-resource.tmpl.xml";

    /**
     *
     */
    private static String springCfgFileOutTmplName = "spring-resource.xml-";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testDeployServiceWithSpring() throws Exception {
        generateConfigXmls(GridServiceInjectionSpringResourceTest.NODES);
        for (int i = 0; i < (GridServiceInjectionSpringResourceTest.TEST_ITERATIONS); ++i) {
            log.info(("Iteration: " + i));
            doOneTestIteration();
        }
    }

    /**
     *
     */
    private static class TestJob implements IgniteCallable {
        /**
         *
         */
        @ServiceResource(serviceName = GridServiceInjectionSpringResourceTest.SERVICE_NAME, proxyInterface = GridServiceInjectionSpringResourceTest.DummyService.class)
        private GridServiceInjectionSpringResourceTest.DummyService svc;

        /**
         * {@inheritDoc }
         */
        @Override
        public Object call() throws Exception {
            assertNotNull(svc);
            svc.noop();
            return null;
        }
    }

    /**
     * Dummy Service.
     */
    public interface DummyService {
        /**
         *
         */
        void noop();
    }

    /**
     * No-op test service.
     */
    public static class DummyServiceImpl implements Service , GridServiceInjectionSpringResourceTest.DummyService {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         *
         */
        @SpringResource(resourceName = GridServiceInjectionSpringResourceTest.DUMMY_BEAN)
        private transient GridServiceInjectionSpringResourceTest.DummyResourceBean dummyRsrcBean;

        /**
         * {@inheritDoc }
         */
        @Override
        public void noop() {
            System.out.println("DummyServiceImpl.noop()");
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

    /**
     * Dummy resource bean.
     */
    public static class DummyResourceBean {
        /**
         *
         */
        private transient Ignite ignite;

        /**
         *
         *
         * @return Ignite.
         */
        public Ignite getIgnite() {
            return ignite;
        }

        /**
         *
         *
         * @param ignite
         * 		Ignite.
         */
        public void setIgnite(Ignite ignite) {
            this.ignite = ignite;
        }

        /**
         *
         *
         * @throws Exception
         * 		If failed.
         */
        @EventListener
        public void init(ContextRefreshedEvent evt) throws Exception {
            GridServiceInjectionSpringResourceTest.DummyService srv = ignite.services().serviceProxy(GridServiceInjectionSpringResourceTest.SERVICE_NAME, GridServiceInjectionSpringResourceTest.DummyService.class, false);
            assertNotNull(srv);
        }
    }
}

