package handlebarsjs.spec;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.HandlebarsError;
import com.github.jknack.handlebars.HandlebarsException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class PartialsTest extends AbstractTest {
    @Test
    public void basicPartials() throws IOException {
        String string = "Dudes: {{#dudes}}{{> dude}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") });
        shouldCompileToWithPartials(string, hash, AbstractTest.$("dude", partial), "Dudes: Yehuda (http://yehuda) Alan (http://alan) ", "Basic partials output based on current context.");
    }

    @Test
    public void partialsWithContext() throws IOException {
        String string = "Dudes: {{>dude dudes}}";
        String partial = "{{#this}}{{name}} ({{url}}) {{/this}}";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") });
        shouldCompileToWithPartials(string, hash, AbstractTest.$("dude", partial), "Dudes: Yehuda (http://yehuda) Alan (http://alan) ", "Partials can be passed a context");
    }

    @Test
    public void partialInPartial() throws IOException {
        String string = "Dudes: {{#dudes}}{{>dude}}{{/dudes}}";
        String dude = "{{name}} {{> url}} ";
        String url = "<a href='{{url}}'>{{url}}</a>";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") });
        shouldCompileToWithPartials(string, hash, AbstractTest.$("dude", dude, "url", url), "Dudes: Yehuda <a href='http://yehuda'>http://yehuda</a> Alan <a href='http://alan'>http://alan</a> ", "Partials are rendered inside of other partials");
    }

    @Test
    public void renderingUndefinedPartialThrowsException() throws IOException {
        try {
            compile("{{> whatever}}").apply(AbstractTest.$);
            Assert.fail("rendering undefined partial throws an exception");
        } catch (HandlebarsException ex) {
            HandlebarsError error = ex.getError();
            Assert.assertNotNull(error);
            Assert.assertEquals("The partial '/whatever.hbs' at '/whatever.hbs' could not be found", error.reason);
        }
    }

    @Test
    public void renderingFunctionPartial() throws IOException {
        String string = "Dudes: {{#dudes}}{{> dude}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") });
        shouldCompileToWithPartials(string, hash, AbstractTest.$("dude", partial), "Dudes: Yehuda (http://yehuda) Alan (http://alan) ", "Function partials output based in VM.");
    }

    @Test
    public void partialPrecedingSelector() throws IOException {
        String string = "Dudes: {{>dude}} {{another_dude}}";
        String dude = "{{name}}";
        Object hash = AbstractTest.$("name", "Jeepers", "another_dude", "Creepers");
        shouldCompileToWithPartials(string, hash, AbstractTest.$("dude", dude), "Dudes: Jeepers Creepers", "Regular selectors can follow a partial");
    }

    @Test
    public void partialWithLiteralPaths() throws IOException {
        String string = "Dudes: {{> [dude]}}";
        String dude = "{{name}}";
        Object hash = AbstractTest.$("name", "Jeepers", "another_dude", "Creepers");
        shouldCompileToWithPartials(string, hash, AbstractTest.$("dude", dude), "Dudes: Jeepers", "Partials can use literal paths");
    }
}

