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
package org.flowable.editor.language;


import org.flowable.bpmn.model.BpmnModel;
import org.junit.Test;


/**
 * Verifies if the sequenceflows are correctly stored when a subprocess is inside
 * and collapseable subprocess.
 *
 * Created by Pardo David on 1/03/2017.
 */
public class CollapsedSubWithSubSequenceFlowTest extends AbstractConverterTest {
    private static final String EXPANED_SUBPROCESS_IN_CP = "sid-65F96E4B-9E0D-462D-AFD4-3FFAD5F7F9B6";

    private static final String COLLAPSED_SUBPROCESS = "sid-44B96119-5A3B-4850-BDAC-2D4A2AECEA0A";

    @Test
    public void oneWay() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        validateModel(bpmnModel);
    }

    @Test
    public void twoWay() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        bpmnModel = convertToJsonAndBack(bpmnModel);
        validateModel(bpmnModel);
    }
}

