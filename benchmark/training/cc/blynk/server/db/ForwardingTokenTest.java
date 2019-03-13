package cc.blynk.server.db;


import cc.blynk.server.core.BlockingIOProcessor;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public class ForwardingTokenTest {
    private static DBManager dbManager;

    private static BlockingIOProcessor blockingIOProcessor;

    @Test
    public void testNoToken() throws Exception {
        Assert.assertNull(ForwardingTokenTest.dbManager.forwardingTokenDBDao.selectHostByToken("123"));
    }

    @Test
    public void testInsertAndSelect() throws Exception {
        Assert.assertTrue(ForwardingTokenTest.dbManager.forwardingTokenDBDao.insertTokenHost("token", "host", "email", 0, 0));
        Assert.assertEquals("host", ForwardingTokenTest.dbManager.forwardingTokenDBDao.selectHostByToken("token"));
    }

    @Test
    public void testInsertAndSelectWrong() throws Exception {
        Assert.assertTrue(ForwardingTokenTest.dbManager.forwardingTokenDBDao.insertTokenHost("token", "host", "email", 0, 0));
        Assert.assertNull(ForwardingTokenTest.dbManager.forwardingTokenDBDao.selectHostByToken("token2"));
    }

    @Test
    public void deleteToken() throws Exception {
        Assert.assertTrue(ForwardingTokenTest.dbManager.forwardingTokenDBDao.insertTokenHost("token", "host", "email", 0, 0));
        Assert.assertTrue(ForwardingTokenTest.dbManager.forwardingTokenDBDao.deleteToken("token"));
        Assert.assertNull(ForwardingTokenTest.dbManager.forwardingTokenDBDao.selectHostByToken("token"));
    }

    @Test
    public void invalidToken() throws Exception {
        Assert.assertNull(ForwardingTokenTest.dbManager.forwardingTokenDBDao.selectHostByToken("\u0000"));
    }

    @Test
    public void deleteTokens() throws Exception {
        Assert.assertTrue(ForwardingTokenTest.dbManager.forwardingTokenDBDao.insertTokenHost("token1", "host1", "email", 0, 0));
        Assert.assertTrue(ForwardingTokenTest.dbManager.forwardingTokenDBDao.insertTokenHost("token2", "host2", "email", 0, 0));
        Assert.assertTrue(ForwardingTokenTest.dbManager.forwardingTokenDBDao.insertTokenHost("token3", "host3", "email", 0, 0));
        Assert.assertTrue(ForwardingTokenTest.dbManager.forwardingTokenDBDao.deleteToken("token1", "token2"));
        Assert.assertNull(ForwardingTokenTest.dbManager.forwardingTokenDBDao.selectHostByToken("token1"));
        Assert.assertNull(ForwardingTokenTest.dbManager.forwardingTokenDBDao.selectHostByToken("token2"));
        Assert.assertEquals("host3", ForwardingTokenTest.dbManager.forwardingTokenDBDao.selectHostByToken("token3"));
    }
}

