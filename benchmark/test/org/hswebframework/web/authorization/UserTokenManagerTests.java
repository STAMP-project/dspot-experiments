package org.hswebframework.web.authorization;


import AllopatricLoginMode.allow;
import AllopatricLoginMode.offlineOther;
import TokenState.deny;
import TokenState.effective;
import org.hswebframework.web.authorization.exception.AccessDenyException;
import org.junit.Assert;
import org.junit.Test;


public class UserTokenManagerTests {
    /**
     * ??????
     *
     * @throws InterruptedException
     * 		Thread.sleep error
     */
    @Test
    public void testDefaultSetting() throws InterruptedException {
        DefaultUserTokenManager userTokenManager = new DefaultUserTokenManager();
        userTokenManager.setAllopatricLoginMode(allow);// ??????

        UserToken userToken = userTokenManager.signIn("test", "sessionId", "admin", 1000);
        Assert.assertNotNull(userToken);
        // ?????
        userTokenManager.signIn("test2", "sessionId", "admin", 30000);
        Assert.assertEquals(userTokenManager.totalToken(), 2);// 2?token

        Assert.assertEquals(userTokenManager.totalUser(), 1);// 1???

        // ??token??
        userTokenManager.changeUserState("admin", deny);
        userToken = userTokenManager.getByToken(userToken.getToken());
        Assert.assertEquals(userToken.getState(), deny);
        userTokenManager.changeUserState("admin", effective);
        Thread.sleep(1200);
        userToken = userTokenManager.getByToken(userToken.getToken());
        Assert.assertTrue(userToken.isExpired());
        userTokenManager.checkExpiredToken();
        userToken = userTokenManager.getByToken(userToken.getToken());
        Assert.assertTrue((userToken == null));
        Assert.assertEquals(userTokenManager.totalToken(), 1);
        Assert.assertEquals(userTokenManager.totalUser(), 1);
    }

    /**
     * ?????????????
     */
    @Test
    public void testDeny() throws InterruptedException {
        DefaultUserTokenManager userTokenManager = new DefaultUserTokenManager();
        userTokenManager.setAllopatricLoginMode(AllopatricLoginMode.deny);// ????????????????

        userTokenManager.signIn("test", "sessionId", "admin", 10000);
        try {
            userTokenManager.signIn("test2", "sessionId", "admin", 30000);
            Assert.assertTrue(false);
        } catch (AccessDenyException e) {
        }
        Assert.assertTrue(userTokenManager.getByToken("test").isNormal());
        Assert.assertTrue(((userTokenManager.getByToken("test2")) == null));
    }

    /**
     * ????????????
     */
    @Test
    public void testOffline() {
        DefaultUserTokenManager userTokenManager = new DefaultUserTokenManager();
        userTokenManager.setAllopatricLoginMode(offlineOther);// ?????????????

        userTokenManager.signIn("test", "sessionId", "admin", 1000);
        userTokenManager.signIn("test2", "sessionId", "admin", 30000);
        Assert.assertTrue(userTokenManager.getByToken("test2").isNormal());
        Assert.assertTrue(userTokenManager.getByToken("test").isOffline());
    }
}

