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


import org.flowable.bpmn.exceptions.XMLException;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.ServiceTask;
import org.junit.Assert;
import org.junit.Test;


public class MapExceptionConverterTest extends AbstractConverterTest {
    String resourceName;

    @Test
    public void testMapExceptionWithInvalidHasChildren() throws Exception {
        resourceName = "mapException/mapExceptionInvalidHasChildrenModel.bpmn";
        try {
            BpmnModel bpmnModel = readXMLFile();
            Assert.fail("No exception is thrown for mapExecution with invalid boolean for hasChildren");
        } catch (XMLException x) {
            Assert.assertTrue(((x.getMessage().indexOf("is not valid boolean")) != (-1)));
        } catch (Exception e) {
            Assert.fail((("wrong exception thrown. XmlException expected, " + (e.getClass())) + " thrown"));
        }
    }

    @Test
    public void testMapExceptionWithNoErrorCode() throws Exception {
        resourceName = "mapException/mapExceptionNoErrorCode.bpmn";
        try {
            BpmnModel bpmnModel = readXMLFile();
            Assert.fail("No exception is thrown for mapExecution with no Error Code");
        } catch (XMLException x) {
            Assert.assertTrue(((x.getMessage().indexOf("No errorCode defined")) != (-1)));
        } catch (Exception e) {
            Assert.fail((("wrong exception thrown. XmlException expected, " + (e.getClass())) + " thrown"));
        }
    }

    @Test
    public void testMapExceptionWithNoExceptionClass() throws Exception {
        resourceName = "mapException/mapExceptionNoExceptionClass.bpmn";
        BpmnModel bpmnModel = readXMLFile();
        FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement("servicetaskWithAndTrueAndChildren");
        Assert.assertNotNull(flowElement);
        Assert.assertTrue((flowElement instanceof ServiceTask));
        Assert.assertEquals("servicetaskWithAndTrueAndChildren", flowElement.getId());
        ServiceTask serviceTask = ((ServiceTask) (flowElement));
        Assert.assertNotNull(serviceTask.getMapExceptions());
        Assert.assertEquals(1, serviceTask.getMapExceptions().size());
        Assert.assertNotNull(serviceTask.getMapExceptions().get(0).getClassName());
        Assert.assertEquals(0, serviceTask.getMapExceptions().get(0).getClassName().length());
    }

    @Test
    public void convertXMLToModel() throws Exception {
        resourceName = "mapException/mapExceptionModel.bpmn";
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }
}

