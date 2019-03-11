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
package org.flowable.app.engine.test.repository;


import org.flowable.app.api.repository.AppDeployment;
import org.flowable.app.engine.test.FlowableAppTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class DeploymentQueryTest extends FlowableAppTestCase {
    private String deploymentId1;

    private String deploymentId2;

    @Test
    public void testQueryNoParams() {
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().list().size());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().count());
        boolean deployment1Found = false;
        boolean deployment2Found = false;
        for (AppDeployment cmmnDeployment : appRepositoryService.createDeploymentQuery().list()) {
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
        Assert.assertNotNull(appRepositoryService.createDeploymentQuery().deploymentId(deploymentId1).singleResult());
        Assert.assertEquals(deploymentId1, appRepositoryService.createDeploymentQuery().deploymentId(deploymentId1).singleResult().getId());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentId(deploymentId1).list().size());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentId(deploymentId1).count());
        Assert.assertNotNull(appRepositoryService.createDeploymentQuery().deploymentId(deploymentId2).singleResult());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentId(deploymentId2).list().size());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentId(deploymentId2).count());
    }

    @Test
    public void testQueryByInvalidDeploymentId() {
        Assert.assertNull(appRepositoryService.createDeploymentQuery().deploymentId("invalid").singleResult());
        Assert.assertEquals(0, appRepositoryService.createDeploymentQuery().deploymentId("invalid").list().size());
        Assert.assertEquals(0, appRepositoryService.createDeploymentQuery().deploymentId("invalid").count());
    }

    @Test
    public void testQueryByDeploymentName() {
        Assert.assertNotNull(appRepositoryService.createDeploymentQuery().deploymentName("testName").singleResult());
        Assert.assertEquals(deploymentId2, appRepositoryService.createDeploymentQuery().deploymentName("testName").singleResult().getId());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentName("testName").list().size());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentName("testName").count());
    }

    @Test
    public void testQueryByInvalidDeploymentName() {
        Assert.assertNull(appRepositoryService.createDeploymentQuery().deploymentName("invalid").singleResult());
        Assert.assertEquals(0, appRepositoryService.createDeploymentQuery().deploymentName("invalid").list().size());
        Assert.assertEquals(0, appRepositoryService.createDeploymentQuery().deploymentName("invalid").count());
    }

    @Test
    public void testQueryByDeploymentNameLike() {
        Assert.assertNotNull(appRepositoryService.createDeploymentQuery().deploymentNameLike("test%").singleResult());
        Assert.assertEquals(deploymentId2, appRepositoryService.createDeploymentQuery().deploymentNameLike("test%").singleResult().getId());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentNameLike("test%").list().size());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentNameLike("test%").count());
        Assert.assertNull(appRepositoryService.createDeploymentQuery().deploymentNameLike("inval%").singleResult());
        Assert.assertEquals(0, appRepositoryService.createDeploymentQuery().deploymentNameLike("inval%").list().size());
        Assert.assertEquals(0, appRepositoryService.createDeploymentQuery().deploymentNameLike("inval%").count());
    }

    @Test
    public void testQueryByDeploymentCategory() {
        Assert.assertNotNull(appRepositoryService.createDeploymentQuery().deploymentCategory("testCategory").singleResult());
        Assert.assertEquals(deploymentId2, appRepositoryService.createDeploymentQuery().deploymentCategory("testCategory").singleResult().getId());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentCategory("testCategory").list().size());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentCategory("testCategory").count());
    }

    @Test
    public void testQueryByInvalidDeploymentCategory() {
        Assert.assertNull(appRepositoryService.createDeploymentQuery().deploymentCategory("invalid").singleResult());
        Assert.assertEquals(0, appRepositoryService.createDeploymentQuery().deploymentCategory("invalid").list().size());
        Assert.assertEquals(0, appRepositoryService.createDeploymentQuery().deploymentCategory("invalid").count());
    }

    @Test
    public void testQueryByDeploymentCategoryNotEquals() {
        Assert.assertNotNull(appRepositoryService.createDeploymentQuery().deploymentCategoryNotEquals("testCategory").singleResult());
        Assert.assertEquals(deploymentId1, appRepositoryService.createDeploymentQuery().deploymentCategoryNotEquals("testCategory").singleResult().getId());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentCategoryNotEquals("testCategory").list().size());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentCategoryNotEquals("testCategory").count());
    }

    @Test
    public void testQueryByDeploymentNameAndCategory() {
        Assert.assertNotNull(appRepositoryService.createDeploymentQuery().deploymentName("testName").deploymentCategory("testCategory").singleResult());
        Assert.assertEquals(deploymentId2, appRepositoryService.createDeploymentQuery().deploymentName("testName").deploymentCategory("testCategory").singleResult().getId());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentName("testName").deploymentCategory("testCategory").list().size());
        Assert.assertEquals(1, appRepositoryService.createDeploymentQuery().deploymentName("testName").deploymentCategory("testCategory").count());
    }

    @Test
    public void testOrdering() {
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymentId().asc().list().size());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymentId().asc().count());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymentId().desc().list().size());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymentId().desc().count());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymenTime().asc().list().size());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymenTime().asc().count());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymenTime().desc().list().size());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymenTime().desc().count());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymentName().asc().list().size());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymentName().asc().count());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymentName().desc().list().size());
        Assert.assertEquals(2, appRepositoryService.createDeploymentQuery().orderByDeploymentName().desc().count());
    }
}

