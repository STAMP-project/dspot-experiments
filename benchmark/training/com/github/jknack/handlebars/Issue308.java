package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Test;


public class Issue308 extends AbstractTest {
    @Test
    public void dowork() throws IOException {
        shouldCompileTo("{{#dowork root/results}}name:{{name}}, age:{{age}}, newval:{{newval}} {{/dowork}}", AbstractTest.$("root", AbstractTest.$("results", new Object[]{ AbstractTest.$("name", "edgar", "age", 34), AbstractTest.$("name", "pato", "age", 34) })), "name:edgar, age:34, newval:colleague name:pato, age:34, newval:friend ");
    }
}

