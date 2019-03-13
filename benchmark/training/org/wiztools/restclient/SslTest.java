package org.wiztools.restclient;


import HTTPMethod.GET;
import HTTPVersion.HTTP_1_1;
import SSLHostnameVerifier.ALLOW_ALL;
import java.io.File;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;
import org.wiztools.restclient.bean.Request;
import org.wiztools.restclient.bean.RequestBean;
import org.wiztools.restclient.bean.SSLReqBean;
import org.wiztools.restclient.persistence.PersistenceRead;
import org.wiztools.restclient.persistence.XmlPersistenceRead;


/**
 *
 *
 * @author subwiz
 */
public class SslTest {
    private PersistenceRead p = new XmlPersistenceRead();

    public SslTest() {
    }

    @Test
    public void testSsl() throws Exception {
        RequestBean expResult = new RequestBean();
        expResult.setUrl(new URL("https://www.webshop.co.uk/"));
        expResult.setMethod(GET);
        expResult.setHttpVersion(HTTP_1_1);
        expResult.setFollowRedirect(true);
        SSLReqBean ssl = new SSLReqBean();
        ssl.setTrustAllCerts(true);
        ssl.setHostNameVerifier(ALLOW_ALL);
        expResult.setSslReq(ssl);
        Request actual = p.getRequestFromFile(new File("src/test/resources/reqSsl.rcq"));
        Assert.assertEquals(expResult, actual);
    }
}

