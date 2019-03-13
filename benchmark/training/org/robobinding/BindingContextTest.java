package org.robobinding;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robobinding.presentationmodel.PresentationModelAdapter;
import org.robobinding.property.DataSetValueModel;
import org.robobinding.util.RandomValues;


/**
 *
 *
 * @since 1.0
 * @author Cheng Wei
 */
public class BindingContextTest {
    @Test
    public void shouldItemBindingContextWithExpectedPreInitializeViews() {
        boolean expectedPreInitializeViews = RandomValues.trueOrFalse();
        String propertyName = "pName";
        DataSetValueModel dataSetProperty = Mockito.mock(DataSetValueModel.class);
        Mockito.when(dataSetProperty.preInitializingViewsWithDefault(ArgumentMatchers.anyBoolean())).thenReturn(expectedPreInitializeViews);
        PresentationModelAdapter presentationModelAdapter = MockPresentationModelAdapterBuilder.aPresentationModelAdapterWithDataSetProperty(propertyName, dataSetProperty);
        BindingContext context = newBindingContext(presentationModelAdapter);
        ItemBindingContext itemBindingContext = context.navigateToItemContext(propertyName);
        Assert.assertThat(itemBindingContext.shouldPreInitializeViews(), Matchers.is(expectedPreInitializeViews));
    }
}

