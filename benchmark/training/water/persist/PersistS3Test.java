package water.persist;


import IcedS3Credentials.S3_CREDENTIALS_DKV_KEY;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import water.fvec.Chunk;
import water.fvec.FileVec;
import water.fvec.Frame;
import water.util.FileUtils;


/**
 * Created by tomas on 6/27/16.
 */
public class PersistS3Test extends TestUtil {
    private static final String AWS_ACCESS_KEY_PROPERTY_NAME = "AWS_ACCESS_KEY_ID";

    private static final String AWS_SECRET_KEY_PROPERTY_NAME = "AWS_SECRET_ACCESS_KEY";

    private static final String IRIS_H2O_AWS = "s3://test.0xdata.com/h2o-unit-tests/iris.csv";

    private static final String IRIS_BUCKET_H2O_AWS = "s3://test.0xdata.com/h2o-unit-tests";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static class XORTask extends MRTask<PersistS3Test.XORTask> {
        long _res = 0;

        @Override
        public void map(Chunk c) {
            for (int i = 0; i < (c._len); ++i)
                _res ^= c.at8(i);

        }

        @Override
        public void reduce(PersistS3Test.XORTask xort) {
            _res = (_res) ^ (xort._res);
        }
    }

    @Test
    public void testS3Import() throws Exception {
        Scope.enter();
        try {
            Key k = H2O.getPM().anyURIToKey(new URI("s3://h2o-public-test-data/smalldata/airlines/AirlinesTrain.csv.zip"));
            Frame fr = DKV.getGet(k);
            FileVec v = ((FileVec) (fr.anyVec()));
            // make sure we have some chunks
            int chunkSize = ((int) ((v.length()) / 3));
            v.setChunkSize(fr, chunkSize);
            long xor = new PersistS3Test.XORTask().doAll(v)._res;
            Key k2 = H2O.getPM().anyURIToKey(new URI(FileUtils.getFile("smalldata/airlines/AirlinesTrain.csv.zip").getAbsolutePath()));
            FileVec v2 = DKV.getGet(k2);
            TestCase.assertEquals(v2.length(), v.length());
            assertVecEquals(v, v2, 0);
            // make sure we have some chunks
            v2.setChunkSize(chunkSize);
            long xor2 = new PersistS3Test.XORTask().doAll(v2)._res;
            TestCase.assertEquals(xor2, xor);
            fr.delete();
            v2.remove();
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testS3UriToKeyChangedCredentials() throws Exception {
        Scope.enter();
        Key k = null;
        Key k2 = null;
        Frame fr = null;
        FileVec v = null;
        FileVec v2 = null;
        Key credentialsKey = Key.make(S3_CREDENTIALS_DKV_KEY);
        try {
            // This test is only runnable in environment with Amazon credentials properly set {AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY}
            final String accessKey = System.getenv(PersistS3Test.AWS_ACCESS_KEY_PROPERTY_NAME);
            final String secretKey = System.getenv(PersistS3Test.AWS_SECRET_KEY_PROPERTY_NAME);
            Assume.assumeTrue((accessKey != null));
            Assume.assumeTrue((secretKey != null));
            final IcedS3Credentials s3Credentials = new IcedS3Credentials(accessKey, secretKey);
            DKV.put(credentialsKey, s3Credentials);
            k = H2O.getPM().anyURIToKey(new URI(PersistS3Test.IRIS_H2O_AWS));
            fr = DKV.getGet(k);
            v = ((FileVec) (fr.anyVec()));
            // make sure we have some chunks
            int chunkSize = ((int) ((v.length()) / 3));
            v.setChunkSize(fr, chunkSize);
            long xor = new PersistS3Test.XORTask().doAll(v)._res;
            k2 = H2O.getPM().anyURIToKey(new URI(FileUtils.getFile("smalldata/iris/iris.csv").getAbsolutePath()));
            v2 = DKV.getGet(k2);
            TestCase.assertEquals(v2.length(), v.length());
            assertVecEquals(v, v2, 0);
            // make sure we have some chunks
            v2.setChunkSize(chunkSize);
            long xor2 = new PersistS3Test.XORTask().doAll(v2)._res;
            TestCase.assertEquals(xor2, xor);
            fr.delete();
            v2.remove();
        } finally {
            Scope.exit();
            if (k != null)
                k.remove();

            if (k2 != null)
                k2.remove();

            if (fr != null)
                fr.remove();

            if (v != null)
                v.remove();

            if (v2 != null)
                v2.remove();

            if (credentialsKey != null)
                DKV.remove(credentialsKey);

        }
    }

    @Test
    public void testS3ImportFiles() throws Exception {
        PersistS3 persistS3 = new PersistS3();
        final ArrayList<String> keys = new ArrayList<>();
        final ArrayList<String> fails = new ArrayList<>();
        final ArrayList<String> deletions = new ArrayList<>();
        final ArrayList<String> files = new ArrayList<>();
        Key credentialsKey = Key.make(S3_CREDENTIALS_DKV_KEY);
        try {
            // This test is only runnable in environment with Amazon credentials properly set {AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY}
            final String accessKeyId = System.getenv(PersistS3Test.AWS_ACCESS_KEY_PROPERTY_NAME);
            final String secretKey = System.getenv(PersistS3Test.AWS_SECRET_KEY_PROPERTY_NAME);
            Assume.assumeTrue((accessKeyId != null));
            Assume.assumeTrue((secretKey != null));
            final IcedS3Credentials s3Credentials = new IcedS3Credentials(accessKeyId, secretKey);
            DKV.put(credentialsKey, s3Credentials);
            persistS3.importFiles(PersistS3Test.IRIS_H2O_AWS, null, files, keys, fails, deletions);
            TestCase.assertEquals(0, fails.size());
            TestCase.assertEquals(0, deletions.size());
            TestCase.assertEquals(1, files.size());
            TestCase.assertEquals(1, keys.size());
            expectedException.expect(AmazonS3Exception.class);
            expectedException.expectMessage("The AWS Access Key Id you provided does not exist in our records. (Service: Amazon S3; Status Code: 403; Error Code: InvalidAccessKeyId; Request ID:");
            final String unexistingAmazonCredential = UUID.randomUUID().toString();
            final IcedS3Credentials unexistingCredentials = new IcedS3Credentials(unexistingAmazonCredential, unexistingAmazonCredential);
            DKV.put(credentialsKey, unexistingCredentials);
            PersistS3Test.deprecateBucketContentCaches(persistS3);
            persistS3.importFiles(PersistS3Test.IRIS_H2O_AWS, null, files, keys, fails, deletions);
        } finally {
            if (credentialsKey != null)
                DKV.remove(credentialsKey);

            for (String key : keys) {
                final Iced iced = DKV.getGet(key);
                Assert.assertTrue((iced instanceof Frame));
                final Frame frame = ((Frame) (iced));
                frame.remove();
            }
        }
    }

    @Test
    public void testS3ImportFiles_noCredentialsSetExplicitely() {
        PersistS3 persistS3 = new PersistS3();
        final ArrayList<String> keys = new ArrayList<>();
        final ArrayList<String> fails = new ArrayList<>();
        final ArrayList<String> deletions = new ArrayList<>();
        final ArrayList<String> files = new ArrayList<>();
        Key credentialsKey = Key.make(S3_CREDENTIALS_DKV_KEY);
        try {
            // This test is only runnable in environment with Amazon credentials properly set {AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY}
            final String accessKeyId = System.getenv(PersistS3Test.AWS_ACCESS_KEY_PROPERTY_NAME);
            final String secretKey = System.getenv(PersistS3Test.AWS_SECRET_KEY_PROPERTY_NAME);
            Assume.assumeTrue((accessKeyId != null));
            Assume.assumeTrue((secretKey != null));
            // Make sure there are no credentials set
            DKV.remove(credentialsKey);
            // Should not fail on this method invocation, should take the credentials from environment
            // Goal is to prove S3 basic credentials set to null in DKV under given key won't cause any exceptions to be thrown
            persistS3.importFiles(PersistS3Test.IRIS_H2O_AWS, null, files, keys, fails, deletions);
            TestCase.assertEquals(0, fails.size());
            TestCase.assertEquals(0, deletions.size());
            TestCase.assertEquals(1, files.size());
            TestCase.assertEquals(1, keys.size());
        } finally {
            if (credentialsKey != null)
                DKV.remove(credentialsKey);

            for (String key : keys) {
                final Iced iced = DKV.getGet(key);
                Assert.assertTrue((iced instanceof Frame));
                final Frame frame = ((Frame) (iced));
                frame.remove();
            }
        }
    }

    @Test
    public void testS3calcTypeaheadMatchesSingleFile() throws Exception {
        Key credentialsKey = Key.make(S3_CREDENTIALS_DKV_KEY);
        try {
            // This test is only runnable in environment with Amazon credentials properly set {AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY}
            final String accessKey = System.getenv(PersistS3Test.AWS_ACCESS_KEY_PROPERTY_NAME);
            final String secretKey = System.getenv(PersistS3Test.AWS_SECRET_KEY_PROPERTY_NAME);
            Assume.assumeTrue((accessKey != null));
            Assume.assumeTrue((secretKey != null));
            final AmazonS3 defaultClient = PersistS3.getClient();// Create a default client

            Assert.assertNotNull(defaultClient);
            final IcedS3Credentials s3Credentials = new IcedS3Credentials(accessKey, secretKey);
            DKV.put(credentialsKey, s3Credentials);
            PersistS3 persistS3 = new PersistS3();
            final List<String> strings = persistS3.calcTypeaheadMatches(PersistS3Test.IRIS_H2O_AWS, 10);
            Assert.assertNotNull(strings);
            TestCase.assertEquals(1, strings.size());// Only single file returned

            TestCase.assertEquals(PersistS3Test.IRIS_H2O_AWS, strings.get(0));
            expectedException.expect(AmazonS3Exception.class);
            expectedException.expectMessage("The AWS Access Key Id you provided does not exist in our records. (Service: Amazon S3; Status Code: 403; Error Code: InvalidAccessKeyId; Request ID:");
            final String unexistingAmazonCredential = UUID.randomUUID().toString();
            // Also tests cache erasure during client credentials chage. The list of files in the bucket is still in the caches unless erased
            final IcedS3Credentials unexistingCredentials = new IcedS3Credentials(unexistingAmazonCredential, unexistingAmazonCredential);
            DKV.put(credentialsKey, unexistingCredentials);
            PersistS3Test.deprecateBucketContentCaches(persistS3);
            final List<String> failed = persistS3.calcTypeaheadMatches(PersistS3Test.IRIS_H2O_AWS, 10);
        } finally {
            if (credentialsKey != null)
                DKV.remove(credentialsKey);

        }
    }

    @Test
    public void testS3calcTypeaheadMatchesBucketOnly() throws Exception {
        Key credentialsKey = Key.make(S3_CREDENTIALS_DKV_KEY);
        try {
            // This test is only runnable in environment with Amazon credentials properly set {AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY}
            final String accessKey = System.getenv(PersistS3Test.AWS_ACCESS_KEY_PROPERTY_NAME);
            final String secretKey = System.getenv(PersistS3Test.AWS_SECRET_KEY_PROPERTY_NAME);
            Assume.assumeTrue((accessKey != null));
            Assume.assumeTrue((secretKey != null));
            final IcedS3Credentials s3Credentials = new IcedS3Credentials(accessKey, secretKey);
            DKV.put(credentialsKey, s3Credentials);
            PersistS3 persistS3 = new PersistS3();
            final List<String> strings = persistS3.calcTypeaheadMatches(PersistS3Test.IRIS_BUCKET_H2O_AWS, 10);
            Assert.assertNotNull(strings);
            TestCase.assertEquals(3, strings.size());
            expectedException.expect(AmazonS3Exception.class);
            expectedException.expectMessage("The AWS Access Key Id you provided does not exist in our records. (Service: Amazon S3; Status Code: 403; Error Code: InvalidAccessKeyId; Request ID:");
            final String unexistingAmazonCredential = UUID.randomUUID().toString();
            final IcedS3Credentials unexistingCredentials = new IcedS3Credentials(unexistingAmazonCredential, unexistingAmazonCredential);
            DKV.put(credentialsKey, unexistingCredentials);
            PersistS3Test.deprecateBucketContentCaches(persistS3);
            PersistS3Test.deprecatedBucketCache(persistS3);
            // Also tests cache erasure during client credentials chage. The list of files in the bucket is still in the caches unless erased
            final List<String> failed = persistS3.calcTypeaheadMatches(PersistS3Test.IRIS_BUCKET_H2O_AWS, 10);
        } finally {
            if (credentialsKey != null)
                DKV.remove(credentialsKey);

        }
    }
}

