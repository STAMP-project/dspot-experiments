package org.activiti.editor.language.xml;


import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.BpmnModel;
import org.junit.Test;


public class SubProcessConverterAutoLayoutTest extends AbstractConverterTest {
    @Test
    public void convertXMLToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }

    @Test
    public void convertModelToXML() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        // Add DI information to bpmn model
        BpmnAutoLayout bpmnAutoLayout = new BpmnAutoLayout(bpmnModel);
        bpmnAutoLayout.execute();
        BpmnModel parsedModel = exportAndReadXMLFile(bpmnModel);
        validateModel(parsedModel);
        deployProcess(parsedModel);
    }
}

