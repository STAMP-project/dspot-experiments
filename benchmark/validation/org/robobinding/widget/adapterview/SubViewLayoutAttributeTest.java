package org.robobinding.widget.adapterview;


import android.content.Context;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.BindingContext;
import org.robobinding.attribute.StaticResourceAttribute;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class SubViewLayoutAttributeTest {
    @Mock
    private BindingContext bindingContext;

    @Mock
    private StaticResourceAttribute attribute;

    private int layoutId = 1;

    @Test
    public void givenAttribute_whenBindTo_thenLayoutIdIsCorrectlySet() {
        Mockito.when(attribute.getResourceId(ArgumentMatchers.any(Context.class))).thenReturn(layoutId);
        SubViewLayoutAttribute subViewLayoutAttribute = new SubViewLayoutAttribute();
        subViewLayoutAttribute.setAttribute(attribute);
        subViewLayoutAttribute.bindTo(bindingContext);
        Assert.assertThat(subViewLayoutAttribute.getLayoutId(), Matchers.is(layoutId));
    }
}

