package com.vaadin.v7.tests.server.component.calendar;


import ContainerEventProvider.ALL_DAY_PROPERTY;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.components.calendar.ContainerEventProvider;
import com.vaadin.v7.ui.components.calendar.event.CalendarEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class ContainerEventProviderTest {
    @Test
    public void testDefaultAllDayProperty() {
        ContainerEventProvider provider = new ContainerEventProvider(null);
        Assert.assertEquals(ALL_DAY_PROPERTY, provider.getAllDayProperty());
    }

    @Test
    public void testSetAllDayProperty() {
        ContainerEventProvider provider = new ContainerEventProvider(null);
        Object prop = new Object();
        provider.setAllDayProperty(prop);
        Assert.assertEquals(prop, provider.getAllDayProperty());
    }

    @Test
    public void testGetEvents() {
        BeanItemContainer<ContainerEventProviderTest.EventBean> container = new BeanItemContainer<ContainerEventProviderTest.EventBean>(ContainerEventProviderTest.EventBean.class);
        ContainerEventProviderTest.EventBean bean = new ContainerEventProviderTest.EventBean();
        container.addBean(bean);
        ContainerEventProvider provider = new ContainerEventProvider(container);
        List<CalendarEvent> events = provider.getEvents(bean.getStart(), bean.getEnd());
        Assert.assertTrue(events.get(0).isAllDay());
    }

    public static class EventBean {
        public boolean isAllDay() {
            return true;
        }

        public void setAllDay(boolean allDay) {
        }

        public Date getStart() {
            return Calendar.getInstance().getTime();
        }

        public Date getEnd() {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 10);
            return calendar.getTime();
        }

        public void setStart(Date date) {
        }

        public void setEnd(Date date) {
        }
    }
}

