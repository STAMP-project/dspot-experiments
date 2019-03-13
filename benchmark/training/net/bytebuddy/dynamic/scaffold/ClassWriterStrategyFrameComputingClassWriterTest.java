package net.bytebuddy.dynamic.scaffold;


import ClassWriterStrategy.FrameComputingClassWriter;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ClassWriterStrategyFrameComputingClassWriterTest {
    private static final String FOO = "pkg/foo";

    private static final String BAR = "pkg/bar";

    private static final String QUX = "pkg/qux";

    private static final String BAZ = "pkg/baz";

    private static final String FOOBAR = "pkg/foobar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypePool typePool;

    @Mock
    private TypeDescription leftType;

    @Mock
    private TypeDescription rightType;

    @Mock
    private TypeDescription superClass;

    @Mock
    private TypeDescription.Generic genericSuperClass;

    private FrameComputingClassWriter frameComputingClassWriter;

    @Test
    public void testLeftIsAssignable() throws Exception {
        Mockito.when(leftType.isAssignableFrom(rightType)).thenReturn(true);
        MatcherAssert.assertThat(frameComputingClassWriter.getCommonSuperClass(ClassWriterStrategyFrameComputingClassWriterTest.FOO, ClassWriterStrategyFrameComputingClassWriterTest.BAR), CoreMatchers.is(ClassWriterStrategyFrameComputingClassWriterTest.QUX));
    }

    @Test
    public void testRightIsAssignable() throws Exception {
        Mockito.when(leftType.isAssignableTo(rightType)).thenReturn(true);
        MatcherAssert.assertThat(frameComputingClassWriter.getCommonSuperClass(ClassWriterStrategyFrameComputingClassWriterTest.FOO, ClassWriterStrategyFrameComputingClassWriterTest.BAR), CoreMatchers.is(ClassWriterStrategyFrameComputingClassWriterTest.BAZ));
    }

    @Test
    public void testLeftIsInterface() throws Exception {
        Mockito.when(leftType.isInterface()).thenReturn(true);
        MatcherAssert.assertThat(frameComputingClassWriter.getCommonSuperClass(ClassWriterStrategyFrameComputingClassWriterTest.FOO, ClassWriterStrategyFrameComputingClassWriterTest.BAR), CoreMatchers.is(TypeDescription.OBJECT.getInternalName()));
    }

    @Test
    public void testRightIsInterface() throws Exception {
        Mockito.when(rightType.isInterface()).thenReturn(true);
        MatcherAssert.assertThat(frameComputingClassWriter.getCommonSuperClass(ClassWriterStrategyFrameComputingClassWriterTest.FOO, ClassWriterStrategyFrameComputingClassWriterTest.BAR), CoreMatchers.is(TypeDescription.OBJECT.getInternalName()));
    }

    @Test
    public void testSuperClassIteration() throws Exception {
        Mockito.when(superClass.isAssignableFrom(rightType)).thenReturn(true);
        MatcherAssert.assertThat(frameComputingClassWriter.getCommonSuperClass(ClassWriterStrategyFrameComputingClassWriterTest.FOO, ClassWriterStrategyFrameComputingClassWriterTest.BAR), CoreMatchers.is(ClassWriterStrategyFrameComputingClassWriterTest.FOOBAR));
    }
}

