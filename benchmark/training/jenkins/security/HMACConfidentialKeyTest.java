package jenkins.security;


import java.util.Set;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class HMACConfidentialKeyTest {
    @Rule
    public ConfidentialStoreRule store = new ConfidentialStoreRule();

    private HMACConfidentialKey key = new HMACConfidentialKey("test", 16);

    @Test
    public void basics() {
        Set<String> unique = new TreeSet<>();
        for (String str : new String[]{ "Hello world", "", "\u0000" }) {
            String mac = key.mac(str);
            unique.add(mac);
            Assert.assertTrue(mac, mac.matches("[0-9A-Fa-f]{32}"));
            Assert.assertTrue(key.checkMac(str, mac));
            Assert.assertFalse(key.checkMac("garbage", mac));
        }
        Assert.assertEquals("all 3 MAC are different", 3, unique.size());
    }

    @Test
    public void loadingExistingKey() {
        // this second key of the same ID will cause it to load the key from the disk
        HMACConfidentialKey key2 = new HMACConfidentialKey("test", 16);
        for (String str : new String[]{ "Hello world", "", "\u0000" }) {
            Assert.assertEquals(key.mac(str), key2.mac(str));
        }
    }
}

