package org.activiti.editor.language.xml;


import org.activiti.bpmn.model.BpmnModel;
import org.junit.Test;


/**
 *
 *
 * @see https://activiti.atlassian.net/browse/ACT-1847
 */
public class ValuedDataObjectConverterTest extends AbstractConverterTest {
    @Test
    public void convertXMLToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }

    @Test
    public void convertModelToXML() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        BpmnModel parsedModel = exportAndReadXMLFile(bpmnModel);
        validateModel(parsedModel);
        deployProcess(parsedModel);
    }
}

