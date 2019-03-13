package org.robobinding.widgetaddon;


import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class ViewAddOnFactoriesTest {
    @Mock
    private Object view;

    @Test(expected = RuntimeException.class)
    public void givenViewWithoutViewAddOnFactory_whenCreateViewAddOn_thenThrowError() {
        Map<Class<?>, ViewAddOnFactory> emptyMappings = Collections.emptyMap();
        ViewAddOnFactories factories = new ViewAddOnFactories(emptyMappings);
        factories.createViewAddOn(view);
    }

    @Test
    public void givenViewWithViewAddOnFactory_whenCreateViewAddOn_thenReturnInstance() {
        ViewAddOnFactory factory = Mockito.mock(ViewAddOnFactory.class);
        ViewAddOn viewAddOn = Mockito.mock(ViewAddOn.class);
        Mockito.when(factory.create(view)).thenReturn(viewAddOn);
        Map<Class<?>, ViewAddOnFactory> mappings = Maps.newHashMap();
        mappings.put(view.getClass(), factory);
        ViewAddOnFactories factories = new ViewAddOnFactories(mappings);
        ViewAddOn actual = factories.createViewAddOn(view);
        Assert.assertThat(actual, Matchers.is(viewAddOn));
    }
}

