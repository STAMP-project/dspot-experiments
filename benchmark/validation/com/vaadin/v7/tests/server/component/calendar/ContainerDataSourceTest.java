package com.vaadin.v7.tests.server.component.calendar;


import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Calendar;
import com.vaadin.v7.ui.components.calendar.ContainerEventProvider;
import com.vaadin.v7.ui.components.calendar.event.BasicEvent;
import com.vaadin.v7.ui.components.calendar.event.CalendarEvent;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.getInstance;


public class ContainerDataSourceTest {
    private Calendar calendar;

    /**
     * Tests adding a bean item container to the Calendar
     */
    @Test
    public void testWithBeanItemContainer() {
        // Create a container to use as a datasource
        Indexed container = ContainerDataSourceTest.createTestBeanItemContainer();
        // Set datasource
        calendar.setContainerDataSource(container);
        // Start and end dates to query for
        java.util.Calendar cal = getInstance();
        cal.setTime(getStart());
        Date start = cal.getTime();
        cal.add(MONTH, 1);
        Date end = cal.getTime();
        // Test the all events are returned
        List<CalendarEvent> events = calendar.getEventProvider().getEvents(start, end);
        Assert.assertEquals(container.size(), events.size());
        // Test that a certain range is returned
        cal.setTime(getStart());
        end = cal.getTime();
        events = calendar.getEventProvider().getEvents(start, end);
        Assert.assertEquals(6, events.size());
    }

    /**
     * This tests tests that if you give the Calendar an unsorted (== not sorted
     * by starting date) container then the calendar should gracefully handle
     * it. In this case the size of the container will be wrong. The test is
     * exactly the same as {@link #testWithBeanItemContainer()} except that the
     * beans has been intentionally sorted by caption instead of date.
     */
    @Test
    public void testWithUnsortedBeanItemContainer() {
        // Create a container to use as a datasource
        Indexed container = ContainerDataSourceTest.createTestBeanItemContainer();
        // Make the container sorted by caption
        sort(new Object[]{ "caption" }, new boolean[]{ true });
        // Set data source
        calendar.setContainerDataSource(container);
        // Start and end dates to query for
        java.util.Calendar cal = getInstance();
        cal.setTime(getStart());
        Date start = cal.getTime();
        cal.add(MONTH, 1);
        Date end = cal.getTime();
        // Test the all events are returned
        List<CalendarEvent> events = calendar.getEventProvider().getEvents(start, end);
        Assert.assertEquals(container.size(), events.size());
        // Test that a certain range is returned
        cal.setTime(getStart());
        end = cal.getTime();
        events = calendar.getEventProvider().getEvents(start, end);
        // The events size is 1 since the getEvents returns the wrong range
        Assert.assertEquals(1, events.size());
    }

    /**
     * Tests adding a Indexed container to the Calendar
     */
    @Test
    public void testWithIndexedContainer() {
        // Create a container to use as a datasource
        Indexed container = ContainerDataSourceTest.createTestIndexedContainer();
        // Set datasource
        calendar.setContainerDataSource(container, "testCaption", "testDescription", "testStartDate", "testEndDate", null);
        // Start and end dates to query for
        java.util.Calendar cal = getInstance();
        cal.setTime(((Date) (container.getItem(container.getIdByIndex(0)).getItemProperty("testStartDate").getValue())));
        Date start = cal.getTime();
        cal.add(MONTH, 1);
        Date end = cal.getTime();
        // Test the all events are returned
        List<CalendarEvent> events = calendar.getEventProvider().getEvents(start, end);
        Assert.assertEquals(container.size(), events.size());
        // Check that event values are present
        CalendarEvent e = events.get(0);
        Assert.assertEquals("Test 1", e.getCaption());
        Assert.assertEquals("Description 1", e.getDescription());
        Assert.assertTrue(((e.getStart().compareTo(start)) == 0));
        // Test that a certain range is returned
        cal.setTime(((Date) (container.getItem(container.getIdByIndex(6)).getItemProperty("testStartDate").getValue())));
        end = cal.getTime();
        events = calendar.getEventProvider().getEvents(start, end);
        Assert.assertEquals(6, events.size());
    }

    @Test
    public void testNullLimitsBeanItemContainer() {
        // Create a container to use as a datasource
        Indexed container = ContainerDataSourceTest.createTestBeanItemContainer();
        // Start and end dates to query for
        java.util.Calendar cal = getInstance();
        cal.setTime(getStart());
        Date start = cal.getTime();
        cal.add(MONTH, 1);
        Date end = cal.getTime();
        // Set datasource
        calendar.setContainerDataSource(container);
        // Test null start time
        List<CalendarEvent> events = calendar.getEventProvider().getEvents(null, end);
        Assert.assertEquals(container.size(), events.size());
        // Test null end time
        events = calendar.getEventProvider().getEvents(start, null);
        Assert.assertEquals(container.size(), events.size());
        // Test both null times
        events = calendar.getEventProvider().getEvents(null, null);
        Assert.assertEquals(container.size(), events.size());
    }

    @Test
    public void testNullLimitsIndexedContainer() {
        // Create a container to use as a datasource
        Indexed container = ContainerDataSourceTest.createTestIndexedContainer();
        // Start and end dates to query for
        java.util.Calendar cal = getInstance();
        cal.setTime(((Date) (container.getItem(container.getIdByIndex(0)).getItemProperty("testStartDate").getValue())));
        Date start = cal.getTime();
        cal.add(MONTH, 1);
        Date end = cal.getTime();
        // Set datasource
        calendar.setContainerDataSource(container, "testCaption", "testDescription", "testStartDate", "testEndDate", null);
        // Test null start time
        List<CalendarEvent> events = calendar.getEventProvider().getEvents(null, end);
        Assert.assertEquals(container.size(), events.size());
        // Test null end time
        events = calendar.getEventProvider().getEvents(start, null);
        Assert.assertEquals(container.size(), events.size());
        // Test both null times
        events = calendar.getEventProvider().getEvents(null, null);
        Assert.assertEquals(container.size(), events.size());
    }

    /**
     * Tests the addEvent convenience method with the default event provider
     */
    @Test
    public void testAddEventConvinienceMethod() {
        // Start and end dates to query for
        java.util.Calendar cal = getInstance();
        Date start = cal.getTime();
        cal.add(MONTH, 1);
        Date end = cal.getTime();
        // Ensure no events
        Assert.assertEquals(0, calendar.getEvents(start, end).size());
        // Add an event
        BasicEvent event = new BasicEvent("Test", "Test", start);
        calendar.addEvent(event);
        // Ensure event exists
        List<CalendarEvent> events = calendar.getEvents(start, end);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(events.get(0).getCaption(), event.getCaption());
        Assert.assertEquals(events.get(0).getDescription(), event.getDescription());
        Assert.assertEquals(getStart(), event.getStart());
    }

    /**
     * Test the removeEvent convenience method with the default event provider
     */
    @Test
    public void testRemoveEventConvinienceMethod() {
        // Start and end dates to query for
        java.util.Calendar cal = getInstance();
        Date start = cal.getTime();
        cal.add(MONTH, 1);
        Date end = cal.getTime();
        // Ensure no events
        Assert.assertEquals(0, calendar.getEvents(start, end).size());
        // Add an event
        CalendarEvent event = new BasicEvent("Test", "Test", start);
        calendar.addEvent(event);
        // Ensure event exists
        Assert.assertEquals(1, calendar.getEvents(start, end).size());
        // Remove event
        calendar.removeEvent(event);
        // Ensure no events
        Assert.assertEquals(0, calendar.getEvents(start, end).size());
    }

    @Test
    public void testAddEventConvinienceMethodWithCustomEventProvider() {
        // Use a container data source
        calendar.setEventProvider(new ContainerEventProvider(new com.vaadin.v7.data.util.BeanItemContainer<BasicEvent>(BasicEvent.class)));
        // Start and end dates to query for
        java.util.Calendar cal = getInstance();
        Date start = cal.getTime();
        cal.add(MONTH, 1);
        Date end = cal.getTime();
        // Ensure no events
        Assert.assertEquals(0, calendar.getEvents(start, end).size());
        // Add an event
        BasicEvent event = new BasicEvent("Test", "Test", start);
        calendar.addEvent(event);
        // Ensure event exists
        List<CalendarEvent> events = calendar.getEvents(start, end);
        Assert.assertEquals(1, events.size());
        Assert.assertEquals(events.get(0).getCaption(), event.getCaption());
        Assert.assertEquals(events.get(0).getDescription(), event.getDescription());
        Assert.assertEquals(getStart(), event.getStart());
    }

    @Test
    public void testRemoveEventConvinienceMethodWithCustomEventProvider() {
        // Use a container data source
        calendar.setEventProvider(new ContainerEventProvider(new com.vaadin.v7.data.util.BeanItemContainer<BasicEvent>(BasicEvent.class)));
        // Start and end dates to query for
        java.util.Calendar cal = getInstance();
        Date start = cal.getTime();
        cal.add(MONTH, 1);
        Date end = cal.getTime();
        // Ensure no events
        Assert.assertEquals(0, calendar.getEvents(start, end).size());
        // Add an event
        BasicEvent event = new BasicEvent("Test", "Test", start);
        calendar.addEvent(event);
        // Ensure event exists
        List<CalendarEvent> events = calendar.getEvents(start, end);
        Assert.assertEquals(1, events.size());
        // Remove event
        calendar.removeEvent(event);
        // Ensure no events
        Assert.assertEquals(0, calendar.getEvents(start, end).size());
    }

    @Test
    public void testStyleNamePropertyRetrieved() {
        IndexedContainer ic = ((IndexedContainer) (ContainerDataSourceTest.createTestIndexedContainer()));
        ic.addContainerProperty("testStyleName", String.class, "");
        for (int i = 0; i < 10; i++) {
            Item item = ic.getItem(ic.getIdByIndex(i));
            @SuppressWarnings("unchecked")
            Property<String> itemProperty = item.getItemProperty("testStyleName");
            itemProperty.setValue("testStyle");
        }
        ContainerEventProvider provider = new ContainerEventProvider(ic);
        provider.setCaptionProperty("testCaption");
        provider.setDescriptionProperty("testDescription");
        provider.setStartDateProperty("testStartDate");
        provider.setEndDateProperty("testEndDate");
        provider.setStyleNameProperty("testStyleName");
        calendar.setEventProvider(provider);
        java.util.Calendar cal = getInstance();
        Date now = cal.getTime();
        cal.add(DAY_OF_MONTH, 20);
        Date then = cal.getTime();
        List<CalendarEvent> events = calendar.getEventProvider().getEvents(now, then);
        for (CalendarEvent ce : events) {
            Assert.assertEquals("testStyle", ce.getStyleName());
        }
    }

    @Test
    public void testAutomaticScaleVisibleHoursOfDay() {
        com.vaadin.v7.data.util.BeanItemContainer<CalendarEvent> container = new com.vaadin.v7.data.util.BeanItemContainer<CalendarEvent>(CalendarEvent.class);
        java.util.Calendar start = getInstance();
        java.util.Calendar end = getInstance();
        start.set(HOUR_OF_DAY, 8);
        start.set(MINUTE, 10);
        // same start and end time
        container.addBean(new BasicEvent("8:00", "Description 1", start.getTime()));
        start.set(HOUR_OF_DAY, 16);
        end.set(HOUR_OF_DAY, 18);
        end.set(MINUTE, 10);
        container.addBean(new BasicEvent("16-18", "Description 2", start.getTime(), end.getTime()));// 16-18

        calendar.setContainerDataSource(container);
        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setLocale(Locale.getDefault());
        calendar.beforeClientResponse(true);// simulate adding to UI

        Assert.assertEquals(0, calendar.getFirstVisibleHourOfDay());
        Assert.assertEquals(23, calendar.getLastVisibleHourOfDay());
        calendar.autoScaleVisibleHoursOfDay();
        Assert.assertEquals(8, calendar.getFirstVisibleHourOfDay());
        Assert.assertEquals(18, calendar.getLastVisibleHourOfDay());
        // reset visible timing to something else, so that the added event is
        // not filtered out
        calendar.resetVisibleHoursOfDay();
        calendar.beforeClientResponse(false);// simulate being attached

        Assert.assertEquals(0, calendar.getFirstVisibleHourOfDay());
        Assert.assertEquals(23, calendar.getLastVisibleHourOfDay());
        start.set(HOUR_OF_DAY, 5);
        end.set(HOUR_OF_DAY, 21);
        container.addBean(new BasicEvent("05-21", "Description 3", start.getTime(), end.getTime()));// 05-21

        calendar.beforeClientResponse(false);// simulate being attached

        calendar.autoScaleVisibleHoursOfDay();
        Assert.assertEquals(5, calendar.getFirstVisibleHourOfDay());
        Assert.assertEquals(21, calendar.getLastVisibleHourOfDay());
    }
}

