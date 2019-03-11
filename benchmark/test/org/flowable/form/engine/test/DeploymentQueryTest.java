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
package org.flowable.form.engine.test;


import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class DeploymentQueryTest extends AbstractFlowableFormTest {
    private String deploymentId1;

    private String deploymentId2;

    private String deploymentId3;

    private String formModel = "{\n" + (((((((((((("    \"key\": \"$changeme$\",\n" + "    \"name\": \"My first form\",\n") + "    \"fields\": [\n") + "        {\n") + "            \"id\": \"input1\",\n") + "            \"name\": \"Input1\",\n") + "            \"type\": \"text\",\n") + "            \"required\": false,\n") + "            \"placeholder\": \"empty\"\n") + "        }\n") + "    ]\n") + "}\n") + "");

    @Test
    public void testQueryById() {
        Assert.assertNotNull(repositoryService.createDeploymentQuery().deploymentId(deploymentId1).singleResult());
        Assertions.assertEquals(1, repositoryService.createDeploymentQuery().deploymentId(deploymentId1).list().size());
        Assertions.assertEquals(1, repositoryService.createDeploymentQuery().deploymentId(deploymentId1).count());
        Assertions.assertNull(repositoryService.createDeploymentQuery().deploymentId("invalid").singleResult());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentId("invalid").list().size());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentId("invalid").count());
    }

    @Test
    public void testQueryByName() {
        Assert.assertNotNull(repositoryService.createDeploymentQuery().deploymentName("test2.form").singleResult());
        Assertions.assertEquals(1, repositoryService.createDeploymentQuery().deploymentName("test2.form").list().size());
        Assertions.assertEquals(1, repositoryService.createDeploymentQuery().deploymentName("test2.form").count());
        Assertions.assertNull(repositoryService.createDeploymentQuery().deploymentName("invalid").singleResult());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentName("invalid").list().size());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentName("invalid").count());
    }

    @Test
    public void testQueryByNameLike() {
        Assertions.assertEquals(3, repositoryService.createDeploymentQuery().deploymentNameLike("test%").list().size());
        Assertions.assertEquals(3, repositoryService.createDeploymentQuery().deploymentNameLike("test%").count());
        Assertions.assertNull(repositoryService.createDeploymentQuery().deploymentNameLike("inva%").singleResult());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentNameLike("inva").list().size());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentNameLike("inva").count());
    }

    @Test
    public void testQueryByCategory() {
        Assert.assertNotNull(repositoryService.createDeploymentQuery().deploymentCategory("testCategoryC").singleResult());
        Assertions.assertEquals(1, repositoryService.createDeploymentQuery().deploymentCategory("testCategoryC").list().size());
        Assertions.assertEquals(1, repositoryService.createDeploymentQuery().deploymentCategory("testCategoryC").count());
        Assertions.assertNull(repositoryService.createDeploymentQuery().deploymentCategory("inva%").singleResult());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentCategory("inva%").list().size());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentCategory("inva%").count());
    }

    @Test
    public void testQueryByCategoryNotEquals() {
        Assertions.assertEquals(2, repositoryService.createDeploymentQuery().deploymentCategoryNotEquals("testCategoryC").list().size());
        Assertions.assertEquals(2, repositoryService.createDeploymentQuery().deploymentCategoryNotEquals("testCategoryC").count());
        Assertions.assertEquals(3, repositoryService.createDeploymentQuery().deploymentCategoryNotEquals("invalid").list().size());
        Assertions.assertEquals(3, repositoryService.createDeploymentQuery().deploymentCategoryNotEquals("invalid").count());
    }

    @Test
    public void testQueryByTenantId() {
        Assertions.assertEquals(2, repositoryService.createDeploymentQuery().deploymentTenantId("tenantA").list().size());
        Assertions.assertEquals(2, repositoryService.createDeploymentQuery().deploymentTenantId("tenantA").count());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentTenantId("invalid").list().size());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentTenantId("invalid").count());
    }

    @Test
    public void testQueryByTenantIdLike() {
        Assertions.assertEquals(3, repositoryService.createDeploymentQuery().deploymentTenantIdLike("tenant%").list().size());
        Assertions.assertEquals(3, repositoryService.createDeploymentQuery().deploymentTenantIdLike("tenant%").count());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentTenantIdLike("invalid").list().size());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().deploymentTenantIdLike("invalid").count());
    }

    @Test
    public void testQueryByDecisionTableKey() {
        Assertions.assertEquals(1, repositoryService.createDeploymentQuery().formDefinitionKey("form2").list().size());
        Assertions.assertEquals(1, repositoryService.createDeploymentQuery().formDefinitionKey("form2").count());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().formDefinitionKey("invalid").list().size());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().formDefinitionKey("invalid").count());
    }

    @Test
    public void testQueryByDecisionTableKeyLike() {
        Assertions.assertEquals(3, repositoryService.createDeploymentQuery().formDefinitionKeyLike("form%").list().size());
        Assertions.assertEquals(3, repositoryService.createDeploymentQuery().formDefinitionKeyLike("form%").count());
        Assertions.assertEquals(1, repositoryService.createDeploymentQuery().formDefinitionKeyLike("form%").listPage(0, 1).size());
        Assertions.assertEquals(2, repositoryService.createDeploymentQuery().formDefinitionKeyLike("form%").listPage(0, 2).size());
        Assertions.assertEquals(2, repositoryService.createDeploymentQuery().formDefinitionKeyLike("form%").listPage(1, 2).size());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().formDefinitionKeyLike("inva%").list().size());
        Assertions.assertEquals(0, repositoryService.createDeploymentQuery().formDefinitionKeyLike("inva%").count());
    }
}

