package cc.blynk.server.db;


import cc.blynk.server.core.BlockingIOProcessor;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 19.02.16.
 */
public class CloneProjectTest {
    private static DBManager dbManager;

    private static BlockingIOProcessor blockingIOProcessor;

    @Test
    public void testNoToken() throws Exception {
        Assert.assertNull(CloneProjectTest.dbManager.cloneProjectDBDao.selectClonedProjectByToken("123"));
    }

    @Test
    public void testInsertAndSelect() throws Exception {
        Assert.assertTrue(CloneProjectTest.dbManager.insertClonedProject("token", "json"));
        Assert.assertEquals("json", CloneProjectTest.dbManager.selectClonedProject("token"));
    }

    @Test
    public void testInsertAndSelectWrong() throws Exception {
        Assert.assertTrue(CloneProjectTest.dbManager.insertClonedProject("token", "json"));
        Assert.assertNull(CloneProjectTest.dbManager.selectClonedProject("token2"));
    }
}

