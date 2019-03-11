package org.mockserver.serialization.model;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockserver.model.Header;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class HeaderDTOTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        HeaderDTO header = new HeaderDTO(new Header("first", "first_one", "first_two"));
        // then
        MatcherAssert.assertThat(header.getValues(), Matchers.containsInAnyOrder(NottableString.string("first_one"), NottableString.string("first_two")));
        MatcherAssert.assertThat(header.buildObject().getValues(), Matchers.containsInAnyOrder(NottableString.string("first_one"), NottableString.string("first_two")));
    }
}

