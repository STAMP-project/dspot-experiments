package marytts.tools.voiceimport;


import java.io.File;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DatabaseLayoutTest {
    File dummyConfigFile;

    DatabaseLayout db;

    @Rule
    public TemporaryFolder dummyVoiceDir = new TemporaryFolder();

    @Test
    public void isInitialized() {
        Assert.assertTrue(db.isInitialized());
        Assert.assertNotNull(db.getAllophoneSet());
        Assert.assertNotNull(db.getLocale());
    }
}

