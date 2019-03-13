/**
 * Copyright 2019 Red Hat, Inc.
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
package org.jboss.as.test.integration.deployment.moduleslisting;


import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.arquillian.api.ContainerResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * List modules which are on deployment?s classpath
 * /deployment=application_war_ear_name:list-modules(verbose=false|true)
 *
 * @author <a href="mailto:szhantem@redhat.com">Sultan Zhantemirov</a> (c) 2019 Red Hat, inc.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DeploymentModulesListTestCase {
    private static final String NODE_TYPE = "deployment";

    private static final String INNER_JAR_ARCHIVE_NAME = "inner-jar-lib.jar";

    private static final String EXAMPLE_MODULE_TO_EXCLUDE = "ibm.jdk";

    private static final String INNER_WAR_ARCHIVE_NAME = "list-modules.war";

    private static final String EAR_DEPLOYMENT_NAME = "list-modules-ear-test.ear";

    private static final String USER_MODULE = "org.hibernate";

    private static final String CUSTOM_SLOT = "5.0";

    @ContainerResource
    private static ManagementClient managementClient;

    @Test
    public void listEarModulesNonVerbose() throws Throwable {
        this.listEarModules(false);
    }

    @Test
    public void listEarModulesVerbose() throws Throwable {
        this.listEarModules(true);
    }
}

