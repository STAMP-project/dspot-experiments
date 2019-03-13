package io.dropwizard.testing.junit;


import io.dropwizard.testing.DropwizardTestSupport;
import io.dropwizard.testing.app.TestConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class DropwizardAppRuleReentrantTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    DropwizardTestSupport<TestConfiguration> testSupport;

    private Statement statement = Mockito.mock(Statement.class);

    private Description description = Mockito.mock(Description.class);

    @Test
    public void testReentrantRuleStartsApplicationOnlyOnce() throws Throwable {
        @SuppressWarnings("deprecation")
        DropwizardAppRule<TestConfiguration> dropwizardAppRule = new DropwizardAppRule(testSupport);
        // recursive
        RuleChain.outerRule(dropwizardAppRule).around(dropwizardAppRule).apply(statement, description).evaluate();
        InOrder inOrder = Mockito.inOrder(testSupport, statement, description);
        inOrder.verify(testSupport, Mockito.times(1)).before();
        inOrder.verify(statement).evaluate();
        inOrder.verify(testSupport, Mockito.times(1)).after();
        inOrder.verifyNoMoreInteractions();
    }
}

