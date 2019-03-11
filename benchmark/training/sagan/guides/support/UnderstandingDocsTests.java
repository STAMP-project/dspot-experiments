package sagan.guides.support;


import java.util.ArrayList;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import sagan.guides.UnderstandingDoc;
import sagan.support.github.RepoContent;


/**
 * Unit tests for {@link UnderstandingDocs}
 */
public class UnderstandingDocsTests {
    private ArrayList<RepoContent> repoContents = new ArrayList<>();

    private GuideOrganization org = Mockito.mock(GuideOrganization.class);

    private UnderstandingDocs understandingGuidesService = new UnderstandingDocs(org, "my-udocs-repo");

    @Test
    public void returnsOnlyDirContents() {
        List<UnderstandingDoc> guides = understandingGuidesService.findAll();
        MatcherAssert.assertThat(guides.size(), Matchers.equalTo(2));
        MatcherAssert.assertThat(guides.get(0).getSubject(), Matchers.equalTo("foo"));
        MatcherAssert.assertThat(guides.get(1).getSubject(), Matchers.equalTo("rest"));
    }

    @Test
    public void testGetsContentForGuide() throws Exception {
        BDDMockito.given(org.getMarkdownFileAsHtml(ArgumentMatchers.matches(".*foo.*README.*"))).willReturn("Understanding: foo!");
        BDDMockito.given(org.getMarkdownFileAsHtml(ArgumentMatchers.matches(".*rest.*README.*"))).willReturn("Understanding: rest");
        List<UnderstandingDoc> guides = understandingGuidesService.findAll();
        MatcherAssert.assertThat(guides.get(0).getContent(), Matchers.equalTo("Understanding: foo!"));
        MatcherAssert.assertThat(guides.get(1).getContent(), Matchers.equalTo("Understanding: rest"));
    }
}

