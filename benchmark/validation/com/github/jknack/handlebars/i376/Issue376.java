package com.github.jknack.handlebars.i376;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue376 extends AbstractTest {
    @Test
    public void noThis() throws IOException {
        shouldCompileTo(("{{#each foo}}" + ("{{#if this.price}}{{numberFormat price \'currency\'}}\n{{/if}}" + "{{/each}}")), AbstractTest.$("foo", new Object[]{ AbstractTest.$("price", 5), AbstractTest.$("price", 7) }), "$5.00\n$7.00\n");
    }

    @Test
    public void withThis() throws IOException {
        shouldCompileTo(("{{#each foo}}" + ("{{#if this.price}}{{numberFormat this.price \'currency\'}}\n{{/if}}" + "{{/each}}")), AbstractTest.$("foo", new Object[]{ AbstractTest.$("price", 5), AbstractTest.$("price", 7) }), "$5.00\n$7.00\n");
    }
}

