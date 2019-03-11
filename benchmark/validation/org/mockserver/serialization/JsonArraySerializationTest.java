package org.mockserver.serialization;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author jamesdbloom
 */
public class JsonArraySerializationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @InjectMocks
    private JsonArraySerializer jsonArraySerializer;

    @Test
    public void shouldHandleExceptionWhileDeserializingArray() throws IOException {
        // given
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("java.io.IOException: TEST EXCEPTION");
        // and
        Mockito.when(objectMapper.readTree(ArgumentMatchers.eq("requestBytes"))).thenThrow(new IOException("TEST EXCEPTION"));
        // when
        jsonArraySerializer.returnJSONObjects("requestBytes");
    }

    @Test
    public void shouldReturnArrayItems() throws IOException {
        // when
        MatcherAssert.assertThat(new JsonArraySerializer().returnJSONObjects("[{'foo':'bar'},{'foo':'bar'}]"), CoreMatchers.hasItems((((("{" + (NEW_LINE)) + "  \"foo\" : \"bar\"") + (NEW_LINE)) + "}"), (((("{" + (NEW_LINE)) + "  \"foo\" : \"bar\"") + (NEW_LINE)) + "}")));
        MatcherAssert.assertThat(new JsonArraySerializer().returnJSONObjects("[{},{}]"), CoreMatchers.hasItems("{ }", "{ }"));
        MatcherAssert.assertThat(new JsonArraySerializer().returnJSONObjects("[{}]"), CoreMatchers.hasItems("{ }"));
        MatcherAssert.assertThat(new JsonArraySerializer().returnJSONObjects("[\"\"]"), CoreMatchers.hasItems("\"\""));
        MatcherAssert.assertThat(new JsonArraySerializer().returnJSONObjects("[\"\",\"\"]"), CoreMatchers.hasItems("\"\"", "\"\""));
        MatcherAssert.assertThat(new JsonArraySerializer().returnJSONObjects("[\"ab\",\"cd\"]"), CoreMatchers.hasItems("\"ab\"", "\"cd\""));
    }
}

