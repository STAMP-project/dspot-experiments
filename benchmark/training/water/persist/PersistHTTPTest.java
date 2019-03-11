package water.persist;


import H2O.ARGS;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import water.parser.ParseDataset;


public class PersistHTTPTest extends TestUtil {
    @Test
    public void importFilesEager() throws Exception {
        try {
            Scope.enter();
            Frame f = Scope.track(parse_test_file(Key.make("prostate.hex"), "smalldata/prostate/prostate.csv"));
            final String localUrl = ((H2O.getURL(((ARGS.jks) != null ? "https" : "http"))) + "/3/DownloadDataset.bin?frame_id=") + (f._key.toString());
            PersistHTTP p = new PersistHTTP();
            Assert.assertEquals((-1L), p.checkRangeSupport(URI.create(localUrl)));// H2O doesn't support byte-ranges

            ArrayList<String> files = new ArrayList<>();
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> fails = new ArrayList<>();
            ArrayList<String> dels = new ArrayList<>();
            p.importFiles(localUrl, null, files, keys, fails, dels);
            Assert.assertTrue(fails.isEmpty());
            Assert.assertTrue(dels.isEmpty());
            Assert.assertEquals(Collections.singletonList(localUrl), files);
            Assert.assertEquals(Collections.singletonList(localUrl), keys);
            Key<Frame> k = Key.make(localUrl);
            Frame imported = k.get();
            Assert.assertEquals(1, imported.numCols());
            Assert.assertTrue(((imported.vec(0)) instanceof UploadFileVec));// Dataset was uploaded eagerly

            Key<Frame> out = Key.make();
            Frame parsed = Scope.track(ParseDataset.parse(out, k));
            Assert.assertTrue(isBitIdentical(f, parsed));
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void importFilesLazy() throws Exception {
        try {
            Scope.enter();
            Frame f = Scope.track(parse_test_file(Key.make("prostate.hex"), "smalldata/prostate/prostate.csv"));
            final String remoteUrl = "https://h2o-public-test-data.s3.amazonaws.com/smalldata/prostate/prostate.csv";
            final long expectedSize = 9254L;
            PersistHTTP p = new PersistHTTP();
            Assert.assertEquals(expectedSize, p.checkRangeSupport(URI.create(remoteUrl)));// S3 supports byte-ranges

            ArrayList<String> files = new ArrayList<>();
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> fails = new ArrayList<>();
            ArrayList<String> dels = new ArrayList<>();
            p.importFiles(remoteUrl, null, files, keys, fails, dels);
            Key<Frame> k = Key.make(remoteUrl);
            Frame imported = Scope.track(k.get());
            Assert.assertEquals(1, imported.numCols());
            Assert.assertTrue(((imported.vec(0)) instanceof HTTPFileVec));
            ((HTTPFileVec) (imported.vec(0))).setChunkSize(imported, 1024);
            Assert.assertEquals(10, imported.vec(0).nChunks());
            Key<Frame> out = Key.make();
            Frame parsed = Scope.track(ParseDataset.parse(out, k));
            Assert.assertTrue(isBitIdentical(f, parsed));
            Assert.assertEquals(imported.vec(0).nChunks(), f.anyVec().nChunks());
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testPubdev5847ParseCompressed() {
        try {
            Scope.enter();
            final String remoteUrl = "https://raw.githubusercontent.com/h2oai/h2o-3/master/h2o-r/h2o-package/inst/extdata/australia.csv";
            PersistHTTP p = new PersistHTTP();
            ArrayList<String> files = new ArrayList<>();
            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> fails = new ArrayList<>();
            ArrayList<String> dels = new ArrayList<>();
            p.importFiles(remoteUrl, null, files, keys, fails, dels);
            Key<Frame> k = Key.make(remoteUrl);
            Frame imported = Scope.track(k.get());
            Assert.assertEquals(1, imported.numCols());
            Assert.assertTrue(((imported.vec(0)) instanceof HTTPFileVec));
            Key<Frame> out = Key.make();
            Frame parsed = Scope.track(ParseDataset.parse(out, k));
            Assert.assertEquals(251, parsed.numRows());
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testReadContentLength() {
        HttpResponse r = Mockito.mock(HttpResponse.class);
        HttpEntity e = Mockito.mock(HttpEntity.class);
        Mockito.when(e.getContentLength()).thenReturn(42L);
        Mockito.when(r.getEntity()).thenReturn(e);
        Assert.assertEquals(42L, PersistHTTP.readContentLength(r));
    }

    @Test
    public void testReadContentLengthFromRange() {
        HttpResponse r = Mockito.mock(HttpResponse.class);
        HttpEntity e = Mockito.mock(HttpEntity.class);
        Header h = Mockito.mock(Header.class);
        Mockito.when(e.getContentLength()).thenReturn((-1L));
        Mockito.when(r.getEntity()).thenReturn(e);
        Mockito.when(r.getFirstHeader(HttpHeaders.CONTENT_RANGE)).thenReturn(h);
        Mockito.when(h.getValue()).thenReturn("bytes 7-48/anything");
        Assert.assertEquals(42L, PersistHTTP.readContentLength(r));
    }
}

