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
package org.test.gridify;


import DeploymentMode.CONTINUOUS;
import DeploymentMode.ISOLATED;
import DeploymentMode.PRIVATE;
import DeploymentMode.SHARED;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.testframework.junits.common.GridCommonTest;
import org.junit.Test;


/**
 * To run this test with JBoss AOP make sure of the following:
 *
 * 1. The JVM is started with following parameters to enable jboss online weaving
 *      (replace ${IGNITE_HOME} to you $IGNITE_HOME):
 *      -javaagent:${IGNITE_HOME}libs/jboss-aop-jdk50-4.0.4.jar
 *      -Djboss.aop.class.path=[path to grid compiled classes (Idea out folder) or path to ignite.jar]
 *      -Djboss.aop.exclude=org,com -Djboss.aop.include=org.apache.ignite
 *
 * 2. The following jars should be in a classpath:
 *      ${IGNITE_HOME}libs/javassist-3.x.x.jar
 *      ${IGNITE_HOME}libs/jboss-aop-jdk50-4.0.4.jar
 *      ${IGNITE_HOME}libs/jboss-aspect-library-jdk50-4.0.4.jar
 *      ${IGNITE_HOME}libs/jboss-common-4.2.2.jar
 *      ${IGNITE_HOME}libs/trove-1.0.2.jar
 *
 * To run this test with AspectJ AOP make sure of the following:
 *
 * 1. The JVM is started with following parameters for enable AspectJ online weaving
 *      (replace ${IGNITE_HOME} to you $IGNITE_HOME):
 *      -javaagent:${IGNITE_HOME}/libs/aspectjweaver-1.7.2.jar
 *
 * 2. Classpath should contains the ${IGNITE_HOME}/modules/tests/config/aop/aspectj folder.
 */
@GridCommonTest(group = "AOP")
public class ExternalNonSpringAopSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    private DeploymentMode depMode = DeploymentMode.PRIVATE;

    /**
     *
     */
    public ExternalNonSpringAopSelfTest() {
        /**
         * start grid
         */
        super(false);
    }

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
    public void testNonDefaultContinuous() throws Exception {
        checkNonDefaultClass(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testNonDefaultShared() throws Exception {
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
    public void testTaskNameAndTaskClassPrivate() throws Exception {
        checkTaskNameAndTaskClass(PRIVATE);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testTaskNameAndTaskClassIsolated() throws Exception {
        checkTaskNameAndTaskClass(ISOLATED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testTaskNameAndTaskClassContinuous() throws Exception {
        checkTaskNameAndTaskClass(CONTINUOUS);
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testTaskNameAndTaskClassShared() throws Exception {
        checkTaskNameAndTaskClass(SHARED);
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
}

