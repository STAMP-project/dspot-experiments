package sagan.blog.support;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.support.github.GitHubClient;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMarkdownServiceTest {
    @Mock
    GitHubClient gitHub;

    private MarkdownService service;

    @Test
    public void renderToHtml_sendsMarkdownToGithub_returnsHtml() {
        String response = "<h3>Title</h3>";
        BDDMockito.given(gitHub.sendPostRequestForHtml(ArgumentMatchers.anyString(), ArgumentMatchers.eq("### Title"))).willReturn(response);
        MatcherAssert.assertThat(service.renderToHtml("### Title"), Matchers.equalTo(response));
    }
}

