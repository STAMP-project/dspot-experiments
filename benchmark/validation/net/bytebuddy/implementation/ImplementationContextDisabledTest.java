package net.bytebuddy.implementation;


import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.TypeInitializer;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.Implementation.Context.Disabled.Factory.INSTANCE;
import static net.bytebuddy.implementation.MethodAccessorFactory.AccessType.DEFAULT;


public class ImplementationContextDisabledTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private TypeWriter.MethodPool methodPool;

    @Mock
    private TypeWriter.MethodPool.Record record;

    @Test
    public void testFactory() throws Exception {
        MatcherAssert.assertThat(INSTANCE.make(instrumentedType, Mockito.mock(AuxiliaryType.NamingStrategy.class), Mockito.mock(TypeInitializer.class), classFileVersion, Mockito.mock(ClassFileVersion.class)), FieldByFieldComparison.hasPrototype(((Implementation.Context.ExtractableView) (new Implementation.Context.Disabled(instrumentedType, classFileVersion)))));
    }

    @Test(expected = IllegalStateException.class)
    public void testFactoryWithTypeInitializer() throws Exception {
        TypeInitializer typeInitializer = Mockito.mock(TypeInitializer.class);
        Mockito.when(typeInitializer.isDefined()).thenReturn(true);
        INSTANCE.make(instrumentedType, Mockito.mock(AuxiliaryType.NamingStrategy.class), typeInitializer, Mockito.mock(ClassFileVersion.class), Mockito.mock(ClassFileVersion.class));
    }

    @Test
    public void testDisabled() throws Exception {
        MatcherAssert.assertThat(new Implementation.Context.Disabled(instrumentedType, classFileVersion).isEnabled(), CoreMatchers.is(false));
    }

    @Test
    public void testAuxiliaryTypes() throws Exception {
        MatcherAssert.assertThat(new Implementation.Context.Disabled(instrumentedType, classFileVersion).getAuxiliaryTypes().size(), CoreMatchers.is(0));
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotCacheValue() throws Exception {
        new Implementation.Context.Disabled(instrumentedType, classFileVersion).cache(Mockito.mock(StackManipulation.class), Mockito.mock(TypeDescription.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotCreateFieldGetter() throws Exception {
        new Implementation.Context.Disabled(instrumentedType, classFileVersion).registerGetterFor(Mockito.mock(FieldDescription.class), DEFAULT);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotCreateFieldSetter() throws Exception {
        new Implementation.Context.Disabled(instrumentedType, classFileVersion).registerSetterFor(Mockito.mock(FieldDescription.class), DEFAULT);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotCreateMethodAccessor() throws Exception {
        new Implementation.Context.Disabled(instrumentedType, classFileVersion).registerAccessorFor(Mockito.mock(Implementation.SpecialMethodInvocation.class), DEFAULT);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotRegisterAuxiliaryType() throws Exception {
        new Implementation.Context.Disabled(instrumentedType, classFileVersion).register(Mockito.mock(AuxiliaryType.class));
    }

    @Test
    public void testClassFileVersion() throws Exception {
        MatcherAssert.assertThat(new Implementation.Context.Disabled(instrumentedType, classFileVersion).getClassFileVersion(), CoreMatchers.is(classFileVersion));
    }

    @Test
    public void testInstrumentationGetter() throws Exception {
        MatcherAssert.assertThat(new Implementation.Context.Disabled(instrumentedType, classFileVersion).getInstrumentedType(), CoreMatchers.is(instrumentedType));
    }
}

