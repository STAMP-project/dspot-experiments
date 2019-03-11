package net.bytebuddy.implementation;


import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class StubMethodOtherTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private InstrumentedType instrumentedType;

    @Mock
    private Implementation implementation;

    @Test
    public void testPreparation() throws Exception {
        MatcherAssert.assertThat(StubMethod.INSTANCE.prepare(instrumentedType), CoreMatchers.is(instrumentedType));
        Mockito.verifyZeroInteractions(instrumentedType);
    }

    @Test
    public void testComposition() throws Exception {
        MatcherAssert.assertThat(andThen(implementation), CoreMatchers.is(implementation));
    }
}

