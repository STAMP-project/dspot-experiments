/**
 * Copyright 2018 Red Hat, Inc.
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
package org.jboss.as.test.integration.web.jsp.taglib.jar;


import java.net.URL;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
@RunAsClient
public class TagLibInJarTestCase {
    private static final String TLD_INSIDE_RESOURCES = "tld-inside-resources";

    private static final String TLD_OUTSIDE_RESOURCES = "tld-outside-resources";

    private static final String TLD_INSIDE_JAR = "tld-inside-jar";

    private static final String JAR_NAME = "taglib.jar";

    private static final String JSP = "index.jsp";

    private static final String WEB_FRAGMENT = "web-fragment.xml";

    @ArquillianResource
    @OperateOnDeployment(TagLibInJarTestCase.TLD_OUTSIDE_RESOURCES)
    private URL urlDep1;

    @ArquillianResource
    @OperateOnDeployment(TagLibInJarTestCase.TLD_INSIDE_RESOURCES)
    private URL urlDep2;

    @ArquillianResource
    @OperateOnDeployment(TagLibInJarTestCase.TLD_INSIDE_JAR)
    private URL urlDep3;

    @Test
    @OperateOnDeployment(TagLibInJarTestCase.TLD_OUTSIDE_RESOURCES)
    public void testTldOutsideResourcesFolder() throws Exception {
        checkJspAvailable(urlDep1);
    }

    @Test
    @OperateOnDeployment(TagLibInJarTestCase.TLD_INSIDE_RESOURCES)
    public void testTldInsideResourcesFolder() throws Exception {
        checkJspAvailable(urlDep2);
    }

    /**
     * Tests if deployment with taglib-location pointing to jar fails during deployment phase.
     * Test passes if the correct response is returned from the JSP.
     *
     * @throws Exception
     * 		
     */
    @Test
    @OperateOnDeployment(TagLibInJarTestCase.TLD_INSIDE_JAR)
    public void testTldInsideJar() throws Exception {
        checkJspAvailable(urlDep3);
    }
}

