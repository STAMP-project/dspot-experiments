package net.bytebuddy.matcher;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FieldTypeMatcherTest extends AbstractElementMatcherTest<FieldTypeMatcher<?>> {
    @Mock
    private ElementMatcher<? super TypeDescription.Generic> typeMatcher;

    @Mock
    private TypeDescription.Generic fieldType;

    @Mock
    private FieldDescription fieldDescription;

    @SuppressWarnings("unchecked")
    public FieldTypeMatcherTest() {
        super(((Class<? extends FieldTypeMatcher<?>>) ((Object) (FieldTypeMatcher.class))), "ofType");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(typeMatcher.matches(fieldType)).thenReturn(true);
        MatcherAssert.assertThat(new FieldTypeMatcher<FieldDescription>(typeMatcher).matches(fieldDescription), CoreMatchers.is(true));
        Mockito.verify(typeMatcher).matches(fieldType);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(typeMatcher.matches(fieldType)).thenReturn(false);
        MatcherAssert.assertThat(new FieldTypeMatcher<FieldDescription>(typeMatcher).matches(fieldDescription), CoreMatchers.is(false));
        Mockito.verify(typeMatcher).matches(fieldType);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }
}

