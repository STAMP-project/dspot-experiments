package com.github.jknack.handlebars.i280;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.AbstractTest;
import org.junit.Test;


public class Issue280 extends AbstractTest {
    @Test
    public void errorWhileLookingParentContextUsingJsonNodeValueResolver() throws Exception {
        JsonNode node = new ObjectMapper().readTree("[{\"key\": \"value\"}]");
        shouldCompileTo("{{#each this}}{{undefinedKey}}{{/each}}", node, "");
    }
}

