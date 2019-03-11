package sagan.guides.support;


import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.StreamUtils;
import sagan.support.github.GitHubClient;


@Ignore
public class DynamicSidebarTests {
    private static final String README_REST_ZIPBALL = "/repos/spring-guides/gs-rest-service/zipball";

    @Mock
    GitHubClient github;

    GuideOrganization org;

    @Test
    public void loadGuideContentWithCategories() throws IOException {
        InputStream inputStream = new DefaultResourceLoader().getResource("classpath:/gs-rest-service-with-tags.zip").getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();
        BDDMockito.given(github.sendRequestForDownload(DynamicSidebarTests.README_REST_ZIPBALL)).willReturn(zipContents);
        AsciidocGuide guide = org.getAsciidocGuide(DynamicSidebarTests.README_REST_ZIPBALL);
        Assert.assertThat(guide.getTableOfContents(), startsWith("<ul class=\"sectlevel1\">"));
        Assert.assertThat(guide.getTableOfContents(), not(containsString("<ul class=\"sectlevel2\">")));
        Assert.assertThat(guide.getContent(), containsString("About 15 minutes"));
    }

    @Test
    public void loadGuideContentWithoutCategories() throws IOException {
        InputStream inputStream = new DefaultResourceLoader().getResource("classpath:/gs-rest-service-without-tags.zip").getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();
        BDDMockito.given(github.sendRequestForDownload(DynamicSidebarTests.README_REST_ZIPBALL)).willReturn(zipContents);
        AsciidocGuide guide = org.getAsciidocGuide(DynamicSidebarTests.README_REST_ZIPBALL);
        Assert.assertThat(guide.getTableOfContents(), startsWith("<ul class=\"sectlevel1\">"));
        Assert.assertThat(guide.getTableOfContents(), not(containsString("<ul class=\"sectlevel2\">")));
        Assert.assertThat(guide.getContent(), containsString("About 15 minutes"));
    }

    @Test
    public void loadGuideContentWithoutCategoriesOrTableOfContents() throws IOException {
        InputStream inputStream = new DefaultResourceLoader().getResource("classpath:/gs-rest-service-no-tags-no-toc.zip").getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();
        BDDMockito.given(github.sendRequestForDownload(DynamicSidebarTests.README_REST_ZIPBALL)).willReturn(zipContents);
        AsciidocGuide guide = org.getAsciidocGuide(DynamicSidebarTests.README_REST_ZIPBALL);
        Assert.assertThat(guide.getTableOfContents(), equalTo(""));
        Assert.assertThat(guide.getContent(), containsString("About 15 minutes"));
    }

    @Test
    public void loadGuideWithCategoriesAndProjects() throws IOException {
        InputStream inputStream = new DefaultResourceLoader().getResource("classpath:/gs-rest-service-with-tags-and-projects.zip").getInputStream();
        byte[] zipContents = StreamUtils.copyToByteArray(inputStream);
        inputStream.close();
        BDDMockito.given(github.sendRequestForDownload(DynamicSidebarTests.README_REST_ZIPBALL)).willReturn(zipContents);
        AsciidocGuide guide = org.getAsciidocGuide(DynamicSidebarTests.README_REST_ZIPBALL);
        Assert.assertThat(guide.getTableOfContents(), startsWith("<ul class=\"sectlevel1\">"));
        Assert.assertThat(guide.getTableOfContents(), not(containsString("<ul class=\"sectlevel2\">")));
        Assert.assertThat(guide.getContent(), containsString("About 15 minutes"));
    }
}

