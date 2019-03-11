package org.mockserver.model;


import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class PortBindingTest {
    @Test
    public void shouldReturnValuesSetInConstructor() {
        // when
        PortBinding portBinding = new PortBinding();
        // then
        MatcherAssert.assertThat(portBinding.getPorts(), Matchers.empty());
    }

    @Test
    public void shouldReturnValuesFromStaticBuilder() {
        // when
        PortBinding portBinding = PortBinding.portBinding(1, 2, 3);
        // then
        MatcherAssert.assertThat(portBinding.getPorts(), Matchers.contains(1, 2, 3));
    }

    @Test
    public void shouldReturnValuesSetInSetter() {
        // when
        PortBinding portBinding = new PortBinding().setPorts(Arrays.asList(1, 2, 3));
        // then
        MatcherAssert.assertThat(portBinding.getPorts(), Matchers.contains(1, 2, 3));
    }
}

