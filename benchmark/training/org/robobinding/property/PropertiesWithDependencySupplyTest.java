package org.robobinding.property;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
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
public class PropertiesWithDependencySupplyTest {
    private static final String PROPERTY_NAME = "property";

    @Mock
    private Dependencies dependencies;

    @Mock
    private PropertySupply propertySupply;

    private PropertyWithDependencySupply withDependencySupply;

    @Test
    public void whenCreatePropertyWithDependency_thenReturnDependencyProperty() {
        SimpleProperty property = Mockito.mock(SimpleProperty.class);
        Mockito.when(propertySupply.tryToCreateProperty(PropertiesWithDependencySupplyTest.PROPERTY_NAME)).thenReturn(property);
        Mockito.when(dependencies.hasDependency(PropertiesWithDependencySupplyTest.PROPERTY_NAME)).thenReturn(true);
        PropertyValueModel propertyCreated = withDependencySupply.createProperty(PropertiesWithDependencySupplyTest.PROPERTY_NAME);
        Assert.assertThat(propertyCreated, Matchers.instanceOf(DependencyProperty.class));
    }

    @Test
    public void whenCreateDataSetPropertyWithDependency_thenReturnDataSetDependencyProperty() {
        DataSetProperty property = Mockito.mock(DataSetProperty.class);
        Mockito.when(propertySupply.tryToCreateDataSetProperty(PropertiesWithDependencySupplyTest.PROPERTY_NAME)).thenReturn(property);
        Mockito.when(dependencies.hasDependency(PropertiesWithDependencySupplyTest.PROPERTY_NAME)).thenReturn(true);
        Dependency dependency = Mockito.mock(Dependency.class);
        Mockito.when(dependencies.createDependency(PropertiesWithDependencySupplyTest.PROPERTY_NAME)).thenReturn(dependency);
        DataSetPropertyValueModel propertyCreated = withDependencySupply.createDataSetProperty(PropertiesWithDependencySupplyTest.PROPERTY_NAME);
        Assert.assertThat(propertyCreated, Matchers.instanceOf(DataSetDependencyProperty.class));
    }

    @Test
    public void whenCreateDataSetPropertyWithDependency_thenListenerAddedToDependency() {
        DataSetProperty property = Mockito.mock(DataSetProperty.class);
        Mockito.when(propertySupply.tryToCreateDataSetProperty(PropertiesWithDependencySupplyTest.PROPERTY_NAME)).thenReturn(property);
        Mockito.when(dependencies.hasDependency(PropertiesWithDependencySupplyTest.PROPERTY_NAME)).thenReturn(true);
        Dependency dependency = Mockito.mock(Dependency.class);
        Mockito.when(dependencies.createDependency(PropertiesWithDependencySupplyTest.PROPERTY_NAME)).thenReturn(dependency);
        withDependencySupply.createDataSetProperty(PropertiesWithDependencySupplyTest.PROPERTY_NAME);
        Mockito.verify(dependency).addListenerToDependentProperties(ArgumentMatchers.any(PropertyChangeListener.class));
    }
}

