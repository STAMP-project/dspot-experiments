package com.querydsl.apt.domain;


import QJodaTest_BaseEntity.baseEntity.createdDate;
import java.util.Date;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import static AccessType.FIELD;
import static TemporalType.TIMESTAMP;


public class JodaTest {
    @MappedSuperclass
    @Access(FIELD)
    public abstract static class BaseEntity {
        public abstract Long getId();

        @Temporal(TIMESTAMP)
        private Date createdDate;

        public boolean isNew() {
            return null == (getId());
        }

        public DateTime getCreatedDate() {
            return null == (createdDate) ? null : new DateTime(createdDate);
        }

        public void setCreatedDate(DateTime creationDate) {
            this.createdDate = (null == creationDate) ? null : creationDate.toDate();
        }
    }

    @Test
    public void test() {
        Assert.assertEquals(Date.class, createdDate.getType());
    }
}

