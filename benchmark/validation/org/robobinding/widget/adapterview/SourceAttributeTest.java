package org.robobinding.widget.adapterview;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.robobinding.BindingContext;
import org.robobinding.ItemBindingContext;
import org.robobinding.attribute.Attributes;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Robert Taylor
 */
@RunWith(MockitoJUnitRunner.class)
public class SourceAttributeTest {
    private final String attributeValue = "{property_name}";

    private final String propertyName = "property_name";

    @Mock
    private DataSetAdapterBuilder dataSetAdapterBuilder;

    @Mock
    private BindingContext bindingContext;

    @Mock
    private ItemBindingContext itemBindingContext;

    @Test
    public void whenBinding_thenSetDataSetValueModelOnDataSetAdapterBuilder() {
        Mockito.when(bindingContext.navigateToItemContext(propertyName)).thenReturn(itemBindingContext);
        SourceAttribute sourceAttribute = new SourceAttribute(dataSetAdapterBuilder);
        sourceAttribute.setAttribute(Attributes.aValueModelAttribute(attributeValue));
        sourceAttribute.bindTo(bindingContext);
        Mockito.verify(dataSetAdapterBuilder).setBindingContext(itemBindingContext);
    }
}

