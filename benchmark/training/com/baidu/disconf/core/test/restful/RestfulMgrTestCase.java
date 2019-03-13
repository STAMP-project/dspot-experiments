package com.baidu.disconf.core.test.restful;


import com.baidu.disconf.core.common.json.ValueVo;
import com.baidu.disconf.core.common.restful.RestfulMgr;
import com.baidu.disconf.core.common.restful.core.RemoteUrl;
import com.baidu.disconf.core.test.common.BaseCoreTestCase;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * ?? WireMock ????
 *
 * @author liaoqiqi
 * @version 2014-7-30
 */
public class RestfulMgrTestCase extends BaseCoreTestCase {
    private static RestfulMgr restfulMgr = null;

    /**
     *
     */
    @Test
    public void tetGetJsonData() {
        try {
            RemoteUrl remoteUrl = new RemoteUrl(RemoteMockServer.ITEM_URL, RemoteMockServer.LOCAL_HOST_LIST);
            ValueVo valueVo = RestfulMgrTestCase.restfulMgr.getJsonData(ValueVo.class, remoteUrl, 3, 3);
            Assert.assertEquals(String.valueOf(RemoteMockServer.DEFAULT_ITEM_VALUE), valueVo.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    /**
     *
     */
    @Test
    public void tetDownloadFromServer() {
        try {
            RemoteUrl remoteUrl = new RemoteUrl(RemoteMockServer.FILE_URL, RemoteMockServer.LOCAL_HOST_LIST);
            String downloadFilePath = RestfulMgrTestCase.restfulMgr.downloadFromServer(remoteUrl, RemoteMockServer.FILE_NAME, RemoteMockServer.LOCAL_DOWNLOAD_DIR, RemoteMockServer.LOCAL_DOWNLOAD_DIR_TEMP, RemoteMockServer.LOCAL_TARGET_DOWNLOAD_DIR, true, 3, 3);
            File file = new File(downloadFilePath);
            String content = FileUtils.readFileToString(file);
            Assert.assertEquals(RemoteMockServer.FILE_CONTENT, content);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    /**
     *
     */
    @Test
    public void testZooPrefixUrl() {
        try {
            RemoteUrl remoteUrl = new RemoteUrl(RemoteMockServer.ZOO_PREFIX_URL, RemoteMockServer.LOCAL_HOST_LIST);
            ValueVo valueVo = RestfulMgrTestCase.restfulMgr.getJsonData(ValueVo.class, remoteUrl, 3, 3);
            Assert.assertEquals(String.valueOf(RemoteMockServer.ZOO_PREFIX_VALUE), valueVo.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    /**
     *
     */
    @Test
    public void testZooHost() {
        try {
            RemoteUrl remoteUrl = new RemoteUrl(RemoteMockServer.ZOO_URL, RemoteMockServer.LOCAL_HOST_LIST);
            ValueVo valueVo = RestfulMgrTestCase.restfulMgr.getJsonData(ValueVo.class, remoteUrl, 3, 3);
            Assert.assertEquals(String.valueOf(RemoteMockServer.ZOO_HOSTS), valueVo.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}

