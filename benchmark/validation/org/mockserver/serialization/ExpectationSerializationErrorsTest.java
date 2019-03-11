package org.mockserver.serialization;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockserver.mock.Expectation;
import org.mockserver.validator.jsonschema.JsonSchemaExpectationValidator;


/**
 *
 *
 * @author jamesdbloom
 */
public class ExpectationSerializationErrorsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @Mock
    private JsonArraySerializer jsonArraySerializer;

    @Mock
    private JsonSchemaExpectationValidator expectationValidator;

    @InjectMocks
    private ExpectationSerializer expectationSerializer;

    @Test
    public void shouldHandleNullAndEmptyWhileSerializingArray() throws IOException {
        // when
        Assert.assertEquals("[]", expectationSerializer.serialize(new Expectation[]{  }));
        Assert.assertEquals("[]", expectationSerializer.serialize(((Expectation[]) (null))));
    }

    @Test
    public void shouldValidateInputForObject() throws IOException {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage((("1 error:" + (NEW_LINE)) + " - an expectation is required but value was \"\""));
        // when
        expectationSerializer.deserialize("");
    }

    @Test
    public void shouldValidateInputForArray() throws IOException {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage((("1 error:" + (NEW_LINE)) + " - an expectation or expectation array is required but value was \"\""));
        // when
        expectationSerializer.deserializeArray("");
    }
}

