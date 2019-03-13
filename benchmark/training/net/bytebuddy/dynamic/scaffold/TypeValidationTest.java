package net.bytebuddy.dynamic.scaffold;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TypeValidationTest {
    private final TypeValidation typeValidation;

    private final boolean enabled;

    public TypeValidationTest(TypeValidation typeValidation, boolean enabled) {
        this.typeValidation = typeValidation;
        this.enabled = enabled;
    }

    @Test
    public void testIsEnabled() throws Exception {
        Assert.assertThat(typeValidation.isEnabled(), CoreMatchers.is(enabled));
    }

    @Test
    public void testReceival() throws Exception {
        Assert.assertThat(TypeValidation.of(enabled), CoreMatchers.is(typeValidation));
    }
}

