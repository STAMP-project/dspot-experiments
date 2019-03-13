package net.bytebuddy.description.method;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.description.method.ParameterList.ForLoadedExecutable.Dispatcher.ForLegacyVm.INSTANCE;


public class ParameterListForLoadedExectutableDispatcherTest {
    @Test
    public void testLegacyMethod() throws Exception {
        MatcherAssert.assertThat(INSTANCE.describe(Object.class.getDeclaredMethod("toString"), new ParameterDescription.ForLoadedParameter.ParameterAnnotationSource.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.<ParameterList<ParameterDescription.InDefinedShape>>is(new ParameterList.ForLoadedExecutable.OfLegacyVmMethod(Object.class.getDeclaredMethod("toString"), new ParameterDescription.ForLoadedParameter.ParameterAnnotationSource.ForLoadedMethod(Object.class.getDeclaredMethod("toString")))));
    }

    @Test
    public void testLegacyConstructor() throws Exception {
        MatcherAssert.assertThat(INSTANCE.describe(Object.class.getDeclaredConstructor(), new ParameterDescription.ForLoadedParameter.ParameterAnnotationSource.ForLoadedConstructor(Object.class.getDeclaredConstructor())), CoreMatchers.<ParameterList<ParameterDescription.InDefinedShape>>is(new ParameterList.ForLoadedExecutable.OfLegacyVmConstructor(Object.class.getDeclaredConstructor(), new ParameterDescription.ForLoadedParameter.ParameterAnnotationSource.ForLoadedConstructor(Object.class.getDeclaredConstructor()))));
    }

    @Test(expected = IllegalStateException.class)
    public void testLegacyGetParameterCount() throws Exception {
        INSTANCE.getParameterCount(Mockito.mock(Object.class));
    }
}

