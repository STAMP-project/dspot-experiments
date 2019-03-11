package org.activiti.editor.language;


import org.activiti.bpmn.model.BpmnModel;
import org.junit.Test;


public class FlowNodeMultipleOutgoingFlowsConverterTest extends AbstractConverterTest {
    @Test
    public void doubleConversionValidation() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        validateModel(bpmnModel);
        bpmnModel = convertToJsonAndBack(bpmnModel);
        // System.out.println("xml " + new String(new
        // BpmnXMLConverter().convertToXML(bpmnModel), "utf-8"));
        validateModel(bpmnModel);
    }
}

