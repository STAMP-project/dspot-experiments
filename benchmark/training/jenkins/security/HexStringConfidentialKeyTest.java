package jenkins.security;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class HexStringConfidentialKeyTest {
    @Rule
    public ConfidentialStoreRule store = new ConfidentialStoreRule();

    @Test
    public void hexStringShouldProduceHexString() {
        HexStringConfidentialKey key = new HexStringConfidentialKey("test", 8);
        Assert.assertTrue(key.get().matches("[A-Fa-f0-9]{8}"));
    }

    @Test
    public void multipleGetsAreIdempotent() {
        HexStringConfidentialKey key = new HexStringConfidentialKey("test", 8);
        Assert.assertEquals(key.get(), key.get());
    }

    @Test
    public void specifyLengthAndMakeSureItTakesEffect() {
        for (int n : new int[]{ 8, 16, 32, 256 }) {
            Assert.assertEquals(n, new HexStringConfidentialKey(("test" + n), n).get().length());
        }
    }

    @Test
    public void loadingExistingKey() {
        HexStringConfidentialKey key1 = new HexStringConfidentialKey("test", 8);
        key1.get();// this causes the ke to be generated

        // this second key of the same ID will cause it to load the key from the disk
        HexStringConfidentialKey key2 = new HexStringConfidentialKey("test", 8);
        Assert.assertEquals(key1.get(), key2.get());
    }
}

