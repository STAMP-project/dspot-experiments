package com.vaadin.data.provider;


import com.vaadin.shared.Registration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class AbstractDataProviderTest {
    private static class TestDataProvider extends AbstractDataProvider<Object, Object> {
        @Override
        public Stream<Object> fetch(Query<Object, Object> t) {
            return null;
        }

        @Override
        public int size(Query<Object, Object> t) {
            return 0;
        }

        @Override
        public boolean isInMemory() {
            return false;
        }
    }

    @Test
    public void refreshAll_notifyListeners() {
        AbstractDataProviderTest.TestDataProvider dataProvider = new AbstractDataProviderTest.TestDataProvider();
        AtomicReference<DataChangeEvent<Object>> event = new AtomicReference<>();
        addDataProviderListener(( ev) -> {
            assertNull(event.get());
            event.set(ev);
        });
        refreshAll();
        Assert.assertNotNull(event.get());
        Assert.assertEquals(dataProvider, event.get().getSource());
    }

    @Test
    public void removeListener_listenerIsNotNotified() {
        AbstractDataProviderTest.TestDataProvider dataProvider = new AbstractDataProviderTest.TestDataProvider();
        AtomicReference<DataChangeEvent<Object>> event = new AtomicReference<>();
        Registration registration = dataProvider.addDataProviderListener(( ev) -> event.set(ev));
        registration.remove();
        refreshAll();
        Assert.assertNull(event.get());
    }
}

