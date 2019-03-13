package org.wiztools.restclient.util;


import org.junit.Assert;
import org.junit.Test;
import org.wiztools.commons.Charsets;
import org.wiztools.restclient.bean.ContentType;


/**
 *
 *
 * @author subwiz
 */
public class HttpUtilTest {
    public HttpUtilTest() {
    }

    /**
     * Test of getContentType method, of class HttpUtil.
     */
    @Test
    public void testGetContentType_String() {
        System.out.println("getContentType");
        String header = "application/vnd.mnet.staticwebspaces+xml;version=1;charset=UTF-8";
        ContentType expResult = new org.wiztools.restclient.bean.ContentTypeBean("application/vnd.mnet.staticwebspaces+xml", Charsets.UTF_8);
        ContentType result = HttpUtil.getContentType(header);
        Assert.assertEquals(expResult, result);
    }
}

