package net.bytebuddy.implementation.bytecode.member;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;


@RunWith(Parameterized.class)
public class FieldAccessTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private final boolean isStatic;

    private final StackSize fieldSize;

    private final int getterChange;

    private final int getterMaximum;

    private final int getterOpcode;

    private final int putterChange;

    private final int putterMaximum;

    private final int putterOpcode;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private FieldDescription.InDefinedShape fieldDescription;

    @Mock
    private TypeDescription declaringType;

    @Mock
    private TypeDescription fieldType;

    @Mock
    private TypeDescription.Generic genericFieldType;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public FieldAccessTest(boolean isStatic, StackSize fieldSize, int getterChange, int getterMaximum, int getterOpcode, int putterChange, int putterMaximum, int putterOpcode) {
        this.isStatic = isStatic;
        this.fieldSize = fieldSize;
        this.getterChange = getterChange;
        this.getterMaximum = getterMaximum;
        this.getterOpcode = getterOpcode;
        this.putterChange = putterChange;
        this.putterMaximum = putterMaximum;
        this.putterOpcode = putterOpcode;
    }

    @Test
    public void testGetter() throws Exception {
        FieldAccess.Defined getter = FieldAccess.forField(fieldDescription);
        MatcherAssert.assertThat(getter.read().isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = getter.read().apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(getterChange));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(getterMaximum));
        Mockito.verify(methodVisitor).visitFieldInsn(getterOpcode, FieldAccessTest.FOO, FieldAccessTest.BAR, FieldAccessTest.QUX);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }

    @Test
    public void testPutter() throws Exception {
        FieldAccess.Defined setter = FieldAccess.forField(fieldDescription);
        MatcherAssert.assertThat(setter.write().isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = setter.write().apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(putterChange));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(putterMaximum));
        Mockito.verify(methodVisitor).visitFieldInsn(putterOpcode, FieldAccessTest.FOO, FieldAccessTest.BAR, FieldAccessTest.QUX);
        Mockito.verifyNoMoreInteractions(methodVisitor);
    }
}

