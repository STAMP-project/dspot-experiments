package com.github.jknack.handlebars;


import java.io.IOException;
import java.util.Date;
import org.junit.Test;


public class Issue332 extends AbstractTest {
    static final long now = System.currentTimeMillis();

    @Test
    public void resolveThis() throws IOException {
        shouldCompileTo("time is {{this}}", new Date(Issue332.now), ("time is " + (Issue332.now)));
    }

    @Test
    public void resolveNamed() throws IOException {
        shouldCompileTo("time is {{now}}", AbstractTest.$("now", new Date(Issue332.now)), ("time is " + (Issue332.now)));
    }

    @Test
    public void resolvePath() throws IOException {
        shouldCompileTo("time is {{this.now}}", AbstractTest.$("now", new Date(Issue332.now)), ("time is " + (Issue332.now)));
    }
}

