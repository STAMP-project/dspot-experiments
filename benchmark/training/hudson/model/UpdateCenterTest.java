package hudson.model;


import UpdateCenter.UpdateCenterConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import net.sf.json.JSONObject;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class UpdateCenterTest {
    @Test
    public void toUpdateCenterCheckUrl_http_noQuery() throws Exception {
        Assert.assertThat(UpdateCenterConfiguration.toUpdateCenterCheckUrl("http://updates.jenkins-ci.org/update-center.json").toExternalForm(), CoreMatchers.is("http://updates.jenkins-ci.org/update-center.json?uctest"));
    }

    @Test
    public void toUpdateCenterCheckUrl_https_noQuery() throws Exception {
        Assert.assertThat(UpdateCenterConfiguration.toUpdateCenterCheckUrl("https://updates.jenkins-ci.org/update-center.json").toExternalForm(), CoreMatchers.is("https://updates.jenkins-ci.org/update-center.json?uctest"));
    }

    @Test
    public void toUpdateCenterCheckUrl_http_query() throws Exception {
        Assert.assertThat(UpdateCenterConfiguration.toUpdateCenterCheckUrl("http://updates.jenkins-ci.org/update-center.json?version=2.7").toExternalForm(), CoreMatchers.is("http://updates.jenkins-ci.org/update-center.json?version=2.7&uctest"));
    }

    @Test
    public void toUpdateCenterCheckUrl_https_query() throws Exception {
        Assert.assertThat(UpdateCenterConfiguration.toUpdateCenterCheckUrl("https://updates.jenkins-ci.org/update-center.json?version=2.7").toExternalForm(), CoreMatchers.is("https://updates.jenkins-ci.org/update-center.json?version=2.7&uctest"));
    }

    @Test
    public void toUpdateCenterCheckUrl_file() throws Exception {
        Assert.assertThat(UpdateCenterConfiguration.toUpdateCenterCheckUrl("file://./foo.jar!update-center.json").toExternalForm(), CoreMatchers.is("file://./foo.jar!update-center.json"));
    }

    @Test
    public void noChecksums() {
        try {
            UpdateCenter.verifyChecksums(new UpdateCenterTest.MockDownloadJob(null, null, null), UpdateCenterTest.buildEntryWithExpectedChecksums(null, null, null), new File("example"));
            Assert.fail();
        } catch (IOException ex) {
            Assert.assertEquals("Unable to confirm integrity of downloaded file, refusing installation", ex.getMessage());
        }
    }

    @Test
    public void sha1Match() throws Exception {
        UpdateCenter.verifyChecksums(new UpdateCenterTest.MockDownloadJob(UpdateCenterTest.EMPTY_SHA1, null, null), UpdateCenterTest.buildEntryWithExpectedChecksums(UpdateCenterTest.EMPTY_SHA1, null, null), new File("example"));
    }

    @Test
    public void sha1Mismatch() {
        try {
            UpdateCenter.verifyChecksums(new UpdateCenterTest.MockDownloadJob(UpdateCenterTest.EMPTY_SHA1.replace('k', 'f'), null, null), UpdateCenterTest.buildEntryWithExpectedChecksums(UpdateCenterTest.EMPTY_SHA1, null, null), new File("example"));
            Assert.fail();
        } catch (IOException ex) {
            Assert.assertTrue(ex.getMessage().contains("does not match expected SHA-1, expected '2jmj7l5rSw0yVb/vlWAYkK/YBwk=', actual '2jmj7l5rSw0yVb/vlWAYfK/YBwf='"));
        }
    }

    @Test
    public void sha512ProvidedOnly() throws IOException {
        UpdateCenter.verifyChecksums(new UpdateCenterTest.MockDownloadJob(UpdateCenterTest.EMPTY_SHA1, UpdateCenterTest.EMPTY_SHA256, UpdateCenterTest.EMPTY_SHA512), UpdateCenterTest.buildEntryWithExpectedChecksums(null, null, UpdateCenterTest.EMPTY_SHA512), new File("example"));
    }

    @Test
    public void sha512and256IgnoreCase() throws IOException {
        UpdateCenter.verifyChecksums(new UpdateCenterTest.MockDownloadJob(UpdateCenterTest.EMPTY_SHA1, UpdateCenterTest.EMPTY_SHA256.toUpperCase(Locale.US), UpdateCenterTest.EMPTY_SHA512.toUpperCase(Locale.US)), UpdateCenterTest.buildEntryWithExpectedChecksums(null, UpdateCenterTest.EMPTY_SHA256, UpdateCenterTest.EMPTY_SHA512), new File("example"));
    }

    @Test
    public void sha1DoesNotIgnoreCase() {
        try {
            UpdateCenter.verifyChecksums(new UpdateCenterTest.MockDownloadJob(UpdateCenterTest.EMPTY_SHA1, UpdateCenterTest.EMPTY_SHA256, UpdateCenterTest.EMPTY_SHA512), UpdateCenterTest.buildEntryWithExpectedChecksums(UpdateCenterTest.EMPTY_SHA1.toUpperCase(Locale.US), null, null), new File("example"));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("does not match expected SHA-1, expected '2JMJ7L5RSW0YVB/VLWAYKK/YBWK=', actual '2jmj7l5rSw0yVb/vlWAYkK/YBwk='"));
        }
    }

    @Test
    public void noOverlapForComputedAndProvidedChecksums() {
        try {
            UpdateCenter.verifyChecksums(new UpdateCenterTest.MockDownloadJob(UpdateCenterTest.EMPTY_SHA1, UpdateCenterTest.EMPTY_SHA256, null), UpdateCenterTest.buildEntryWithExpectedChecksums(null, null, UpdateCenterTest.EMPTY_SHA512), new File("example"));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().equals("Unable to confirm integrity of downloaded file, refusing installation"));
        }
    }

    @Test
    public void noOverlapForComputedAndProvidedChecksumsForSpecIncompliantJVM() {
        try {
            UpdateCenter.verifyChecksums(new UpdateCenterTest.MockDownloadJob(UpdateCenterTest.EMPTY_SHA1, null, null), UpdateCenterTest.buildEntryWithExpectedChecksums(null, UpdateCenterTest.EMPTY_SHA256, UpdateCenterTest.EMPTY_SHA512), new File("example"));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().equals("Unable to confirm integrity of downloaded file, refusing installation"));
        }
    }

    private static String EMPTY_SHA1 = "2jmj7l5rSw0yVb/vlWAYkK/YBwk=";

    private static String EMPTY_SHA256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

    private static String EMPTY_SHA512 = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e";

    private static class MockEntry extends UpdateSite.Entry {
        MockEntry(JSONObject o) {
            // needs name, version, url, optionally sha1, sha256, sha512
            super("default", o);
        }
    }

    private static class MockDownloadJob implements UpdateCenter.WithComputedChecksums {
        private final String computedSHA1;

        private final String computedSHA256;

        private final String computedSHA512;

        public MockDownloadJob(String computedSHA1, String computedSHA256, String computedSHA512) {
            this.computedSHA1 = computedSHA1;
            this.computedSHA256 = computedSHA256;
            this.computedSHA512 = computedSHA512;
        }

        public String getComputedSHA1() {
            return this.computedSHA1;
        }

        public String getComputedSHA256() {
            return computedSHA256;
        }

        public String getComputedSHA512() {
            return computedSHA512;
        }
    }
}

