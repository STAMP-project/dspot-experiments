package com.querydsl.sql;


import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.testutil.H2;
import com.querydsl.sql.domain.Employee;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(H2.class)
public class SQLCloseListenerTest {
    private SQLQuery<Employee> query;

    @Test
    public void fetch() {
        Assert.assertFalse(query.fetch().isEmpty());
    }

    @Test
    public void fetchOne() {
        Assert.assertNotNull(query.limit(1).fetchOne());
    }

    @Test
    public void fetchFirst() {
        Assert.assertNotNull(query.fetchFirst());
    }

    @Test
    public void fetchResults() {
        Assert.assertNotNull(query.fetchResults());
    }

    @Test
    public void iterate() {
        CloseableIterator<Employee> it = query.iterate();
        try {
            while (it.hasNext()) {
                it.next();
            } 
        } finally {
            it.close();
        }
    }
}

