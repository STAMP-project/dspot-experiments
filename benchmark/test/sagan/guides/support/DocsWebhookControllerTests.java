package sagan.guides.support;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.Charset;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;


/**
 * Unit tests for {@link DocsWebhookController}.
 */
public class DocsWebhookControllerTests {
    private ObjectMapper objectMapper;

    @Mock
    private Tutorials tutorials;

    @Mock
    private UnderstandingDocs understandingDocs;

    @Mock
    private GettingStartedGuides gettingStartedGuides;

    @Mock
    private Topicals topicals;

    private DocsWebhookController controller;

    @Test
    public void testHmacValue() throws Exception {
        this.controller.verifyHmacSignature("this is a test message", "sha1=5df34e8979dc9a831873a42c6e172546f6937190");
    }

    @Test(expected = WebhookAuthenticationException.class)
    public void testInvalidHmacValue() throws Exception {
        this.controller.verifyHmacSignature("this is a test message", "sha1=wronghmacvalue");
    }

    @Test(expected = JsonParseException.class)
    public void testInvalidPayload() throws Exception {
        String payload = "{ invalid: true";
        this.controller.processGuidesUpdate(payload, "sha1=57a5af868a58183684f68ffe9ff44f112cfbfdaf", "push");
    }

    @Test
    public void testGuideWebhookPing() throws Exception {
        BDDMockito.given(this.gettingStartedGuides.parseGuideName("gs-test-guide")).willReturn("test-guide");
        String payload = StreamUtils.copyToString(new ClassPathResource("fixtures/webhooks/pingWebhook.json").getInputStream(), Charset.forName("UTF-8")).replaceAll("[\\n|\\r]", "");
        ResponseEntity response = this.controller.processGuidesUpdate(payload, "sha1=9E629DCCF4472F600D048510354BE400B8EB25CB", "ping");
        MatcherAssert.assertThat(response.getBody(), Matchers.is("{ \"message\": \"Successfully processed ping event\" }\n"));
        MatcherAssert.assertThat(response.getStatusCode().value(), Matchers.is(200));
        Mockito.verify(this.gettingStartedGuides, Mockito.never()).evictFromCache("test-guide");
    }

    @Test
    public void testGuideCacheEviction() throws Exception {
        BDDMockito.given(this.gettingStartedGuides.parseGuideName("gs-test-guide")).willReturn("test-guide");
        String payload = StreamUtils.copyToString(new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), Charset.forName("UTF-8")).replaceAll("[\\n|\\r]", "");
        ResponseEntity response = this.controller.processGuidesUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98", "push");
        MatcherAssert.assertThat(response.getBody(), Matchers.is("{ \"message\": \"Successfully processed update\" }\n"));
        MatcherAssert.assertThat(response.getStatusCode().value(), Matchers.is(200));
        Mockito.verify(this.gettingStartedGuides, Mockito.times(1)).evictFromCache("test-guide");
    }

    @Test
    public void testGuideCacheEviction2() throws Exception {
        BDDMockito.given(this.gettingStartedGuides.parseGuideName("gs-test-guide")).willReturn("test-guide");
        String payload = StreamUtils.copyToString(new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), Charset.forName("UTF-8")).replaceAll("[\\n|\\r]", "");
        ResponseEntity response = this.controller.processGuidesUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98", "push", "gs-test-guide");
        MatcherAssert.assertThat(response.getBody(), Matchers.is("{ \"message\": \"Successfully processed update\" }\n"));
        MatcherAssert.assertThat(response.getStatusCode().value(), Matchers.is(200));
        Mockito.verify(this.gettingStartedGuides, Mockito.times(1)).evictFromCache("test-guide");
    }

    @Test
    public void testTutorialCacheEviction() throws Exception {
        BDDMockito.given(this.tutorials.parseGuideName("gs-test-guide")).willReturn("test-guide");
        String payload = StreamUtils.copyToString(new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), Charset.forName("UTF-8")).replaceAll("[\\n|\\r]", "");
        ResponseEntity response = this.controller.processTutorialsUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98", "push");
        MatcherAssert.assertThat(response.getBody(), Matchers.is("{ \"message\": \"Successfully processed update\" }\n"));
        MatcherAssert.assertThat(response.getStatusCode().value(), Matchers.is(200));
        Mockito.verify(this.tutorials, Mockito.times(1)).evictFromCache("test-guide");
    }

    @Test
    public void testTutorialCacheEviction2() throws Exception {
        BDDMockito.given(this.tutorials.parseGuideName("gs-test-guide")).willReturn("test-guide");
        String payload = StreamUtils.copyToString(new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), Charset.forName("UTF-8")).replaceAll("[\\n|\\r]", "");
        ResponseEntity response = this.controller.processTutorialsUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98", "push", "gs-test-guide");
        MatcherAssert.assertThat(response.getBody(), Matchers.is("{ \"message\": \"Successfully processed update\" }\n"));
        MatcherAssert.assertThat(response.getStatusCode().value(), Matchers.is(200));
        Mockito.verify(this.tutorials, Mockito.times(1)).evictFromCache("test-guide");
    }

    @Test
    public void testUnderstandingCacheEviction() throws Exception {
        String payload = StreamUtils.copyToString(new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), Charset.forName("UTF-8")).replaceAll("[\\n|\\r]", "");
        ResponseEntity response = this.controller.processUnderstandingUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98", "push");
        MatcherAssert.assertThat(response.getBody(), Matchers.is("{ \"message\": \"Successfully processed update\" }\n"));
        MatcherAssert.assertThat(response.getStatusCode().value(), Matchers.is(200));
        Mockito.verify(this.understandingDocs, Mockito.times(1)).clearCache();
    }

    @Test
    public void testTopicalCacheEviction() throws Exception {
        BDDMockito.given(this.topicals.parseGuideName("top-test-guide")).willReturn("test-guide");
        String payload = StreamUtils.copyToString(new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), Charset.forName("UTF-8")).replaceAll("[\\n|\\r]", "");
        ResponseEntity response = this.controller.processTopicalsUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98", "push", "top-test-guide");
        MatcherAssert.assertThat(response.getBody(), Matchers.is("{ \"message\": \"Successfully processed update\" }\n"));
        MatcherAssert.assertThat(response.getStatusCode().value(), Matchers.is(200));
        Mockito.verify(this.topicals, Mockito.times(1)).evictFromCache("test-guide");
    }
}

