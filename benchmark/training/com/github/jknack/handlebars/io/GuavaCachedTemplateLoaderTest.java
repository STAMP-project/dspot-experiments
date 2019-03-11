package com.github.jknack.handlebars.io;


import com.google.common.base.Stopwatch;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class GuavaCachedTemplateLoaderTest {
    @Test
    public void testCacheWithExpiration() throws Exception {
        TemplateLoader loader = new FileTemplateLoader(new File("src/test/resources"));
        TemplateLoader cachedLoader = GuavaCachedTemplateLoader.cacheWithExpiration(loader, 200, TimeUnit.MILLISECONDS);
        Assert.assertNotNull(loader.sourceAt("template"));
        Assert.assertNotNull(cachedLoader.sourceAt("template"));
        final int TOTAL = 1000;
        Stopwatch sw = Stopwatch.createStarted();
        for (int i = 0; i < TOTAL; i++) {
            loader.sourceAt("template");
        }
        sw.stop();
        System.out.println(("Loader took: " + sw));
        sw.reset().start();
        for (int i = 0; i < TOTAL; i++) {
            cachedLoader.sourceAt("template");
        }
        System.out.println(("Cached took: " + sw));
    }

    @Test
    public void testCacheWithExpirationAgain() throws Exception {
        // lazy mans JVM warming
        testCacheWithExpiration();
    }
}

