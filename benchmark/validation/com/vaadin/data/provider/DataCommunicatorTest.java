package com.vaadin.data.provider;


import com.vaadin.data.provider.DataCommunicator.ActiveDataHandler;
import com.vaadin.server.MockVaadinSession;
import com.vaadin.server.SerializableConsumer;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Range;
import com.vaadin.shared.Registration;
import com.vaadin.ui.UI;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class DataCommunicatorTest {
    private static final Object TEST_OBJECT = new Object();

    private static final Object TEST_OBJECT_TWO = new Object();

    public static class TestUI extends UI {
        private final VaadinSession session;

        public TestUI(VaadinSession session) {
            this.session = session;
        }

        @Override
        protected void init(VaadinRequest request) {
        }

        @Override
        public VaadinSession getSession() {
            return session;
        }

        @Override
        public Future<Void> access(Runnable runnable) {
            runnable.run();
            return null;
        }
    }

    private static class TestDataProvider extends ListDataProvider<Object> implements Registration {
        private Registration registration;

        public TestDataProvider() {
            super(new ArrayList());
            addItem(DataCommunicatorTest.TEST_OBJECT);
        }

        @Override
        public Registration addDataProviderListener(DataProviderListener<Object> listener) {
            registration = super.addDataProviderListener(listener);
            return this;
        }

        @Override
        public void remove() {
            registration.remove();
            registration = null;
        }

        public boolean isListenerAdded() {
            return (registration) != null;
        }

        public void setItem(Object item) {
            clear();
            addItem(item);
        }

        public void clear() {
            getItems().clear();
        }

        public void addItem(Object item) {
            getItems().add(item);
        }
    }

    private static class TestDataCommunicator extends DataCommunicator<Object> {
        protected void extend(UI ui) {
            super.extend(ui);
        }
    }

    private static class TestDataGenerator implements DataGenerator<Object> {
        Object refreshed = null;

        Object generated = null;

        @Override
        public void generateData(Object item, JsonObject jsonObject) {
            generated = item;
        }

        @Override
        public void refreshData(Object item) {
            refreshed = item;
        }
    }

    private final MockVaadinSession session = new MockVaadinSession(Mockito.mock(VaadinService.class));

    @Test
    public void attach_dataProviderListenerIsNotAddedBeforeAttachAndAddedAfter() {
        session.lock();
        UI ui = new DataCommunicatorTest.TestUI(session);
        DataCommunicatorTest.TestDataCommunicator communicator = new DataCommunicatorTest.TestDataCommunicator();
        DataCommunicatorTest.TestDataProvider dataProvider = new DataCommunicatorTest.TestDataProvider();
        setDataProvider(dataProvider, null);
        Assert.assertFalse(dataProvider.isListenerAdded());
        communicator.extend(ui);
        Assert.assertTrue(dataProvider.isListenerAdded());
    }

    @Test
    public void detach_dataProviderListenerIsRemovedAfterDetach() {
        session.lock();
        UI ui = new DataCommunicatorTest.TestUI(session);
        DataCommunicatorTest.TestDataCommunicator communicator = new DataCommunicatorTest.TestDataCommunicator();
        DataCommunicatorTest.TestDataProvider dataProvider = new DataCommunicatorTest.TestDataProvider();
        setDataProvider(dataProvider, null);
        communicator.extend(ui);
        Assert.assertTrue(dataProvider.isListenerAdded());
        detach();
        Assert.assertFalse(dataProvider.isListenerAdded());
    }

    @Test
    public void refresh_dataProviderListenerCallsRefreshInDataGeneartors() {
        session.lock();
        UI ui = new DataCommunicatorTest.TestUI(session);
        DataCommunicatorTest.TestDataCommunicator communicator = new DataCommunicatorTest.TestDataCommunicator();
        communicator.extend(ui);
        DataCommunicatorTest.TestDataProvider dataProvider = new DataCommunicatorTest.TestDataProvider();
        setDataProvider(dataProvider, null);
        DataCommunicatorTest.TestDataGenerator generator = new DataCommunicatorTest.TestDataGenerator();
        addDataGenerator(generator);
        // Generate initial data.
        beforeClientResponse(true);
        Assert.assertEquals("DataGenerator generate was not called", DataCommunicatorTest.TEST_OBJECT, generator.generated);
        generator.generated = null;
        // Make sure data does not get re-generated
        beforeClientResponse(false);
        Assert.assertEquals("DataGenerator generate was called again", null, generator.generated);
        // Refresh a data object to trigger an update.
        refreshItem(DataCommunicatorTest.TEST_OBJECT);
        Assert.assertEquals("DataGenerator refresh was not called", DataCommunicatorTest.TEST_OBJECT, generator.refreshed);
        // Test refreshed data generation
        beforeClientResponse(false);
        Assert.assertEquals("DataGenerator generate was not called", DataCommunicatorTest.TEST_OBJECT, generator.generated);
    }

    @Test
    public void refreshDataProviderRemovesOldObjectsFromActiveDataHandler() {
        session.lock();
        UI ui = new DataCommunicatorTest.TestUI(session);
        DataCommunicatorTest.TestDataProvider dataProvider = new DataCommunicatorTest.TestDataProvider();
        DataCommunicatorTest.TestDataCommunicator communicator = new DataCommunicatorTest.TestDataCommunicator();
        setDataProvider(dataProvider, null);
        communicator.extend(ui);
        beforeClientResponse(true);
        DataKeyMapper keyMapper = getKeyMapper();
        Assert.assertTrue("Object not mapped by key mapper", keyMapper.has(DataCommunicatorTest.TEST_OBJECT));
        ActiveDataHandler handler = getActiveDataHandler();
        Assert.assertTrue("Object not amongst active data", handler.getActiveData().containsKey(DataCommunicatorTest.TEST_OBJECT));
        dataProvider.setItem(DataCommunicatorTest.TEST_OBJECT_TWO);
        refreshAll();
        beforeClientResponse(false);
        // assert that test object is marked as removed
        Assert.assertTrue("Object not marked as dropped", handler.getDroppedData().containsKey(DataCommunicatorTest.TEST_OBJECT));
        communicator.setPushRows(Range.between(0, getMinPushSize()));
        beforeClientResponse(false);
        Assert.assertFalse("Object still mapped by key mapper", keyMapper.has(DataCommunicatorTest.TEST_OBJECT));
        Assert.assertTrue("Object not mapped by key mapper", keyMapper.has(DataCommunicatorTest.TEST_OBJECT_TWO));
        Assert.assertFalse("Object still amongst active data", handler.getActiveData().containsKey(DataCommunicatorTest.TEST_OBJECT));
        Assert.assertTrue("Object not amongst active data", handler.getActiveData().containsKey(DataCommunicatorTest.TEST_OBJECT_TWO));
    }

    @Test
    public void testDestroyData() {
        session.lock();
        UI ui = new DataCommunicatorTest.TestUI(session);
        DataCommunicatorTest.TestDataCommunicator communicator = new DataCommunicatorTest.TestDataCommunicator();
        DataCommunicatorTest.TestDataProvider dataProvider = new DataCommunicatorTest.TestDataProvider();
        setDataProvider(dataProvider, null);
        communicator.extend(ui);
        // Put a test object into a cache
        pushData(1, Collections.singletonList(DataCommunicatorTest.TEST_OBJECT));
        // Put the test object into an update queue
        refresh(DataCommunicatorTest.TEST_OBJECT);
        // Drop the test object from the cache
        String key = communicator.getKeyMapper().key(DataCommunicatorTest.TEST_OBJECT);
        JsonArray keys = Json.createArray();
        keys.set(0, key);
        communicator.onDropRows(keys);
        // Replace everything
        communicator.setDataProvider(new ListDataProvider(Collections.singleton(new Object())));
        // The communicator does not have to throw exceptions during
        // request finalization
        beforeClientResponse(false);
        Assert.assertFalse("Stalled object in KeyMapper", communicator.getKeyMapper().has(DataCommunicatorTest.TEST_OBJECT));
    }

    @Test
    public void testFilteringLock() {
        session.lock();
        UI ui = new DataCommunicatorTest.TestUI(session);
        DataCommunicatorTest.TestDataCommunicator communicator = new DataCommunicatorTest.TestDataCommunicator();
        communicator.extend(ui);
        ListDataProvider<Object> dataProvider = DataProvider.ofItems("one", "two", "three");
        SerializableConsumer<SerializablePredicate<Object>> filterSlot = communicator.setDataProvider(dataProvider, null);
        beforeClientResponse(true);
        // Mock empty request
        filterSlot.accept(( t) -> String.valueOf(t).contains("a"));
        beforeClientResponse(false);
        // Assume client clears up the filter
        filterSlot.accept(( t) -> String.valueOf(t).contains(""));
        beforeClientResponse(false);
        // And in the next request sets a non-matching filter
        // and has the data request for previous change
        onRequestRows(0, 3, 0, 0);
        filterSlot.accept(( t) -> String.valueOf(t).contains("a"));
        beforeClientResponse(false);
        // Mark communicator clean
        ui.getConnectorTracker().markClean(communicator);
        Assert.assertFalse("Communicator should not be marked for hard reset", communicator.reset);
        Assert.assertFalse("DataCommunicator should not be marked as dirty", ui.getConnectorTracker().isDirty(communicator));
        // Set a filter that gets results again.
        filterSlot.accept(( t) -> String.valueOf(t).contains(""));
        Assert.assertTrue("DataCommunicator should be marked as dirty", ui.getConnectorTracker().isDirty(communicator));
    }
}

