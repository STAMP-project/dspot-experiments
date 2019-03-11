package sagan.docs.support;


import SearchType.API_DOC;
import java.io.InputStream;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectReleaseBuilder;
import sagan.search.types.ApiDoc;


public class ApiDocumentMapperTests {
    private Project project = new Project("spring", "Spring Project", "http://www.example.com/repo/spring-framework", "http://www.example.com/spring-framework", Collections.<ProjectRelease>emptyList(), "release");

    private ProjectRelease version = new ProjectReleaseBuilder().versionName("3.2.1.RELEASE").releaseStatus(GENERAL_AVAILABILITY).current(true).build();

    private ApiDocumentMapper apiDocumentMapper = new ApiDocumentMapper(project, version);

    @Test
    public void mapOlderJdkApiDocContent() throws Exception {
        InputStream html = new ClassPathResource("/fixtures/apidocs/apiDocument.html", getClass()).getInputStream();
        Document document = Jsoup.parse(html, "UTF-8", "http://example.com/docs");
        ApiDoc searchEntry = apiDocumentMapper.map(document);
        MatcherAssert.assertThat(searchEntry.getRawContent(), IsEqual.equalTo("SomeClass"));
        MatcherAssert.assertThat(searchEntry.getClassName(), IsEqual.equalTo("ClassPathBeanDefinitionScanner"));
    }

    @Test
    public void mapJdk7ApiDocContent() throws Exception {
        InputStream html = new ClassPathResource("/fixtures/apidocs/jdk7javaDoc.html", getClass()).getInputStream();
        Document document = Jsoup.parse(html, "UTF-8", "http://example.com/docs");
        ApiDoc searchEntry = apiDocumentMapper.map(document);
        MatcherAssert.assertThat(searchEntry.getRawContent(), IsEqual.equalTo(document.select(".block").text()));
    }

    @Test
    public void mapApiDoc() throws Exception {
        InputStream html = new ClassPathResource("/fixtures/apidocs/jdk7javaDoc.html", getClass()).getInputStream();
        Document document = Jsoup.parse(html, "UTF-8", "http://example.com/docs");
        ApiDoc searchEntry = apiDocumentMapper.map(document);
        MatcherAssert.assertThat(searchEntry.getType(), IsEqual.equalTo(API_DOC.toString()));
        MatcherAssert.assertThat(searchEntry.getVersion(), IsEqual.equalTo("3.2.1.RELEASE"));
        MatcherAssert.assertThat(searchEntry.getProjectId(), IsEqual.equalTo(project.getId()));
        MatcherAssert.assertThat(searchEntry.getSubTitle(), IsEqual.equalTo("Spring Project (3.2.1.RELEASE API)"));
        MatcherAssert.assertThat(searchEntry.getClassName(), IsEqual.equalTo("ApplicationContext"));
        MatcherAssert.assertThat(searchEntry.getPackageName(), IsEqual.equalTo("org.springframework.context"));
    }
}

