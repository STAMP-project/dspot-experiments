package org.robobinding.widgetaddon;


import android.content.Context;
import android.view.View;
import org.hamcrest.CoreMatchers;
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
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class ViewAddOnInjectorTest {
    @Mock
    private View view;

    @Test
    public void whenInjectNonViewListenersAwareAttribute_thenSafelyIgnored() {
        ViewAddOnInjector injector = new ViewAddOnInjector(null);
        injector.injectIfRequired(new ViewAddOnInjectorTest.NonViewListenersAwareAttribute(), view);
    }

    @Test
    public void whenInjectViewAddOnAwareAttribute_thenViewAddOnIsInjected() {
        ViewAddOns viewAddOns = Mockito.mock(ViewAddOns.class);
        ViewAddOnInjectorTest.ViewAddOnForView viewAddOn = Mockito.mock(ViewAddOnInjectorTest.ViewAddOnForView.class);
        Mockito.when(viewAddOns.getMostSuitable(view)).thenReturn(viewAddOn);
        ViewAddOnInjector injector = new ViewAddOnInjector(viewAddOns);
        ViewAddOnInjectorTest.ViewAddOnAwareAttribute viewAttribute = new ViewAddOnInjectorTest.ViewAddOnAwareAttribute();
        injector.injectIfRequired(viewAttribute, view);
        Assert.assertThat(viewAttribute.viewAddOn, CoreMatchers.sameInstance(viewAddOn));
    }

    @Test(expected = RuntimeException.class)
    public void whenIncompatibleViewAddOnIsFoundAndInject_thenThrowExceptionWithDetailedMessage() {
        ViewAddOns viewAddOns = Mockito.mock(ViewAddOns.class);
        ViewAddOnInjectorTest.ViewAddOnForView viewAddOn = Mockito.mock(ViewAddOnInjectorTest.ViewAddOnForView.class);
        Mockito.when(viewAddOns.getMostSuitable(view)).thenReturn(viewAddOn);
        ViewAddOnInjector injector = new ViewAddOnInjector(viewAddOns);
        ViewAddOnInjectorTest.ViewAddOnSubclassAwareAttribute viewAttributeSubclass = new ViewAddOnInjectorTest.ViewAddOnSubclassAwareAttribute();
        injector.injectIfRequired(viewAttributeSubclass, view);
    }

    private static class NonViewListenersAwareAttribute {}

    private static class ViewAddOnAwareAttribute implements ViewAddOnAware<ViewAddOnInjectorTest.ViewAddOnForView> {
        public ViewAddOnInjectorTest.ViewAddOnForView viewAddOn;

        @Override
        public void setViewAddOn(ViewAddOnInjectorTest.ViewAddOnForView viewAddOn) {
            this.viewAddOn = viewAddOn;
        }
    }

    private static class ViewAddOnSubclassAwareAttribute implements ViewAddOnAware<ViewAddOnInjectorTest.ViewAddOnSubclass> {
        @Override
        public void setViewAddOn(ViewAddOnInjectorTest.ViewAddOnSubclass viewAddOn) {
        }
    }

    private static class ViewAddOnForView implements ViewAddOn {
        public ViewAddOnForView(View view) {
        }
    }

    private static class ViewAddOnSubclass extends ViewAddOnInjectorTest.ViewAddOnForView {
        public ViewAddOnSubclass(ViewAddOnInjectorTest.ViewSubclass view) {
            super(view);
        }
    }

    private static class ViewSubclass extends View {
        public ViewSubclass(Context context) {
            super(context);
        }
    }
}

