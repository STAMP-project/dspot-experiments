package org.activiti.editor.language.xml;


import org.activiti.bpmn.exceptions.XMLException;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ServiceTask;
import org.junit.Assert;
import org.junit.Test;


public class MapExceptionConverterTest extends AbstractConverterTest {
    String resourceName;

    @Test
    public void testMapExceptionWithInvalidHasChildren() throws Exception {
        resourceName = "mapException/mapExceptionInvalidHasChildrenModel.bpmn";
        try {
            readXMLFile();
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
            readXMLFile();
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

