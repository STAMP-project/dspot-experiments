package com.github.jknack.handlebars.i356;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue356 extends AbstractTest {
    @Test
    public void traversal() throws IOException {
        shouldCompileTo(("{{#data}}{{#each items}}\n" + (((((((((("   1111:  {{this.trayType}} \n" + "   2222:  {{../this.trayType}}\n") + "   3333:  {{../../trayType}}\n") + "    {{#if imageOverridden ~}}\n") + "        image-overridden\n") + "    {{else ~}}\n") + "        {{#if ../../trayType ~}}\n") + "            size-{{../../trayType}}\n") + "        {{~/if}}\n") + "    {{~/if}}    \n") + "{{/each}}{{/data}}")), AbstractTest.$("trayType", "video", "data", AbstractTest.$("items", new Object[]{ AbstractTest.$("id", "id-1", "name", "name-1") }, "config", AbstractTest.$)), ("\n" + (((("   1111:   \n" + "   2222:  \n") + "   3333:  video\n") + "    size-video    \n") + "")));
    }
}

