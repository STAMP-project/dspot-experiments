/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.adapter.example.authorization;


import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.adapter.AbstractExampleAdapterTest;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY_DEPRECATED)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
@AppServerContainer(ContainerConstants.APP_SERVER_UNDERTOW)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
@AppServerContainer(ContainerConstants.APP_SERVER_TOMCAT7)
@AppServerContainer(ContainerConstants.APP_SERVER_TOMCAT8)
@AppServerContainer(ContainerConstants.APP_SERVER_TOMCAT9)
public class DefaultAuthzConfigAdapterTest extends AbstractExampleAdapterTest {
    private static final String REALM_NAME = "hello-world-authz";

    private static final String RESOURCE_SERVER_ID = "hello-world-authz-service";

    @ArquillianResource
    private Deployer deployer;

    @Test
    public void testDefaultAuthzConfig() throws Exception {
        try {
            configureAuthorizationServices();
            this.deployer.deploy(DefaultAuthzConfigAdapterTest.RESOURCE_SERVER_ID);
            login();
            Assert.assertTrue(this.driver.getPageSource().contains("Your permissions are"));
            Assert.assertTrue(this.driver.getPageSource().contains("Default Resource"));
            boolean hasDefaultPermission = false;
            boolean hasDefaultPolicy = false;
            for (PolicyRepresentation policy : getAuthorizationResource().policies().policies()) {
                if ("Default Policy".equals(policy.getName())) {
                    hasDefaultPolicy = true;
                }
                if ("Default Permission".equals(policy.getName())) {
                    hasDefaultPermission = true;
                }
            }
            Assert.assertTrue(hasDefaultPermission);
            Assert.assertTrue(hasDefaultPolicy);
        } finally {
            this.deployer.undeploy(DefaultAuthzConfigAdapterTest.RESOURCE_SERVER_ID);
        }
    }
}

