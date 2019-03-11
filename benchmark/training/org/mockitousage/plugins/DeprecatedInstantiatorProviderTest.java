/**
 * Copyright (c) 2018 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.plugins;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.plugins.InstantiatorProvider;


public class DeprecatedInstantiatorProviderTest {
    @Test
    public void provides_default_instance_for_deprecated_plugin() {
        InstantiatorProvider plugin = Mockito.framework().getPlugins().getDefaultPlugin(InstantiatorProvider.class);
        Assert.assertNotNull(plugin);
    }

    @SuppressWarnings("CheckReturnValue")
    @Test
    public void uses_custom_instantiator_provider() {
        MyDeprecatedInstantiatorProvider.invokedFor.remove();
        Mockito.mock(DeprecatedInstantiatorProviderTest.class);
        Assert.assertEquals(MyDeprecatedInstantiatorProvider.invokedFor.get(), Arrays.asList(DeprecatedInstantiatorProviderTest.class));
    }

    @SuppressWarnings("CheckReturnValue")
    @Test(expected = InstantiationException.class)
    public void exception_while_instantiating() throws Throwable {
        MyDeprecatedInstantiatorProvider.shouldExcept.set(true);
        try {
            Mockito.mock(DeprecatedInstantiatorProviderTest.class);
        } catch (MockitoException e) {
            throw e.getCause();
        } finally {
            MyDeprecatedInstantiatorProvider.shouldExcept.remove();
        }
    }
}

