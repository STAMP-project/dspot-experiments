package com.github.jknack.handlebars;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


// TODO: Re-enabled me later
@Ignore
public class ToJavaScriptTest extends AbstractTest {
    @Test
    public void toSingleJavaScript() throws InterruptedException {
        ToJavaScriptTest.assertConcurrent("toJavaScript", new Runnable() {
            int execution = 0;

            @Override
            public void run() {
                try {
                    Template template = compile("<ul>{{#list}}<li>{{name}}</li>{{/list}}</ul>");
                    long start = System.currentTimeMillis();
                    String js = template.toJavaScript();
                    long end = System.currentTimeMillis();
                    Assert.assertEquals(1258, js.length());
                    System.out.printf("Single execution: %s took: %sms\n", ((execution)++), (end - start));
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }, 10, 1000);
    }

    @Test
    public void toSharedJavaScript() throws IOException, InterruptedException {
        final Template template = compile("<ul>{{#list}}<li>{{name}}</li>{{/list}}</ul>");
        ToJavaScriptTest.assertConcurrent("toJavaScript", new Runnable() {
            int execution = 0;

            @Override
            public void run() {
                long start = System.currentTimeMillis();
                String js = template.toJavaScript();
                long end = System.currentTimeMillis();
                Assert.assertEquals(1258, js.length());
                System.out.printf("Shared execution: %s took: %sms\n", ((execution)++), (end - start));
            }
        }, 10, 2000);
    }
}

