package net.bytebuddy.build.gradle;


import java.io.File;
import java.util.Collections;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class ClassLoaderResolverTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private File file;

    @Test
    public void testResolution() throws Exception {
        ClassLoaderResolver classLoaderResolver = new ClassLoaderResolver();
        MatcherAssert.assertThat(classLoaderResolver.resolve(Collections.singleton(file)), CoreMatchers.sameInstance(classLoaderResolver.resolve(Collections.singleton(file))));
    }

    @Test
    public void testClose() throws Exception {
        ClassLoaderResolver classLoaderResolver = new ClassLoaderResolver();
        classLoaderResolver.resolve(Collections.singleton(file));
        classLoaderResolver.close();
    }
}

