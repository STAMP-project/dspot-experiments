package com.vaadin.tests.data.converter;


import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.DateTimeField;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class LocalDateTimeToDateConverterTest extends AbstractConverterTest {
    private static final LocalDateTime LOCAL_DATE = LocalDateTime.of(2017, 1, 1, 1, 1, 1);

    private static final Date DATE = LocalDateTimeToDateConverterTest.createDate();

    @Test
    public void testToModel() {
        assertValue(LocalDateTimeToDateConverterTest.DATE, getConverter().convertToModel(LocalDateTimeToDateConverterTest.LOCAL_DATE, new ValueContext()));
    }

    @Test
    public void testToModelFromSqlDate() {
        // Check that SQL dates also work (e.g. java.sql.Date.toInstant throws)
        assertValue(new java.sql.Date(LocalDateTimeToDateConverterTest.DATE.getTime()), getConverter().convertToModel(LocalDateTimeToDateConverterTest.LOCAL_DATE, new ValueContext()));
    }

    @Test
    public void testToPresentation() {
        Assert.assertEquals(LocalDateTimeToDateConverterTest.LOCAL_DATE, getConverter().convertToPresentation(LocalDateTimeToDateConverterTest.DATE, new ValueContext()));
    }

    @Test
    public void useWithBinder() throws ValidationException {
        Binder<LocalDateTimeToDateConverterTest.BeanWithDate> binder = new Binder();
        DateTimeField dateField = new DateTimeField();
        binder.forField(dateField).withConverter(getConverter()).bind(LocalDateTimeToDateConverterTest.BeanWithDate::getDate, LocalDateTimeToDateConverterTest.BeanWithDate::setDate);
        dateField.setValue(LocalDateTimeToDateConverterTest.LOCAL_DATE);
        LocalDateTimeToDateConverterTest.BeanWithDate bean = new LocalDateTimeToDateConverterTest.BeanWithDate();
        binder.writeBean(bean);
        Assert.assertEquals(LocalDateTimeToDateConverterTest.DATE, bean.getDate());
    }

    public static class BeanWithDate {
        private Date date;

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }
    }
}

