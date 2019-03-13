package water.persist;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class PersistManagerTest extends TestUtil {
    PersistManager persistManager;

    @Test
    public void calcTypeaheadMatches_emptyPath() {
        // Completely empty path
        List<String> matches = persistManager.calcTypeaheadMatches("", 100);
        Assert.assertNotNull(matches);
        Assert.assertEquals(0, matches.size());
        // Path with spaces (testing trim is being done)
        matches = persistManager.calcTypeaheadMatches("   ", 100);
        Assert.assertNotNull(matches);
        Assert.assertEquals(0, matches.size());
    }
}

