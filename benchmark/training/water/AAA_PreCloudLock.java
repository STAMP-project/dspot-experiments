package water;


import Paxos._cloudLocked;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;

import static TypeMap._check_no_locking;


public class AAA_PreCloudLock extends TestUtil {
    static boolean testRan = false;

    static final int CLOUD_SIZE = 5;

    static final int PARTIAL_CLOUD_SIZE = 2;

    // ---
    // Should be able to load basic status pages without locking the cloud.
    @Test
    public void testBasicStatusPages() {
        // Serve some pages and confirm cloud does not lock
        try {
            _check_no_locking = true;// Blow a nice assert if locking

            Assert.assertFalse(AAA_PreCloudLock.testRan);
            Assert.assertFalse(_cloudLocked);
            AAA_PreCloudLock.stall();
            Assert.assertFalse(_cloudLocked);
            serve("/", null);
            serve("/3/Cloud", null);
            serve("/junk", null);
            serve("/HTTP404", null);
            Properties parms = new Properties();
            parms.setProperty("src", "./smalldata/iris");
            serve("/3/Typeahead/files", parms);
            water.util.Log.info("Testing that logging will not lock a cloud");
            serve("/3/ModelBuilders", null);// Note: no modelbuilders registered yet, so this is a vacuous result

            serve("/3/About", null);
            serve("/3/NodePersistentStorage/categories/environment/names/clips/exists", null);// Flow check for prior Flow clips

            Assert.assertFalse("Check of pre-cloud classes failed.  You likely made a Key before any outside action triggers cloud-lock.  ", _cloudLocked);
        } finally {
            _check_no_locking = false;
            AAA_PreCloudLock.testRan = true;
        }
    }
}

