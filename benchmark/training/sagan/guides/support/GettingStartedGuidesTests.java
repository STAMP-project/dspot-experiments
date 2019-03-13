package sagan.guides.support;


import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.web.client.RestClientException;
import sagan.guides.GettingStartedGuide;
import sagan.guides.Guide;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;
import sagan.support.github.Readme;


/**
 * Unit tests for {@link GettingStartedGuides}.
 */
public class GettingStartedGuidesTests {
    private static final String GUIDE_ID = "rest-service";

    private static final String GUIDE_REPO_NAME = "gs-rest-service";

    private static final GitHubRepo REPO_INFO = new GitHubRepo();

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private GuideOrganization org;

    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private Guide gsRestService;

    private GettingStartedGuides gsGuides;

    private Readme readme = new Readme();

    @Test
    public void loadGuideTitle() throws IOException {
        String description = "Awesome Guide :: Learn awesome stuff with this guide";
        GettingStartedGuidesTests.REPO_INFO.setDescription(description);
        BDDMockito.given(org.getReadme(ArgumentMatchers.anyString())).willReturn(readme);
        BDDMockito.given(org.getRepoInfo(GettingStartedGuidesTests.GUIDE_REPO_NAME)).willReturn(GettingStartedGuidesTests.REPO_INFO);
        BDDMockito.given(org.getAsciidocGuide("/repos/mock-org/gs-rest-service/zipball")).willReturn(new AsciidocGuide("Awesome Guide", "Table of C"));
        GettingStartedGuide guide = gsGuides.find(GettingStartedGuidesTests.GUIDE_ID);
        Assert.assertThat(guide.getTitle(), Matchers.equalTo("Awesome Guide"));
    }

    @Test
    public void loadGuideSubtitle() throws IOException {
        String description = "Awesome Guide :: Learn awesome stuff with this guide";
        GettingStartedGuidesTests.REPO_INFO.setDescription(description);
        BDDMockito.given(org.getReadme(ArgumentMatchers.anyString())).willReturn(readme);
        BDDMockito.given(org.getRepoInfo(GettingStartedGuidesTests.GUIDE_REPO_NAME)).willReturn(GettingStartedGuidesTests.REPO_INFO);
        BDDMockito.given(org.getAsciidocGuide("/repos/mock-org/gs-rest-service/zipball")).willReturn(new AsciidocGuide("Awesome Guide", "Table of C"));
        GettingStartedGuide guide = gsGuides.find(GettingStartedGuidesTests.GUIDE_ID);
        Assert.assertThat(guide.getSubtitle(), Matchers.equalTo("Learn awesome stuff with this guide"));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ResourceNotFoundException.class)
    public void unknownGuide() {
        String unknownGuideId = "foo";
        BDDMockito.given(org.getRepoInfo(ArgumentMatchers.anyString())).willThrow(ResourceNotFoundException.class);
        BDDMockito.given(org.getAsciidocGuide(ArgumentMatchers.anyString())).willThrow(ResourceNotFoundException.class);
        GettingStartedGuide unknownGuide = gsGuides.find(unknownGuideId);
        unknownGuide.getContent();// should throw

    }

    @Test
    public void loadImage() throws IOException {
        byte[] bytes = new byte[]{ 'a' };
        String imageName = "welcome.png";
        GitHubRepo dummyRepo = new GitHubRepo();
        dummyRepo.setDescription("dummy");
        BDDMockito.given(org.getRepoInfo(ArgumentMatchers.anyString())).willReturn(dummyRepo);
        BDDMockito.given(org.getGuideImage(ArgumentMatchers.eq(GettingStartedGuidesTests.GUIDE_REPO_NAME), ArgumentMatchers.eq(imageName))).willReturn(bytes);
        byte[] result = gsGuides.loadImage(gsRestService, imageName);
        Assert.assertThat(result, Matchers.equalTo(bytes));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ResourceNotFoundException.class)
    public void unknownImage() {
        String unknownImage = "uknown_image.png";
        GitHubRepo dummyRepo = new GitHubRepo();
        dummyRepo.setDescription("dummy");
        BDDMockito.given(org.getRepoInfo(ArgumentMatchers.anyString())).willReturn(dummyRepo);
        BDDMockito.given(org.getGuideImage(ArgumentMatchers.eq(GettingStartedGuidesTests.GUIDE_REPO_NAME), ArgumentMatchers.eq(unknownImage))).willThrow(RestClientException.class);
        gsGuides.loadImage(gsRestService, unknownImage);
    }

    @Test
    public void testParseGuideName() throws Exception {
        String guideName = gsGuides.parseGuideName("gs-guide-name");
        Assert.assertThat(guideName, Matchers.is("guide-name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseEmptyGuideName() throws Exception {
        gsGuides.parseGuideName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidGuideName() throws Exception {
        gsGuides.parseGuideName("invalid");
    }
}

