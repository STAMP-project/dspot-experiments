package org.robobinding.binder;


import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.NonBindingViewInflater;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class BindingViewInflaterTest {
    @Mock
    private BindingAttributeResolver bindingAttributeResolver;

    @Mock
    private BindingAttributeParser bindingAttributeParser;

    private BindingViewInflater bindingViewInflater;

    private int layoutId = 0;

    @Test
    public void givenAChildViewWithBindingAttributes_whenInflateView_thenAChildViewBindingAttributesShouldBeAdded() {
        declareAChildView();
        InflatedViewWithRoot inflatedView = bindingViewInflater.inflateView(layoutId);
        Assert.assertThat(numberOfChildViewBindingAttributes(inflatedView), CoreMatchers.equalTo(1));
    }

    @Test
    public void givenAChildViewWithoutBindingAttributes_whenInflateBindingView_thenNoChildViewBindingAttributesShouldBeAdded() {
        declareEmptyChildViews();
        InflatedViewWithRoot inflatedView = bindingViewInflater.inflateView(layoutId);
        Assert.assertThat(numberOfChildViewBindingAttributes(inflatedView), CoreMatchers.equalTo(0));
    }

    @Test
    public void givenAPredefinedPendingAttributesForView_whenInflateView_thenChildViewBindingAttributesIsAdded() {
        InflatedViewWithRoot inflatedView = bindingViewInflater.inflateView(layoutId, createAPredefinedPendingAttributesForView(), null, false);
        Assert.assertThat(numberOfChildViewBindingAttributes(inflatedView), CoreMatchers.equalTo(1));
    }

    private class NonBindingViewInflaterWithOnViewCreationCall implements NonBindingViewInflater {
        @Override
        public View inflateWithoutRoot(int layoutId) {
            View resultView = null;
            performOnViewCreationCall(resultView);
            return resultView;
        }

        private void performOnViewCreationCall(View view) {
            bindingViewInflater.onViewCreated(view, Mockito.mock(AttributeSet.class));
        }

        @Override
        public View inflate(int layoutId, ViewGroup root, boolean attachToRoot) {
            View resultView = null;
            performOnViewCreationCall(resultView);
            return resultView;
        }
    }
}

