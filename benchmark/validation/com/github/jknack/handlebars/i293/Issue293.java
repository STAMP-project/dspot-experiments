package com.github.jknack.handlebars.i293;


import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue293 extends AbstractTest {
    @Test
    public void defaultI18N() throws IOException {
        shouldCompileTo("{{i18n \"hello\"}}", AbstractTest.$, "i293 AR");
    }
}

