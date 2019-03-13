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


import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class CaseDefinitionCategoryTest extends FlowableCmmnTestCase {
    private String deploymentId1;

    @Test
    public void testUpdateCategory() {
        CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId1).singleResult();
        Assert.assertEquals("http://flowable.org/cmmn", caseDefinition.getCategory());
        cmmnRepositoryService.setCaseDefinitionCategory(caseDefinition.getId(), "testCategory");
        caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId1).singleResult();
        Assert.assertEquals("testCategory", caseDefinition.getCategory());
        caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId1).caseDefinitionCategory("testCategory").singleResult();
        Assert.assertNotNull(caseDefinition);
    }

    @Test
    public void testDescriptionPersistency() {
        CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(deploymentId1).singleResult();
        Assert.assertEquals("This is a sample description", caseDefinition.getDescription());
    }
}

