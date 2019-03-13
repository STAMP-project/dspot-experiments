package cc.blynk.server.reset;


import cc.blynk.server.internal.token.ResetPassToken;
import cc.blynk.server.internal.token.TokensPool;
import cc.blynk.utils.AppNameUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TokensPoolTest {
    @Test
    public void addTokenTest() {
        final ResetPassToken user = new ResetPassToken("test.gmail.com", AppNameUtil.BLYNK);
        final String token = "123";
        final TokensPool tokensPool = new TokensPool("");
        tokensPool.addToken(token, user);
        Assert.assertEquals(user, tokensPool.getBaseToken(token));
    }

    @Test
    public void addTokenTwiceTest() {
        final ResetPassToken user = new ResetPassToken("test.gmail.com", AppNameUtil.BLYNK);
        final String token = "123";
        final TokensPool tokensPool = new TokensPool("");
        tokensPool.addToken(token, user);
        tokensPool.addToken(token, user);
        Assert.assertEquals(1, tokensPool.size());
    }

    @Test
    public void remoteTokenTest() {
        final ResetPassToken user = new ResetPassToken("test.gmail.com", AppNameUtil.BLYNK);
        final String token = "123";
        final TokensPool tokensPool = new TokensPool("");
        tokensPool.addToken(token, user);
        Assert.assertEquals(user, tokensPool.getBaseToken(token));
        tokensPool.removeToken(token);
        Assert.assertEquals(0, tokensPool.size());
        Assert.assertNull(tokensPool.getBaseToken(token));
    }
}

