package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.FieldVisitor;

import static net.bytebuddy.dynamic.scaffold.FieldRegistry.Compiled.NoOp.INSTANCE;


public class FieldRegistryCompiledNoOpTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private FieldDescription fieldDescription;

    @Test(expected = IllegalStateException.class)
    public void testCannotResolveDefault() throws Exception {
        INSTANCE.target(fieldDescription).resolveDefault(FieldDescription.NO_DEFAULT_VALUE);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolveFieldAppender() throws Exception {
        INSTANCE.target(fieldDescription).getFieldAppender();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotApplyPartially() throws Exception {
        INSTANCE.target(fieldDescription).apply(Mockito.mock(FieldVisitor.class), Mockito.mock(AnnotationValueFilter.Factory.class));
    }

    @Test
    public void testReturnsFieldAttributeAppender() throws Exception {
        TypeWriter.FieldPool.Record record = INSTANCE.target(fieldDescription);
        MatcherAssert.assertThat(record.isImplicit(), CoreMatchers.is(true));
    }
}

