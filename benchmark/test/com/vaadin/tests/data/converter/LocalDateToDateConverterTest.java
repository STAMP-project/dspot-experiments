package com.vaadin.tests.data.converter;


import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.DateField;
import java.time.LocalDate;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class LocalDateToDateConverterTest extends AbstractConverterTest {
    private static final String TIME_ZONE = "UTC";

    private static final LocalDate LOCAL_DATE = LocalDate.of(2017, 1, 1);

    private static final Date DATE = LocalDateToDateConverterTest.createDate();

    @Test
    public void testToModel() {
        assertValue(LocalDateToDateConverterTest.DATE, getConverter().convertToModel(LocalDateToDateConverterTest.LOCAL_DATE, new ValueContext()));
    }

    @Test
    public void testToModelFromSqlDate() {
        // Check that SQL dates also work (e.g. java.sql.Date.toInstant throws)
        assertValue(new java.sql.Date(LocalDateToDateConverterTest.DATE.getTime()), getConverter().convertToModel(LocalDateToDateConverterTest.LOCAL_DATE, new ValueContext()));
    }

    @Test
    public void testToPresentation() {
        Assert.assertEquals(LocalDateToDateConverterTest.LOCAL_DATE, getConverter().convertToPresentation(LocalDateToDateConverterTest.DATE, new ValueContext()));
    }

    @Test
    public void useWithBinder() throws ValidationException {
        Binder<LocalDateToDateConverterTest.BeanWithDate> binder = new Binder();
        DateField dateField = new DateField();
        binder.forField(dateField).withConverter(getConverter()).bind(LocalDateToDateConverterTest.BeanWithDate::getDate, LocalDateToDateConverterTest.BeanWithDate::setDate);
        dateField.setValue(LocalDateToDateConverterTest.LOCAL_DATE);
        LocalDateToDateConverterTest.BeanWithDate bean = new LocalDateToDateConverterTest.BeanWithDate();
        binder.writeBean(bean);
        Assert.assertEquals(LocalDateToDateConverterTest.DATE, bean.getDate());
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

