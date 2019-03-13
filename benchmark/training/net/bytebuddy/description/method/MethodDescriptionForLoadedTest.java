package net.bytebuddy.description.method;


import java.lang.reflect.Method;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class MethodDescriptionForLoadedTest extends AbstractMethodDescriptionTest {
    @Test
    public void testGetLoadedMethod() throws Exception {
        Method method = Object.class.getDeclaredMethod("toString");
        MatcherAssert.assertThat(new MethodDescription.ForLoadedMethod(method).getLoadedMethod(), CoreMatchers.sameInstance(method));
    }
}

