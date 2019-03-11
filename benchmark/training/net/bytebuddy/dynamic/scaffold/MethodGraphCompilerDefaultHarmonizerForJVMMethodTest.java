package net.bytebuddy.dynamic.scaffold;


import java.util.Collections;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Reifying.INITIATING;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Compiler.Default.Harmonizer.ForJVMMethod.INSTANCE;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Compiler.Default.Merger.Directional.LEFT;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Compiler.Default.forJVMHierarchy;


public class MethodGraphCompilerDefaultHarmonizerForJVMMethodTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription first;

    @Mock
    private TypeDescription second;

    private MethodGraph.Compiler.Default.Harmonizer<MethodGraph.Compiler.Default.Harmonizer.ForJVMMethod.Token> harmonizer;

    @Test
    public void testMethodEqualityHashCode() throws Exception {
        MatcherAssert.assertThat(harmonizer.harmonize(new MethodDescription.TypeToken(first, Collections.singletonList(first))).hashCode(), CoreMatchers.is(harmonizer.harmonize(new MethodDescription.TypeToken(first, Collections.singletonList(first))).hashCode()));
    }

    @Test
    public void testMethodEquality() throws Exception {
        MatcherAssert.assertThat(harmonizer.harmonize(new MethodDescription.TypeToken(first, Collections.singletonList(first))), CoreMatchers.is(harmonizer.harmonize(new MethodDescription.TypeToken(first, Collections.singletonList(first)))));
    }

    @Test
    public void testMethodReturnTypeInequalityHashCode() throws Exception {
        MatcherAssert.assertThat(harmonizer.harmonize(new MethodDescription.TypeToken(first, Collections.singletonList(first))).hashCode(), CoreMatchers.not(harmonizer.harmonize(new MethodDescription.TypeToken(second, Collections.singletonList(first))).hashCode()));
    }

    @Test
    public void testMethodReturnTypeInequality() throws Exception {
        MatcherAssert.assertThat(harmonizer.harmonize(new MethodDescription.TypeToken(first, Collections.singletonList(first))), CoreMatchers.not(harmonizer.harmonize(new MethodDescription.TypeToken(second, Collections.singletonList(first)))));
    }

    @Test
    public void testMethodParameterTypesHashCode() throws Exception {
        MatcherAssert.assertThat(harmonizer.harmonize(new MethodDescription.TypeToken(first, Collections.singletonList(first))).hashCode(), CoreMatchers.not(harmonizer.harmonize(new MethodDescription.TypeToken(first, Collections.singletonList(second))).hashCode()));
    }

    @Test
    public void testMethodParameterTypesEquality() throws Exception {
        MatcherAssert.assertThat(harmonizer.harmonize(new MethodDescription.TypeToken(first, Collections.singletonList(first))), CoreMatchers.not(harmonizer.harmonize(new MethodDescription.TypeToken(first, Collections.singletonList(second)))));
    }

    @Test
    public void testFactory() throws Exception {
        MatcherAssert.assertThat(forJVMHierarchy(), FieldByFieldComparison.hasPrototype(((MethodGraph.Compiler) (new MethodGraph.Compiler.Default<MethodGraph.Compiler.Default.Harmonizer.ForJVMMethod.Token>(INSTANCE, LEFT, INITIATING)))));
    }
}

