package net.bytebuddy.implementation.auxiliary;


import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;


public class TrivialTypeTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private MethodAccessorFactory methodAccessorFactory;

    @Test
    public void testPlain() throws Exception {
        Mockito.when(classFileVersion.getMinorMajorVersion()).thenReturn(ClassFileVersion.JAVA_V5.getMinorMajorVersion());
        DynamicType dynamicType = TrivialType.PLAIN.make(TrivialTypeTest.FOO, classFileVersion, methodAccessorFactory);
        MatcherAssert.assertThat(dynamicType.getTypeDescription().getName(), CoreMatchers.is(TrivialTypeTest.FOO));
        MatcherAssert.assertThat(dynamicType.getTypeDescription().getModifiers(), CoreMatchers.is(Opcodes.ACC_SYNTHETIC));
        MatcherAssert.assertThat(dynamicType.getTypeDescription().getDeclaredAnnotations().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getLoadedTypeInitializers().get(dynamicType.getTypeDescription()).isAlive(), CoreMatchers.is(false));
    }

    @Test
    public void testEager() throws Exception {
        Mockito.when(classFileVersion.getMinorMajorVersion()).thenReturn(ClassFileVersion.JAVA_V5.getMinorMajorVersion());
        DynamicType dynamicType = TrivialType.SIGNATURE_RELEVANT.make(TrivialTypeTest.FOO, classFileVersion, methodAccessorFactory);
        MatcherAssert.assertThat(dynamicType.getTypeDescription().getName(), CoreMatchers.is(TrivialTypeTest.FOO));
        MatcherAssert.assertThat(dynamicType.getTypeDescription().getModifiers(), CoreMatchers.is(Opcodes.ACC_SYNTHETIC));
        MatcherAssert.assertThat(dynamicType.getTypeDescription().getDeclaredAnnotations().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(dynamicType.getTypeDescription().getDeclaredAnnotations().isAnnotationPresent(AuxiliaryType.SignatureRelevant.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(dynamicType.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        MatcherAssert.assertThat(dynamicType.getLoadedTypeInitializers().get(dynamicType.getTypeDescription()).isAlive(), CoreMatchers.is(false));
    }
}

