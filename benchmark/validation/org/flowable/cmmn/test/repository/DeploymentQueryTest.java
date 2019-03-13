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


import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class DeploymentQueryTest extends FlowableCmmnTestCase {
    private String deploymentId1;

    private String deploymentId2;

    @Test
    public void testQueryNoParams() {
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().count());
        boolean deployment1Found = false;
        boolean deployment2Found = false;
        for (CmmnDeployment cmmnDeployment : cmmnRepositoryService.createDeploymentQuery().list()) {
            if (deploymentId1.equals(cmmnDeployment.getId())) {
                deployment1Found = true;
            } else
                if (deploymentId2.equals(cmmnDeployment.getId())) {
                    deployment2Found = true;
                }

        }
        Assert.assertTrue(deployment1Found);
        Assert.assertTrue(deployment2Found);
    }

    @Test
    public void testQueryByDeploymentId() {
        Assert.assertNotNull(cmmnRepositoryService.createDeploymentQuery().deploymentId(deploymentId1).singleResult());
        Assert.assertEquals(deploymentId1, cmmnRepositoryService.createDeploymentQuery().deploymentId(deploymentId1).singleResult().getId());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentId(deploymentId1).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentId(deploymentId1).count());
        Assert.assertNotNull(cmmnRepositoryService.createDeploymentQuery().deploymentId(deploymentId2).singleResult());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentId(deploymentId2).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentId(deploymentId2).count());
    }

    @Test
    public void testQueryByInvalidDeploymentId() {
        Assert.assertNull(cmmnRepositoryService.createDeploymentQuery().deploymentId("invalid").singleResult());
        Assert.assertEquals(0, cmmnRepositoryService.createDeploymentQuery().deploymentId("invalid").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createDeploymentQuery().deploymentId("invalid").count());
    }

    @Test
    public void testQueryByDeploymentName() {
        Assert.assertNotNull(cmmnRepositoryService.createDeploymentQuery().deploymentName("testName").singleResult());
        Assert.assertEquals(deploymentId2, cmmnRepositoryService.createDeploymentQuery().deploymentName("testName").singleResult().getId());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentName("testName").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentName("testName").count());
    }

    @Test
    public void testQueryByInvalidDeploymentName() {
        Assert.assertNull(cmmnRepositoryService.createDeploymentQuery().deploymentName("invalid").singleResult());
        Assert.assertEquals(0, cmmnRepositoryService.createDeploymentQuery().deploymentName("invalid").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createDeploymentQuery().deploymentName("invalid").count());
    }

    @Test
    public void testQueryByDeploymentNameLike() {
        Assert.assertNotNull(cmmnRepositoryService.createDeploymentQuery().deploymentNameLike("test%").singleResult());
        Assert.assertEquals(deploymentId2, cmmnRepositoryService.createDeploymentQuery().deploymentNameLike("test%").singleResult().getId());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentNameLike("test%").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentNameLike("test%").count());
        Assert.assertNull(cmmnRepositoryService.createDeploymentQuery().deploymentNameLike("inval%").singleResult());
        Assert.assertEquals(0, cmmnRepositoryService.createDeploymentQuery().deploymentNameLike("inval%").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createDeploymentQuery().deploymentNameLike("inval%").count());
    }

    @Test
    public void testQueryByDeploymentCategory() {
        Assert.assertNotNull(cmmnRepositoryService.createDeploymentQuery().deploymentCategory("testCategory").singleResult());
        Assert.assertEquals(deploymentId2, cmmnRepositoryService.createDeploymentQuery().deploymentCategory("testCategory").singleResult().getId());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentCategory("testCategory").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentCategory("testCategory").count());
    }

    @Test
    public void testQueryByInvalidDeploymentCategory() {
        Assert.assertNull(cmmnRepositoryService.createDeploymentQuery().deploymentCategory("invalid").singleResult());
        Assert.assertEquals(0, cmmnRepositoryService.createDeploymentQuery().deploymentCategory("invalid").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createDeploymentQuery().deploymentCategory("invalid").count());
    }

    @Test
    public void testQueryByDeploymentCategoryNotEquals() {
        Assert.assertNotNull(cmmnRepositoryService.createDeploymentQuery().deploymentCategoryNotEquals("testCategory").singleResult());
        Assert.assertEquals(deploymentId1, cmmnRepositoryService.createDeploymentQuery().deploymentCategoryNotEquals("testCategory").singleResult().getId());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentCategoryNotEquals("testCategory").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentCategoryNotEquals("testCategory").count());
    }

    @Test
    public void testQueryByDeploymentNameAndCategory() {
        Assert.assertNotNull(cmmnRepositoryService.createDeploymentQuery().deploymentName("testName").deploymentCategory("testCategory").singleResult());
        Assert.assertEquals(deploymentId2, cmmnRepositoryService.createDeploymentQuery().deploymentName("testName").deploymentCategory("testCategory").singleResult().getId());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentName("testName").deploymentCategory("testCategory").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createDeploymentQuery().deploymentName("testName").deploymentCategory("testCategory").count());
    }

    @Test
    public void testOrdering() {
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymentId().asc().list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymentId().asc().count());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymentId().desc().list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymentId().desc().count());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymenTime().asc().list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymenTime().asc().count());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymenTime().desc().list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymenTime().desc().count());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymentName().asc().list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymentName().asc().count());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymentName().desc().list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createDeploymentQuery().orderByDeploymentName().desc().count());
    }
}

