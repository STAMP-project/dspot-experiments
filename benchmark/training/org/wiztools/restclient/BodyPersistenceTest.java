package org.wiztools.restclient;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.wiztools.commons.Charsets;
import org.wiztools.commons.FileUtil;
import org.wiztools.restclient.persistence.PersistenceRead;
import org.wiztools.restclient.persistence.XmlPersistenceRead;

import static MultipartSubtype.FORM_DATA;


/**
 *
 *
 * @author subwiz
 */
public class BodyPersistenceTest {
    private PersistenceRead p = new XmlPersistenceRead();

    public BodyPersistenceTest() {
    }

    @Test
    public void testStringBody() throws Exception {
        System.out.println("testStringBody");
        RequestBean expResult = getRequestBeanWithoutBody();
        ContentType ct = new ContentTypeBean("text/plain", Charsets.UTF_8);
        ReqEntityStringBean body = new ReqEntityStringBean("Subhash loves Aarthi", ct);
        expResult.setBody(body);
        Request actual = p.getRequestFromFile(new File("src/test/resources/reqBodyString.rcq"));
        Assert.assertEquals(expResult, actual);
    }

    @Test
    public void testFileBody() throws Exception {
        System.out.println("testFileBody");
        RequestBean expResult = getRequestBeanWithoutBody();
        ContentType ct = new ContentTypeBean("text/plain", Charsets.UTF_8);
        ReqEntityFileBean body = new ReqEntityFileBean(new File("/etc/hosts"), ct);
        expResult.setBody(body);
        Request actual = p.getRequestFromFile(new File("src/test/resources/reqBodyFile.rcq"));
        Assert.assertEquals(expResult, actual);
    }

    @Test
    public void testByteArrayBody() throws Exception {
        System.out.println("testByteArrayBody");
        RequestBean expResult = getRequestBeanWithoutBody();
        ContentType ct = new ContentTypeBean("image/jpeg", null);
        byte[] arr = FileUtil.getContentAsBytes(new File("src/test/resources/subhash_by_aarthi.jpeg"));
        ReqEntityByteArrayBean body = new ReqEntityByteArrayBean(arr, ct);
        expResult.setBody(body);
        Request actual = p.getRequestFromFile(new File("src/test/resources/reqBodyByteArray.rcq"));
        Assert.assertEquals(expResult, actual);
    }

    @Test
    public void testMultipartBody() throws Exception {
        System.out.println("testMultipartBody");
        List<ReqEntityPart> parts = new ArrayList<>();
        ContentType ct = new ContentTypeBean("text/plain", Charsets.UTF_8);
        ReqEntityFilePartBean partFile = new ReqEntityFilePartBean("hosts-txt-name", "hosts.txt", ct, new File("/etc/hosts"));
        ReqEntityStringPartBean partString = new ReqEntityStringPartBean("aarthi.txt", ct, "Hello Babes!");
        parts.add(partFile);
        parts.add(partString);
        ReqEntityMultipartBean expResult = new ReqEntityMultipartBean(parts, null, FORM_DATA);
        Request actual = p.getRequestFromFile(new File("src/test/resources/reqBodyMultipart.rcq"));
        Assert.assertEquals(expResult, actual.getBody());
    }
}

