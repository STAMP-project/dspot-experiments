package org.activiti.editor.language.xml;


import org.activiti.bpmn.model.BpmnModel;
import org.junit.Test;


public class FormPropertiesConverterTest extends AbstractConverterTest {
    @Test
    public void convertJsonToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }

    @Test
    public void doubleConversionValidation() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
        bpmnModel = exportAndReadXMLFile(bpmnModel);
        validateModel(bpmnModel);
    }
}

