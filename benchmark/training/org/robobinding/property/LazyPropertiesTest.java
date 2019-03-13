package org.robobinding.property;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class LazyPropertiesTest {
    private static final String PROPERTY_NAME = "property";

    @Test
    public void givenGetReadOnlyPropertyOnce_whenGetReadOnlyPropertyAgain_thenReturnSameInstance() {
        PropertyWithDependencySupply supply = Mockito.mock(PropertyWithDependencySupply.class);
        Mockito.when(supply.createProperty(LazyPropertiesTest.PROPERTY_NAME)).thenReturn(Mockito.mock(PropertyValueModel.class));
        LazyProperties lazyProperties = createLazyProperties(supply);
        ValueModel<Object> property = lazyProperties.getReadOnlyProperty(LazyPropertiesTest.PROPERTY_NAME);
        ValueModel<Object> cachedProperty = lazyProperties.getReadOnlyProperty(LazyPropertiesTest.PROPERTY_NAME);
        Assert.assertThat(cachedProperty, Matchers.sameInstance(property));
    }

    @Test
    public void givenAccessReadWritePropertyUsingGetReadOnlyPropertyAtFirst_whenAccessUsingGetReadWritePropertyLater_thenReturnSameInstance() {
        PropertyWithDependencySupply supply = Mockito.mock(PropertyWithDependencySupply.class);
        Mockito.when(supply.createProperty(LazyPropertiesTest.PROPERTY_NAME)).thenReturn(Mockito.mock(PropertyValueModel.class));
        LazyProperties lazyProperties = createLazyProperties(supply);
        ValueModel<Boolean> propertyUsingGetReadOnlyProperty = lazyProperties.getReadOnlyProperty(LazyPropertiesTest.PROPERTY_NAME);
        ValueModel<Boolean> propertyUsingGetReadWriteProperty = lazyProperties.getReadWriteProperty(LazyPropertiesTest.PROPERTY_NAME);
        Assert.assertThat(propertyUsingGetReadWriteProperty, Matchers.sameInstance(propertyUsingGetReadOnlyProperty));
    }

    @Test
    public void givenGetDatSetPropertyOnce_whenGetDataSetPropertyAgain_thenReturnSameInstance() {
        PropertyWithDependencySupply supply = Mockito.mock(PropertyWithDependencySupply.class);
        Mockito.when(supply.createDataSetProperty(LazyPropertiesTest.PROPERTY_NAME)).thenReturn(Mockito.mock(DataSetPropertyValueModel.class));
        LazyProperties lazyProperties = createLazyProperties(supply);
        DataSetValueModel dataSetProperty = lazyProperties.getDataSetProperty(LazyPropertiesTest.PROPERTY_NAME);
        DataSetValueModel cachedDataSetProperty = lazyProperties.getDataSetProperty(LazyPropertiesTest.PROPERTY_NAME);
        Assert.assertThat(cachedDataSetProperty, Matchers.sameInstance(dataSetProperty));
    }
}

