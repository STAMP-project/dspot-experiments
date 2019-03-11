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
 * Created by Pardo David on 16/01/2017.
 */
public class CollapsedSubProcessConverterTest extends AbstractConverterTest {
    private static final String START_EVENT = "sid-89C70A03-C51B-4185-AB85-B8476E7A4F0C";

    private static final String SEQUENCEFLOW_TO_COLLAPSEDSUBPROCESS = "sid-B80498C9-A45C-4D58-B4AA-5393A409ACAA";

    private static final String COLLAPSEDSUBPROCESS = "sid-C20D5023-C2B9-4102-AA17-7F16E49E47C1";

    private static final String IN_CSB_START_EVENT = "sid-D8198785-4F74-43A8-A4CD-AF383CEEBE04";

    private static final String IN_CSB_SEQUENCEFLOW_TO_USERTASK = "sid-C633903D-1169-42A4-933D-4D9AAB959792";

    private static final String IN_CSB_USERTASK = "sid-F64640C9-9585-4927-806B-8B0A03DB2B8B";

    private static final String IN_CSB_SEQUENCEFLOW_TO_END = "sid-C1EFE310-3B12-42DA-AEE6-5E442C2FEF19";

    @Test
    public void convertFromXmlToJava() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
        validateGraphicInfo(bpmnModel);
    }

    @Test
    public void convertFromJavaToXml() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
        bpmnModel = exportAndReadXMLFile(bpmnModel);
        validateModel(bpmnModel);
        validateGraphicInfo(bpmnModel);
    }
}

