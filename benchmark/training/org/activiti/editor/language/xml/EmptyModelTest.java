package org.activiti.editor.language.xml;


import org.activiti.bpmn.exceptions.XMLException;
import org.junit.Assert;
import org.junit.Test;


public class EmptyModelTest extends AbstractConverterTest {
    @Test
    public void convertXMLToModel() throws Exception {
        try {
            readXMLFile();
            Assert.fail("Expected xml exception");
        } catch (XMLException e) {
            // exception expected
        }
    }

    @Test
    public void convertModelToXML() throws Exception {
        try {
            readXMLFile();
            Assert.fail("Expected xml exception");
        } catch (XMLException e) {
            // exception expected
        }
    }
}

