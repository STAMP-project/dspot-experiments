package com.github.jknack.handlebars.i368;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.AbstractTest;
import java.io.IOException;
import org.junit.Test;


public class Issue368 extends AbstractTest {
    @Test
    public void jsonItShouldHaveIndexVar() throws IOException {
        String value = "{ \"names\": [ { \"id\": \"a\" }, { \"id\": \"b\" }, { \"id\": \"c\" }, { \"id\": \"d\" } ] }";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(value);
        shouldCompileTo("{{#each names}}index={{@index}}, id={{id}}, {{/each}}", jsonNode, "index=0, id=a, index=1, id=b, index=2, id=c, index=3, id=d, ");
    }
}

