package sagan.guides.support;


import Tutorials.REPO_PREFIX;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.social.github.api.GitHubRepo;
import sagan.guides.Tutorial;
import sagan.support.github.Readme;


/**
 * Unit tests for {@link Tutorials}.
 */
public class TutorialsTests {
    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private GuideOrganization org;

    private Tutorials tutorials;

    private GitHubRepo repo;

    private Readme readme;

    private AsciidocGuide tutorial;

    @Test
    public void findAll() {
        BDDMockito.given(org.findRepositoriesByPrefix(REPO_PREFIX)).willReturn(Collections.singletonList(repo));
        BDDMockito.given(org.getAsciidocGuide("/repos/mock-org/tut-rest/zipball")).willReturn(tutorial);
        List<Tutorial> all = tutorials.findAll();
        Assert.assertThat(all.size(), Matchers.is(1));
        Tutorial first = all.get(0);
        Assert.assertThat(first.getGuideId(), Matchers.equalTo("rest"));
        Assert.assertThat(first.getTitle(), Matchers.equalTo("Rest tutorial"));
        Assert.assertThat(first.getSubtitle(), Matchers.equalTo("Learn some rest stuff"));
    }

    @Test
    public void pageZero() throws IOException {
        BDDMockito.given(org.getRepoInfo(ArgumentMatchers.eq("tut-rest"))).willReturn(repo);
        BDDMockito.given(org.getReadme(ArgumentMatchers.eq("/repos/mock-org/tut-rest/readme"))).willReturn(readme);
        BDDMockito.given(org.getAsciidocGuide("/repos/mock-org/tut-rest/zipball")).willReturn(tutorial);
        Tutorial tutorial = tutorials.find("rest");
        Assert.assertThat(tutorial.getContent(), Matchers.equalTo("REST Tutorial"));
    }

    @Test
    public void testParseTutorialName() throws Exception {
        String name = tutorials.parseGuideName("tut-tutorial-name");
        Assert.assertThat(name, Matchers.is("tutorial-name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseEmptyTutorialName() throws Exception {
        tutorials.parseGuideName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidTutorialName() throws Exception {
        tutorials.parseGuideName("invalid");
    }
}

