package net.bytebuddy.matcher;


import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DeclaringFieldMatcherTest extends AbstractElementMatcherTest<DeclaringFieldMatcher<?>> {
    @Mock
    private ElementMatcher<? super FieldList<?>> fieldMatcher;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private FieldList<FieldDescription.InDefinedShape> fieldList;

    @SuppressWarnings("unchecked")
    public DeclaringFieldMatcherTest() {
        super(((Class<DeclaringFieldMatcher<?>>) ((Object) (DeclaringFieldMatcher.class))), "declaresFields");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(typeDescription.getDeclaredFields()).thenReturn(fieldList);
        Mockito.when(fieldMatcher.matches(fieldList)).thenReturn(true);
        MatcherAssert.assertThat(new DeclaringFieldMatcher<TypeDescription>(fieldMatcher).matches(typeDescription), CoreMatchers.is(true));
        Mockito.verify(fieldMatcher).matches(fieldList);
        Mockito.verifyNoMoreInteractions(fieldMatcher);
        Mockito.verify(typeDescription).getDeclaredFields();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(typeDescription.getDeclaredFields()).thenReturn(fieldList);
        Mockito.when(fieldMatcher.matches(fieldList)).thenReturn(false);
        MatcherAssert.assertThat(new DeclaringFieldMatcher<TypeDescription>(fieldMatcher).matches(typeDescription), CoreMatchers.is(false));
        Mockito.verify(fieldMatcher).matches(fieldList);
        Mockito.verifyNoMoreInteractions(fieldMatcher);
        Mockito.verify(typeDescription).getDeclaredFields();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }
}

