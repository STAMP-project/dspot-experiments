package net.bytebuddy.dynamic.scaffold;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.scaffold.MethodGraph.Compiler.Default.Key.<init>;


public class MethodGraphCompilerDefaultKeyTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodGraphCompilerDefaultKeyTest.SampleKey foo;

    @Mock
    private MethodGraphCompilerDefaultKeyTest.SampleKey bar;

    @Mock
    private MethodGraphCompilerDefaultKeyTest.SampleKey qux;

    @Test
    public void testEqualsSimilar() throws Exception {
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo)).hashCode(), CoreMatchers.is(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo)).hashCode()));
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo)), CoreMatchers.is(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo))));
    }

    @Test
    public void testNotEqualsDifferentName() throws Exception {
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo)).hashCode(), CoreMatchers.not(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.BAR, Collections.singleton(foo)).hashCode()));
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo)), CoreMatchers.not(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.BAR, Collections.singleton(foo))));
    }

    @Test
    public void testNotEqualDifferentToken() throws Exception {
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo)).hashCode(), CoreMatchers.is(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(bar)).hashCode()));
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo)), CoreMatchers.not(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.BAR, Collections.singleton(bar))));
    }

    @Test
    public void testEqualsSuperSet() throws Exception {
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, new HashSet<MethodGraphCompilerDefaultKeyTest.SampleKey>(Arrays.asList(foo, bar))).hashCode(), CoreMatchers.is(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(bar)).hashCode()));
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, new HashSet<MethodGraphCompilerDefaultKeyTest.SampleKey>(Arrays.asList(foo, bar))), CoreMatchers.is(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo))));
    }

    @Test
    public void testEqualsSubSet() throws Exception {
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo)).hashCode(), CoreMatchers.is(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, new HashSet<MethodGraphCompilerDefaultKeyTest.SampleKey>(Arrays.asList(foo, bar))).hashCode()));
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(foo)), CoreMatchers.is(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, new HashSet<MethodGraphCompilerDefaultKeyTest.SampleKey>(Arrays.asList(foo, bar)))));
    }

    @Test
    public void testNotEqualsDistinctSet() throws Exception {
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, new HashSet<MethodGraphCompilerDefaultKeyTest.SampleKey>(Arrays.asList(foo, bar))).hashCode(), CoreMatchers.is(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(qux)).hashCode()));
        MatcherAssert.assertThat(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, new HashSet<MethodGraphCompilerDefaultKeyTest.SampleKey>(Arrays.asList(foo, bar))), CoreMatchers.not(new MethodGraphCompilerDefaultKeyTest.PseudoKey(MethodGraphCompilerDefaultKeyTest.FOO, Collections.singleton(qux))));
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testInitialEntryCannotInject() throws Exception {
        new MethodGraph.Compiler.Default.Key.Store.Entry.Initial(new MethodGraph.Compiler.Default.Key.Harmonized(MethodGraphCompilerDefaultKeyTest.FOO, (-1), Collections.emptyMap())).inject(Mockito.mock(MethodGraph.Compiler.Default.Key.Harmonized.class), Visibility.PUBLIC);
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testInitialEntryCannotBeTransformed() throws Exception {
        new MethodGraph.Compiler.Default.Key.Store.Entry.Initial(new MethodGraph.Compiler.Default.Key.Harmonized(MethodGraphCompilerDefaultKeyTest.FOO, (-1), Collections.emptyMap())).asNode(Mockito.mock(MethodGraph.Compiler.Default.Merger.class));
    }

    @Test(expected = IllegalStateException.class)
    @SuppressWarnings("unchecked")
    public void testInitialEntryCannotExposeKey() throws Exception {
        new MethodGraph.Compiler.Default.Key.Store.Entry.Initial(new MethodGraph.Compiler.Default.Key.Harmonized(MethodGraphCompilerDefaultKeyTest.FOO, (-1), Collections.emptyMap())).getKey();
    }

    protected static class PseudoKey extends MethodGraph.Compiler.Default.Key<MethodGraphCompilerDefaultKeyTest.SampleKey> {
        private final Set<MethodGraphCompilerDefaultKeyTest.SampleKey> identifiers;

        protected PseudoKey(String internalName, Set<MethodGraphCompilerDefaultKeyTest.SampleKey> identifiers) {
            super(internalName, (-1));
            this.identifiers = identifiers;
        }

        @Override
        protected Set<MethodGraphCompilerDefaultKeyTest.SampleKey> getIdentifiers() {
            return identifiers;
        }
    }

    /* empty */
    public static class SampleKey {}
}

