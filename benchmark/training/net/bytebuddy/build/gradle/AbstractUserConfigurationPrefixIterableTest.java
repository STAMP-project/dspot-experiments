package net.bytebuddy.build.gradle;


import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class AbstractUserConfigurationPrefixIterableTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private File primary;

    @Mock
    private File other;

    @Test
    public void testIteration() throws Exception {
        Iterator<? extends File> iterator = new AbstractUserConfiguration.PrefixIterable(primary, Collections.singleton(other)).iterator();
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(primary));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(iterator.next(), CoreMatchers.is(other));
        MatcherAssert.assertThat(iterator.hasNext(), CoreMatchers.is(false));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoval() throws Exception {
        new AbstractUserConfiguration.PrefixIterable(primary, Collections.<File>emptySet()).iterator().remove();
    }
}

