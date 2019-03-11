package com.github.jknack.handlebars;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;


public class Issue412 extends AbstractTest {
    @Test
    public void keyShouldWork() throws IOException {
        JsonNode tree = new ObjectMapper().readTree("{\"firstName\":\"John\", \"lastName\":\"Smith\"}");
        shouldCompileTo("{{#each this}}{{@key}}: {{this}} {{/each}}", tree, "firstName: John lastName: Smith ");
    }
}

