package com.github.jknack.handlebars;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;


public class Issue260 extends AbstractTest {
    @Test
    public void stringArray() throws IOException {
        JsonNode tree = new ObjectMapper().readTree("[\"a\", \"b\", \"c\"]");
        shouldCompileTo("{{#each this}}{{{.}}}{{/each}}", tree, "abc");
    }

    @Test
    public void hashStringArray() throws IOException {
        JsonNode tree = new ObjectMapper().readTree("{\"string\": [\"a\", \"b\", \"c\"]}");
        shouldCompileTo("{{#each string}}{{{.}}}{{/each}}", tree, "abc");
    }

    @Test
    public void hashIteratorStringArray() throws IOException {
        JsonNode tree = new ObjectMapper().readTree("{\"string\": [\"a\", \"b\", \"c\"]}");
        shouldCompileTo("{{#each this}}{{#each this}}{{{.}}}{{/each}}{{/each}}", tree, "abc");
    }
}

