package net.bytebuddy.dynamic.scaffold.subclass;


import java.util.Collections;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;


public class ConstructorStrategyForDefaultConstructorTest {
    private static final String FOO = "foo";

    private static final int MODIFIERS = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodRegistry methodRegistry;

    @Mock
    private InstrumentedType instrumentedType;

    @Mock
    private TypeDescription.Generic superClass;

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private MethodDescription.InGenericShape methodDescription;

    @Mock
    private MethodDescription.Token token;

    @Mock
    private AnnotationValue<?, ?> defaultValue;

    private MethodDescription.Token stripped;

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleConstructorsStrategy() throws Exception {
        MatcherAssert.assertThat(new ConstructorStrategy.ForDefaultConstructor().extractConstructors(instrumentedType), CoreMatchers.is(Collections.singletonList(new MethodDescription.Token(Opcodes.ACC_PUBLIC))));
        Mockito.when(methodDescription.getParameters()).thenReturn(new ParameterList.Empty());
        MatcherAssert.assertThat(new ConstructorStrategy.ForDefaultConstructor().inject(instrumentedType, methodRegistry), CoreMatchers.is(methodRegistry));
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testSingleConstructorsStrategyNoSuperConstuctorExtract() throws Exception {
        TypeDescription noConstructor = Mockito.mock(TypeDescription.class);
        TypeDescription.Generic noConstructorSuper = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(noConstructor.getSuperClass()).thenReturn(noConstructorSuper);
        Mockito.when(noConstructorSuper.getDeclaredMethods()).thenReturn(new MethodList.Empty());
        new ConstructorStrategy.ForDefaultConstructor().extractConstructors(noConstructor);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testSingleConstructorsStrategyNoSuperConstuctorInject() throws Exception {
        TypeDescription noConstructor = Mockito.mock(TypeDescription.class);
        TypeDescription.Generic noConstructorSuper = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(noConstructor.getSuperClass()).thenReturn(noConstructorSuper);
        Mockito.when(noConstructorSuper.getDeclaredMethods()).thenReturn(new MethodList.Empty());
        new ConstructorStrategy.ForDefaultConstructor().inject(noConstructor, methodRegistry);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testSingleConstructorsStrategyMultipleSuperConstuctorInject() throws Exception {
        TypeDescription noConstructor = Mockito.mock(TypeDescription.class);
        TypeDescription.Generic noConstructorSuper = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(noConstructor.getSuperClass()).thenReturn(noConstructorSuper);
        Mockito.when(noConstructorSuper.getDeclaredMethods()).thenReturn(new MethodList.Explicit(methodDescription, methodDescription));
        Mockito.when(methodDescription.getParameters()).thenReturn(new ParameterList.Empty());
        new ConstructorStrategy.ForDefaultConstructor().inject(noConstructor, methodRegistry);
    }
}

