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
 * Created by Pardo David on 21/02/2017.
 */
public class EventSubprocessSequenceFlowTest extends AbstractConverterTest {
    private static final String EVENT_SUBPROCESS_ID = "sid-3AE5DD30-CE0E-4660-871F-A515E39EECA6";

    private static final String FROM_SE_TO_TASK = "sid-45B32336-D4E3-4576-8377-2D81C0EE02C4";

    private static final double PRECISION = 0.1;

    @Test
    public void oneWay() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        validate(bpmnModel);
    }

    @Test
    public void twoWay() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        bpmnModel = convertToJsonAndBack(bpmnModel);
        validate(bpmnModel);
    }
}

