package net.bytebuddy.implementation.bytecode.collection;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
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
public class ArrayAccessTest {
    private final TypeDescription typeDescription;

    private final int loadOpcode;

    private final int storeOpcode;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public ArrayAccessTest(Class<?> type, int loadOpcode, int storeOpcode) {
        typeDescription = of(type);
        this.loadOpcode = loadOpcode;
        this.storeOpcode = storeOpcode;
    }

    @Test
    public void testLoad() throws Exception {
        ArrayAccess arrayAccess = ArrayAccess.of(typeDescription);
        MatcherAssert.assertThat(arrayAccess.load().isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = arrayAccess.load().apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(((typeDescription.getStackSize().getSize()) - 2)));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(typeDescription.getStackSize().getSize()));
        Mockito.verify(methodVisitor).visitInsn(loadOpcode);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }

    @Test
    public void testStore() throws Exception {
        ArrayAccess arrayAccess = ArrayAccess.of(typeDescription);
        MatcherAssert.assertThat(arrayAccess.store().isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = arrayAccess.store().apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is((-((typeDescription.getStackSize().getSize()) + 2))));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(0));
        Mockito.verify(methodVisitor).visitInsn(storeOpcode);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

