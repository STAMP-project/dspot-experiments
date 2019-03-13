package org.robobinding.codegen.apt.type;


import com.google.testing.compile.CompilationRule;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class WrappedDeclaredTypeTest {
    @Rule
    public final CompilationRule compilation = new CompilationRule();

    private WrappedDeclaredType type;

    @Test
    public void shouldReturnCorrectClassName() {
        Assert.assertThat(type.className(), Matchers.equalTo(GenericClass.class.getName()));
    }

    @Test
    public void shouldIsOfTypeReturnTrue() {
        Assert.assertThat(type.isOfType(GenericClass.class), Matchers.equalTo(true));
    }

    @Test
    public void shouldAssignableToSelfAndParent() {
        Assert.assertThat(type.isAssignableTo(GenericClass.class), Matchers.equalTo(true));
        Assert.assertThat(type.isAssignableTo(ParentClass.class), Matchers.equalTo(true));
    }

    @Test
    public void shouldHaveSingleParameterizedType() {
        Assert.assertThat(type.getTypeArguments().size(), Matchers.equalTo(1));
    }
}

