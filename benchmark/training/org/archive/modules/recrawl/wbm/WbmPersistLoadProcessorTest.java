package org.archive.modules.recrawl.wbm;


import ProcessResult.PROCEED;
import RecrawlAttributeConstants.A_CONTENT_DIGEST;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.archive.modules.CrawlURI;
import org.archive.modules.ProcessResult;
import org.archive.net.UURIFactory;


/**
 * unit test for {@link WbmPersistLoadProcessor}.
 *
 * TODO:
 * <ul>
 * <li>test for pathological cases: illegal chars, incorrect length, etc.
 * <li>test if connection is properly released back to the pool for all possible cases.
 * </ul>
 *
 * @author kenji
 */
public class WbmPersistLoadProcessorTest extends TestCase {
    public void testBuildURL() throws Exception {
        WbmPersistLoadProcessor t = new WbmPersistLoadProcessor();
        t.setQueryURL("http://web.archive.org/cdx/search/cdx?url=$u&startDate=$s&limit=1");
        final String URL = "http://archive.org/";
        String url = t.buildURL(URL);
        System.err.println(url);
        TestCase.assertTrue("has encode URL", Pattern.matches(((".*[&?]url=" + (URLEncoder.encode(URL, "UTF-8"))) + "([&].*)?"), url));
        TestCase.assertTrue("has startDate", Pattern.matches(".*[&?]startDate=\\d{14}([&].*)?", url));
        TestCase.assertTrue("has limit", Pattern.matches(".*[&?]limit=\\d+([&].*)?", url));
        // assertTrue("has last=true", Pattern.matches(".*[&?]last=true([&].*)?", url));
    }

    /**
     * stub HttpResponse for normal case.
     *
     * @author kenji
     */
    public static class TestNormalHttpResponse extends BasicHttpResponse {
        public static final String EXPECTED_TS = "20121101155310";

        public static final String EXPECTED_HASH = "GHN5VKF3TBKNSEZTASOM23BJRTKFFNJK";

        public TestNormalHttpResponse() {
            super(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 0), 200, "OK"));
            setEntity(new ByteArrayEntity(((((("org,archive)/ " + (WbmPersistLoadProcessorTest.TestNormalHttpResponse.EXPECTED_TS)) + " http://archive.org/ text/html 200 ") + (WbmPersistLoadProcessorTest.TestNormalHttpResponse.EXPECTED_HASH)) + " - - 6908 982548871 ") + "google.es-20121101-155506/IA-FOC-google.es-20121101073708-00001.warc.gz\n").getBytes()));
        }
    }

    public void testInnerProcessResultSingleShotWithRealServer() throws Exception {
        WbmPersistLoadProcessor t = new WbmPersistLoadProcessor();
        // CrawlURI curi = new CrawlURI(UURIFactory.getInstance("http://archive.org/"));
        CrawlURI curi = new CrawlURI(UURIFactory.getInstance("http://www.mext.go.jp/null.gif"));
        ProcessResult result = t.innerProcessResult(curi);
        Map<String, Object> history = getFetchHistory(curi, 0);
        TestCase.assertNotNull("getFetchHistory returns non-null", history);
        String hash = ((String) (history.get(A_CONTENT_DIGEST)));
        TestCase.assertNotNull("CONTENT_DIGEST is non-null", hash);
        TestCase.assertTrue("CONTENT_DIGEST starts with scheme", hash.startsWith(t.getContentDigestScheme()));
        TestCase.assertEquals("CONTENT_DIGEST is a String of length 32", 32, hash.substring(t.getContentDigestScheme().length()).length());
        TestCase.assertEquals("should always return PROCEED", PROCEED, result);
    }

    public static class LoadTask implements Runnable {
        private WbmPersistLoadProcessor p;

        private String uri;

        public LoadTask(WbmPersistLoadProcessor p, String uri) {
            this.p = p;
            this.uri = uri;
        }

        @Override
        public void run() {
            try {
                CrawlURI curi = new CrawlURI(UURIFactory.getInstance(this.uri));
                p.innerProcessResult(curi);
                // System.err.println(curi.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

