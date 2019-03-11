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
package org.apache.ignite.gridify;


import DeploymentMode.CONTINUOUS;
import DeploymentMode.ISOLATED;
import DeploymentMode.PRIVATE;
import DeploymentMode.SHARED;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.events.Event;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Abstract AOP test.
 */
@SuppressWarnings({ "OverlyStrongTypeCast", "ProhibitedExceptionDeclared", "IfMayBeConditional" })
public abstract class AbstractAopTest extends GridCommonAbstractTest {
    /**
     *
     */
    private DeploymentMode depMode = DeploymentMode.PRIVATE;

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultPrivate() throws Exception {
        checkDefault(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultIsolated() throws Exception {
        checkDefault(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultContinuous() throws Exception {
        checkDefault(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultShared() throws Exception {
        checkDefault(SHARED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultWithUserClassLoaderPrivate() throws Exception {
        checkDefaultWithUserClassLoader(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultWithUserClassLoaderIsolated() throws Exception {
        checkDefaultWithUserClassLoader(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultWithUserClassLoaderContinuous() throws Exception {
        checkDefaultWithUserClassLoader(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultWithUserClassLoaderShared() throws Exception {
        checkDefaultWithUserClassLoader(SHARED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testSingleDeploymentWithUserClassLoaderPrivate() throws Exception {
        checkSingleDeploymentWithUserClassLoader(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testSingleDeploymentWithUserClassLoaderIsolated() throws Exception {
        checkSingleDeploymentWithUserClassLoader(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testSingleDeploymentWithUserClassLoaderContinuous() throws Exception {
        checkSingleDeploymentWithUserClassLoader(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testSingleDeploymentWithUserClassLoaderShared() throws Exception {
        checkSingleDeploymentWithUserClassLoader(SHARED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultResourceWithUserClassLoaderPrivate() throws Exception {
        checkDefaultResourceWithUserClassLoader(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultResourceWithUserClassLoaderIsolated() throws Exception {
        checkDefaultResourceWithUserClassLoader(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultResourceWithUserClassLoaderContinuous() throws Exception {
        checkDefaultResourceWithUserClassLoader(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultResourceWithUserClassLoaderShared() throws Exception {
        checkDefaultResourceWithUserClassLoader(SHARED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultClassPrivate() throws Exception {
        checkNonDefaultClass(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultClassIsolated() throws Exception {
        checkNonDefaultClass(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultClassContinuous() throws Exception {
        checkNonDefaultClass(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultClassShared() throws Exception {
        checkNonDefaultClass(SHARED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultNamePrivate() throws Exception {
        checkNonDefaultName(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultNameIsolated() throws Exception {
        checkNonDefaultName(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultNameContinuous() throws Exception {
        checkNonDefaultName(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultNameShared() throws Exception {
        checkNonDefaultName(SHARED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultExceptionPrivate() throws Exception {
        checkDefaultException(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultExceptionIsolated() throws Exception {
        checkDefaultException(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultExceptionContinuous() throws Exception {
        checkDefaultException(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultExceptionShared() throws Exception {
        checkDefaultException(SHARED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultResourcePrivate() throws Exception {
        checkDefaultResource(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultResourceIsolated() throws Exception {
        checkDefaultResource(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultResourceContinuous() throws Exception {
        checkDefaultResource(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testDefaultResourceShared() throws Exception {
        checkDefaultResource(SHARED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultClassResourcePrivate() throws Exception {
        checkNonDefaultClassResource(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultClassResourceIsolated() throws Exception {
        checkNonDefaultClassResource(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultClassResourceContinuous() throws Exception {
        checkNonDefaultClassResource(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultClassResourceShared() throws Exception {
        checkNonDefaultClassResource(SHARED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultNameResourcePrivate() throws Exception {
        checkNonDefaultNameResource(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultNameResourceIsolated() throws Exception {
        checkNonDefaultNameResource(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultNameResourceContinuous() throws Exception {
        checkNonDefaultNameResource(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultNameResourceShared() throws Exception {
        checkNonDefaultNameResource(SHARED);
    }

    /**
     * Event listener.
     */
    private static final class TestEventListener implements IgnitePredicate<Event> {
        /**
         *
         */
        private static final long serialVersionUID = 0L;

        /**
         * Counter.
         */
        private final AtomicInteger cnt;

        /**
         *
         *
         * @param cnt
         * 		Deploy counter.
         */
        private TestEventListener(AtomicInteger cnt) {
            this.cnt = cnt;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean apply(Event evt) {
            if (((((evt.type()) == (EVT_TASK_DEPLOYED)) || ((evt.type()) == (EVT_CLASS_DEPLOYED))) && ((evt.message()) != null)) && (evt.message().contains("TestAopTarget")))
                cnt.addAndGet(1);

            return true;
        }
    }
}

