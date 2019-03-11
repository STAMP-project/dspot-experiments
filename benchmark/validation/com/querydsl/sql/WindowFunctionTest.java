package com.querydsl.sql;


import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.Assert;
import org.junit.Test;


public class WindowFunctionTest {
    @Test
    public void complex() {
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
        Expression<?> wf = SQLExpressions.sum(path).over().partitionBy(path2).orderBy(path);
        Assert.assertEquals("sum(path) over (partition by path2 order by path asc)", WindowFunctionTest.toString(wf));
    }

    @Test
    public void complex_nullsFirst() {
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
        Expression<?> wf = SQLExpressions.sum(path).over().partitionBy(path2).orderBy(path.desc().nullsFirst());
        Assert.assertEquals("sum(path) over (partition by path2 order by path desc nulls first)", WindowFunctionTest.toString(wf));
    }

    @Test
    public void all() {
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
        Assert.assertEquals("avg(path)", WindowFunctionTest.toString(SQLExpressions.avg(path)));
        Assert.assertEquals("count(path)", WindowFunctionTest.toString(SQLExpressions.count(path)));
        Assert.assertEquals("corr(path,path2)", WindowFunctionTest.toString(SQLExpressions.corr(path, path2)));
        Assert.assertEquals("covar_pop(path,path2)", WindowFunctionTest.toString(SQLExpressions.covarPop(path, path2)));
        Assert.assertEquals("covar_samp(path,path2)", WindowFunctionTest.toString(SQLExpressions.covarSamp(path, path2)));
        Assert.assertEquals("cume_dist()", WindowFunctionTest.toString(SQLExpressions.cumeDist()));
        Assert.assertEquals("dense_rank()", WindowFunctionTest.toString(SQLExpressions.denseRank()));
        Assert.assertEquals("first_value(path)", WindowFunctionTest.toString(SQLExpressions.firstValue(path)));
        Assert.assertEquals("lag(path)", WindowFunctionTest.toString(SQLExpressions.lag(path)));
        Assert.assertEquals("last_value(path)", WindowFunctionTest.toString(SQLExpressions.lastValue(path)));
        Assert.assertEquals("lead(path)", WindowFunctionTest.toString(SQLExpressions.lead(path)));
        Assert.assertEquals("max(path)", WindowFunctionTest.toString(SQLExpressions.max(path)));
        Assert.assertEquals("min(path)", WindowFunctionTest.toString(SQLExpressions.min(path)));
        Assert.assertEquals("nth_value(path, ?)", WindowFunctionTest.toString(SQLExpressions.nthValue(path, 3)));
        Assert.assertEquals("ntile(?)", WindowFunctionTest.toString(SQLExpressions.ntile(4)));
        Assert.assertEquals("percent_rank()", WindowFunctionTest.toString(SQLExpressions.percentRank()));
        Assert.assertEquals("rank()", WindowFunctionTest.toString(SQLExpressions.rank()));
        Assert.assertEquals("ratio_to_report(path)", WindowFunctionTest.toString(SQLExpressions.ratioToReport(path)));
        Assert.assertEquals("row_number()", WindowFunctionTest.toString(SQLExpressions.rowNumber()));
        Assert.assertEquals("stddev(path)", WindowFunctionTest.toString(SQLExpressions.stddev(path)));
        Assert.assertEquals("stddev(distinct path)", WindowFunctionTest.toString(SQLExpressions.stddevDistinct(path)));
        Assert.assertEquals("stddev_pop(path)", WindowFunctionTest.toString(SQLExpressions.stddevPop(path)));
        Assert.assertEquals("stddev_samp(path)", WindowFunctionTest.toString(SQLExpressions.stddevSamp(path)));
        Assert.assertEquals("sum(path)", WindowFunctionTest.toString(SQLExpressions.sum(path)));
        Assert.assertEquals("variance(path)", WindowFunctionTest.toString(SQLExpressions.variance(path)));
        Assert.assertEquals("var_pop(path)", WindowFunctionTest.toString(SQLExpressions.varPop(path)));
        Assert.assertEquals("var_samp(path)", WindowFunctionTest.toString(SQLExpressions.varSamp(path)));
        // TODO FIRST
        // TODO LAST
        // TODO NTH_VALUE ... FROM (FIRST|LAST) (RESPECT|IGNORE) NULLS
    }

    @Test
    public void regr() {
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
        Assert.assertEquals("regr_slope(path, path2)", WindowFunctionTest.toString(SQLExpressions.regrSlope(path, path2)));
        Assert.assertEquals("regr_intercept(path, path2)", WindowFunctionTest.toString(SQLExpressions.regrIntercept(path, path2)));
        Assert.assertEquals("regr_count(path, path2)", WindowFunctionTest.toString(SQLExpressions.regrCount(path, path2)));
        Assert.assertEquals("regr_r2(path, path2)", WindowFunctionTest.toString(SQLExpressions.regrR2(path, path2)));
        Assert.assertEquals("regr_avgx(path, path2)", WindowFunctionTest.toString(SQLExpressions.regrAvgx(path, path2)));
        Assert.assertEquals("regr_avgy(path, path2)", WindowFunctionTest.toString(SQLExpressions.regrAvgy(path, path2)));
        Assert.assertEquals("regr_sxx(path, path2)", WindowFunctionTest.toString(SQLExpressions.regrSxx(path, path2)));
        Assert.assertEquals("regr_syy(path, path2)", WindowFunctionTest.toString(SQLExpressions.regrSyy(path, path2)));
        Assert.assertEquals("regr_sxy(path, path2)", WindowFunctionTest.toString(SQLExpressions.regrSxy(path, path2)));
    }

    @Test
    public void rows_between() {
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        NumberPath<Integer> intPath = Expressions.numberPath(Integer.class, "intPath");
        WindowFunction<Long> wf = SQLExpressions.sum(path).over().orderBy(path);
        Assert.assertEquals("sum(path) over (order by path asc rows between current row and unbounded following)", WindowFunctionTest.toString(wf.rows().between().currentRow().unboundedFollowing()));
        Assert.assertEquals("sum(path) over (order by path asc rows between preceding intPath and following intPath)", WindowFunctionTest.toString(wf.rows().between().preceding(intPath).following(intPath)));
        Assert.assertEquals("sum(path) over (order by path asc rows between preceding ? and following ?)", WindowFunctionTest.toString(wf.rows().between().preceding(1).following(3)));
    }

    @Test
    public void rows_unboundedPreceding() {
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        WindowFunction<Long> wf = SQLExpressions.sum(path).over().orderBy(path);
        Assert.assertEquals("sum(path) over (order by path asc rows unbounded preceding)", WindowFunctionTest.toString(wf.rows().unboundedPreceding()));
    }

    @Test
    public void rows_currentRow() {
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        WindowFunction<Long> wf = SQLExpressions.sum(path).over().orderBy(path);
        Assert.assertEquals("sum(path) over (order by path asc rows current row)", WindowFunctionTest.toString(wf.rows().currentRow()));
    }

    @Test
    public void rows_precedingRow() {
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        NumberPath<Integer> intPath = Expressions.numberPath(Integer.class, "intPath");
        WindowFunction<Long> wf = SQLExpressions.sum(path).over().orderBy(path);
        Assert.assertEquals("sum(path) over (order by path asc rows preceding intPath)", WindowFunctionTest.toString(wf.rows().preceding(intPath)));
        Assert.assertEquals("sum(path) over (order by path asc rows preceding ?)", WindowFunctionTest.toString(wf.rows().preceding(3)));
    }

    @Test
    public void keep_first() {
        // MIN(salary) KEEP (DENSE_RANK FIRST ORDER BY commission_pct) OVER (PARTITION BY department_id)
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
        NumberPath<Long> path3 = Expressions.numberPath(Long.class, "path3");
        Assert.assertEquals("min(path) keep (dense_rank first order by path2 asc)", WindowFunctionTest.toString(SQLExpressions.min(path).keepFirst().orderBy(path2)));
        Assert.assertEquals("min(path) keep (dense_rank first order by path2 asc) over (partition by path3)", WindowFunctionTest.toString(SQLExpressions.min(path).keepFirst().orderBy(path2).over().partitionBy(path3)));
    }

    @Test
    public void keep_last() {
        // MIN(salary) KEEP (DENSE_RANK FIRST ORDER BY commission_pct) OVER (PARTITION BY department_id)
        NumberPath<Long> path = Expressions.numberPath(Long.class, "path");
        NumberPath<Long> path2 = Expressions.numberPath(Long.class, "path2");
        NumberPath<Long> path3 = Expressions.numberPath(Long.class, "path3");
        Assert.assertEquals("min(path) keep (dense_rank last order by path2 asc) over (partition by path3)", WindowFunctionTest.toString(SQLExpressions.min(path).keepLast().orderBy(path2).over().partitionBy(path3)));
    }
}

