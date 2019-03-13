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
package org.flowable.editor.language.xml;


import org.flowable.bpmn.model.BpmnModel;
import org.junit.Test;


/**
 *
 *
 * @see <a href="https://github.com/flowable/flowable-engine/issues/474">Issue 474</a>
 */
public class MultiInstanceTaskConverterTest2 extends AbstractConverterTest {
    private static final String PARTICIPANT_VALUE = "[\n" + (((((((((((("                   {\n" + "                     \"principalType\" : \"User\",\n") + "                     \"role\" : \"PotentialOwner\",\n") + "                     \"principal\" : \"wfuser1\",\n") + "                     \"version\" : 1\n") + "                   },\n") + "                   {\n") + "                     \"principalType\" : \"User\",\n") + "                     \"role\" : \"PotentialOwner\",\n") + "                     \"principal\" : \"wfuser2\",\n") + "                     \"version\" : 1\n") + "                   }\n") + "                 ]");

    @Test
    public void convertXMLToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }

    @Test
    public void convertModelToXML() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        BpmnModel parsedModel = exportAndReadXMLFile(bpmnModel);
        validateModel(parsedModel);
    }
}

