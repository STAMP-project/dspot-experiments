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
package org.flowable.engine.test.bpmn.deployment;


import java.io.UnsupportedEncodingException;
import java.util.List;
import org.flowable.engine.impl.bpmn.deployer.ParsedDeployment;
import org.flowable.engine.impl.bpmn.deployer.ParsedDeploymentBuilder;
import org.flowable.engine.impl.bpmn.deployer.ParsedDeploymentBuilderFactory;
import org.flowable.engine.impl.bpmn.deployer.ResourceNameUtil;
import org.flowable.engine.impl.persistence.entity.DeploymentEntity;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


public class ParsedDeploymentTest extends PluggableFlowableTestCase {
    private static final String NAMESPACE = "xmlns='http://www.omg.org/spec/BPMN/20100524/MODEL'";

    private static final String TARGET_NAMESPACE = "targetNamespace='http://activiti.org/BPMN20'";

    private static final String ID1_ID = "id1";

    private static final String ID2_ID = "id2";

    private static final String IDR_PROCESS_XML = ParsedDeploymentTest.assembleXmlResourceString((("<process id='" + (ParsedDeploymentTest.ID1_ID)) + "' name='Insurance Damage Report 1' />"), (("<process id='" + (ParsedDeploymentTest.ID2_ID)) + "' name='Insurance Damager Report 2' />"));

    private static final String IDR_XML_NAME = "idr." + (ResourceNameUtil.BPMN_RESOURCE_SUFFIXES[0]);

    private static final String EN1_ID = "en1";

    private static final String EN2_ID = "en2";

    private static final String EN_PROCESS_XML = ParsedDeploymentTest.assembleXmlResourceString((("<process id='" + (ParsedDeploymentTest.EN1_ID)) + "' name='Expense Note 1' />"), (("<process id='" + (ParsedDeploymentTest.EN2_ID)) + "' name='Expense Note 2' />"));

    private static final String EN_XML_NAME = "en." + (ResourceNameUtil.BPMN_RESOURCE_SUFFIXES[1]);

    @Test
    public void testCreateAndQuery() throws UnsupportedEncodingException {
        DeploymentEntity entity = assembleUnpersistedDeploymentEntity();
        ParsedDeploymentBuilderFactory builderFactory = processEngineConfiguration.getParsedDeploymentBuilderFactory();
        ParsedDeploymentBuilder builder = builderFactory.getBuilderForDeployment(entity);
        ParsedDeployment parsedDeployment = builder.build();
        List<ProcessDefinitionEntity> processDefinitions = parsedDeployment.getAllProcessDefinitions();
        MatcherAssert.assertThat(parsedDeployment.getDeployment(), CoreMatchers.sameInstance(entity));
        MatcherAssert.assertThat(processDefinitions.size(), CoreMatchers.equalTo(4));
        ProcessDefinitionEntity id1 = getProcessDefinitionEntityFromList(processDefinitions, ParsedDeploymentTest.ID1_ID);
        ProcessDefinitionEntity id2 = getProcessDefinitionEntityFromList(processDefinitions, ParsedDeploymentTest.ID2_ID);
        MatcherAssert.assertThat(parsedDeployment.getBpmnParseForProcessDefinition(id1), CoreMatchers.sameInstance(parsedDeployment.getBpmnParseForProcessDefinition(id2)));
        MatcherAssert.assertThat(parsedDeployment.getBpmnModelForProcessDefinition(id1), CoreMatchers.sameInstance(parsedDeployment.getBpmnParseForProcessDefinition(id1).getBpmnModel()));
        MatcherAssert.assertThat(parsedDeployment.getProcessModelForProcessDefinition(id1), CoreMatchers.sameInstance(parsedDeployment.getBpmnParseForProcessDefinition(id1).getBpmnModel().getProcessById(id1.getKey())));
        MatcherAssert.assertThat(parsedDeployment.getResourceForProcessDefinition(id1).getName(), CoreMatchers.equalTo(ParsedDeploymentTest.IDR_XML_NAME));
        MatcherAssert.assertThat(parsedDeployment.getResourceForProcessDefinition(id2).getName(), CoreMatchers.equalTo(ParsedDeploymentTest.IDR_XML_NAME));
        ProcessDefinitionEntity en1 = getProcessDefinitionEntityFromList(processDefinitions, ParsedDeploymentTest.EN1_ID);
        ProcessDefinitionEntity en2 = getProcessDefinitionEntityFromList(processDefinitions, ParsedDeploymentTest.EN2_ID);
        MatcherAssert.assertThat(parsedDeployment.getBpmnParseForProcessDefinition(en1), CoreMatchers.sameInstance(parsedDeployment.getBpmnParseForProcessDefinition(en2)));
        MatcherAssert.assertThat(parsedDeployment.getBpmnParseForProcessDefinition(en1), CoreMatchers.not(CoreMatchers.equalTo(parsedDeployment.getBpmnParseForProcessDefinition(id2))));
        MatcherAssert.assertThat(parsedDeployment.getResourceForProcessDefinition(en1).getName(), CoreMatchers.equalTo(ParsedDeploymentTest.EN_XML_NAME));
        MatcherAssert.assertThat(parsedDeployment.getResourceForProcessDefinition(en2).getName(), CoreMatchers.equalTo(ParsedDeploymentTest.EN_XML_NAME));
    }
}

