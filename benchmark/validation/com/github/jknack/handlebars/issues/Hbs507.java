package com.github.jknack.handlebars.issues;


import NullTemplateCache.INSTANCE;
import com.github.jknack.handlebars.cache.HighConcurrencyTemplateCache;
import com.github.jknack.handlebars.v4Test;
import java.io.IOException;
import org.junit.Test;


public class Hbs507 extends v4Test {
    @Test
    public void shouldCallPartialWithoutSideEffect() throws IOException {
        noSideEffect(INSTANCE);
        noSideEffect(new HighConcurrencyTemplateCache());
    }
}

