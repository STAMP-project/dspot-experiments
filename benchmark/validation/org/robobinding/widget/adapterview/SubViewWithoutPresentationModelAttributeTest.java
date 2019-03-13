package org.robobinding.widget.adapterview;


import android.view.View;
import android.view.ViewGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.BindingContext;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class SubViewWithoutPresentationModelAttributeTest {
    @Mock
    private SubViewHolder subViewHolder;

    @Mock
    private View staticSubView;

    @Test
    public void whenBindTo_thenStaticSubViewIsSetOnHolder() {
        BindingContext bindingContext = Mockito.mock(BindingContext.class);
        Mockito.when(bindingContext.inflateWithoutAttachingToRoot(ArgumentMatchers.anyInt(), ArgumentMatchers.any(ViewGroup.class))).thenReturn(staticSubView);
        SubViewWithoutPresentationModelAttribute attribute = new SubViewWithoutPresentationModelAttribute(null, 0, subViewHolder);
        attribute.bindTo(bindingContext);
        Mockito.verify(subViewHolder).setSubView(staticSubView);
    }
}

