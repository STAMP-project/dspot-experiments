package org.mockserver.serialization.model;


import Body.Type.PARAMETERS;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class ParameterBodyDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        ParameterBodyDTO parameterBody = new ParameterBodyDTO(new ParameterBody(new Parameter("some", "value")));
        // then
        MatcherAssert.assertThat(parameterBody.getParameters().getEntries(), Matchers.containsInAnyOrder(new Parameter("some", "value")));
        MatcherAssert.assertThat(parameterBody.getType(), Is.is(PARAMETERS));
    }

    @Test
    public void shouldBuildCorrectObject() {
        // when
        ParameterBody parameterBody = new ParameterBodyDTO(new ParameterBody(new Parameter("some", "value"))).buildObject();
        // then
        MatcherAssert.assertThat(parameterBody.getValue().getEntries(), Matchers.containsInAnyOrder(new Parameter("some", "value")));
        MatcherAssert.assertThat(parameterBody.getType(), Is.is(PARAMETERS));
    }

    @Test
    public void shouldReturnCorrectObjectFromStaticBuilder() {
        MatcherAssert.assertThat(ParameterBody.params(new Parameter("some", "value")), Is.is(new ParameterBody(new Parameter("some", "value"))));
        MatcherAssert.assertThat(ParameterBody.params(Arrays.asList(new Parameter("some", "value"))), Is.is(new ParameterBody(new Parameter("some", "value"))));
    }
}

