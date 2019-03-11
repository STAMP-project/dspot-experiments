package org.activiti.editor.language;


import org.activiti.bpmn.model.BpmnModel;
import org.junit.Test;


public class FlowNodeInSubProcessConverterTest extends AbstractConverterTest {
    @Test
    public void doubleConversionValidation() throws Exception {
        BpmnModel bpmnModel = readJsonFile();
        validateModel(bpmnModel);
        bpmnModel = convertToJsonAndBack(bpmnModel);
        validateModel(bpmnModel);
    }
}

