package cc.blynk.utils.structure;


import cc.blynk.utils.TokenGeneratorUtil;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 23.01.18.
 */
public class TokenGeneratorUtilTest {
    @Test
    public void testCorrectWork() {
        UUID uuid = UUID.randomUUID();
        Assert.assertEquals(uuid.toString().replace("-", ""), TokenGeneratorUtil.fastToString(uuid));
    }
}

