package com.querydsl.apt.domain;


import QProperties3Test_Order.order;
import com.querydsl.core.types.dsl.DateTimePath;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.joda.time.LocalDateTime;
import org.junit.Test;


public class Properties3Test extends AbstractTest {
    @Entity
    public static class Order {
        @Column(name = "order_date")
        @Temporal(TemporalType.TIMESTAMP)
        private Date orderDate;

        public LocalDateTime getOrderDate() {
            return (orderDate) != null ? new LocalDateTime(orderDate) : null;
        }
    }

    @Test
    public void propertyType() throws IllegalAccessException, NoSuchFieldException {
        start(QProperties3Test_Order.class, order);
        match(DateTimePath.class, "orderDate");
        matchType(Date.class, "orderDate");
    }
}

