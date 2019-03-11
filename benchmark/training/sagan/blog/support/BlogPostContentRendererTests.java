package sagan.blog.support;


import PostFormat.MARKDOWN;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class BlogPostContentRendererTests {
    private PostContentRenderer renderer;

    @Mock
    private MarkdownService markdownService;

    @Test
    public void sendsContentToMarkdownRenderer() throws Exception {
        BDDMockito.given(markdownService.renderToHtml("CONTENT")).willReturn("RENDERED CONTENT");
        MatcherAssert.assertThat(renderer.render("CONTENT", MARKDOWN), Matchers.equalTo("RENDERED CONTENT"));
    }

    @Test
    public void rendersDecodedHtml() throws Exception {
        String encoded = "FIRST\n" + ((("<pre>!{iframe src=\"//www.youtube.com/embed/D6nJSyWB-xA\"}{/iframe}</pre>\n" + "SECOND\n") + "<pre>!{iframe src=\"//www.youtube.com/embed/jplkJIHPGos\"}{/iframe}</pre>\n") + "END");
        String decoded = "FIRST\n" + ((("<iframe src=\"//www.youtube.com/embed/D6nJSyWB-xA\"></iframe>\n" + "SECOND\n") + "<iframe src=\"//www.youtube.com/embed/jplkJIHPGos\"></iframe>\n") + "END");
        BDDMockito.given(markdownService.renderToHtml(encoded)).willReturn(encoded);
        MatcherAssert.assertThat(renderer.render(encoded, MARKDOWN), Matchers.equalTo(decoded));
    }

    @Test
    public void rendersCallouts() throws Exception {
        BDDMockito.given(markdownService.renderToHtml("CONTENT")).willReturn("[callout title=Title]Callout body[/callout]");
        MatcherAssert.assertThat(renderer.render("CONTENT", MARKDOWN), Matchers.equalTo(("<div class=\"callout\">\n" + (("<div class=\"callout-title\">Title</div>\n" + "Callout body\n") + "</div>"))));
    }

    @Test
    public void rendersMultipleCallouts() throws Exception {
        BDDMockito.given(markdownService.renderToHtml("CONTENT")).willReturn("[callout title=Title]Callout body[/callout] other content [callout title=Other Title]Other Callout body[/callout]");
        MatcherAssert.assertThat(renderer.render("CONTENT", MARKDOWN), Matchers.equalTo(("<div class=\"callout\">\n" + ((((((("<div class=\"callout-title\">Title</div>\n" + "Callout body\n") + "</div>") + " other content ") + "<div class=\"callout\">\n") + "<div class=\"callout-title\">Other Title</div>\n") + "Other Callout body\n") + "</div>"))));
    }
}

