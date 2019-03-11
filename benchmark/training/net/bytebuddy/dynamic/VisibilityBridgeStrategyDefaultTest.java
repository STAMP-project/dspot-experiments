package net.bytebuddy.dynamic;


import VisibilityBridgeStrategy.Default.ALWAYS;
import VisibilityBridgeStrategy.Default.NEVER;
import VisibilityBridgeStrategy.Default.ON_NON_GENERIC_METHOD;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class VisibilityBridgeStrategyDefaultTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Test
    public void testAlwaysStrategy() {
        MatcherAssert.assertThat(ALWAYS.generateVisibilityBridge(methodDescription), Is.is(true));
    }

    @Test
    public void testNonGenericStrategyOnNonGeneric() {
        Mockito.when(methodDescription.isGenerified()).thenReturn(false);
        MatcherAssert.assertThat(ON_NON_GENERIC_METHOD.generateVisibilityBridge(methodDescription), Is.is(true));
    }

    @Test
    public void testNonGenericStrategyOnGeneric() {
        Mockito.when(methodDescription.isGenerified()).thenReturn(true);
        MatcherAssert.assertThat(ON_NON_GENERIC_METHOD.generateVisibilityBridge(methodDescription), Is.is(false));
    }

    @Test
    public void testNeverStrategy() {
        MatcherAssert.assertThat(NEVER.generateVisibilityBridge(methodDescription), Is.is(false));
    }
}

