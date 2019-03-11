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
package org.flowable.cmmn.test.authorization;


import java.util.ArrayList;
import java.util.List;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.User;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class StartAuthorizationTest extends FlowableCmmnTestCase {
    protected IdmIdentityService identityService;

    protected User userInGroup1;

    protected User userInGroup2;

    protected User userInGroup3;

    protected Group group1;

    protected Group group2;

    protected Group group3;

    @Test
    @CmmnDeployment
    public void testIdentityLinks() throws Exception {
        setupUsersAndGroups();
        try {
            CaseDefinition latestCaseDef = cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("oneTaskCase").singleResult();
            Assert.assertNotNull(latestCaseDef);
            List<IdentityLink> links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(0, links.size());
            latestCaseDef = cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("case2").singleResult();
            Assert.assertNotNull(latestCaseDef);
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(2, links.size());
            Assert.assertTrue(containsUserOrGroup("user1", null, links));
            Assert.assertTrue(containsUserOrGroup("user2", null, links));
            latestCaseDef = cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("case3").singleResult();
            Assert.assertNotNull(latestCaseDef);
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(1, links.size());
            Assert.assertEquals("user1", links.get(0).getUserId());
            latestCaseDef = cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("case4").singleResult();
            Assert.assertNotNull(latestCaseDef);
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(3, links.size());
            Assert.assertTrue(containsUserOrGroup("user1", null, links));
            Assert.assertTrue(containsUserOrGroup(null, "group1", links));
            Assert.assertTrue(containsUserOrGroup(null, "group2", links));
            // Case instance identity links should not have an impcat on the identityLinks query
            Authentication.setAuthenticatedUserId("user1");
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionId(latestCaseDef.getId()).start();
            List<IdentityLink> identityLinksForCaseInstance = cmmnRuntimeService.getIdentityLinksForCaseInstance(caseInstance.getId());
            Assert.assertTrue(((identityLinksForCaseInstance.size()) > 0));
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(3, links.size());
        } finally {
            tearDownUsersAndGroups();
            Authentication.setAuthenticatedUserId(null);
        }
    }

    @Test
    @CmmnDeployment
    public void testAddAndRemoveIdentityLinks() throws Exception {
        setupUsersAndGroups();
        try {
            CaseDefinition latestCaseDef = cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("oneTaskCase").singleResult();
            Assert.assertNotNull(latestCaseDef);
            List<IdentityLink> links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(0, links.size());
            cmmnRepositoryService.addCandidateStarterGroup(latestCaseDef.getId(), "group1");
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(1, links.size());
            Assert.assertEquals("group1", links.get(0).getGroupId());
            cmmnRepositoryService.addCandidateStarterUser(latestCaseDef.getId(), "user1");
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(2, links.size());
            Assert.assertTrue(containsUserOrGroup(null, "group1", links));
            Assert.assertTrue(containsUserOrGroup("user1", null, links));
            cmmnRepositoryService.deleteCandidateStarterGroup(latestCaseDef.getId(), "nonexisting");
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(2, links.size());
            cmmnRepositoryService.deleteCandidateStarterGroup(latestCaseDef.getId(), "group1");
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(1, links.size());
            Assert.assertEquals("user1", links.get(0).getUserId());
            cmmnRepositoryService.deleteCandidateStarterUser(latestCaseDef.getId(), "user1");
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(0, links.size());
        } finally {
            tearDownUsersAndGroups();
        }
    }

    @Test
    @CmmnDeployment
    public void testCaseDefinitionList() throws Exception {
        setupUsersAndGroups();
        try {
            // Case 1 has no potential starters
            CaseDefinition latestCaseDef = cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("case1").singleResult();
            List<IdentityLink> links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(0, links.size());
            // user1 and user2 are potential starters of Case 2
            latestCaseDef = cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("case2").singleResult();
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(2, links.size());
            Assert.assertTrue(containsUserOrGroup("user1", null, links));
            Assert.assertTrue(containsUserOrGroup("user2", null, links));
            // Case 3 has 3 groups as authorized starter groups
            latestCaseDef = cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionKey("case3").singleResult();
            links = cmmnRepositoryService.getIdentityLinksForCaseDefinition(latestCaseDef.getId());
            Assert.assertEquals(3, links.size());
            Assert.assertTrue(containsUserOrGroup(null, "group1", links));
            Assert.assertTrue(containsUserOrGroup(null, "group2", links));
            Assert.assertTrue(containsUserOrGroup(null, "group3", links));
            // do not mention user, all cases should be selected
            List<CaseDefinition> caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().list();
            Assert.assertEquals(3, caseDefinitions.size());
            List<String> caseDefinitionIds = new ArrayList<>();
            for (CaseDefinition caseDefinition : caseDefinitions) {
                caseDefinitionIds.add(caseDefinition.getKey());
            }
            Assert.assertTrue(caseDefinitionIds.contains("case1"));
            Assert.assertTrue(caseDefinitionIds.contains("case2"));
            Assert.assertTrue(caseDefinitionIds.contains("case3"));
            // check user1, case2 has two authorized starters, of which one is "user1"
            caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().orderByCaseDefinitionName().asc().startableByUser("user1").list();
            Assert.assertEquals(1, caseDefinitions.size());
            Assert.assertEquals("case2", caseDefinitions.get(0).getKey());
            // no ccase could be started with "user4"
            caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().startableByUser("user4").list();
            Assert.assertEquals(0, caseDefinitions.size());
            // "userInGroup3" is in "group3" and can start only case 3 via group authorization
            caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().startableByUser("userInGroup3").list();
            Assert.assertEquals(1, caseDefinitions.size());
            Assert.assertEquals("case3", caseDefinitions.get(0).getKey());
            // "userInGroup2" can start case 3, via both user and group authorizations
            // but we have to be sure that case 3 appears only once
            cmmnRepositoryService.addCandidateStarterUser(caseDefinitions.get(0).getId(), "userInGroup2");
            caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().startableByUser("userInGroup2").list();
            Assert.assertEquals(1, caseDefinitions.size());
            Assert.assertEquals("case3", caseDefinitions.get(0).getKey());
        } finally {
            tearDownUsersAndGroups();
        }
    }
}

