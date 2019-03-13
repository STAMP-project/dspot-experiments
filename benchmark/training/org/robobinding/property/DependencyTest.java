package org.robobinding.property;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
@RunWith(MockitoJUnitRunner.class)
public class DependencyTest {
    private DependencyTest.ObservableTestBean bean;

    @Mock
    private PropertyChangeListener listener;

    @Test
    public void whenAddListener_thenListenersAddedToAllDependentProperties() {
        Set<String> dependentProperties = Sets.newHashSet("prop1", "prop2");
        Dependency dependency = new Dependency(bean, dependentProperties);
        dependency.addListenerToDependentProperties(listener);
        assertListenerOnProperties(dependentProperties);
    }

    @Test
    public void givenListenerOnDependentProperties_whenRemoveListener_thenListenerRemovedOffDependentProperties() {
        Set<String> dependentProperties = Sets.newHashSet("prop1", "prop2");
        Dependency dependency = new Dependency(bean, dependentProperties);
        dependency.addListenerToDependentProperties(listener);
        dependency.removeListenerOffDependentProperties(listener);
        assertNoListenerOnProperties(dependentProperties);
    }

    public static class ObservableTestBean implements ObservableBean {
        private final Map<String, PropertyChangeListener> propertyChangeListenerMap;

        public ObservableTestBean() {
            propertyChangeListenerMap = Maps.newHashMap();
        }

        @Override
        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            propertyChangeListenerMap.put(propertyName, listener);
        }

        @Override
        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            propertyChangeListenerMap.remove(propertyName);
        }
    }
}

