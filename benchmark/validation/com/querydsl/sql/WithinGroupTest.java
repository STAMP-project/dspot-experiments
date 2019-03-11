package com.querydsl.sql;


import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.Assert;
import org.junit.Test;


public class WithinGroupTest {
    @Test
    public void all() {
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
        Assert.assertEquals("cume_dist(path)", WithinGroupTest.toString(SQLExpressions.cumeDist(path)));
        Assert.assertEquals("cume_dist(path, path2)", WithinGroupTest.toString(SQLExpressions.cumeDist(path, path2)));
        Assert.assertEquals("dense_rank(path, path2)", WithinGroupTest.toString(SQLExpressions.denseRank(path, path2)));
        Assert.assertEquals("listagg(path,',')", WithinGroupTest.toString(SQLExpressions.listagg(path, ",")));
        Assert.assertEquals("percent_rank(path, path2)", WithinGroupTest.toString(SQLExpressions.percentRank(path, path2)));
        Assert.assertEquals("percentile_cont(path)", WithinGroupTest.toString(SQLExpressions.percentileCont(path)));
        Assert.assertEquals("percentile_disc(path)", WithinGroupTest.toString(SQLExpressions.percentileDisc(path)));
        Assert.assertEquals("rank(path, path2)", WithinGroupTest.toString(SQLExpressions.rank(path, path2)));
    }
}

