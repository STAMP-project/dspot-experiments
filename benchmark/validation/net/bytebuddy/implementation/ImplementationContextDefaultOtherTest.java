package net.bytebuddy.implementation;


import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.TypeInitializer;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;

import static net.bytebuddy.implementation.Implementation.Context.Default.Factory.INSTANCE;


public class ImplementationContextDefaultOtherTest {
    @Test
    public void testFactory() throws Exception {
        MatcherAssert.assertThat(INSTANCE.make(Mockito.mock(TypeDescription.class), Mockito.mock(AuxiliaryType.NamingStrategy.class), Mockito.mock(TypeInitializer.class), Mockito.mock(ClassFileVersion.class), Mockito.mock(ClassFileVersion.class)), CoreMatchers.instanceOf(Implementation.Context.Default.class));
    }

    @Test
    public void testEnabled() throws Exception {
        MatcherAssert.assertThat(new Implementation.Context.Default(Mockito.mock(TypeDescription.class), Mockito.mock(ClassFileVersion.class), Mockito.mock(AuxiliaryType.NamingStrategy.class), Mockito.mock(TypeInitializer.class), Mockito.mock(ClassFileVersion.class)).isEnabled(), CoreMatchers.is(true));
    }

    @Test
    public void testInstrumentationGetter() throws Exception {
        TypeDescription instrumentedType = Mockito.mock(TypeDescription.class);
        MatcherAssert.assertThat(new Implementation.Context.Default(instrumentedType, Mockito.mock(ClassFileVersion.class), Mockito.mock(AuxiliaryType.NamingStrategy.class), Mockito.mock(TypeInitializer.class), Mockito.mock(ClassFileVersion.class)).getInstrumentedType(), CoreMatchers.is(instrumentedType));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDefaultContext() throws Exception {
        new Implementation.Context.Default.DelegationRecord(Mockito.mock(MethodDescription.InDefinedShape.class), Visibility.PACKAGE_PRIVATE) {
            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                throw new AssertionError();
            }

            @Override
            protected Implementation.Context.Default.DelegationRecord with(MethodAccessorFactory.AccessType accessType) {
                throw new AssertionError();
            }
        }.prepend(Mockito.mock(ByteCodeAppender.class));
    }
}

