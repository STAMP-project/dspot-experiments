package io.dropwizard.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;


public class DiscoverableSubtypeResolverTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private final DiscoverableSubtypeResolver resolver = new DiscoverableSubtypeResolver(ExampleTag.class);

    @Test
    public void discoversSubtypes() throws Exception {
        assertThat(mapper.readValue("{\"type\":\"a\"}", ExampleSPI.class)).isInstanceOf(ImplA.class);
        assertThat(mapper.readValue("{\"type\":\"b\"}", ExampleSPI.class)).isInstanceOf(ImplB.class);
    }
}

