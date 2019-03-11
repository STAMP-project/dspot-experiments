package com.github.jknack.handlebars;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;


public class Issue598 extends AbstractTest {
    @Test
    public void shouldExtendJsonArrayWithLenghtProperty() throws IOException {
        JsonNode tree = new ObjectMapper().readTree("[1, 2, 3]");
        shouldCompileTo("{{this.length}}", tree, "3");
    }
}

