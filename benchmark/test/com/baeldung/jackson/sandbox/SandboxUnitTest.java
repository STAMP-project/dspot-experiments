package com.baeldung.jackson.sandbox;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;


// 
public class SandboxUnitTest {
    @Test
    public final void whenDeserializing_thenCorrect() throws JsonParseException, JsonMappingException, IOException {
        final TestElement testElement = new TestElement();
        testElement.setX(10);
        testElement.setY("adasd");
        final ObjectMapper om = new ObjectMapper();
        om.setVisibility(om.getSerializationConfig().getDefaultVisibilityChecker().withFieldVisibility(ANY).withGetterVisibility(NONE));
        final String serialized = om.writeValueAsString(testElement);
        System.err.println(serialized);
    }
}

