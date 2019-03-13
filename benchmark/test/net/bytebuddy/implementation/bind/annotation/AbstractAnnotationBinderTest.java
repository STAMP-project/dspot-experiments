package net.bytebuddy.implementation.bind.annotation;


import java.lang.annotation.Annotation;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public abstract class AbstractAnnotationBinderTest<T extends Annotation> extends AbstractAnnotationTest<T> {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    protected AnnotationDescription.Loadable<T> annotationDescription;

    protected T annotation;

    @Mock
    protected MethodDescription.InDefinedShape source;

    @Mock
    protected ParameterDescription target;

    @Mock
    protected Implementation.Target implementationTarget;

    @Mock
    protected TypeDescription instrumentedType;

    @Mock
    protected TypeDescription sourceDeclaringType;

    @Mock
    protected TypeDescription targetDeclaringType;

    @Mock
    protected Assigner assigner;

    @Mock
    protected StackManipulation stackManipulation;

    protected AbstractAnnotationBinderTest(Class<T> annotationType) {
        super(annotationType);
    }

    @Test
    public void testHandledType() throws Exception {
        MatcherAssert.assertThat(getSimpleBinder().getHandledType(), CoreMatchers.<Class<?>>is(annotationType));
    }
}

