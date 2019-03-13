package sagan.rewrite.support;


import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import org.junit.Test;


public class RewriteTests {
    private FilterChain filterChain;

    @Test
    public void rossensWebSocketPostIsRedirectedToOldBlog() throws Exception {
        validateTemporaryRedirect("http://spring.io/blog/2013/07/24/spring-framework-4-0-m2-websocket-messaging-architectures", "http://assets.spring.io/wp/WebSocketBlogPost.html");
    }

    @Test
    public void videosRedirectToYoutube() throws IOException, URISyntaxException, ServletException {
        validateTemporaryRedirect("http://spring.io/video", "http://www.youtube.com/springsourcedev");
        validateTemporaryRedirect("http://spring.io/videos", "http://www.youtube.com/springsourcedev");
    }

    @Test
    public void supportRenamedMongodbGSGuide() throws IOException, URISyntaxException, ServletException {
        validatePermanentRedirect("/guides/gs/accessing-data-mongo", "/guides/gs/accessing-data-mongodb/");
        validatePermanentRedirect("/guides/gs/accessing-data-mongo/", "/guides/gs/accessing-data-mongodb/");
        validateOk("/guides/gs/accessing-data-mongodb/");
    }

    @Test
    public void supportRenamedXDGuide() throws IOException, URISyntaxException, ServletException {
        validatePermanentRedirect("/guides/gs/spring-xd-osx/", "/guides/gs/spring-xd/");
        validateOk("/guides/gs/spring-xd/");
    }

    @Test
    public void gsgGuidesShouldAlwaysHaveTrailingSlash() throws IOException, URISyntaxException, ServletException {
        validatePermanentRedirect("/guides/gs/guide-name", "/guides/gs/guide-name/");
        validateOk("/guides/gs/guide-name/");
    }

    @Test
    public void tutorialRootShouldHaveTrailingSlash() throws IOException, URISyntaxException, ServletException {
        validatePermanentRedirect("/guides/tutorials/guide-name", "/guides/tutorials/guide-name/");
        validateOk("/guides/tutorials/guide-name/");
    }

    @Test
    public void tutorialPagesShouldAlwaysHaveTrailingSlash() throws IOException, URISyntaxException, ServletException {
        validatePermanentRedirect("/guides/tutorials/guide-name/1", "/guides/tutorials/guide-name/1/");
        validateOk("/guides/tutorials/guide-name/1/");
    }

    @Test
    public void tutorialImagesShouldNeverHaveTrailingSlash() throws IOException, URISyntaxException, ServletException {
        validateOk("/guides/tutorials/rest/images/yummynoodle.jpg");
    }

    @Test
    public void gsgGuidesListingRedirectsToIndex() throws IOException, URISyntaxException, ServletException {
        validateTemporaryRedirect("/guides/gs/", "/guides#gs");
        validateTemporaryRedirect("/guides/gs", "/guides#gs");
    }

    @Test
    public void gsgTutorialsListingRedirectsToIndex() throws IOException, URISyntaxException, ServletException {
        validateTemporaryRedirect("/guides/tutorials/", "/guides#tutorials");
        validateTemporaryRedirect("/guides/tutorials", "/guides#tutorials");
    }

    @Test
    public void stripsWwwSubdomain() throws IOException, URISyntaxException, ServletException {
        validatePermanentRedirect("http://www.spring.io", "https://spring.io/");
        validatePermanentRedirect("http://www.spring.io/something", "https://spring.io/something");
    }

    @Test
    public void projectPageIndexIsNotRedirected() throws IOException, URISyntaxException, ServletException {
        validateOk("http://spring.io/projects");
    }

    @Test
    public void projectPageIndexWithSlashIsNotRedirected() throws IOException, URISyntaxException, ServletException {
        validateOk("http://spring.io/projects/");
    }

    @Test
    public void projectPagesAreRedirected() throws IOException, URISyntaxException, ServletException {
        validateTemporaryRedirect("http://spring.io/spring-data", "https://projects.spring.io/spring-data");
    }

    @Test
    public void gplusIsRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/gplus", "https://plus.google.com/+springframework/");
    }

    @Test
    public void linkedinIsRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/linkedin", "http://www.linkedin.com/groups/46964");
    }

    @Test
    public void deprecatedTutorialsRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/guides/tutorials/data/", "/guides");
        validateTemporaryRedirect("http://spring.io/guides/tutorials/web/", "/guides");
    }

    @Test
    public void tools4IsRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/tools4", "/tools");
    }

    @Test
    public void eclipseToolsIsRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/tools/eclipse", "/tools3/eclipse");
    }

    @Test
    public void sts3IsRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/tools/sts", "/tools");
        validateTemporaryRedirect("http://spring.io/tools/sts/all", "/tools3/sts/all");
    }

    @Test
    public void deprecatedWarGuideRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/guides/gs/convert-jar-to-war-maven/", "/guides/gs/convert-jar-to-war/");
    }
}

