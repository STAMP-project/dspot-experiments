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
package org.flowable.standalone.validation;


import org.flowable.bpmn.exceptions.XMLException;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class DisabledSchemaValidationTest {
    protected ProcessEngine processEngine;

    protected RepositoryService repositoryService;

    @Test
    public void testDisableValidation() {
        // Should fail
        try {
            repositoryService.createDeployment().addClasspathResource("org/flowable/standalone/validation/invalid_process_xsd_error.bpmn20.xml").deploy();
            Assert.fail();
        } catch (XMLException e) {
            // expected exception
        }
        // Should fail with validation errors
        try {
            repositoryService.createDeployment().addClasspathResource("org/flowable/standalone/validation/invalid_process_xsd_error.bpmn20.xml").disableSchemaValidation().deploy();
            Assert.fail();
        } catch (FlowableException e) {
            // expected exception
        }
    }
}

