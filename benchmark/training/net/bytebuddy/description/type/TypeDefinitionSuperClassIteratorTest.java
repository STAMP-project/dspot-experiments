package net.bytebuddy.description.type;


import java.util.Iterator;
import java.util.NoSuchElementException;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class TypeDefinitionSuperClassIteratorTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription.Generic superClass;

    @Test
    public void testHasNext() throws Exception {
        Iterator<TypeDefinition> iterator = new TypeDefinition.SuperClassIterator(typeDescription);
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (typeDescription))));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (superClass))));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test(expected = NoSuchElementException.class)
    public void testHasNotNext() throws Exception {
        Iterator<TypeDefinition> iterator = new TypeDefinition.SuperClassIterator(typeDescription);
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (typeDescription))));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(((TypeDefinition) (superClass))));
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoRemoval() throws Exception {
        new TypeDefinition.SuperClassIterator(typeDescription).remove();
    }
}

