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


import org.flowable.app.api.repository.AppDefinition;
import org.flowable.app.engine.test.FlowableAppTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class CaseDefinitionCategoryTest extends FlowableAppTestCase {
    private String deploymentId1;

    @Test
    public void testUpdateCategory() {
        AppDefinition appDefinition = appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId1).singleResult();
        Assert.assertNull(appDefinition.getCategory());
        appRepositoryService.setAppDefinitionCategory(appDefinition.getId(), "testCategory");
        appDefinition = appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId1).singleResult();
        Assert.assertEquals("testCategory", appDefinition.getCategory());
        appDefinition = appRepositoryService.createAppDefinitionQuery().deploymentId(deploymentId1).appDefinitionCategory("testCategory").singleResult();
        Assert.assertNotNull(appDefinition);
    }
}

