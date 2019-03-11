package jenkins.security;


import hudson.FilePath;
import hudson.Functions;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DefaultConfidentialStoreTest {
    @Rule
    public TemporaryFolder tmpRule = new TemporaryFolder();

    @Test
    public void roundtrip() throws Exception {
        File tmp = new File(tmpRule.getRoot(), "tmp");// let ConfidentialStore create a directory

        DefaultConfidentialStore store = new DefaultConfidentialStore(tmp);
        ConfidentialKey key = new ConfidentialKey("test") {};
        // basic roundtrip
        String str = "Hello world!";
        store.store(key, str.getBytes());
        Assert.assertEquals(str, new String(store.load(key)));
        // data storage should have some stuff
        Assert.assertTrue(new File(tmp, "test").exists());
        Assert.assertTrue(new File(tmp, "master.key").exists());
        Assert.assertThat(FileUtils.readFileToString(new File(tmp, "test")), CoreMatchers.not(CoreMatchers.containsString("Hello")));// the data shouldn't be a plain text, obviously

        if (!(Functions.isWindows())) {
            Assert.assertEquals(448, ((new FilePath(tmp).mode()) & 511));// should be read only

        }
        // if the master key changes, we should gracefully fail to load the store
        new File(tmp, "master.key").delete();
        DefaultConfidentialStore store2 = new DefaultConfidentialStore(tmp);
        Assert.assertTrue(new File(tmp, "master.key").exists());// we should have a new key now

        Assert.assertNull(store2.load(key));
    }
}

