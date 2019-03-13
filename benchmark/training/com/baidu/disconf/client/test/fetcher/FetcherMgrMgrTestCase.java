package com.baidu.disconf.client.test.fetcher;


import com.baidu.disconf.client.fetcher.FetcherMgr;
import com.baidu.disconf.client.test.common.BaseSpringTestCase;
import com.baidu.disconf.client.test.fetcher.inner.restful.RestfulMgrMock;
import com.baidu.disconf.core.common.restful.RestfulMgr;
import java.util.ArrayList;
import junit.framework.Assert;
import org.junit.Test;


/**
 * FetcherMgrMgr?? (??Jmockit????)
 *
 * @author liaoqiqi
 * @version 2014-6-17
 */
public class FetcherMgrMgrTestCase extends BaseSpringTestCase {
    private static String requestUrl = "/url";

    /**
     * ?????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetValueFromServer() throws Exception {
        final RestfulMgr restfulMgr = getMockInstance();
        FetcherMgr fetcherMgr = new com.baidu.disconf.client.fetcher.impl.FetcherMgrImpl(restfulMgr, 3, 5, true, "", "", new ArrayList<String>());
        try {
            String valueString = fetcherMgr.getValueFromServer(FetcherMgrMgrTestCase.requestUrl);
            Assert.assertEquals(RestfulMgrMock.defaultValue, valueString);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    /**
     * ?????????
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDownloadFileFromServer() throws Exception {
        final RestfulMgr restfulMgr = getMockInstance();
        FetcherMgr fetcherMgr = new com.baidu.disconf.client.fetcher.impl.FetcherMgrImpl(restfulMgr, 3, 5, true, "", "", new ArrayList<String>());
        try {
            String valueString = fetcherMgr.downloadFileFromServer(FetcherMgrMgrTestCase.requestUrl, RestfulMgrMock.defaultFileName, "./disconf");
            Assert.assertEquals(RestfulMgrMock.defaultFileName, valueString);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}

