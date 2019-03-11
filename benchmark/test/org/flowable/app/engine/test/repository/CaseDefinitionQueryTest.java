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


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.flowable.app.api.repository.AppDefinition;
import org.flowable.app.engine.test.FlowableAppTestCase;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class CaseDefinitionQueryTest extends FlowableAppTestCase {
    private String deploymentId1;

    private String deploymentId2;

    private String deploymentId3;

    private String deploymentId4;

    @Test
    public void testQueryNoParams() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().count());
    }

    @Test
    public void testQueryByDeploymentId() {
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId1).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId1).count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId2).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId2).count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId3).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId3).count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId4).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId4).count());
    }

    @Test
    public void testQueryByInvalidDeploymentId() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().deploymentId("invalid").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().deploymentId("invalid").count());
    }

    @Test
    public void testQueryByDeploymentIds() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet(Arrays.asList(deploymentId1, deploymentId2, deploymentId3, deploymentId4))).list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet(Arrays.asList(deploymentId1, deploymentId2, deploymentId3, deploymentId4))).count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList(deploymentId1))).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList(deploymentId1))).count());
        Assert.assertEquals(2, appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet(Arrays.asList(deploymentId2, deploymentId3))).list().size());
        Assert.assertEquals(2, appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet(Arrays.asList(deploymentId2, deploymentId3))).count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList(deploymentId3))).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList(deploymentId3))).count());
    }

    @Test
    public void testQueryByInvalidDeploymentIds() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList("invalid"))).list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList("invalid"))).count());
    }

    @Test
    public void testQueryByEmptyDeploymentIds() {
        try {
            appRepositoryService.createAppDefinitionQuery().deploymentIds(new HashSet()).list();
            Assert.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByAppDefinitionId() {
        List<String> appDefinitionIdsDeployment1 = getAppDefinitionIds(deploymentId1);
        List<String> appDefinitionIdsDeployment2 = getAppDefinitionIds(deploymentId2);
        List<String> appDefinitionIdsDeployment3 = getAppDefinitionIds(deploymentId3);
        Assert.assertNotNull(appRepositoryService.createAppDefinitionQuery().appDefinitionId(appDefinitionIdsDeployment1.get(0)).singleResult());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionId(appDefinitionIdsDeployment1.get(0)).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionId(appDefinitionIdsDeployment1.get(0)).count());
        Assert.assertNotNull(appRepositoryService.createAppDefinitionQuery().appDefinitionId(appDefinitionIdsDeployment2.get(0)).singleResult());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionId(appDefinitionIdsDeployment2.get(0)).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionId(appDefinitionIdsDeployment2.get(0)).count());
        Assert.assertNotNull(appRepositoryService.createAppDefinitionQuery().appDefinitionId(appDefinitionIdsDeployment3.get(0)).singleResult());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionId(appDefinitionIdsDeployment3.get(0)).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionId(appDefinitionIdsDeployment3.get(0)).count());
    }

    @Test
    public void testQueryByInvalidAppDefinitionId() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionId("invalid").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionId("invalid").count());
    }

    @Test
    public void testQueryByAppDefinitionIds() {
        List<String> appDefinitionIdsDeployment1 = getAppDefinitionIds(deploymentId1);
        List<String> appDefinitionIdsDeployment2 = getAppDefinitionIds(deploymentId2);
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionIds(new HashSet(appDefinitionIdsDeployment1)).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionIds(new HashSet(appDefinitionIdsDeployment1)).count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionIds(new HashSet(appDefinitionIdsDeployment2)).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionIds(new HashSet(appDefinitionIdsDeployment2)).count());
    }

    @Test
    public void testQueryByEmptyAppDefinitionIds() {
        try {
            appRepositoryService.createAppDefinitionQuery().appDefinitionIds(new HashSet()).list();
            Assert.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByInvalidAppDefinitionIds() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionIds(new HashSet(Arrays.asList("invalid1", "invalid2"))).list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionIds(new HashSet(Arrays.asList("invalid1", "invalid2"))).count());
    }

    @Test
    public void testQueryByAppDefinitionCategory() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionCategory("http://flowable.org/app").list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionCategory("http://flowable.org/app").count());
    }

    @Test
    public void testQueryByInvalidAppDefinitionCategory() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionCategory("invalid").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionCategory("invalid").count());
    }

    @Test
    public void testQueryByAppDefinitionCategoryLike() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionCategoryLike("http%").list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionCategoryLike("http%").count());
    }

    @Test
    public void testQueryByInvalidAppDefinitionCategoryLike() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionCategoryLike("invalid%").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionCategoryLike("invalid%n").count());
    }

    @Test
    public void testQueryByAppDefinitionCategoryNotEquals() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionCategoryNotEquals("another").list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionCategoryNotEquals("another").count());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionCategoryNotEquals("http://flowable.org/app").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionCategoryNotEquals("http://flowable.org/app").count());
    }

    @Test
    public void testQueryByAppDefinitionName() {
        Assert.assertEquals(3, appRepositoryService.createAppDefinitionQuery().appDefinitionName("Test app").list().size());
        Assert.assertEquals(3, appRepositoryService.createAppDefinitionQuery().appDefinitionName("Test app").count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionName("Full info app").list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionName("Full info app").count());
        Assert.assertEquals(deploymentId2, appRepositoryService.createAppDefinitionQuery().appDefinitionName("Full info app").singleResult().getDeploymentId());
    }

    @Test
    public void testQueryByInvalidAppDefinitionName() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionName("Case 3").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionName("Case 3").count());
    }

    @Test
    public void testQueryByAppDefinitionNameLike() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionNameLike("%app").list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionNameLike("%app").count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionNameLike("Full%").list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionNameLike("Full%").count());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionNameLike("invalid%").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionNameLike("invalid%").count());
    }

    @Test
    public void testQueryByAppDefinitionKey() {
        Assert.assertEquals(3, appRepositoryService.createAppDefinitionQuery().appDefinitionKey("testApp").list().size());
        Assert.assertEquals(3, appRepositoryService.createAppDefinitionQuery().appDefinitionKey("testApp").count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionKey("fullInfoApp").list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionKey("fullInfoApp").count());
    }

    @Test
    public void testQueryByInvalidAppDefinitionKey() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionKey("invalid").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionKey("invalid").count());
    }

    @Test
    public void testQueryByAppDefinitionKeyLike() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionKeyLike("%App").list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionKeyLike("%App").count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionKeyLike("full%").list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionKeyLike("full%").count());
    }

    @Test
    public void testQueryByInvalidAppDefinitionKeyLike() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionKeyLike("%invalid").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionKeyLike("%invalid").count());
    }

    @Test
    public void testQueryByAppDefinitionVersion() {
        Assert.assertEquals(2, appRepositoryService.createAppDefinitionQuery().appDefinitionVersion(1).list().size());
        Assert.assertEquals(2, appRepositoryService.createAppDefinitionQuery().appDefinitionVersion(1).count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionVersion(2).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionVersion(2).count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionVersion(2).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionVersion(2).count());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionVersion(4).list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionVersion(4).count());
    }

    @Test
    public void testQueryByAppDefinitionVersionGreaterThan() {
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionGreaterThan(2).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionGreaterThan(2).count());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionGreaterThan(3).list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionGreaterThan(3).count());
    }

    @Test
    public void testQueryByAppDefinitionVersionGreaterThanOrEquals() {
        Assert.assertEquals(2, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionGreaterThanOrEquals(2).list().size());
        Assert.assertEquals(2, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionGreaterThanOrEquals(2).count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionGreaterThanOrEquals(3).list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionGreaterThanOrEquals(3).count());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionGreaterThanOrEquals(4).list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionGreaterThanOrEquals(4).count());
    }

    @Test
    public void testQueryByAppDefinitionVersionLowerThan() {
        Assert.assertEquals(2, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionLowerThan(2).list().size());
        Assert.assertEquals(2, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionLowerThan(2).count());
        Assert.assertEquals(3, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionLowerThan(3).list().size());
        Assert.assertEquals(3, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionLowerThan(3).count());
    }

    @Test
    public void testQueryByAppDefinitionVersionLowerThanOrEquals() {
        Assert.assertEquals(3, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionLowerThanOrEquals(2).list().size());
        Assert.assertEquals(3, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionLowerThanOrEquals(2).count());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionLowerThanOrEquals(3).list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionLowerThanOrEquals(3).count());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionLowerThanOrEquals(4).list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionVersionLowerThanOrEquals(4).count());
    }

    @Test
    public void testQueryByLatestVersion() {
        Assert.assertEquals(2, appRepositoryService.createAppDefinitionQuery().latestVersion().list().size());
        Assert.assertEquals(2, appRepositoryService.createAppDefinitionQuery().latestVersion().count());
    }

    @Test
    public void testQueryByLatestVersionAndKey() {
        AppDefinition appDefinition = appRepositoryService.createAppDefinitionQuery().appDefinitionKey("testApp").latestVersion().singleResult();
        Assert.assertNotNull(appDefinition);
        Assert.assertEquals(3, appDefinition.getVersion());
        Assert.assertEquals(deploymentId4, appDefinition.getDeploymentId());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionKey("testApp").latestVersion().list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionKey("testApp").latestVersion().count());
    }

    @Test
    public void testQueryByAppDefinitionResourceName() {
        Assert.assertEquals(3, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceName("org/flowable/app/engine/test/test.app").list().size());
        Assert.assertEquals(3, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceName("org/flowable/app/engine/test/test.app").count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceName("org/flowable/app/engine/test/fullinfo.app").list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceName("org/flowable/app/engine/test/fullinfo.app").count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceName("org/flowable/app/engine/test/test.app").latestVersion().list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceName("org/flowable/app/engine/test/test.app").latestVersion().count());
    }

    @Test
    public void testQueryByInvalidAppDefinitionResourceName() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceName("invalid.app").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceName("invalid.app").count());
    }

    @Test
    public void testQueryByAppDefinitionResourceNameLike() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceNameLike("%.app").list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceNameLike("%.app").count());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceNameLike("%full%").list().size());
        Assert.assertEquals(1, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceNameLike("%full%").count());
    }

    @Test
    public void testQueryByInvalidAppDefinitionResourceNameLike() {
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceNameLike("%invalid%").list().size());
        Assert.assertEquals(0, appRepositoryService.createAppDefinitionQuery().appDefinitionResourceNameLike("%invalid%").count());
    }

    @Test
    public void testQueryOrderByAppDefinitionCategory() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionCategory().asc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionCategory().asc().count());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionCategory().desc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionCategory().desc().count());
    }

    @Test
    public void testQueryOrderByCaseDefinitionKey() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionKey().asc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionKey().asc().count());
        List<AppDefinition> appDefinitions = appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionKey().asc().list();
        for (int i = 0; i < (appDefinitions.size()); i++) {
            if (i > 0) {
                Assert.assertEquals("testApp", appDefinitions.get(i).getKey());
            } else {
                Assert.assertEquals("fullInfoApp", appDefinitions.get(i).getKey());
            }
        }
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionKey().desc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionKey().desc().count());
        appDefinitions = appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionKey().desc().list();
        for (int i = 0; i < (appDefinitions.size()); i++) {
            if (i <= 2) {
                Assert.assertEquals("testApp", appDefinitions.get(i).getKey());
            } else {
                Assert.assertEquals("fullInfoApp", appDefinitions.get(i).getKey());
            }
        }
    }

    @Test
    public void testQueryOrderByAppDefinitionId() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionId().asc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionId().asc().count());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionId().desc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionId().desc().count());
    }

    @Test
    public void testQueryOrderByAppDefinitionName() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionName().asc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionName().asc().count());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionName().desc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionName().desc().count());
    }

    @Test
    public void testQueryOrderByAppDefinitionDeploymentId() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByDeploymentId().asc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByDeploymentId().asc().count());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByDeploymentId().desc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByDeploymentId().desc().count());
    }

    @Test
    public void testQueryOrderByAppDefinitionVersion() {
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionVersion().asc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionVersion().asc().count());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionVersion().desc().list().size());
        Assert.assertEquals(4, appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionVersion().desc().count());
        List<AppDefinition> appDefinitions = appRepositoryService.createAppDefinitionQuery().orderByAppDefinitionVersion().desc().list();
        Assert.assertEquals(3, appDefinitions.get(0).getVersion());
        Assert.assertEquals(2, appDefinitions.get(1).getVersion());
        Assert.assertEquals(1, appDefinitions.get(2).getVersion());
        Assert.assertEquals(1, appDefinitions.get(3).getVersion());
        appDefinitions = appRepositoryService.createAppDefinitionQuery().latestVersion().orderByAppDefinitionVersion().asc().list();
        Assert.assertEquals(1, appDefinitions.get(0).getVersion());
        Assert.assertEquals("fullInfoApp", appDefinitions.get(0).getKey());
        Assert.assertEquals(3, appDefinitions.get(1).getVersion());
        Assert.assertEquals("testApp", appDefinitions.get(1).getKey());
    }
}

