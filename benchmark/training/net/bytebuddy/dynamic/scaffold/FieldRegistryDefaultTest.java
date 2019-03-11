package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.attribute.FieldAttributeAppender;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.FieldVisitor;


public class FieldRegistryDefaultTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private InstrumentedType instrumentedType;

    @Mock
    private FieldAttributeAppender.Factory distinctFactory;

    @Mock
    private FieldAttributeAppender distinct;

    @Mock
    private FieldDescription knownField;

    @Mock
    private FieldDescription unknownField;

    @Mock
    private FieldDescription instrumentedField;

    @Mock
    private LatentMatcher<FieldDescription> latentMatcher;

    @Mock
    private ElementMatcher<FieldDescription> matcher;

    @Mock
    private Object defaultValue;

    @Mock
    private Object otherDefaultValue;

    @Mock
    private Transformer<FieldDescription> transformer;

    @Test(expected = IllegalStateException.class)
    public void testImplicitFieldCannotResolveDefaultValue() throws Exception {
        new FieldRegistry.Default().compile(instrumentedType).target(unknownField).resolveDefault(defaultValue);
    }

    @Test(expected = IllegalStateException.class)
    public void testImplicitFieldCannotReceiveAppender() throws Exception {
        new FieldRegistry.Default().compile(instrumentedType).target(unknownField).getFieldAppender();
    }

    @Test(expected = IllegalStateException.class)
    public void testImplicitFieldCannotRApplyPartially() throws Exception {
        new FieldRegistry.Default().compile(instrumentedType).target(unknownField).apply(Mockito.mock(FieldVisitor.class), Mockito.mock(AnnotationValueFilter.Factory.class));
    }

    @Test
    public void testKnownFieldRegistered() throws Exception {
        TypeWriter.FieldPool fieldPool = new FieldRegistry.Default().prepend(latentMatcher, distinctFactory, defaultValue, transformer).compile(instrumentedType);
        MatcherAssert.assertThat(fieldPool.target(knownField).isImplicit(), CoreMatchers.is(false));
        MatcherAssert.assertThat(fieldPool.target(knownField).getField(), CoreMatchers.is(instrumentedField));
        MatcherAssert.assertThat(fieldPool.target(knownField).getFieldAppender(), CoreMatchers.is(distinct));
        MatcherAssert.assertThat(fieldPool.target(knownField).resolveDefault(otherDefaultValue), CoreMatchers.is(defaultValue));
        MatcherAssert.assertThat(fieldPool.target(unknownField).isImplicit(), CoreMatchers.is(true));
        MatcherAssert.assertThat(fieldPool.target(unknownField).getField(), CoreMatchers.is(unknownField));
    }
}

