package org.robobinding.property;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class PropertyChangeSupportTest {
    private PropertyChangeSupport propertyChangeSupport;

    @Test(expected = IllegalArgumentException.class)
    public void whenAddListenerToNonExistingProperty_thenThrowException() {
        createListenerOnProperty("nonExistingProperty");
    }

    @Test
    public void givenListenerOnProperty1_whenFirePropertyChange_thenShouldReceiveNotification() {
        MockPropertyChangeListener mockListener = createListenerOnProperty(PropertyChangeSupportTest.Bean.PROPERTY1);
        propertyChangeSupport.firePropertyChange(PropertyChangeSupportTest.Bean.PROPERTY1);
        Assert.assertTrue(mockListener.propertyChangedFired);
    }

    @Test
    public void givenAddListenerToPropertyTwice_whenFirePropertyChange_thenShouldReceiveNotificationOnlyOnce() {
        MockPropertyChangeListener mockListener = new MockPropertyChangeListener();
        propertyChangeSupport.addPropertyChangeListener(PropertyChangeSupportTest.Bean.PROPERTY1, mockListener);
        propertyChangeSupport.addPropertyChangeListener(PropertyChangeSupportTest.Bean.PROPERTY1, mockListener);
        propertyChangeSupport.firePropertyChange(PropertyChangeSupportTest.Bean.PROPERTY1);
        Assert.assertThat(mockListener.timesNotified, Matchers.is(1));
    }

    @Test
    public void givenListenerOnProperty1_whenRemoveIt_thenShouldNotReceiveNotification() {
        MockPropertyChangeListener mockListener = createListenerOnProperty(PropertyChangeSupportTest.Bean.PROPERTY1);
        propertyChangeSupport.removePropertyChangeListener(PropertyChangeSupportTest.Bean.PROPERTY1, mockListener);
        propertyChangeSupport.firePropertyChange(PropertyChangeSupportTest.Bean.PROPERTY1);
        Assert.assertFalse(mockListener.propertyChangedFired);
    }

    @Test
    public void givenListenersOnProperty1AndProperty2_whenFireChangeAll_thenShouldAllReceiveNotifications() {
        MockPropertyChangeListener listenerOnProperty1 = createListenerOnProperty(PropertyChangeSupportTest.Bean.PROPERTY1);
        MockPropertyChangeListener listenerOnProperty2 = createListenerOnProperty(PropertyChangeSupportTest.Bean.PROPERTY2);
        propertyChangeSupport.fireChangeAll();
        Assert.assertTrue(listenerOnProperty1.propertyChangedFired);
        Assert.assertTrue(listenerOnProperty2.propertyChangedFired);
    }

    public static class Bean {
        public static final String PROPERTY1 = "property1";

        public static final String PROPERTY2 = "property2";

        public boolean getProperty1() {
            return true;
        }

        public String getProperty2() {
            return null;
        }
    }
}

