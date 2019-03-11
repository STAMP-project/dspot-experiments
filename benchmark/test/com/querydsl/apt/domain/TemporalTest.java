package com.querydsl.apt.domain;


import QTemporalTest_MyEntity.myEntity.date;
import QTemporalTest_MyEntity.myEntity.time;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.TimePath;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.junit.Assert;
import org.junit.Test;


public class TemporalTest {
    @Entity
    public static class MyEntity {
        @Temporal(TemporalType.DATE)
        private Date date;

        @Temporal(TemporalType.TIME)
        private Date time;
    }

    @Test
    public void test() {
        Assert.assertEquals(DatePath.class, date.getClass());
        Assert.assertEquals(TimePath.class, time.getClass());
    }
}

