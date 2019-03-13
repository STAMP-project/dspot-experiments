package com.baidu.disconf.core.test.zookeeper;


import com.baidu.disconf.core.common.zookeeper.ZookeeperMgr;
import com.baidu.disconf.core.common.zookeeper.inner.ResilientActiveKeyValueStore;
import com.baidu.disconf.core.test.zookeeper.mock.ResilientActiveKeyValueStoreMock;
import java.util.List;
import java.util.Random;
import mockit.NonStrictExpectations;
import org.junit.Assert;
import org.junit.Test;


/**
 * ??Jmockit????
 *
 * @author liaoqiqi
 * @version 2014-6-16
 */
public class ZookeeperMgrTest {
    /**
     * ????Root???
     */
    @Test
    public final void testGetRootChildren() {
        final ZookeeperMgr obj = ZookeeperMgr.getInstance();
        // 
        // ??
        // 
        new NonStrictExpectations(obj) {
            {
                ResilientActiveKeyValueStore store = new ResilientActiveKeyValueStoreMock();
                this.setField(obj, "store", store);
            }
        };
        List<String> list = ZookeeperMgr.getInstance().getRootChildren();
        for (String item : list) {
            System.out.println(item);
        }
        Assert.assertTrue(((list.size()) > 0));
    }

    /**
     * ???
     */
    @Test
    public final void testWritePersistentUrl() {
        try {
            Random random = new Random();
            int randomInt = random.nextInt();
            // ?
            String url = "/disconfserver/dan_dnwebbilling_1_0_online";
            ZookeeperMgr.getInstance().writePersistentUrl(url, String.valueOf(randomInt));
            // ?
            String readData = ZookeeperMgr.getInstance().readUrl(url, null);
            Assert.assertEquals(String.valueOf(randomInt), readData);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}

