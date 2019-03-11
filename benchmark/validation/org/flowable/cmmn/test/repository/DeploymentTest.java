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
package org.flowable.cmmn.test.repository;


import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.engine.impl.persistence.entity.deploy.CaseDefinitionCacheEntry;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.cmmn.engine.test.org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.model.CmmnModel;
import org.flowable.cmmn.model.PlanItem;
import org.flowable.common.engine.impl.persistence.deploy.DefaultDeploymentCache;
import org.flowable.common.engine.impl.persistence.deploy.DeploymentCache;
import org.h2.util.IOUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class DeploymentTest extends FlowableCmmnTestCase {
    /**
     * Simplest test possible: deploy the simple-case.cmmn (from the cmmn-converter module) and see if
     * - a deployment exists
     * - a resouce exists
     * - a case definition was created
     * - that case definition is in the cache
     * - case definition properties set
     */
    @Test
    public void testCaseDefinitionDeployed() throws Exception {
        DeploymentCache<CaseDefinitionCacheEntry> caseDefinitionCache = cmmnEngineConfiguration.getCaseDefinitionCache();
        caseDefinitionCache.clear();
        String deploymentId = cmmnRepositoryService.createDeployment().addClasspathResource("org/flowable/cmmn/test/repository/DeploymentTest.testCaseDefinitionDeployed.cmmn").deploy().getId();
        CmmnDeployment cmmnDeployment = cmmnRepositoryService.createDeploymentQuery().singleResult();
        Assert.assertNotNull(cmmnDeployment);
        List<String> resourceNames = cmmnRepositoryService.getDeploymentResourceNames(cmmnDeployment.getId());
        Assert.assertEquals(1, resourceNames.size());
        Assert.assertEquals("org/flowable/cmmn/test/repository/DeploymentTest.testCaseDefinitionDeployed.cmmn", resourceNames.get(0));
        InputStream inputStream = cmmnRepositoryService.getResourceAsStream(cmmnDeployment.getId(), resourceNames.get(0));
        Assert.assertNotNull(inputStream);
        inputStream.close();
        Assert.assertEquals(1, ((DefaultDeploymentCache<CaseDefinitionCacheEntry>) (caseDefinitionCache)).getAll().size());
        CaseDefinitionCacheEntry cachedCaseDefinition = ((DefaultDeploymentCache<CaseDefinitionCacheEntry>) (caseDefinitionCache)).getAll().iterator().next();
        Assert.assertNotNull(cachedCaseDefinition.getCase());
        Assert.assertNotNull(cachedCaseDefinition.getCmmnModel());
        Assert.assertNotNull(cachedCaseDefinition.getCaseDefinition());
        CaseDefinition caseDefinition = cachedCaseDefinition.getCaseDefinition();
        Assert.assertNotNull(caseDefinition.getId());
        Assert.assertNotNull(caseDefinition.getDeploymentId());
        Assert.assertNotNull(caseDefinition.getKey());
        Assert.assertNotNull(caseDefinition.getResourceName());
        Assert.assertTrue(((caseDefinition.getVersion()) > 0));
        caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(cmmnDeployment.getId()).singleResult();
        Assert.assertNotNull(caseDefinition.getId());
        Assert.assertNotNull(caseDefinition.getDeploymentId());
        Assert.assertNotNull(caseDefinition.getKey());
        Assert.assertNotNull(caseDefinition.getResourceName());
        Assert.assertEquals(1, caseDefinition.getVersion());
        CmmnModel cmmnModel = cmmnRepositoryService.getCmmnModel(caseDefinition.getId());
        Assert.assertNotNull(cmmnModel);
        // CmmnParser should have added behavior to plan items
        for (PlanItem planItem : cmmnModel.getPrimaryCase().getPlanModel().getPlanItems()) {
            Assert.assertNotNull(planItem.getBehavior());
        }
        cmmnRepositoryService.deleteDeployment(deploymentId, true);
    }

    @Test
    @org.flowable.cmmn.engine.test.CmmnDeployment
    public void testCaseDefinitionDI() throws Exception {
        CmmnDeployment cmmnDeployment = cmmnRepositoryService.createDeploymentQuery().singleResult();
        Assert.assertNotNull(cmmnDeployment);
        List<String> resourceNames = cmmnRepositoryService.getDeploymentResourceNames(cmmnDeployment.getId());
        Assert.assertEquals(2, resourceNames.size());
        String resourceName = "org/flowable/cmmn/test/repository/DeploymentTest.testCaseDefinitionDI.cmmn";
        String diagramResourceName = "org/flowable/cmmn/test/repository/DeploymentTest.testCaseDefinitionDI.caseB.png";
        Assert.assertTrue(resourceNames.contains(resourceName));
        Assert.assertTrue(resourceNames.contains(diagramResourceName));
        InputStream inputStream = cmmnRepositoryService.getResourceAsStream(cmmnDeployment.getId(), resourceName);
        Assert.assertNotNull(inputStream);
        IOUtils.closeSilently(inputStream);
        InputStream diagramInputStream = cmmnRepositoryService.getResourceAsStream(cmmnDeployment.getId(), diagramResourceName);
        Assert.assertNotNull(diagramInputStream);
        IOUtils.closeSilently(diagramInputStream);
        CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(cmmnDeployment.getId()).singleResult();
        InputStream caseDiagramInputStream = cmmnRepositoryService.getCaseDiagram(caseDefinition.getId());
        Assert.assertNotNull(caseDiagramInputStream);
        IOUtils.closeSilently(caseDiagramInputStream);
    }

    @Test
    public void testBulkInsertCmmnDeployments() {
        List<String> deploymentIds = cmmnEngineConfiguration.getCommandExecutor().execute(( commandContext) -> {
            CmmnDeployment deployment1 = cmmnRepositoryService.createDeployment().name("First deployment").key("one-human").category("test").addClasspathResource("org/flowable/cmmn/test/one-human-task-model.cmmn").deploy();
            CmmnDeployment deployment2 = cmmnRepositoryService.createDeployment().name("Second deployment").key("example-task").addClasspathResource("org/flowable/cmmn/test/example-task-model.cmmn").deploy();
            return Arrays.asList(deployment1.getId(), deployment2.getId());
        });
        assertThat(cmmnRepositoryService.getDeploymentResourceNames(deploymentIds.get(0))).containsExactlyInAnyOrder("org/flowable/cmmn/test/one-human-task-model.cmmn");
        assertThat(cmmnRepositoryService.getDeploymentResourceNames(deploymentIds.get(1))).containsExactlyInAnyOrder("org/flowable/cmmn/test/example-task-model.cmmn");
        assertThat(cmmnRepositoryService.createDeploymentQuery().list()).as("Deployment time not null").allSatisfy(( deployment) -> assertThat(deployment.getDeploymentTime()).as(deployment.getName()).isNotNull()).extracting(org.flowable.cmmn.api.repository.CmmnDeployment::getId, org.flowable.cmmn.api.repository.CmmnDeployment::getName, org.flowable.cmmn.api.repository.CmmnDeployment::getKey, org.flowable.cmmn.api.repository.CmmnDeployment::getCategory).as("id, name, key, category").containsExactlyInAnyOrder(tuple(deploymentIds.get(0), "First deployment", "one-human", "test"), tuple(deploymentIds.get(1), "Second deployment", "example-task", null));
        deploymentIds.forEach(( deploymentId) -> cmmnRepositoryService.deleteDeployment(deploymentId, true));
    }
}

