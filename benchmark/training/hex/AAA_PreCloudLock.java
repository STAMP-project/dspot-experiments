package hex;


import Paxos._cloudLocked;
import hex.schemas.CoxPHV3;
import hex.schemas.DRFModelV3;
import hex.schemas.DRFV3;
import hex.schemas.DeepLearningModelV3;
import hex.schemas.DeepLearningV3;
import hex.schemas.ExampleModelV3;
import hex.schemas.ExampleV3;
import hex.schemas.GBMModelV3;
import hex.schemas.GBMV3;
import hex.schemas.GLMModelV3;
import hex.schemas.GLMV3;
import hex.schemas.GLRMModelV3;
import hex.schemas.GLRMV3;
import hex.schemas.GrepModelV3;
import hex.schemas.GrepV3;
import hex.schemas.KMeansModelV3;
import hex.schemas.KMeansV3;
import hex.schemas.MakeGLMModelV3;
import hex.schemas.NaiveBayesModelV3;
import hex.schemas.NaiveBayesV3;
import hex.schemas.PCAModelV3;
import hex.schemas.PCAV3;
import hex.schemas.SharedTreeModelV3;
import hex.schemas.SharedTreeV3;
import hex.schemas.TreeStatsV3;
import hex.schemas.Word2VecModelV3;
import hex.schemas.Word2VecSynonymsV3;
import hex.schemas.Word2VecV3;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.TypeMap;


/**
 * Test class which is used by h2o-algos/testMultiNode.sh to launch a cloud and assure that it is up.
 * Note that it has the same name as and is almost identical to water.AAA_PreCloudLock.
 *
 * @see water.AAA_PreCloudLock
 */
public class AAA_PreCloudLock extends TestUtil {
    static boolean testRan = false;

    static final int CLOUD_SIZE = 4;

    static final int PARTIAL_CLOUD_SIZE = 2;

    // ---
    // Should be able to load basic status pages without locking the cloud.
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    @Test
    public void testBasicStatusPages() {
        TypeMap._check_no_locking = true;// Blow a nice assert if locking

        Assert.assertFalse(AAA_PreCloudLock.testRan);
        Assert.assertFalse(_cloudLocked);
        AAA_PreCloudLock.stall();
        Assert.assertFalse(_cloudLocked);
        // Serve some pages and confirm cloud does not lock
        try {
            serve("/", null);
            serve("/Cloud.json", null);
            serve("/junk", null);
            serve("/HTTP404", null);
            Properties parms = new Properties();
            parms.setProperty("src", "./smalldata/iris");
            serve("/Typeahead/files", parms);
            // Make some Schemas
            new CoxPHV3();
            new DRFModelV3();
            new DRFV3();
            new DeepLearningModelV3();
            new DeepLearningV3();
            new ExampleModelV3();
            new ExampleV3();
            new GBMModelV3();
            new GBMV3();
            new GLMModelV3();
            new GLMV3();
            new GLRMV3();
            new GLRMModelV3();
            new GrepModelV3();
            new GrepV3();
            new KMeansModelV3();
            new KMeansV3();
            new MakeGLMModelV3();
            new NaiveBayesModelV3();
            new NaiveBayesV3();
            new PCAModelV3();
            new PCAV3();
            new SharedTreeModelV3();
            new SharedTreeV3();
            new Word2VecSynonymsV3();
            new TreeStatsV3();
            new Word2VecModelV3();
            new Word2VecV3();
            Assert.assertFalse("Check of pre-cloud classes failed.  You likely made a Key before any outside action triggers cloud-lock.  ", _cloudLocked);
        } finally {
            AAA_PreCloudLock.testRan = true;
            TypeMap._check_no_locking = false;
        }
    }
}

