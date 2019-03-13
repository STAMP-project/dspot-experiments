package org.docx4j.model.datastorage;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import org.docx4j.XmlUtils;
import org.docx4j.org.docx4j.XmlUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class HistgramQNameTest {
    @Test
    public void test() {
        Document doc = org.docx4j.XmlUtils.neww3cDomDocument();
        Element el = doc.createElement("foo");
        HistgramQNameTest.diagnostics(el);
        // + el.getLocalName() );  // null <---- never use this!
        el = doc.createElementNS(null, "bar");
        HistgramQNameTest.diagnostics(el);
        el = doc.createElementNS("http://foo", "bar");// you shouldn't do this...

        HistgramQNameTest.diagnostics(el);
        el.setPrefix("foo");// .. unless you do this

        HistgramQNameTest.diagnostics(el);
        el = doc.createElementNS("http://foo", "foo:bar");
        HistgramQNameTest.diagnostics(el);
        el.setPrefix("foo");
        HistgramQNameTest.diagnostics(el);
    }

    @Test
    public void testNotNamespaceAware() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        Document doc = dbf.newDocumentBuilder().newDocument();
        Element el = doc.createElement("foo");
        HistgramQNameTest.diagnostics(el);
        // el.getLocalName() );  // null <---- never use this!
        /* We don't support:
        el = doc.createElement("foo:bar"); // ie in not namespace aware parser
        diagnostics(el);
         */
        el = doc.createElementNS(null, "bar");// even though not NS aware...

        HistgramQNameTest.diagnostics(el);
        el = doc.createElementNS("http://foo", "bar");
        HistgramQNameTest.diagnostics(el);
    }

    @Test
    public void testNamespaceAware() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().newDocument();
        Element el = doc.createElement("foo");
        HistgramQNameTest.diagnostics(el);
        // + el.getLocalName() );  // null <---- never use this!
        el = doc.createElementNS(null, "bar");
        HistgramQNameTest.diagnostics(el);
        el = doc.createElementNS("http://foo", "bar");
        HistgramQNameTest.diagnostics(el);
    }

    @Test
    public void testQName() {
        QName qn = new QName("http://foo", "b:ar");// that is ok! 	:-(

    }

    @Test
    public void testQName2() {
        QName qn = new QName(null, "ar");// should be ok

    }

    @Test
    public void testMarshall() throws JAXBException {
        HistgramQNameTest.CaseData case1 = new HistgramQNameTest.CaseData();
        case1.setName("118905");
        JAXBContext jaxbContext = JAXBContext.newInstance(HistgramQNameTest.CaseData.class);
        // that won't use MOXy, which will cause problems
        // if MOXy is on your classpath during testing
        System.out.println(XmlUtils.marshaltoString(case1, jaxbContext));
        Document d = XmlUtils.marshaltoW3CDomDocument(case1, jaxbContext);
        Element el = d.getDocumentElement();
        HistgramQNameTest.diagnostics(el);
    }

    @Test
    public void testMarshall2() throws JAXBException, ParserConfigurationException {
        HistgramQNameTest.CaseData case1 = new HistgramQNameTest.CaseData();
        case1.setName("118905");
        JAXBContext jc = JAXBContext.newInstance(HistgramQNameTest.CaseData.class);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Marshaller marshaller = jc.createMarshaller();
        Document d = dbf.newDocumentBuilder().newDocument();
        marshaller.marshal(case1, d);
        Element el = d.getDocumentElement();
        HistgramQNameTest.diagnostics(el);
    }

    @Test
    public void testMarshall3() throws JAXBException, ParserConfigurationException {
        HistgramQNameTest.CaseData case1 = new HistgramQNameTest.CaseData();
        case1.setName("118905");
        JAXBContext jc = JAXBContext.newInstance(HistgramQNameTest.CaseData.class);
        Marshaller marshaller = jc.createMarshaller();
        DOMResult res = new DOMResult();
        marshaller.marshal(case1, res);
        Document d = ((Document) (res.getNode()));
        Element el = d.getDocumentElement();
        HistgramQNameTest.diagnostics(el);
    }

    @XmlRootElement(name = "case", namespace = "http://gctrack.gao.gov/templates/case-data")
    public static class CaseData {
        // <ns2:case xmlns:ns2="http://gctrack.gao.gov/templates/case-data">
        // <name>118905</name>
        String name;

        public String getName() {
            return name;
        }

        @XmlElement
        public void setName(String name) {
            this.name = name;
        }
    }
}

