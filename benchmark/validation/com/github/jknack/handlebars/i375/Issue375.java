package com.github.jknack.handlebars.i375;


import com.github.jknack.handlebars.AbstractTest;
import com.github.jknack.handlebars.HandlebarsException;
import java.io.IOException;
import org.junit.Test;


public class Issue375 extends AbstractTest {
    @Test
    public void dynamicPartials() throws IOException {
        String string = "Dudes: {{#dudes}}{{> (dude)}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") });
        shouldCompileTo(string, hash, AbstractTest.$("dude", "dyndude"), AbstractTest.$("dyndude", partial), "Dudes: Yehuda (http://yehuda) Alan (http://alan) ", "Basic partials output based on current context.");
    }

    @Test
    public void dynamicPartialsWithParam() throws IOException {
        String string = "Dudes: {{#dudes}}{{> (dude) p1=1}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) p{{p1}} ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") });
        shouldCompileTo(string, hash, AbstractTest.$("dude", "dyndude"), AbstractTest.$("dyndude", partial), "Dudes: Yehuda (http://yehuda) p1 Alan (http://alan) p1 ", "Dynamic partials output based on current context.");
    }

    @Test
    public void dynamicPartialsViaLookupHelper() throws IOException {
        String string = "Dudes: {{#dudes}}{{> (lookup . 'dude')}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") }, "dude", "dyndude");
        shouldCompileToWithPartials(string, hash, AbstractTest.$("dyndude", partial), "Dudes: Yehuda (http://yehuda) Alan (http://alan) ", "Basic partials output based on current context.");
    }

    @Test
    public void dynamicPartialsViaLookupHelper3() throws IOException {
        String string = "Dudes: {{#dudes}}{{> (lookup dude)}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") }, "dude", "dyndude");
        shouldCompileToWithPartials(string, hash, AbstractTest.$("dyndude", partial), "Dudes: Yehuda (http://yehuda) Alan (http://alan) ", "Basic partials output based on current context.");
    }

    @Test
    public void dynamicPartialsViaLookupHelper2() throws IOException {
        String string = "Dudes: {{#dudes}}{{> (lookup ../. 'dude')}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") }, "dude", "dyndude");
        shouldCompileToWithPartials(string, hash, AbstractTest.$("dyndude", partial), "Dudes: Yehuda (http://yehuda) Alan (http://alan) ", "Basic partials output based on current context.");
    }

    @Test
    public void dynamicPartialsViaLookupHelper4() throws IOException {
        String string = "Dudes: {{#dudes}}{{> (lookup this 'name')}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") });
        shouldCompileToWithPartials(string, hash, AbstractTest.$("Yehuda", partial, "Alan", partial), "Dudes: Yehuda (http://yehuda) Alan (http://alan) ", "Basic partials output based on current context.");
    }

    @Test
    public void dynamicPartialsViaLookupHelper5() throws IOException {
        String string = "Dudes: {{#dudes}}{{> (lookup name)}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") });
        shouldCompileToWithPartials(string, hash, AbstractTest.$("Yehuda", partial, "Alan", partial), "Dudes: Yehuda (http://yehuda) Alan (http://alan) ", "Basic partials output based on current context.");
    }

    @Test(expected = HandlebarsException.class)
    public void dynamicPartialNotFound() throws IOException {
        String string = "Dudes: {{#dudes}}{{> (lookup missing 'name')}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") });
        shouldCompileToWithPartials(string, hash, AbstractTest.$("Yehuda", partial, "Alan", partial), "");
    }

    @Test(expected = HandlebarsException.class)
    public void dynamicPartialNotFound2() throws IOException {
        String string = "Dudes: {{#dudes}}{{> (lookup this 'missing')}}{{/dudes}}";
        String partial = "{{name}} ({{url}}) ";
        Object hash = AbstractTest.$("dudes", new Object[]{ AbstractTest.$("name", "Yehuda", "url", "http://yehuda"), AbstractTest.$("name", "Alan", "url", "http://alan") });
        shouldCompileToWithPartials(string, hash, AbstractTest.$("Yehuda", partial, "Alan", partial), "");
    }
}

