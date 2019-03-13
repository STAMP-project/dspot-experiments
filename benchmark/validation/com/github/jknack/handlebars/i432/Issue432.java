package com.github.jknack.handlebars.i432;


import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Issue432 extends v4Test {
    @Test
    public void withShouldSupportAs() throws IOException {
        shouldCompileTo(("{{#with this as |foo|}}\n" + ("    Foo: {{foo.baz}} \n" + "{{/with}}")), v4Test.$("hash", v4Test.$("baz", "foo")), ("\n" + ("    Foo: foo \n" + "")));
    }
}

