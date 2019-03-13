/**
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.bpmn.converter;


import BpmnXMLConstants.ATTRIBUTE_TASK_IMPLEMENTATION;
import javax.xml.stream.XMLStreamReader;
import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.BpmnModel;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.BDDMockito;
import org.mockito.Mock;


public class ServiceTaskXMLConverterTest {
    private ServiceTaskXMLConverter converter = new ServiceTaskXMLConverter();

    @Mock(answer = Answers.RETURNS_MOCKS)
    private XMLStreamReader reader;

    @Test
    public void convertXMLToElementShouldSetTheImplementationFromXMLImplementationAttribute() throws Exception {
        // given
        BDDMockito.given(reader.getAttributeValue(null, ATTRIBUTE_TASK_IMPLEMENTATION)).willReturn("myConnector");
        // when
        BaseElement element = converter.convertXMLToElement(reader, new BpmnModel());
        // then
        assertThat(getImplementation()).isEqualTo("myConnector");
    }
}

