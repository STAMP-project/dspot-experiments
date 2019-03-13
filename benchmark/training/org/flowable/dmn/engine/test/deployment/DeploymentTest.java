/**
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
package org.flowable.dmn.engine.test.deployment;


import org.flowable.dmn.api.DecisionExecutionAuditContainer;
import org.flowable.dmn.api.DmnDecisionTable;
import org.flowable.dmn.engine.test.AbstractFlowableDmnTest;
import org.flowable.dmn.engine.test.DmnDeployment;
import org.junit.Assert;
import org.junit.Test;


public class DeploymentTest extends AbstractFlowableDmnTest {
    @Test
    @DmnDeployment(resources = "org/flowable/dmn/engine/test/deployment/multiple_conclusions.dmn")
    public void deploySingleDecision() {
        DmnDecisionTable decision = repositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision").singleResult();
        Assert.assertNotNull(decision);
        Assert.assertEquals("decision", decision.getKey());
    }

    @Test
    @DmnDeployment(resources = "org/flowable/dmn/engine/test/deployment/multiple_conclusions_DMN12.dmn")
    public void deploySingleDecisionDMN12() {
        DmnDecisionTable decision = repositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision").singleResult();
        Assert.assertNotNull(decision);
        Assert.assertEquals("decision", decision.getKey());
    }

    @Test
    @DmnDeployment(resources = "org/flowable/dmn/engine/test/deployment/multiple_conclusions.dmn")
    public void deploySingleDecisionAndValidateCache() {
        DmnDecisionTable decision = repositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision").singleResult();
        Assert.assertNotNull(decision);
        Assert.assertEquals("decision", decision.getKey());
        Assert.assertTrue(dmnEngineConfiguration.getDeploymentManager().getDecisionCache().contains(decision.getId()));
        dmnEngineConfiguration.getDeploymentManager().getDecisionCache().clear();
        Assert.assertFalse(dmnEngineConfiguration.getDeploymentManager().getDecisionCache().contains(decision.getId()));
        decision = repositoryService.getDecisionTable(decision.getId());
        Assert.assertNotNull(decision);
        Assert.assertEquals("decision", decision.getKey());
    }

    @Test
    @DmnDeployment(resources = "org/flowable/dmn/engine/test/deployment/multiple_conclusions.dmn")
    public void deploySingleDecisionAndValidateVersioning() {
        DmnDecisionTable decision = repositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision").singleResult();
        Assert.assertEquals(1, decision.getVersion());
        repositoryService.createDeployment().name("secondDeployment").addClasspathResource("org/flowable/dmn/engine/test/deployment/multiple_conclusions.dmn").deploy();
        decision = repositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision").singleResult();
        Assert.assertEquals(2, decision.getVersion());
    }

    @Test
    public void deploySingleDecisionInTenantAndValidateCache() throws Exception {
        repositoryService.createDeployment().name("secondDeployment").addClasspathResource("org/flowable/dmn/engine/test/deployment/multiple_conclusions.dmn").tenantId("testTenant").deploy();
        DmnDecisionTable decision = repositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision").decisionTableTenantId("testTenant").singleResult();
        Assert.assertNotNull(decision);
        Assert.assertEquals("decision", decision.getKey());
        Assert.assertEquals("testTenant", decision.getTenantId());
        Assert.assertEquals(1, decision.getVersion());
        Assert.assertTrue(dmnEngineConfiguration.getDeploymentManager().getDecisionCache().contains(decision.getId()));
        dmnEngineConfiguration.getDeploymentManager().getDecisionCache().clear();
        Assert.assertFalse(dmnEngineConfiguration.getDeploymentManager().getDecisionCache().contains(decision.getId()));
        decision = repositoryService.getDecisionTable(decision.getId());
        Assert.assertNotNull(decision);
        Assert.assertEquals("decision", decision.getKey());
        deleteDeployments();
    }

    @Test
    public void deploySingleDecisionInTenantAndValidateVersioning() throws Exception {
        repositoryService.createDeployment().name("secondDeployment").addClasspathResource("org/flowable/dmn/engine/test/deployment/multiple_conclusions.dmn").tenantId("testTenant").deploy();
        DmnDecisionTable decision = repositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision").decisionTableTenantId("testTenant").singleResult();
        Assert.assertEquals(1, decision.getVersion());
        repositoryService.createDeployment().name("secondDeployment").addClasspathResource("org/flowable/dmn/engine/test/deployment/multiple_conclusions.dmn").tenantId("testTenant").deploy();
        decision = repositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision").decisionTableTenantId("testTenant").singleResult();
        Assert.assertEquals(2, decision.getVersion());
        deleteDeployments();
    }

    @Test
    @DmnDeployment(resources = "org/flowable/dmn/engine/test/deployment/multiple_decisions.dmn")
    public void deployMultipleDecisions() throws Exception {
        DmnDecisionTable decision = repositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision").singleResult();
        Assert.assertNotNull(decision);
        Assert.assertEquals("decision", decision.getKey());
        Assert.assertTrue(dmnEngineConfiguration.getDeploymentManager().getDecisionCache().contains(decision.getId()));
        dmnEngineConfiguration.getDeploymentManager().getDecisionCache().clear();
        Assert.assertFalse(dmnEngineConfiguration.getDeploymentManager().getDecisionCache().contains(decision.getId()));
        decision = repositoryService.getDecisionTable(decision.getId());
        Assert.assertNotNull(decision);
        Assert.assertEquals("decision", decision.getKey());
        DmnDecisionTable decision2 = repositoryService.createDecisionTableQuery().latestVersion().decisionTableKey("decision2").singleResult();
        Assert.assertNotNull(decision2);
        Assert.assertEquals("decision2", decision2.getKey());
        Assert.assertTrue(dmnEngineConfiguration.getDeploymentManager().getDecisionCache().contains(decision2.getId()));
        dmnEngineConfiguration.getDeploymentManager().getDecisionCache().clear();
        Assert.assertFalse(dmnEngineConfiguration.getDeploymentManager().getDecisionCache().contains(decision2.getId()));
        decision2 = repositoryService.getDecisionTable(decision2.getId());
        Assert.assertNotNull(decision2);
        Assert.assertEquals("decision2", decision2.getKey());
    }

    @Test
    public void deployWithCategory() throws Exception {
        repositoryService.createDeployment().name("secondDeployment").addClasspathResource("org/flowable/dmn/engine/test/deployment/simple.dmn").tenantId("testTenant").category("TEST_DEPLOYMENT_CATEGORY").deploy();
        org.flowable.dmn.api.DmnDeployment deployment = repositoryService.createDeploymentQuery().deploymentCategory("TEST_DEPLOYMENT_CATEGORY").singleResult();
        Assert.assertNotNull(deployment);
        DmnDecisionTable decisionTable = repositoryService.createDecisionTableQuery().decisionTableKey("decision").singleResult();
        Assert.assertNotNull(decisionTable);
        repositoryService.setDecisionTableCategory(decisionTable.getId(), "TEST_DECISION_TABLE_CATEGORY");
        DmnDecisionTable decisionTableWithCategory = repositoryService.createDecisionTableQuery().decisionTableCategory("TEST_DECISION_TABLE_CATEGORY").singleResult();
        Assert.assertNotNull(decisionTableWithCategory);
        deleteDeployments();
    }

    @Test
    public void deploySingleDecisionWithParentDeploymentId() {
        org.flowable.dmn.api.DmnDeployment deployment = repositoryService.createDeployment().addClasspathResource("org/flowable/dmn/engine/test/deployment/multiple_conclusions.dmn").parentDeploymentId("someDeploymentId").deploy();
        org.flowable.dmn.api.DmnDeployment newDeployment = repositoryService.createDeployment().addClasspathResource("org/flowable/dmn/engine/test/deployment/multiple_conclusions.dmn").deploy();
        try {
            DmnDecisionTable decision = repositoryService.createDecisionTableQuery().deploymentId(deployment.getId()).singleResult();
            Assert.assertNotNull(decision);
            Assert.assertEquals("decision", decision.getKey());
            Assert.assertEquals(1, decision.getVersion());
            DmnDecisionTable newDecision = repositoryService.createDecisionTableQuery().deploymentId(newDeployment.getId()).singleResult();
            Assert.assertNotNull(newDecision);
            Assert.assertEquals("decision", newDecision.getKey());
            Assert.assertEquals(2, newDecision.getVersion());
            DecisionExecutionAuditContainer auditContainer = ruleService.createExecuteDecisionBuilder().decisionKey("decision").parentDeploymentId("someDeploymentId").executeWithAuditTrail();
            Assert.assertEquals("decision", auditContainer.getDecisionKey());
            Assert.assertEquals(1, auditContainer.getDecisionVersion());
            dmnEngineConfiguration.setAlwaysLookupLatestDefinitionVersion(true);
            auditContainer = ruleService.createExecuteDecisionBuilder().decisionKey("decision").executeWithAuditTrail();
            Assert.assertEquals("decision", auditContainer.getDecisionKey());
            Assert.assertEquals(2, auditContainer.getDecisionVersion());
        } finally {
            dmnEngineConfiguration.setAlwaysLookupLatestDefinitionVersion(false);
            repositoryService.deleteDeployment(deployment.getId());
            repositoryService.deleteDeployment(newDeployment.getId());
        }
    }

    @Test
    @DmnDeployment
    public void testNativeQuery() {
        org.flowable.dmn.api.DmnDeployment deployment = repositoryService.createDeploymentQuery().singleResult();
        Assert.assertNotNull(deployment);
        long count = repositoryService.createNativeDeploymentQuery().sql((((((("SELECT count(*) FROM " + (managementService.getTableName(org.flowable.dmn.engine.impl.persistence.entity.DmnDeploymentEntity.class))) + " D1, ") + (managementService.getTableName(org.flowable.dmn.engine.impl.persistence.entity.DecisionTableEntity.class))) + " D2 ") + "WHERE D1.ID_ = D2.DEPLOYMENT_ID_ ") + "AND D1.ID_ = #{deploymentId}")).parameter("deploymentId", deployment.getId()).count();
        Assert.assertEquals(2, count);
    }

    @Test
    @DmnDeployment
    public void testDeployWithXmlSuffix() {
        Assert.assertEquals(1, repositoryService.createDeploymentQuery().count());
    }
}

