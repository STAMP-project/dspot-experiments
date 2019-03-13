package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.FieldAttributeAppender;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;


public class TypeWriterFieldPoolRecordTest {
    private static final int MODIFIER = 42;

    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private FieldAttributeAppender fieldAttributeAppender;

    @Mock
    private AnnotationValueFilter valueFilter;

    @Mock
    private AnnotationValueFilter.Factory annotationValueFilterFactory;

    @Mock
    private ClassVisitor classVisitor;

    @Mock
    private FieldVisitor fieldVisitor;

    @Mock
    private AnnotationVisitor annotationVisitor;

    @Mock
    private FieldDescription fieldDescription;

    @Mock
    private AnnotationDescription annotationDescription;

    @Mock
    private TypeDescription annotationType;

    @Mock
    private Object defaultValue;

    @Test
    public void testExplicitFieldEntryProperties() throws Exception {
        TypeWriter.FieldPool.Record record = new TypeWriter.FieldPool.Record.ForExplicitField(fieldAttributeAppender, defaultValue, fieldDescription);
        MatcherAssert.assertThat(record.getFieldAppender(), CoreMatchers.is(fieldAttributeAppender));
        MatcherAssert.assertThat(record.resolveDefault(FieldDescription.NO_DEFAULT_VALUE), CoreMatchers.is(defaultValue));
        MatcherAssert.assertThat(record.isImplicit(), CoreMatchers.is(false));
    }

    @Test
    public void testExplicitFieldEntryWritesField() throws Exception {
        TypeWriter.FieldPool.Record record = new TypeWriter.FieldPool.Record.ForExplicitField(fieldAttributeAppender, defaultValue, fieldDescription);
        record.apply(classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitField(TypeWriterFieldPoolRecordTest.MODIFIER, TypeWriterFieldPoolRecordTest.FOO, TypeWriterFieldPoolRecordTest.BAR, TypeWriterFieldPoolRecordTest.QUX, defaultValue);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(fieldAttributeAppender).apply(fieldVisitor, fieldDescription, valueFilter);
        Mockito.verifyNoMoreInteractions(fieldAttributeAppender);
        Mockito.verify(fieldVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(fieldVisitor);
    }

    @Test
    public void testExplicitFieldEntryWritesFieldPartialApplication() throws Exception {
        TypeWriter.FieldPool.Record record = new TypeWriter.FieldPool.Record.ForExplicitField(fieldAttributeAppender, defaultValue, fieldDescription);
        record.apply(fieldVisitor, annotationValueFilterFactory);
        Mockito.verify(fieldAttributeAppender).apply(fieldVisitor, fieldDescription, valueFilter);
        Mockito.verifyNoMoreInteractions(fieldAttributeAppender);
        Mockito.verifyZeroInteractions(fieldVisitor);
    }

    @Test
    public void testImplicitFieldEntryProperties() throws Exception {
        TypeWriter.FieldPool.Record record = new TypeWriter.FieldPool.Record.ForImplicitField(fieldDescription);
        MatcherAssert.assertThat(record.isImplicit(), CoreMatchers.is(true));
    }

    @Test
    public void testImplicitFieldEntryWritesField() throws Exception {
        TypeWriter.FieldPool.Record record = new TypeWriter.FieldPool.Record.ForImplicitField(fieldDescription);
        record.apply(classVisitor, annotationValueFilterFactory);
        Mockito.verify(classVisitor).visitField(TypeWriterFieldPoolRecordTest.MODIFIER, TypeWriterFieldPoolRecordTest.FOO, TypeWriterFieldPoolRecordTest.BAR, TypeWriterFieldPoolRecordTest.QUX, FieldDescription.NO_DEFAULT_VALUE);
        Mockito.verifyNoMoreInteractions(classVisitor);
        Mockito.verify(fieldVisitor).visitAnnotation(TypeWriterFieldPoolRecordTest.BAZ, true);
        Mockito.verify(fieldVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(fieldVisitor);
        Mockito.verify(annotationVisitor).visitEnd();
        Mockito.verifyNoMoreInteractions(annotationVisitor);
    }

    @Test(expected = IllegalStateException.class)
    public void testImplicitFieldWritesFieldPartialApplication() throws Exception {
        new TypeWriter.FieldPool.Record.ForImplicitField(fieldDescription).apply(fieldVisitor, annotationValueFilterFactory);
    }

    @Test(expected = IllegalStateException.class)
    public void testImplicitFieldEntryAppliedToField() throws Exception {
        new TypeWriter.FieldPool.Record.ForImplicitField(fieldDescription).apply(fieldVisitor, annotationValueFilterFactory);
    }
}

