package net.bytebuddy.implementation.bytecode.assign.primitive;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.test.utility.MockitoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.objectweb.asm.MethodVisitor;


@RunWith(Parameterized.class)
public class VoidAwareAssignerNonVoidToVoidTest {
    private final Class<?> sourceType;

    private final int opcode;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic source;

    @Mock
    private TypeDescription.Generic target;

    @Mock
    private TypeDescription rawSource;

    @Mock
    private Assigner chainedAssigner;

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public VoidAwareAssignerNonVoidToVoidTest(Class<?> sourceType, int opcode) {
        this.sourceType = sourceType;
        this.opcode = opcode;
    }

    @Test
    public void testAssignDefaultValue() throws Exception {
        testAssignDefaultValue(true);
    }

    @Test
    public void testAssignNoDefaultValue() throws Exception {
        testAssignDefaultValue(false);
    }
}

