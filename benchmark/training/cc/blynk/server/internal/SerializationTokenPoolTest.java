package cc.blynk.server.internal;


import cc.blynk.server.internal.token.BaseToken;
import cc.blynk.server.internal.token.ResetPassToken;
import cc.blynk.server.internal.token.TokensPool;
import cc.blynk.utils.TokenGeneratorUtil;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;


public class SerializationTokenPoolTest {
    @Test
    public void someTEst() {
        String path = System.getProperty("java.io.tmpdir");
        TokensPool tokensPool = new TokensPool(path);
        String token = TokenGeneratorUtil.generateNewToken();
        ResetPassToken resetPassToken = new ResetPassToken("dima@mail.us", "Blynk");
        tokensPool.addToken(token, resetPassToken);
        tokensPool.close();
        TokensPool tokensPool2 = new TokensPool(path);
        ConcurrentHashMap<String, BaseToken> tokens = tokensPool2.getTokens();
        Assert.assertNotNull(tokens);
        Assert.assertEquals(1, tokens.size());
        ResetPassToken resetPassToken2 = ((ResetPassToken) (tokens.get(token)));
        Assert.assertNotNull(resetPassToken2);
        Assert.assertEquals("dima@mail.us", resetPassToken2.email);
        Assert.assertEquals("Blynk", resetPassToken2.appName);
    }
}

