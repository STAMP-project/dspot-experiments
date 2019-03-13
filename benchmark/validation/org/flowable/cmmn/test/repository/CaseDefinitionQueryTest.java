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


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class CaseDefinitionQueryTest extends FlowableCmmnTestCase {
    private String deploymentId1;

    private String deploymentId2;

    private String deploymentId3;

    @Test
    public void testQueryNoParams() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().count());
    }

    @Test
    public void testQueryByDeploymentId() {
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId1).list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId1).count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId2).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId2).count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId3).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId3).count());
    }

    @Test
    public void testQueryByInvalidDeploymentId() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().deploymentId("invalid").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().deploymentId("invalid").count());
    }

    @Test
    public void testQueryByDeploymentIds() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet(Arrays.asList(deploymentId1, deploymentId2, deploymentId3))).list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet(Arrays.asList(deploymentId1, deploymentId2, deploymentId3))).count());
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList(deploymentId1))).list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList(deploymentId1))).count());
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet(Arrays.asList(deploymentId2, deploymentId3))).list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet(Arrays.asList(deploymentId2, deploymentId3))).count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList(deploymentId3))).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList(deploymentId3))).count());
    }

    @Test
    public void testQueryByInvalidDeploymentIds() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList("invalid"))).list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet(Collections.singletonList("invalid"))).count());
    }

    @Test
    public void testQueryByEmptyDeploymentIds() {
        try {
            cmmnRepositoryService.createCaseDefinitionQuery().deploymentIds(new HashSet()).list();
            Assert.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByCaseDefinitionId() {
        List<String> caseDefinitionIdsDeployment1 = getCaseDefinitionIds(deploymentId1);
        List<String> caseDefinitionIdsDeployment2 = getCaseDefinitionIds(deploymentId2);
        List<String> caseDefinitionIdsDeployment3 = getCaseDefinitionIds(deploymentId3);
        Assert.assertNotNull(cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment1.get(0)).singleResult());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment1.get(0)).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment1.get(0)).count());
        Assert.assertNotNull(cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment1.get(1)).singleResult());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment1.get(1)).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment1.get(1)).count());
        Assert.assertNotNull(cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment2.get(0)).singleResult());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment2.get(0)).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment2.get(0)).count());
        Assert.assertNotNull(cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment3.get(0)).singleResult());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment3.get(0)).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseDefinitionIdsDeployment3.get(0)).count());
    }

    @Test
    public void testQueryByInvalidCaseDefinitionId() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId("invalid").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId("invalid").count());
    }

    @Test
    public void testQueryByCaseDefinitionIds() {
        List<String> caseDefinitionIdsDeployment1 = getCaseDefinitionIds(deploymentId1);
        List<String> caseDefinitionIdsDeployment2 = getCaseDefinitionIds(deploymentId2);
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionIds(new HashSet(caseDefinitionIdsDeployment1)).list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionIds(new HashSet(caseDefinitionIdsDeployment1)).count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionIds(new HashSet(caseDefinitionIdsDeployment2)).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionIds(new HashSet(caseDefinitionIdsDeployment2)).count());
    }

    @Test
    public void testQueryByEmptyCaseDefinitionIds() {
        try {
            cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionIds(new HashSet()).list();
            Assert.fail();
        } catch (FlowableIllegalArgumentException e) {
        }
    }

    @Test
    public void testQueryByInvalidCaseDefinitionIds() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionIds(new HashSet(Arrays.asList("invalid1", "invalid2"))).list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionIds(new HashSet(Arrays.asList("invalid1", "invalid2"))).count());
    }

    @Test
    public void testQueryByCaseDefinitionCategory() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategory("http://flowable.org/cmmn").list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategory("http://flowable.org/cmmn").count());
    }

    @Test
    public void testQueryByInvalidCaseDefinitionCategory() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategory("invalid").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategory("invalid").count());
    }

    @Test
    public void testQueryByCaseDefinitionCategoryLike() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategoryLike("http%").list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategoryLike("http%n").count());
    }

    @Test
    public void testQueryByInvalidCaseDefinitionCategoryLike() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategoryLike("invalid%").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategoryLike("invalid%n").count());
    }

    @Test
    public void testQueryByCaseDefinitionCategoryNotEquals() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategoryNotEquals("another").list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategoryNotEquals("another").count());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategoryNotEquals("http://flowable.org/cmmn").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionCategoryNotEquals("http://flowable.org/cmmn").count());
    }

    @Test
    public void testQueryByCaseDefinitionName() {
        Assert.assertEquals(3, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionName("Case 1").list().size());
        Assert.assertEquals(3, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionName("Case 1").count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionName("Case 2").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionName("Case 2").count());
        Assert.assertEquals(deploymentId1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionName("Case 2").singleResult().getDeploymentId());
    }

    @Test
    public void testQueryByInvalidCaseDefinitionName() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionName("Case 3").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionName("Case 3").count());
    }

    @Test
    public void testQueryByCaseDefinitionNameLike() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionNameLike("Ca%").list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionNameLike("Ca%").count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionNameLike("%2").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionNameLike("%2").count());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionNameLike("invalid%").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionNameLike("invalid%").count());
    }

    @Test
    public void testQueryByCaseDefinitionKey() {
        Assert.assertEquals(3, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("myCase").list().size());
        Assert.assertEquals(3, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("myCase").count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("myCase2").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("myCase2").count());
    }

    @Test
    public void testQueryByInvalidCaseDefinitionKey() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("invalid").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("invalid").count());
    }

    @Test
    public void testQueryByCaseDefinitionKeyLike() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKeyLike("my%").list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKeyLike("my%").count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKeyLike("%2").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKeyLike("%2").count());
    }

    @Test
    public void testQueryByInvalidCaseDefinitionKeyLike() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKeyLike("%invalid").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKeyLike("%invalid").count());
    }

    @Test
    public void testQueryByCaseDefinitionVersion() {
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersion(1).list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersion(1).count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersion(2).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersion(2).count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersion(2).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersion(2).count());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersion(4).list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersion(4).count());
    }

    @Test
    public void testQueryByCaseDefinitionVersionGreaterThan() {
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionGreaterThan(2).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionGreaterThan(2).count());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionGreaterThan(3).list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionGreaterThan(3).count());
    }

    @Test
    public void testQueryByCaseDefinitionVersionGreaterThanOrEquals() {
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionGreaterThanOrEquals(2).list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionGreaterThanOrEquals(2).count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionGreaterThanOrEquals(3).list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionGreaterThanOrEquals(3).count());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionGreaterThanOrEquals(4).list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionGreaterThanOrEquals(4).count());
    }

    @Test
    public void testQueryByCaseDefinitionVersionLowerThan() {
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionLowerThan(2).list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionLowerThan(2).count());
        Assert.assertEquals(3, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionLowerThan(3).list().size());
        Assert.assertEquals(3, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionLowerThan(3).count());
    }

    @Test
    public void testQueryByCaseDefinitionVersionLowerThanOrEquals() {
        Assert.assertEquals(3, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionLowerThanOrEquals(2).list().size());
        Assert.assertEquals(3, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionLowerThanOrEquals(2).count());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionLowerThanOrEquals(3).list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionLowerThanOrEquals(3).count());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionLowerThanOrEquals(4).list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionVersionLowerThanOrEquals(4).count());
    }

    @Test
    public void testQueryByLatestVersion() {
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().latestVersion().list().size());
        Assert.assertEquals(2, cmmnRepositoryService.createCaseDefinitionQuery().latestVersion().count());
    }

    @Test
    public void testQueryByLatestVersionAndKey() {
        CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("myCase").latestVersion().singleResult();
        Assert.assertNotNull(caseDefinition);
        Assert.assertEquals(3, caseDefinition.getVersion());
        Assert.assertEquals(deploymentId3, caseDefinition.getDeploymentId());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("myCase").latestVersion().list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("myCase").latestVersion().count());
    }

    @Test
    public void testQueryByCaseDefinitionResourceName() {
        Assert.assertEquals(3, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceName("org/flowable/cmmn/test/repository/simple-case.cmmn").list().size());
        Assert.assertEquals(3, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceName("org/flowable/cmmn/test/repository/simple-case.cmmn").count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceName("org/flowable/cmmn/test/repository/simple-case2.cmmn").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceName("org/flowable/cmmn/test/repository/simple-case2.cmmn").count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceName("org/flowable/cmmn/test/repository/simple-case.cmmn").latestVersion().list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceName("org/flowable/cmmn/test/repository/simple-case.cmmn").latestVersion().count());
    }

    @Test
    public void testQueryByInvalidCaseDefinitionResourceName() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceName("invalid.cmmn").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceName("invalid.cmmn").count());
    }

    @Test
    public void testQueryByCaseDefinitionResourceNameLike() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceNameLike("%.cmmn").list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceNameLike("%.cmmn").count());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceNameLike("%2%").list().size());
        Assert.assertEquals(1, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceNameLike("%2%").count());
    }

    @Test
    public void testQueryByInvalidCaseDefinitionResourceNameLike() {
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceNameLike("%invalid%").list().size());
        Assert.assertEquals(0, cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionResourceNameLike("%invalid%").count());
    }

    @Test
    public void testQueryOrderByCaseDefinitionCategory() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionCategory().asc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionCategory().asc().count());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionCategory().desc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionCategory().desc().count());
    }

    @Test
    public void testQueryOrderByCaseDefinitionKey() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionKey().asc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionKey().asc().count());
        List<CaseDefinition> caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionKey().asc().list();
        for (int i = 0; i < (caseDefinitions.size()); i++) {
            if (i <= 2) {
                Assert.assertEquals("myCase", caseDefinitions.get(i).getKey());
            } else {
                Assert.assertEquals("myCase2", caseDefinitions.get(i).getKey());
            }
        }
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionKey().desc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionKey().desc().count());
        caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionKey().desc().list();
        for (int i = 0; i < (caseDefinitions.size()); i++) {
            if (i > 0) {
                Assert.assertEquals("myCase", caseDefinitions.get(i).getKey());
            } else {
                Assert.assertEquals("myCase2", caseDefinitions.get(i).getKey());
            }
        }
    }

    @Test
    public void testQueryOrderByCaseDefinitionId() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionId().asc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionId().asc().count());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionId().desc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionId().desc().count());
    }

    @Test
    public void testQueryOrderByCaseDefinitionName() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionName().asc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionName().asc().count());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionName().desc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionName().desc().count());
    }

    @Test
    public void testQueryOrderByCaseDefinitionDeploymentId() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByDeploymentId().asc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByDeploymentId().asc().count());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByDeploymentId().desc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByDeploymentId().desc().count());
    }

    @Test
    public void testQueryOrderByCaseDefinitionVersion() {
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionVersion().asc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionVersion().asc().count());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionVersion().desc().list().size());
        Assert.assertEquals(4, cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionVersion().desc().count());
        List<CaseDefinition> caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionVersion().desc().list();
        Assert.assertEquals(3, caseDefinitions.get(0).getVersion());
        Assert.assertEquals(2, caseDefinitions.get(1).getVersion());
        Assert.assertEquals(1, caseDefinitions.get(2).getVersion());
        Assert.assertEquals(1, caseDefinitions.get(3).getVersion());
        caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().latestVersion().orderByCaseDefinitionVersion().asc().list();
        Assert.assertEquals(1, caseDefinitions.get(0).getVersion());
        Assert.assertEquals("myCase2", caseDefinitions.get(0).getKey());
        Assert.assertEquals(3, caseDefinitions.get(1).getVersion());
        Assert.assertEquals("myCase", caseDefinitions.get(1).getKey());
    }
}

