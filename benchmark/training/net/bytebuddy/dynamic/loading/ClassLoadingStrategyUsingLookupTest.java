package net.bytebuddy.dynamic.loading;


import java.util.Collections;
import java.util.Map;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.loading.ClassLoadingStrategy.UsingLookup.<init>;


public class ClassLoadingStrategyUsingLookupTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    private TypeDescription typeDescription;

    private byte[] binaryRepresentation;

    @Mock
    private ClassInjector classInjector;

    @Test
    @SuppressWarnings("unchecked")
    public void testInjection() throws Exception {
        Mockito.when(classInjector.inject(Collections.singletonMap(typeDescription, binaryRepresentation))).thenReturn(((Map) (Collections.singletonMap(typeDescription, ClassLoadingStrategyUsingLookupTest.Foo.class))));
        Map<TypeDescription, Class<?>> loaded = new ClassLoadingStrategy.UsingLookup(classInjector).load(ClassLoadingStrategyUsingLookupTest.Foo.class.getClassLoader(), Collections.singletonMap(typeDescription, binaryRepresentation));
        MatcherAssert.assertThat(loaded.size(), CoreMatchers.is(1));
        Class<?> type = loaded.get(typeDescription);
        MatcherAssert.assertThat(type.getName(), CoreMatchers.is(ClassLoadingStrategyUsingLookupTest.Foo.class.getName()));
    }

    /* empty */
    private static class Foo {}
}

