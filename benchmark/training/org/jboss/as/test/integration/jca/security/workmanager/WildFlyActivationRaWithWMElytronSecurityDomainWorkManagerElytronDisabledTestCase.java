/**
 * Copyright 2017 Red Hat, Inc.
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
package org.jboss.as.test.integration.jca.security.workmanager;


import java.util.function.Consumer;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.dmr.ModelNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.wildfly.test.security.common.AbstractElytronSetupTask;
import org.wildfly.test.security.common.elytron.ConfigurableElement;
import org.wildfly.test.security.common.elytron.PropertyFileBasedDomain;


/**
 * Test security inflow with JCA work manager where RA is configured with Elytron security domain
 * and Workmanager is configured with legacy security (it doesn't have elytron-enabled=true),
 * it is not allowed to mix security configuration and it should fail
 */
@RunWith(Arquillian.class)
@ServerSetup({ WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase.ElytronSetup.class, WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase.JcaSetup.class, WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase.RaSetup.class })
@RunAsClient
public class WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase {
    private static final String ADMIN_OBJ_JNDI_NAME = "java:jboss/admObj";

    private static final String WM_ELYTRON_SECURITY_DOMAIN_NAME = "RaRealmElytron";

    private static final String BOOTSTRAP_CTX_NAME = "wrongContext";

    static class ElytronSetup extends AbstractElytronSetupTask {
        @Override
        protected ConfigurableElement[] getConfigurableElements() {
            final PropertyFileBasedDomain domain = PropertyFileBasedDomain.builder().withName(WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase.WM_ELYTRON_SECURITY_DOMAIN_NAME).withUser("rauser", "rauserpassword").build();
            return new ConfigurableElement[]{ domain };
        }
    }

    static class JcaSetup extends AbstractJcaSetup {
        private static final String WM_NAME = "wrongWM";

        @Override
        protected String getWorkManagerName() {
            return WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase.JcaSetup.WM_NAME;
        }

        @Override
        protected String getBootstrapContextName() {
            return WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase.BOOTSTRAP_CTX_NAME;
        }

        @Override
        protected Boolean getElytronEnabled() {
            return null;
        }
    }

    static class RaSetup extends AbstractRaSetup {
        private static final String RA_NAME = "wf-ra-wm-security-domain";

        @Override
        protected String getResourceAdapterName() {
            return WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase.RaSetup.RA_NAME;
        }

        @Override
        protected String getBootstrapContextName() {
            return WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase.BOOTSTRAP_CTX_NAME;
        }

        @Override
        protected String getAdminObjectJNDIName() {
            return WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase.ADMIN_OBJ_JNDI_NAME;
        }

        @Override
        protected Consumer<ModelNode> getAddRAOperationConsumer() {
            return ( addRaOperation) -> {
                addRaOperation.get("wm-security").set(true);
                addRaOperation.get("wm-elytron-security-domain").set(WildFlyActivationRaWithWMElytronSecurityDomainWorkManagerElytronDisabledTestCase.WM_ELYTRON_SECURITY_DOMAIN_NAME);
                addRaOperation.get("wm-security-default-principal").set("wm-default-principal");
                addRaOperation.get("wm-security-default-groups").set(new ModelNode().setEmptyList().add("wm-default-group"));
            };
        }
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @ArquillianResource
    private Deployer deployer;

    @Test
    public void testMixedSecurity() throws Throwable {
        expectedException.expectMessage(CoreMatchers.containsString("WFLYJCA0111"));
        deployer.deploy("wf-ra-wm-security-domain-rar");
        try {
            deployer.undeploy("wf-ra-wm-security-domain-rar");
        } catch (Exception ex) {
            // ignore
        }
    }
}

