package cc.blynk.server.core.model;


import cc.blynk.server.core.model.serialization.CopyUtil;
import org.junit.Assert;
import org.junit.Test;


public class CopyObjectTest {
    @Test
    public void testDeepCopy() {
        Profile profile = new Profile();
        Profile copy = CopyUtil.deepCopy(profile);
        Assert.assertNotNull(copy);
        Assert.assertNotSame(copy, profile);
        Assert.assertNotSame(copy.pinsStorage, profile.pinsStorage);
    }
}

