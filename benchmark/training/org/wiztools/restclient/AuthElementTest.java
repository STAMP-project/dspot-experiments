package org.wiztools.restclient;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.wiztools.restclient.persistence.PersistenceRead;
import org.wiztools.restclient.persistence.XmlPersistenceRead;


/**
 *
 *
 * @author subwiz
 */
public class AuthElementTest {
    public AuthElementTest() {
    }

    @Test
    public void testOAuth2Bearer() throws Exception {
        System.out.println("testOAuth2Bearer");
        PersistenceRead p = new XmlPersistenceRead();
        Request req = p.getRequestFromFile(new File("src/test/resources/reqOAuth2Bearer.rcq"));
        Auth a = req.getAuth();
        OAuth2BearerAuth auth = ((OAuth2BearerAuth) (a));
        Assert.assertEquals("subhash", auth.getOAuth2BearerToken());
    }

    @Test
    public void testNtlm() throws Exception {
        System.out.println("testNtlm");
        PersistenceRead p = new XmlPersistenceRead();
        Request req = p.getRequestFromFile(new File("src/test/resources/reqNtlm.rcq"));
        Auth a = req.getAuth();
        NtlmAuth auth = ((NtlmAuth) (a));
        NtlmAuthBean expResult = new NtlmAuthBean();
        expResult.setDomain("testdomain");
        expResult.setWorkstation("testworkstation");
        expResult.setUsername("subwiz");
        expResult.setPassword("password".toCharArray());
        Assert.assertEquals(expResult, auth);
    }
}

