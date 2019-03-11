package net.bytebuddy.description.type;


import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.PARAMETERIZED;
import static net.bytebuddy.description.type.TypeDescription.Generic.OfParameterizedType.RenderingDelegate.CURRENT;


public class TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription ownerErasure;

    @Mock
    private TypeDescription.Generic ownerType;

    @Test
    public void testJava8OwnerTypeParameterized() throws Exception {
        Mockito.when(ownerType.getSort()).thenReturn(PARAMETERIZED);
        StringBuilder stringBuilder = new StringBuilder();
        FOR_JAVA_8_CAPABLE_VM.apply(stringBuilder, typeDescription, ownerType);
        Assert.assertThat(stringBuilder.toString(), CoreMatchers.is((((((((TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.QUX) + ".") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.BAZ)) + "$") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.FOO)) + ".") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.BAR))));
    }

    @Test
    public void testJava8NonParameterized() throws Exception {
        Mockito.when(ownerType.getSort()).thenReturn(NON_GENERIC);
        StringBuilder stringBuilder = new StringBuilder();
        FOR_JAVA_8_CAPABLE_VM.apply(stringBuilder, typeDescription, ownerType);
        Assert.assertThat(stringBuilder.toString(), CoreMatchers.is((((((TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.QUX) + ".") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.BAZ)) + "$") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.BAR))));
    }

    @Test
    public void testJava8NoOwner() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        FOR_JAVA_8_CAPABLE_VM.apply(stringBuilder, typeDescription, null);
        Assert.assertThat(stringBuilder.toString(), CoreMatchers.is((((TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.FOO) + ".") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.BAR))));
    }

    @Test
    public void testLegacyParameterized() throws Exception {
        Mockito.when(ownerType.getSort()).thenReturn(PARAMETERIZED);
        StringBuilder stringBuilder = new StringBuilder();
        FOR_LEGACY_VM.apply(stringBuilder, typeDescription, ownerType);
        Assert.assertThat(stringBuilder.toString(), CoreMatchers.is((((((TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.QUX) + ".") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.BAZ)) + ".") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.BAR))));
    }

    @Test
    public void testLegacyNonParameterized() throws Exception {
        Mockito.when(ownerType.getSort()).thenReturn(NON_GENERIC);
        StringBuilder stringBuilder = new StringBuilder();
        FOR_LEGACY_VM.apply(stringBuilder, typeDescription, ownerType);
        Assert.assertThat(stringBuilder.toString(), CoreMatchers.is((((((((TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.QUX) + ".") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.BAZ)) + ".") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.FOO)) + ".") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.BAR))));
    }

    @Test
    public void testLegacy8NoOwner() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        FOR_LEGACY_VM.apply(stringBuilder, typeDescription, null);
        Assert.assertThat(stringBuilder.toString(), CoreMatchers.is((((TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.FOO) + ".") + (TypeDescriptionGenericOfParameterizedTypeRenderingDelegateTest.BAR))));
    }

    @Test
    public void testCurrent() throws Exception {
        Assert.assertThat(CURRENT, CoreMatchers.is((ClassFileVersion.ofThisVm().isAtLeast(ClassFileVersion.JAVA_V8) ? FOR_JAVA_8_CAPABLE_VM : FOR_LEGACY_VM)));
    }
}

