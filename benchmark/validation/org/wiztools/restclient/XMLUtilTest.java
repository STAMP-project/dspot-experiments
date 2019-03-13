package org.wiztools.restclient;


import Charsets.UTF_8;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.RequestBean;
import org.wiztools.restclient.bean.Response;
import org.wiztools.restclient.bean.ResponseBean;
import org.wiztools.restclient.persistence.PersistenceRead;
import org.wiztools.restclient.persistence.PersistenceWrite;
import org.wiztools.restclient.persistence.XmlPersistenceRead;
import org.wiztools.restclient.persistence.XmlPersistenceWrite;
import org.wiztools.restclient.util.XMLUtil;


/**
 *
 *
 * @author subwiz
 */
public class XMLUtilTest {
    private PersistenceRead pRead = new XmlPersistenceRead();

    private PersistenceWrite pWrite = new XmlPersistenceWrite();

    public XMLUtilTest() {
    }

    /**
     * Test of getDocumentCharset method, of class XMLUtil.
     */
    @Test
    public void testGetDocumentCharset() throws Exception {
        System.out.println("getDocumentCharset");
        // When document complies to standard:
        {
            File f = new File("src/test/resources/org/wiztools/restclient/xml/charset1.xml");
            String expResult = "UTF-8";
            String result = XMLUtil.getDocumentCharset(f);
            System.out.println(("encoding attribute: " + result));
            Assert.assertEquals(expResult, result);
        }
        // When document does not have encoding attribute:
        {
            File f = new File("src/test/resources/org/wiztools/restclient/xml/charset2.xml");
            String expResult = UTF_8.name();
            System.out.println(("expResult: " + expResult));
            String result = XMLUtil.getDocumentCharset(f);
            System.out.println(("encoding attribute: " + result));
            Assert.assertEquals(expResult, result);
        }
        // When document does not have XML declaration:
        {
            File f = new File("src/test/resources/org/wiztools/restclient/xml/charset3.xml");
            String expResult = UTF_8.name();
            System.out.println(("expResult: " + expResult));
            String result = XMLUtil.getDocumentCharset(f);
            System.out.println(("encoding attribute: " + result));
            Assert.assertEquals(expResult, result);
        }
    }

    /**
     * Test of writeRequestXML method, of class XMLUtil.
     */
    @Test
    public void testWriteRequestXML() throws Exception {
        System.out.println("writeRequestXML");
        RequestBean bean = getDefaultRequestBean();
        File f = File.createTempFile("prefix", ".rcq");
        pWrite.writeRequest(bean, f);
        Request expResult = pRead.getRequestFromFile(f);
        Assert.assertEquals(expResult, bean);
    }

    /**
     * Test of writeResponseXML method, of class XMLUtil.
     */
    @Test
    public void testWriteResponseXML() throws Exception {
        System.out.println("writeResponseXML");
        ResponseBean bean = getDefaultResponseBean();
        File f = File.createTempFile("prefix", ".rcs");
        pWrite.writeResponse(bean, f);
        Response expResult = pRead.getResponseFromFile(f);
        Assert.assertEquals(expResult, bean);
    }

    /**
     * Test of getRequestFromXMLFile method, of class XMLUtil.
     */
    @Test
    public void testGetRequestFromXMLFile() throws Exception {
        System.out.println("getRequestFromXMLFile");
        File f = new File("src/test/resources/reqFromXml.rcq");
        RequestBean expResult = getDefaultRequestBean();
        Request result = pRead.getRequestFromFile(f);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of getResponseFromXMLFile method, of class XMLUtil.
     */
    @Test
    public void testGetResponseFromXMLFile() throws Exception {
        System.out.println("getResponseFromXMLFile");
        File f = new File("src/test/resources/resFromXml.rcs");
        ResponseBean expResult = getDefaultResponseBean();
        Response result = pRead.getResponseFromFile(f);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test to verify if the write operation of Request corrupts
     * the content of the test script.
     *
     * @throws java.lang.Exception
     * 		
     */
    @Test
    public void testIntegrityOfTestScript() throws Exception {
        File f = new File("src/test/resources/resTestScriptIntegrity.rcq");
        Request req = pRead.getRequestFromFile(f);
        File outFile = File.createTempFile("abc", "xyz");
        pWrite.writeRequest(req, outFile);
        Request req1 = pRead.getRequestFromFile(outFile);
        Assert.assertEquals(req.getTestScript(), req1.getTestScript());
    }
}

