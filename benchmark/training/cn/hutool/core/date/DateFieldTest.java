package cn.hutool.core.date;


import DateField.HOUR_OF_DAY;
import DateField.MINUTE;
import org.junit.Assert;
import org.junit.Test;


public class DateFieldTest {
    @Test
    public void ofTest() {
        DateField field = DateField.of(11);
        Assert.assertEquals(HOUR_OF_DAY, field);
        field = DateField.of(12);
        Assert.assertEquals(MINUTE, field);
    }
}

