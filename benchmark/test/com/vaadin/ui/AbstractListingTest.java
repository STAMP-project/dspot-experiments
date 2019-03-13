package com.vaadin.ui;


import com.vaadin.data.HasDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.ui.AbstractListing.AbstractListingExtension;
import com.vaadin.ui.declarative.DesignContext;
import elemental.json.JsonObject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


public class AbstractListingTest {
    private final class TestListing extends AbstractSingleSelect<String> implements HasDataProvider<String> {
        /**
         * Used to execute data generation
         *
         * @param initial
         * 		{@code true} to mock initial data request; {@code false}
         * 		for follow-up request.
         */
        public void runDataGeneration(boolean initial) {
            super.getDataCommunicator().beforeClientResponse(initial);
        }

        @Override
        protected Element writeItem(Element design, String item, DesignContext context) {
            return null;
        }

        @Override
        protected void readItems(Element design, DesignContext context) {
        }

        @Override
        public DataProvider<String, ?> getDataProvider() {
            return internalGetDataProvider();
        }

        @Override
        public void setDataProvider(DataProvider<String, ?> dataProvider) {
            internalSetDataProvider(dataProvider);
        }
    }

    private final class CountGenerator extends AbstractListingExtension<String> {
        int callCount = 0;

        @Override
        public void generateData(String data, JsonObject jsonObject) {
            ++(callCount);
        }

        @Override
        public void destroyData(String data) {
        }

        @Override
        public void refresh(String data) {
            super.refresh(data);
        }
    }

    private static final String[] ITEM_ARRAY = new String[]{ "Foo", "Bar", "Baz" };

    private AbstractListingTest.TestListing listing;

    private List<String> items;

    @Test
    public void testSetItemsWithCollection() {
        listing.setItems(items);
        List<String> list = new LinkedList<>(items);
        listing.getDataProvider().fetch(new Query()).forEach(( str) -> assertTrue("Unexpected item in data provider", list.remove(str)));
        Assert.assertTrue("Not all items from list were in data provider", list.isEmpty());
    }

    @Test
    public void testSetItemsWithVarargs() {
        listing.setItems(AbstractListingTest.ITEM_ARRAY);
        listing.getDataProvider().fetch(new Query()).forEach(( str) -> assertTrue("Unexpected item in data provider", items.remove(str)));
        Assert.assertTrue("Not all items from list were in data provider", items.isEmpty());
    }

    @Test
    public void testSetDataProvider() {
        ListDataProvider<String> dataProvider = DataProvider.ofCollection(items);
        listing.setDataProvider(dataProvider);
        Assert.assertEquals("setDataProvider did not set data provider", dataProvider, listing.getDataProvider());
        listing.setDataProvider(DataProvider.fromCallbacks(( query) -> Stream.of(ITEM_ARRAY).skip(query.getOffset()).limit(query.getLimit()), ( query) -> ITEM_ARRAY.length));
        Assert.assertNotEquals("setDataProvider did not replace data provider", dataProvider, listing.getDataProvider());
    }

    @Test
    public void testAddDataGeneratorBeforeDataProvider() {
        AbstractListingTest.CountGenerator generator = new AbstractListingTest.CountGenerator();
        extend(listing);
        setItems("Foo");
        listing.runDataGeneration(true);
        Assert.assertEquals("Generator should have been called once", 1, generator.callCount);
    }

    @Test
    public void testAddDataGeneratorAfterDataProvider() {
        AbstractListingTest.CountGenerator generator = new AbstractListingTest.CountGenerator();
        setItems("Foo");
        extend(listing);
        listing.runDataGeneration(true);
        Assert.assertEquals("Generator should have been called once", 1, generator.callCount);
    }

    @Test
    public void testDataNotGeneratedTwice() {
        setItems("Foo");
        AbstractListingTest.CountGenerator generator = new AbstractListingTest.CountGenerator();
        extend(listing);
        listing.runDataGeneration(true);
        Assert.assertEquals("Generator should have been called once", 1, generator.callCount);
        listing.runDataGeneration(false);
        Assert.assertEquals("Generator should not have been called again", 1, generator.callCount);
    }

    @Test
    public void testRemoveDataGenerator() {
        setItems("Foo");
        AbstractListingTest.CountGenerator generator = new AbstractListingTest.CountGenerator();
        extend(listing);
        remove();
        listing.runDataGeneration(true);
        Assert.assertEquals("Generator should not have been called", 0, generator.callCount);
    }

    @Test
    public void testDataRefresh() {
        setItems("Foo");
        AbstractListingTest.CountGenerator generator = new AbstractListingTest.CountGenerator();
        extend(listing);
        listing.runDataGeneration(true);
        Assert.assertEquals("Generator should have been called once", 1, generator.callCount);
        generator.refresh("Foo");
        listing.runDataGeneration(false);
        Assert.assertEquals("Generator should have been called again", 2, generator.callCount);
    }
}

