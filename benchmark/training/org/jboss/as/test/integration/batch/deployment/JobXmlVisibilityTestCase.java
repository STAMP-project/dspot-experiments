/**
 * Copyright 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.test.integration.batch.deployment;


import java.util.Arrays;
import java.util.Collections;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.test.integration.batch.common.AbstractBatchTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunAsClient
@RunWith(Arquillian.class)
public class JobXmlVisibilityTestCase extends AbstractBatchTestCase {
    private static final String EAR = "test-visibility.ear";

    private static final String EAR_ISOLATED = "test-visibility-isolated.ear";

    private static final String EAR_WITH_LIB = "test-with-lib.ear";

    private static final String EAR_WITH_LIB_ISOLATED = "test-isolated-with-lib.ear";

    private static final String WAR_WITH_LIB = "test-war-with-lib.war";

    // @OperateOnDeployment is required until WFARQ-13 is resolved, any deployment should suffice though
    @ArquillianResource
    @OperateOnDeployment(JobXmlVisibilityTestCase.EAR)
    private ManagementClient managementClient;

    /**
     * Tests that all job XML files are visible from the WAR.
     */
    @Test
    public void testJobXmlIsVisible() throws Exception {
        validateJobXmlNames(JobXmlVisibilityTestCase.deploymentAddress(JobXmlVisibilityTestCase.EAR, "war-in-ear-visible.war"), Arrays.asList("test-war.xml", "test-ejb.xml"));
    }

    /**
     * Tests that the WAR can only see the job XML from the WAR and the EJB can only see the job XML from the EJB.
     */
    @Test
    public void testJobXmlIsIsolated() throws Exception {
        validateJobXmlNames(JobXmlVisibilityTestCase.deploymentAddress(JobXmlVisibilityTestCase.EAR_ISOLATED, "war-in-ear-isolated.war"), Collections.singleton("test-war.xml"));
        validateJobXmlNames(JobXmlVisibilityTestCase.deploymentAddress(JobXmlVisibilityTestCase.EAR_ISOLATED, "ejb-in-ear-isolated.jar"), Collections.singleton("test-ejb.xml"));
    }

    /**
     * Tests that the WAR can see the job XML from the WAR itself, the EJB and the EAR's global dependency. The EJB
     * should be able to see the job XML from the EJB itself and the EAR's global dependency.
     */
    @Test
    public void testJobXmlIsVisibleJar() throws Exception {
        validateJobXmlNames(JobXmlVisibilityTestCase.deploymentAddress(JobXmlVisibilityTestCase.EAR_WITH_LIB, "war-in-ear-with-lib.war"), Arrays.asList("test-war.xml", "test-ejb.xml", "test-jar.xml"));
        validateJobXmlNames(JobXmlVisibilityTestCase.deploymentAddress(JobXmlVisibilityTestCase.EAR_WITH_LIB, "ejb-in-ear-with-lib.jar"), Arrays.asList("test-ejb.xml", "test-jar.xml"));
    }

    /**
     * Tests that the WAR can see the job XML from the WAR itself and the EAR's global dependency. The EJB
     * should be able to see the job XML from the EJB itself and the EAR's global dependency.
     */
    @Test
    public void testJobXmlIsIsolatedJar() throws Exception {
        validateJobXmlNames(JobXmlVisibilityTestCase.deploymentAddress(JobXmlVisibilityTestCase.EAR_WITH_LIB_ISOLATED, "war-in-ear-isolated-with-lib.war"), Arrays.asList("test-war.xml", "test-jar.xml"));
        validateJobXmlNames(JobXmlVisibilityTestCase.deploymentAddress(JobXmlVisibilityTestCase.EAR_WITH_LIB_ISOLATED, "ejb-in-ear-isolated-with-lib.jar"), Arrays.asList("test-ejb.xml", "test-jar.xml"));
    }

    /**
     * Test that a WAR will see the job XML from a direct dependency.
     */
    @Test
    public void testJobXmlInWar() throws Exception {
        validateJobXmlNames(JobXmlVisibilityTestCase.deploymentAddress(JobXmlVisibilityTestCase.WAR_WITH_LIB, null), Arrays.asList("test-war.xml", "test-jar.xml"));
    }
}

