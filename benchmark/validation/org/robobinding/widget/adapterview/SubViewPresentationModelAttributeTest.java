package org.robobinding.widget.adapterview;


import android.view.View;
import android.view.ViewGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.BindingContext;
import org.robobinding.SubBindingContext;
import org.robobinding.attribute.Attributes;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class SubViewPresentationModelAttributeTest {
    @Test
    public void whenBind_thenBoundSubViewIsSetOnHolder() {
        int layoutId = 1;
        String presentationModelPropertyName = "propertyName";
        ViewGroup parent = Mockito.mock(ViewGroup.class);
        View boundSubView = Mockito.mock(View.class);
        BindingContext context = Mockito.mock(BindingContext.class);
        SubViewHolder subViewHolder = Mockito.mock(SubViewHolder.class);
        SubBindingContext subBindingContext = Mockito.mock(SubBindingContext.class);
        Mockito.when(context.navigateToSubContext(presentationModelPropertyName)).thenReturn(subBindingContext);
        Mockito.when(subBindingContext.inflateAndBindWithoutAttachingToRoot(layoutId, parent)).thenReturn(boundSubView);
        SubViewPresentationModelAttribute subViewAttribute = new SubViewPresentationModelAttribute(parent, layoutId, subViewHolder);
        subViewAttribute.setAttribute(Attributes.aValueModelAttribute(presentationModelPropertyName));
        subViewAttribute.bindTo(context);
        Mockito.verify(subViewHolder).setSubView(boundSubView);
    }
}

