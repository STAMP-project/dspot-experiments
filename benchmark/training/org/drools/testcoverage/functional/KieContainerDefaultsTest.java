/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.testcoverage.functional;


import KieServices.Factory;
import KieSessionModel.KieSessionType.STATELESS;
import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;


/**
 * This class's purpose is to test obtaining default KieSessions and default KieBases from KieContainer. It tests
 * KieContainer's behaviour in other cases than simple one default KieBase with one default KieSession.
 */
public class KieContainerDefaultsTest {
    private static final String DRL = "package defaultKBase;\n rule testRule when then end\n";

    private static final ReleaseId RELEASE_ID = Factory.get().newReleaseId(TestConstants.PACKAGE_TESTCOVERAGE, "kie-container-defaults-test", "1.0.0");

    private KieServices kieServices;

    /**
     * This test checks if default KieBases behave as expected.
     */
    @Test
    public void testTwoKieBasesOneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(false);
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        final KieBase firstKBase = kieContainer.getKieBase("firstKBase");
        final KieBase secondKBase = kieContainer.getKieBase("secondKBase");
        Assertions.assertThat(kieContainer.getKieBase()).isEqualTo(firstKBase);
        Assertions.assertThat(kieContainer.getKieBase()).isNotEqualTo(secondKBase);
    }

    /**
     * This test checks how KieBases behave when all are explicitly set not to be default.
     */
    @Test
    public void testTwoKieBasesNoneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(false);
        kieModule.newKieBaseModel("secondKBase").setDefault(false);
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        Assertions.assertThatThrownBy(() -> kieContainer.getKieBase()).isInstanceOf(RuntimeException.class).hasMessageContaining("Cannot find a default KieBase");
    }

    /**
     * This test checks if default KieSessions behave as expected.
     */
    @Test
    public void testTwoKieSessionsOneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession");
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        final KieSession firstKSession = kieContainer.newKieSession("firstKSession");
        final KieSession secondKSession = kieContainer.newKieSession("secondKSession");
        try {
            Assertions.assertThat(firstKSession).isEqualTo(getKieSession());
            Assertions.assertThat(secondKSession).isNotEqualTo(getKieSession());
        } finally {
            firstKSession.dispose();
            secondKSession.dispose();
        }
    }

    /**
     * This test checks how KieSessions behave when more than one is set as default.
     */
    @Test
    public void testTwoKieSessionsBothDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setDefault(true);
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        Assertions.assertThatThrownBy(() -> kieContainer.newKieSession()).isInstanceOf(RuntimeException.class).hasMessageContaining("Cannot find a default KieSession");
    }

    /**
     * This test checks how KieSessions behave when all are explicitly set not to be default.
     */
    @Test
    public void testTwoKieSessionsNoneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setDefault(false);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setDefault(false);
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        Assertions.assertThatThrownBy(() -> kieContainer.newKieSession()).isInstanceOf(RuntimeException.class).hasMessageContaining("Cannot find a default KieSession");
    }

    /**
     * This test checks how KieSessions behave when default state isn't explicitly set.
     */
    @Test
    public void testTwoKieSessionsDefaultNotSet() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession");
        kieModule.newKieBaseModel("secondKBase").setDefault(false).newKieSessionModel("secondKSession");
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        Assertions.assertThatThrownBy(() -> kieContainer.newKieSession()).isInstanceOf(RuntimeException.class).hasMessageContaining("Cannot find a default KieSession");
    }

    /**
     * This test checks if default KieSessions behave as expected even if one of them is Stateless.
     */
    @Test
    public void testTwoKieSessionsOneStatelessDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setType(STATELESS).setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setDefault(false);
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        final StatelessKieSession firstKSession = kieContainer.newStatelessKieSession("firstKSession");
        final KieSession secondKSession = kieContainer.newKieSession("secondKSession");
        try {
            Assertions.assertThat(firstKSession).isEqualTo(getStatelessKieSession());
            Assertions.assertThat(secondKSession).isNotEqualTo(getStatelessKieSession());
        } finally {
            secondKSession.dispose();
        }
    }

    /**
     * This test checks if default StatelessKieSessions behave as expected.
     */
    @Test
    public void testTwoStatelessKieSessionsOneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setType(STATELESS).setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setType(STATELESS).setDefault(false);
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        final StatelessKieSession firstKSession = kieContainer.newStatelessKieSession("firstKSession");
        final StatelessKieSession secondKSession = kieContainer.newStatelessKieSession("secondKSession");
        Assertions.assertThat(firstKSession).isEqualTo(getStatelessKieSession());
        Assertions.assertThat(secondKSession).isNotEqualTo(getStatelessKieSession());
    }

    /**
     * This test checks how StatelessKieSessions behave when more than one is set as default.
     */
    @Test
    public void testTwoStatelessKieSessionsBothDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setType(STATELESS).setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setType(STATELESS).setDefault(true);
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        Assertions.assertThatThrownBy(() -> kieContainer.newStatelessKieSession()).isInstanceOf(RuntimeException.class).hasMessageContaining("Cannot find a default StatelessKieSession");
    }

    /**
     * This test checks how StatelessKieSessions behave when all are explicitly set not to be default.
     */
    @Test
    public void testTwoStatelessKieSessionsNoneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setType(STATELESS).setDefault(false);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setType(STATELESS).setDefault(false);
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        Assertions.assertThatThrownBy(() -> kieContainer.newStatelessKieSession()).isInstanceOf(RuntimeException.class).hasMessageContaining("Cannot find a default StatelessKieSession");
    }

    /**
     * This test checks how StatelessKieSessions behave when default state isn't explicitly set.
     */
    @Test
    public void testTwoStatelessKieSessionsDefaultNotSet() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setType(STATELESS);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setType(STATELESS);
        buildKieModule(kieModule);
        final KieContainer kieContainer = kieServices.newKieContainer(KieContainerDefaultsTest.RELEASE_ID);
        Assertions.assertThatThrownBy(() -> kieContainer.newStatelessKieSession()).isInstanceOf(RuntimeException.class).hasMessageContaining("Cannot find a default StatelessKieSession");
    }
}

