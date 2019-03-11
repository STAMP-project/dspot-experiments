package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.test.utility.MockitoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.ClassVisitor;


public class TypeInitializerDrainDefaultTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private TypeWriter.MethodPool methodPool;

    @Mock
    private AnnotationValueFilter.Factory annotationValueFilterFactory;

    @Mock
    private ClassVisitor classVisitor;

    @Mock
    private TypeInitializer typeInitializer;

    @Mock
    private Implementation.Context implementationContext;

    @Mock
    private TypeWriter.MethodPool.Record record;

    @Mock
    private TypeWriter.MethodPool.Record transformed;

    @Test
    public void testDrain() throws Exception {
        Mockito.when(methodPool.target(new MethodDescription.Latent.TypeInitializer(instrumentedType))).thenReturn(record);
        Mockito.when(typeInitializer.wrap(record)).thenReturn(transformed);
        new TypeInitializer.Drain.Default(instrumentedType, methodPool, annotationValueFilterFactory).apply(classVisitor, typeInitializer, implementationContext);
        Mockito.verify(transformed).apply(classVisitor, implementationContext, annotationValueFilterFactory);
        Mockito.verifyNoMoreInteractions(transformed);
    }
}

