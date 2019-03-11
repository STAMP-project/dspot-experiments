package org.activiti.editor.language.xml;


import org.activiti.bpmn.model.BpmnModel;
import org.junit.Test;


/**
 * Test for ACT-1657
 */
public class EventBasedGatewayConverterTest extends AbstractConverterTest {
    @Test
    public void convertXMLToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }
}

