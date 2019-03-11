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


import java.util.HashSet;
import java.util.Set;
import org.flowable.bpmn.model.BpmnModel;
import org.junit.Test;


public class ShellTaskConverterTest extends AbstractConverterTest {
    private static final Set<String> EXPECTED_FIELDS = new HashSet<>();

    static {
        ShellTaskConverterTest.EXPECTED_FIELDS.add("command");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("arg1");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("arg2");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("arg3");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("arg4");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("arg5");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("wait");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("errorRedirect");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("errorCodeVariable");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("directory");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("outputVariable");
        ShellTaskConverterTest.EXPECTED_FIELDS.add("cleanEnv");
    }

    @Test
    public void convertJsonToModel() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        validateModel(bpmnModel);
    }

    @Test
    public void doubleConversionValidation() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        bpmnModel = convertToJsonAndBack(bpmnModel);
        validateModel(bpmnModel);
    }
}

