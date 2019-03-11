package com.github.jknack.handlebars.bench;


import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;


public class HbsBench {
    private Map<String, Object> context;

    private Template template;

    @Test
    public void single() throws IOException {
        template.apply(context);
    }

    @Test
    public void benchmark() throws IOException {
        new Bench(1000, 5, 30).run(new Bench.Unit() {
            @Override
            public void run() throws IOException {
                template.apply(context);
            }

            @Override
            public String toString() {
                return "stocks.hbs";
            }
        });
    }
}

