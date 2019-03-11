package com.querydsl.sql.teradata;


import com.querydsl.sql.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class SetQueryBandClauseTest {
    private Configuration conf;

    private SetQueryBandClause clause;

    @Test
    public void toString_() {
        clause.set("a", "b");
        Assert.assertEquals("set query_band='a=b;' for session", clause.toString());
    }

    @Test
    public void toString2() {
        conf.setUseLiterals(false);
        clause.set("a", "b");
        clause.forTransaction();
        Assert.assertEquals("set query_band=? for transaction", clause.toString());
    }

    @Test
    public void forTransaction() {
        clause.forTransaction();
        clause.set("a", "b");
        clause.set("b", "c");
        Assert.assertEquals("set query_band='a=b;b=c;' for transaction", clause.toString());
    }

    @Test
    public void getSQL() {
        clause.forTransaction();
        clause.set("a", "b");
        clause.set("b", "c");
        Assert.assertEquals("set query_band='a=b;b=c;' for transaction", clause.getSQL().get(0).getSQL());
    }
}

