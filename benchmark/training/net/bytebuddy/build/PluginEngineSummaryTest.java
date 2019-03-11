package net.bytebuddy.build;


import java.util.Collections;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class PluginEngineSummaryTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private Throwable throwable;

    @Test
    public void testGetters() {
        Plugin.Engine.Summary summary = new Plugin.Engine.Summary(Collections.singletonList(typeDescription), Collections.singletonMap(typeDescription, Collections.singletonList(throwable)), Collections.singletonList(PluginEngineSummaryTest.FOO));
        MatcherAssert.assertThat(summary.getTransformed(), Is.is(Collections.singletonList(typeDescription)));
        MatcherAssert.assertThat(summary.getFailed(), Is.is(Collections.singletonMap(typeDescription, Collections.singletonList(throwable))));
        MatcherAssert.assertThat(summary.getUnresolved(), Is.is(Collections.singletonList(PluginEngineSummaryTest.FOO)));
    }

    @Test
    public void testSummaryObjectProperties() {
        Plugin.Engine.Summary summary = new Plugin.Engine.Summary(Collections.singletonList(typeDescription), Collections.singletonMap(typeDescription, Collections.singletonList(throwable)), Collections.singletonList(PluginEngineSummaryTest.FOO));
        MatcherAssert.assertThat(summary.hashCode(), Is.is(new Plugin.Engine.Summary(Collections.singletonList(typeDescription), Collections.singletonMap(typeDescription, Collections.singletonList(throwable)), Collections.singletonList(PluginEngineSummaryTest.FOO)).hashCode()));
        MatcherAssert.assertThat(summary, Is.is(summary));
        MatcherAssert.assertThat(summary, CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(summary.equals(null), Is.is(false));
        MatcherAssert.assertThat(summary, Is.is(new Plugin.Engine.Summary(Collections.singletonList(typeDescription), Collections.singletonMap(typeDescription, Collections.singletonList(throwable)), Collections.singletonList(PluginEngineSummaryTest.FOO))));
    }
}

