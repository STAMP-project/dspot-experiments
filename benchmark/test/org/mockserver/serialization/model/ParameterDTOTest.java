package org.mockserver.serialization.model;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockserver.model.NottableString;
import org.mockserver.model.Parameter;


/**
 *
 *
 * @author jamesdbloom
 */
public class ParameterDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        ParameterDTO parameter = new ParameterDTO(new Parameter("first", "first_one", "first_two"));
        // then
        MatcherAssert.assertThat(parameter.getValues(), Matchers.containsInAnyOrder(NottableString.string("first_one"), NottableString.string("first_two")));
        MatcherAssert.assertThat(parameter.buildObject().getValues(), Matchers.containsInAnyOrder(NottableString.string("first_one"), NottableString.string("first_two")));
    }
}

