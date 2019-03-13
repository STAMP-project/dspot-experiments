package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.ClassVisitor;

import static net.bytebuddy.dynamic.scaffold.MethodRegistry.Handler.ForAbstractMethod.INSTANCE;
import static net.bytebuddy.dynamic.scaffold.TypeWriter.MethodPool.Record.Sort.DEFINED;
import static net.bytebuddy.dynamic.scaffold.TypeWriter.MethodPool.Record.Sort.IMPLEMENTED;


public class MethodRegistryHandlerTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private InstrumentedType instrumentedType;

    @Mock
    private InstrumentedType preparedInstrumentedType;

    @Mock
    private Implementation implementation;

    @Mock
    private AnnotationValue<?, ?> annotationValue;

    @Mock
    private Implementation.Target implementationTarget;

    @Mock
    private MethodAttributeAppender attributeAppender;

    @Mock
    private ClassVisitor classVisitor;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private MethodDescription methodDescription;

    @Test
    public void testHandlerForAbstractMethod() throws Exception {
        MethodRegistry.Handler handler = INSTANCE;
        MatcherAssert.assertThat(handler.prepare(instrumentedType), CoreMatchers.is(instrumentedType));
        TypeWriter.MethodPool.Record record = handler.compile(implementationTarget).assemble(methodDescription, attributeAppender, Visibility.PUBLIC);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(DEFINED));
    }

    @Test
    public void testHandlerForImplementation() throws Exception {
        MethodRegistry.Handler handler = new MethodRegistry.Handler.ForImplementation(implementation);
        MatcherAssert.assertThat(handler.prepare(instrumentedType), CoreMatchers.is(preparedInstrumentedType));
        TypeWriter.MethodPool.Record record = handler.compile(implementationTarget).assemble(methodDescription, attributeAppender, Visibility.PUBLIC);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(IMPLEMENTED));
    }

    @Test
    public void testHandlerForAnnotationValue() throws Exception {
        MethodRegistry.Handler handler = new MethodRegistry.Handler.ForAnnotationValue(annotationValue);
        MatcherAssert.assertThat(handler.prepare(instrumentedType), CoreMatchers.is(instrumentedType));
        TypeWriter.MethodPool.Record record = handler.compile(implementationTarget).assemble(methodDescription, attributeAppender, Visibility.PUBLIC);
        MatcherAssert.assertThat(record.getSort(), CoreMatchers.is(DEFINED));
    }

    @Test(expected = IllegalStateException.class)
    public void testVisibilityBridgeHandlerPreparationThrowsException() throws Exception {
        MethodRegistry.Handler.ForVisibilityBridge.INSTANCE.prepare(Mockito.mock(InstrumentedType.class));
    }
}

