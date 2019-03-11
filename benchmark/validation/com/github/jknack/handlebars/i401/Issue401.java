package com.github.jknack.handlebars.i401;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;


public class Issue401 extends AbstractTest {
    @Test
    public void shouldIndentPartial() throws IOException {
        shouldCompileToWithPartials(("{{#each items}}\n" + ((((((("parent line 1 {{this}}\n" + "    {{#withKey ../map key=this}}\n") + "        {{#each this}}\n") + "    {{> child}}\n") + "        {{/each}}\n") + "    {{/withKey}}\n") + "parent line 2 {{this}}\n") + "{{/each}}")), AbstractTest.$("items", Arrays.asList("a", "b", "c"), "map", AbstractTest.$("a", Arrays.asList("one", "two", "three"), "b", Arrays.asList("four", "five", "six"), "c", Arrays.asList("seven", "eight", "nine"))), AbstractTest.$("child", "child {{this}}\n"), ("parent line 1 a\n" + (((((((((((((("    child one\n" + "    child two\n") + "    child three\n") + "parent line 2 a\n") + "parent line 1 b\n") + "    child four\n") + "    child five\n") + "    child six\n") + "parent line 2 b\n") + "parent line 1 c\n") + "    child seven\n") + "    child eight\n") + "    child nine\n") + "parent line 2 c\n") + "")));
    }
}

