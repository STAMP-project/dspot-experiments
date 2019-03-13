/**
 * CHECKSTYLERULE:OFF: FileLength
 */
/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql;


import DatePart.day;
import DatePart.hour;
import DatePart.minute;
import DatePart.month;
import DatePart.second;
import DatePart.week;
import DatePart.year;
import Expressions.FALSE;
import Expressions.ONE;
import Expressions.TRUE;
import Ops.STRING_CONTAINS;
import Wildcard.all;
import Wildcard.count;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.Target;
import com.querydsl.core.group.Group;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.sql.domain.Employee;
import com.querydsl.sql.domain.Expression;
import com.querydsl.sql.domain.IdName;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QEmployeeNoPK;
import com.querydsl.sql.domain.QIdName;
import com.querydsl.sql.domain.QNumberTest;
import com.querydsl.sql.domain.QSurvey;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Assert;
import org.junit.Test;

import static Expressions.FOUR;
import static Expressions.ONE;
import static Expressions.THREE;
import static Expressions.TWO;
import static Module.SQL;
import static com.querydsl.sql.AbstractBaseTest.TestQuery.from;
import static java.time.LocalDateTime.ofInstant;


public class SelectBase extends AbstractBaseTest {
    private static final Expression<?>[] NO_EXPRESSIONS = new Expression[0];

    private final QueryExecution standardTest = new QueryExecution(SQL, Connections.getTarget()) {
        @Override
        protected Fetchable<?> createQuery() {
            return from(Constants.employee, Constants.employee2);
        }

        @Override
        protected Fetchable<?> createQuery(Predicate filter) {
            return testQuery().from(Constants.employee, Constants.employee2).where(filter).select(Constants.employee.firstname);
        }
    };

    @Test
    public void aggregate_list() {
        int min = 30000;
        int avg = 65000;
        int max = 160000;
        // fetch
        Assert.assertEquals(min, query().from(Constants.employee).select(Constants.employee.salary.min()).fetch().get(0).intValue());
        Assert.assertEquals(avg, query().from(Constants.employee).select(Constants.employee.salary.avg()).fetch().get(0).intValue());
        Assert.assertEquals(max, query().from(Constants.employee).select(Constants.employee.salary.max()).fetch().get(0).intValue());
    }

    @Test
    public void aggregate_uniqueResult() {
        int min = 30000;
        int avg = 65000;
        int max = 160000;
        // fetchOne
        Assert.assertEquals(min, query().from(Constants.employee).select(Constants.employee.salary.min()).fetchOne().intValue());
        Assert.assertEquals(avg, query().from(Constants.employee).select(Constants.employee.salary.avg()).fetchOne().intValue());
        Assert.assertEquals(max, query().from(Constants.employee).select(Constants.employee.salary.max()).fetchOne().intValue());
    }

    @Test
    @ExcludeIn(ORACLE)
    @SkipForQuoted
    public void alias() {
        expectedQuery = "select e.ID as id from EMPLOYEE e";
        query().from().select(Constants.employee.id.as(Constants.employee.id)).from(Constants.employee).fetch();
    }

    @Test
    @ExcludeIn({ MYSQL, ORACLE })
    @SkipForQuoted
    public void alias_quotes() {
        expectedQuery = "select e.FIRSTNAME as \"First Name\" from EMPLOYEE e";
        query().from(Constants.employee).select(Constants.employee.firstname.as("First Name")).fetch();
    }

    @Test
    @IncludeIn(MYSQL)
    @SkipForQuoted
    public void alias_quotes_MySQL() {
        expectedQuery = "select e.FIRSTNAME as `First Name` from EMPLOYEE e";
        query().from(Constants.employee).select(Constants.employee.firstname.as("First Name")).fetch();
    }

    @Test
    @IncludeIn(ORACLE)
    @SkipForQuoted
    public void alias_quotes_Oracle() {
        expectedQuery = "select e.FIRSTNAME \"First Name\" from EMPLOYEE e";
        query().from(Constants.employee).select(Constants.employee.firstname.as("First Name"));
    }

    @Test
    public void all() {
        for (Expression<?> expr : Constants.survey.all()) {
            Path<?> path = ((Path<?>) (expr));
            Assert.assertEquals(Constants.survey, path.getMetadata().getParent());
        }
    }

    @Test
    public void arithmetic() {
        NumberExpression<Integer> one = Expressions.numberTemplate(Integer.class, "(1.0)");
        NumberExpression<Integer> two = Expressions.numberTemplate(Integer.class, "(2.0)");
        NumberExpression<Integer> three = Expressions.numberTemplate(Integer.class, "(3.0)");
        NumberExpression<Integer> four = Expressions.numberTemplate(Integer.class, "(4.0)");
        arithmeticTests(one, two, three, four);
        // the following one doesn't work with integer arguments
        Assert.assertEquals(2, firstResult(four.multiply(one.divide(two))).intValue());
    }

    @Test
    public void arithmetic2() {
        NumberExpression<Integer> one = ONE;
        NumberExpression<Integer> two = TWO;
        NumberExpression<Integer> three = THREE;
        NumberExpression<Integer> four = FOUR;
        arithmeticTests(one, two, three, four);
    }

    @Test
    public void arithmetic_mod() {
        NumberExpression<Integer> one = Expressions.numberTemplate(Integer.class, "(1)");
        NumberExpression<Integer> two = Expressions.numberTemplate(Integer.class, "(2)");
        NumberExpression<Integer> three = Expressions.numberTemplate(Integer.class, "(3)");
        NumberExpression<Integer> four = Expressions.numberTemplate(Integer.class, "(4)");
        Assert.assertEquals(4, firstResult(four.mod(three).add(three)).intValue());
        Assert.assertEquals(1, firstResult(four.mod(two.add(one))).intValue());
        Assert.assertEquals(0, firstResult(four.mod(two.multiply(one))).intValue());
        Assert.assertEquals(2, firstResult(four.add(one).mod(three)).intValue());
    }

    // TODO generalize array literal projections
    @Test
    @IncludeIn(POSTGRESQL)
    public void array() {
        Expression<Integer[]> expr = Expressions.template(Integer[].class, "'{1,2,3}'::int[]");
        Integer[] result = firstResult(expr);
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(1, result[0].intValue());
        Assert.assertEquals(2, result[1].intValue());
        Assert.assertEquals(3, result[2].intValue());
    }

    // TODO generalize array literal projections
    @Test
    @IncludeIn(POSTGRESQL)
    public void array2() {
        Expression<int[]> expr = Expressions.template(int[].class, "'{1,2,3}'::int[]");
        int[] result = firstResult(expr);
        Assert.assertEquals(3, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(2, result[1]);
        Assert.assertEquals(3, result[2]);
    }

    @Test
    @ExcludeIn({ DERBY, HSQLDB })
    public void array_null() {
        Expression<Integer[]> expr = Expressions.template(Integer[].class, "null");
        Assert.assertNull(firstResult(expr));
    }

    @Test
    public void array_projection() {
        List<String[]> results = query().from(Constants.employee).select(new ArrayConstructorExpression<String>(String[].class, Constants.employee.firstname)).fetch();
        Assert.assertFalse(results.isEmpty());
        for (String[] result : results) {
            Assert.assertNotNull(result[0]);
        }
    }

    @Test
    public void beans() {
        List<Beans> rows = query().from(Constants.employee, Constants.employee2).select(new QBeans(Constants.employee, Constants.employee2)).fetch();
        Assert.assertFalse(rows.isEmpty());
        for (Beans row : rows) {
            Assert.assertEquals(Employee.class, row.get(Constants.employee).getClass());
            Assert.assertEquals(Employee.class, row.get(Constants.employee2).getClass());
        }
    }

    @Test
    public void between() {
        // 11-13
        Assert.assertEquals(ImmutableList.of(11, 12, 13), query().from(Constants.employee).where(Constants.employee.id.between(11, 13)).orderBy(Constants.employee.id.asc()).select(Constants.employee.id).fetch());
    }

    @Test
    @ExcludeIn({ ORACLE, CUBRID, FIREBIRD, DB2, DERBY, SQLSERVER, SQLITE, TERADATA })
    public void boolean_all() {
        Assert.assertTrue(query().from(Constants.employee).select(SQLExpressions.all(Constants.employee.firstname.isNotNull())).fetchOne());
    }

    @Test
    @ExcludeIn({ ORACLE, CUBRID, FIREBIRD, DB2, DERBY, SQLSERVER, SQLITE, TERADATA })
    public void boolean_any() {
        Assert.assertTrue(query().from(Constants.employee).select(SQLExpressions.any(Constants.employee.firstname.isNotNull())).fetchOne());
    }

    @Test
    public void case_() {
        NumberExpression<Float> numExpression = Constants.employee.salary.floatValue().divide(Constants.employee2.salary.floatValue()).multiply(100.1);
        NumberExpression<Float> numExpression2 = Constants.employee.id.when(0).then(0.0F).otherwise(numExpression);
        Assert.assertEquals(ImmutableList.of(87, 90, 88, 87, 83, 80, 75), query().from(Constants.employee, Constants.employee2).where(Constants.employee.id.eq(Constants.employee2.id.add(1))).orderBy(Constants.employee.id.asc(), Constants.employee2.id.asc()).select(numExpression2.floor().intValue()).fetch());
    }

    @Test
    public void casts() throws SQLException {
        NumberExpression<?> num = Constants.employee.id;
        List<Expression<?>> exprs = Lists.newArrayList();
        add(exprs, num.byteValue(), MYSQL);
        add(exprs, num.doubleValue());
        add(exprs, num.floatValue());
        add(exprs, num.intValue());
        add(exprs, num.longValue(), MYSQL);
        add(exprs, num.shortValue(), MYSQL);
        add(exprs, num.stringValue(), DERBY);
        for (Expression<?> expr : exprs) {
            for (Object o : query().from(Constants.employee).select(expr).fetch()) {
                Assert.assertEquals(expr.getType(), o.getClass());
            }
        }
    }

    @Test
    public void coalesce() {
        Coalesce<String> c = new Coalesce<String>(Constants.employee.firstname, Constants.employee.lastname).add("xxx");
        Assert.assertEquals(Arrays.asList(), query().from(Constants.employee).where(c.getValue().eq("xxx")).select(Constants.employee.id).fetch());
    }

    @Test
    public void compact_join() {
        // verbose
        Assert.assertEquals(8, query().from(Constants.employee).innerJoin(Constants.employee2).on(Constants.employee.superiorId.eq(Constants.employee2.id)).select(Constants.employee.id, Constants.employee2.id).fetch().size());
        // compact
        Assert.assertEquals(8, query().from(Constants.employee).innerJoin(Constants.employee.superiorIdKey, Constants.employee2).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    public void complex_boolean() {
        BooleanExpression first = Constants.employee.firstname.eq("Mike").and(Constants.employee.lastname.eq("Smith"));
        BooleanExpression second = Constants.employee.firstname.eq("Joe").and(Constants.employee.lastname.eq("Divis"));
        Assert.assertEquals(2, query().from(Constants.employee).where(first.or(second)).fetchCount());
        Assert.assertEquals(0, query().from(Constants.employee).where(Constants.employee.firstname.eq("Mike"), Constants.employee.lastname.eq("Smith").or(Constants.employee.firstname.eq("Joe")), Constants.employee.lastname.eq("Divis")).fetchCount());
    }

    @Test
    public void complex_subQuery() {
        // alias for the salary
        NumberPath<BigDecimal> sal = Expressions.numberPath(BigDecimal.class, "sal");
        // alias for the subquery
        PathBuilder<BigDecimal> sq = new PathBuilder<BigDecimal>(BigDecimal.class, "sq");
        // query execution
        query().from(query().from(Constants.employee).select(Constants.employee.salary.add(Constants.employee.salary).add(Constants.employee.salary).as(sal)).as(sq)).select(sq.get(sal).avg(), sq.get(sal).min(), sq.get(sal).max()).fetch();
    }

    @Test
    public void constructor_projection() {
        for (IdName idAndName : query().from(Constants.survey).select(new QIdName(Constants.survey.id, Constants.survey.name)).fetch()) {
            Assert.assertNotNull(idAndName);
            Assert.assertNotNull(idAndName.getId());
            Assert.assertNotNull(idAndName.getName());
        }
    }

    @Test
    public void constructor_projection2() {
        List<SimpleProjection> projections = query().from(Constants.employee).select(Projections.constructor(SimpleProjection.class, Constants.employee.firstname, Constants.employee.lastname)).fetch();
        Assert.assertFalse(projections.isEmpty());
        for (SimpleProjection projection : projections) {
            Assert.assertNotNull(projection);
        }
    }

    @Test
    public void count_with_pK() {
        Assert.assertEquals(10, query().from(Constants.employee).fetchCount());
    }

    @Test
    public void count_without_pK() {
        Assert.assertEquals(10, query().from(QEmployeeNoPK.employee).fetchCount());
    }

    @Test
    public void count2() {
        Assert.assertEquals(10, query().from(Constants.employee).select(count()).fetchFirst().intValue());
    }

    @Test
    @SkipForQuoted
    @ExcludeIn(ORACLE)
    public void count_all() {
        expectedQuery = "select count(*) as rc from EMPLOYEE e";
        NumberPath<Long> rowCount = Expressions.numberPath(Long.class, "rc");
        Assert.assertEquals(10, query().from(Constants.employee).select(count.as(rowCount)).fetchOne().intValue());
    }

    @Test
    @SkipForQuoted
    @IncludeIn(ORACLE)
    public void count_all_Oracle() {
        expectedQuery = "select count(*) rc from EMPLOYEE e";
        NumberPath<Long> rowCount = Expressions.numberPath(Long.class, "rc");
        Assert.assertEquals(10, query().from(Constants.employee).select(count.as(rowCount)).fetchOne().intValue());
    }

    @Test
    public void count_distinct_with_pK() {
        Assert.assertEquals(10, query().from(Constants.employee).distinct().fetchCount());
    }

    @Test
    public void count_distinct_without_pK() {
        Assert.assertEquals(10, query().from(QEmployeeNoPK.employee).distinct().fetchCount());
    }

    @Test
    public void count_distinct2() {
        query().from(Constants.employee).select(countDistinct()).fetchFirst();
    }

    @Test
    public void custom_projection() {
        List<Projection> tuples = query().from(Constants.employee).select(new QProjection(Constants.employee.firstname, Constants.employee.lastname)).fetch();
        Assert.assertFalse(tuples.isEmpty());
        for (Projection tuple : tuples) {
            Assert.assertNotNull(tuple.get(Constants.employee.firstname));
            Assert.assertNotNull(tuple.get(Constants.employee.lastname));
            Assert.assertNotNull(tuple.getExpr(Constants.employee.firstname));
            Assert.assertNotNull(tuple.getExpr(Constants.employee.lastname));
        }
    }

    @Test
    @ExcludeIn({ CUBRID, DB2, DERBY, HSQLDB, POSTGRESQL, SQLITE, TERADATA })
    public void dates() {
        long ts = ((long) (Math.floor(((System.currentTimeMillis()) / 1000)))) * 1000;
        long tsDate = new LocalDate(ts).toDateMidnight().getMillis();
        long tsTime = new LocalTime(ts).getMillisOfDay();
        List<Object> data = Lists.newArrayList();
        data.add(Constants.date);
        data.add(Constants.time);
        data.add(new Date(ts));
        data.add(new Date(tsDate));
        data.add(new Date(tsTime));
        data.add(new Timestamp(ts));
        data.add(new Timestamp(tsDate));
        data.add(new java.sql.Date(110, 0, 1));
        data.add(new java.sql.Date(tsDate));
        data.add(new Time(0, 0, 0));
        data.add(new Time(12, 30, 0));
        data.add(new Time(23, 59, 59));
        // data.add(new java.sql.Time(tsTime));
        data.add(new DateTime(ts));
        data.add(new DateTime(tsDate));
        data.add(new DateTime(tsTime));
        data.add(new LocalDateTime(ts));
        data.add(new LocalDateTime(tsDate));
        data.add(new LocalDateTime(2014, 3, 30, 2, 0));
        data.add(new LocalDate(2010, 1, 1));
        data.add(new LocalDate(ts));
        data.add(new LocalDate(tsDate));
        data.add(new LocalTime(0, 0, 0));
        data.add(new LocalTime(12, 30, 0));
        data.add(new LocalTime(23, 59, 59));
        data.add(new LocalTime(ts));
        data.add(new LocalTime(tsTime));
        Instant javaInstant = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        java.time.LocalDateTime javaDateTime = ofInstant(javaInstant, ZoneId.of("Z"));
        java.time.LocalDate javaDate = javaDateTime.toLocalDate();
        java.time.LocalTime javaTime = javaDateTime.toLocalTime();
        data.add(javaInstant);
        // java.time.Instant
        data.add(javaDateTime);
        // java.time.LocalDateTime
        data.add(javaDate);
        // java.time.LocalDate
        data.add(javaTime);
        // java.time.LocalTime
        data.add(javaDateTime.atOffset(ZoneOffset.UTC));// java.time.OffsetDateTime

        data.add(javaTime.atOffset(ZoneOffset.UTC));// java.time.OffsetTime

        data.add(javaDateTime.atZone(ZoneId.of("Z")));// java.time.ZonedDateTime

        Map<Object, Object> failures = Maps.newIdentityHashMap();
        for (Object dt : data) {
            Object dt2 = firstResult(Expressions.constant(dt));
            if (!(dt.equals(dt2))) {
                failures.put(dt, dt2);
            }
        }
        if (!(failures.isEmpty())) {
            for (Map.Entry<Object, Object> entry : failures.entrySet()) {
                System.out.println((((((entry.getKey().getClass().getName()) + ": ") + (entry.getKey())) + " != ") + (entry.getValue())));
            }
            Assert.fail(("Failed with " + failures));
        }
    }

    @Test
    @ExcludeIn({ CUBRID, SQLITE, TERADATA })
    public void dates_literals() {
        if (configuration.getUseLiterals()) {
            dates();
        }
    }

    @Test
    @ExcludeIn({ SQLITE })
    public void date_add() {
        SQLQuery<?> query = query().from(Constants.employee);
        java.sql.Date date1 = query.select(Constants.employee.datefield).fetchFirst();
        java.sql.Date date2 = query.select(SQLExpressions.addYears(Constants.employee.datefield, 1)).fetchFirst();
        java.sql.Date date3 = query.select(SQLExpressions.addMonths(Constants.employee.datefield, 1)).fetchFirst();
        java.sql.Date date4 = query.select(SQLExpressions.addDays(Constants.employee.datefield, 1)).fetchFirst();
        Assert.assertTrue(((date2.getTime()) > (date1.getTime())));
        Assert.assertTrue(((date3.getTime()) > (date1.getTime())));
        Assert.assertTrue(((date4.getTime()) > (date1.getTime())));
    }

    @Test
    @ExcludeIn({ SQLITE })
    public void date_add_Timestamp() {
        List<Expression<?>> exprs = Lists.newArrayList();
        DateTimeExpression<Date> dt = Expressions.currentTimestamp();
        add(exprs, SQLExpressions.addYears(dt, 1));
        add(exprs, SQLExpressions.addMonths(dt, 1));
        add(exprs, SQLExpressions.addDays(dt, 1));
        add(exprs, SQLExpressions.addHours(dt, 1), TERADATA);
        add(exprs, SQLExpressions.addMinutes(dt, 1), TERADATA);
        add(exprs, SQLExpressions.addSeconds(dt, 1), TERADATA);
        for (Expression<?> expr : exprs) {
            Assert.assertNotNull(firstResult(expr));
        }
    }

    @Test
    @ExcludeIn({ DB2, SQLITE, TERADATA })
    public void date_diff() {
        QEmployee employee2 = new QEmployee("employee2");
        SQLQuery<?> query = query().from(Constants.employee).orderBy(Constants.employee.id.asc());
        SQLQuery<?> query2 = query().from(Constants.employee, employee2).orderBy(Constants.employee.id.asc(), employee2.id.desc());
        List<DatePart> dps = Lists.newArrayList();
        add(dps, year);
        add(dps, month);
        add(dps, week);
        add(dps, day);
        add(dps, hour, HSQLDB);
        add(dps, minute, HSQLDB);
        add(dps, second, HSQLDB);
        LocalDate localDate = new LocalDate(1970, 1, 10);
        java.sql.Date date = new java.sql.Date(localDate.toDateMidnight().getMillis());
        for (DatePart dp : dps) {
            int diff1 = query.select(SQLExpressions.datediff(dp, date, Constants.employee.datefield)).fetchFirst();
            int diff2 = query.select(SQLExpressions.datediff(dp, Constants.employee.datefield, date)).fetchFirst();
            int diff3 = query2.select(SQLExpressions.datediff(dp, Constants.employee.datefield, employee2.datefield)).fetchFirst();
            Assert.assertEquals(diff1, (-diff2));
        }
    }

    // TDO Date_diff with timestamps
    @Test
    @ExcludeIn({ DB2, HSQLDB, SQLITE, TERADATA })
    public void date_diff2() {
        SQLQuery<?> query = query().from(Constants.employee).orderBy(Constants.employee.id.asc());
        LocalDate localDate = new LocalDate(1970, 1, 10);
        java.sql.Date date = new java.sql.Date(localDate.toDateMidnight().getMillis());
        int years = query.select(SQLExpressions.datediff(year, date, Constants.employee.datefield)).fetchFirst();
        int months = query.select(SQLExpressions.datediff(month, date, Constants.employee.datefield)).fetchFirst();
        // weeks
        int days = query.select(SQLExpressions.datediff(day, date, Constants.employee.datefield)).fetchFirst();
        int hours = query.select(SQLExpressions.datediff(hour, date, Constants.employee.datefield)).fetchFirst();
        int minutes = query.select(SQLExpressions.datediff(minute, date, Constants.employee.datefield)).fetchFirst();
        int seconds = query.select(SQLExpressions.datediff(second, date, Constants.employee.datefield)).fetchFirst();
        Assert.assertEquals(949363200, seconds);
        Assert.assertEquals(15822720, minutes);
        Assert.assertEquals(263712, hours);
        Assert.assertEquals(10988, days);
        Assert.assertEquals(361, months);
        Assert.assertEquals(30, years);
    }

    // FIXME
    @Test
    @ExcludeIn({ SQLITE })
    public void date_trunc() {
        DateTimeExpression<Date> expr = DateTimeExpression.currentTimestamp();
        List<DatePart> dps = Lists.newArrayList();
        add(dps, year);
        add(dps, month);
        add(dps, week, DERBY, FIREBIRD, SQLSERVER);
        add(dps, day);
        add(dps, hour);
        add(dps, minute);
        add(dps, second);
        for (DatePart dp : dps) {
            firstResult(SQLExpressions.datetrunc(dp, expr));
        }
    }

    // FIXME
    @Test
    @ExcludeIn({ SQLITE, TERADATA })
    public void date_trunc2() {
        DateTimeExpression<DateTime> expr = DateTimeExpression.currentTimestamp(DateTime.class);
        Tuple tuple = firstResult(expr, SQLExpressions.datetrunc(year, expr), SQLExpressions.datetrunc(month, expr), SQLExpressions.datetrunc(day, expr), SQLExpressions.datetrunc(hour, expr), SQLExpressions.datetrunc(minute, expr), SQLExpressions.datetrunc(second, expr));
        DateTime date = tuple.get(expr);
        DateTime toYear = tuple.get(SQLExpressions.datetrunc(year, expr));
        DateTime toMonth = tuple.get(SQLExpressions.datetrunc(month, expr));
        DateTime toDay = tuple.get(SQLExpressions.datetrunc(day, expr));
        DateTime toHour = tuple.get(SQLExpressions.datetrunc(hour, expr));
        DateTime toMinute = tuple.get(SQLExpressions.datetrunc(minute, expr));
        DateTime toSecond = tuple.get(SQLExpressions.datetrunc(second, expr));
        Assert.assertEquals(date.getZone(), toYear.getZone());
        Assert.assertEquals(date.getZone(), toMonth.getZone());
        Assert.assertEquals(date.getZone(), toDay.getZone());
        Assert.assertEquals(date.getZone(), toHour.getZone());
        Assert.assertEquals(date.getZone(), toMinute.getZone());
        Assert.assertEquals(date.getZone(), toSecond.getZone());
        // year
        Assert.assertEquals(date.getYear(), toYear.getYear());
        Assert.assertEquals(date.getYear(), toMonth.getYear());
        Assert.assertEquals(date.getYear(), toDay.getYear());
        Assert.assertEquals(date.getYear(), toHour.getYear());
        Assert.assertEquals(date.getYear(), toMinute.getYear());
        Assert.assertEquals(date.getYear(), toSecond.getYear());
        // month
        Assert.assertEquals(1, toYear.getMonthOfYear());
        Assert.assertEquals(date.getMonthOfYear(), toMonth.getMonthOfYear());
        Assert.assertEquals(date.getMonthOfYear(), toDay.getMonthOfYear());
        Assert.assertEquals(date.getMonthOfYear(), toHour.getMonthOfYear());
        Assert.assertEquals(date.getMonthOfYear(), toMinute.getMonthOfYear());
        Assert.assertEquals(date.getMonthOfYear(), toSecond.getMonthOfYear());
        // day
        Assert.assertEquals(1, toYear.getDayOfMonth());
        Assert.assertEquals(1, toMonth.getDayOfMonth());
        Assert.assertEquals(date.getDayOfMonth(), toDay.getDayOfMonth());
        Assert.assertEquals(date.getDayOfMonth(), toHour.getDayOfMonth());
        Assert.assertEquals(date.getDayOfMonth(), toMinute.getDayOfMonth());
        Assert.assertEquals(date.getDayOfMonth(), toSecond.getDayOfMonth());
        // hour
        Assert.assertEquals(0, toYear.getHourOfDay());
        Assert.assertEquals(0, toMonth.getHourOfDay());
        Assert.assertEquals(0, toDay.getHourOfDay());
        Assert.assertEquals(date.getHourOfDay(), toHour.getHourOfDay());
        Assert.assertEquals(date.getHourOfDay(), toMinute.getHourOfDay());
        Assert.assertEquals(date.getHourOfDay(), toSecond.getHourOfDay());
        // minute
        Assert.assertEquals(0, toYear.getMinuteOfHour());
        Assert.assertEquals(0, toMonth.getMinuteOfHour());
        Assert.assertEquals(0, toDay.getMinuteOfHour());
        Assert.assertEquals(0, toHour.getMinuteOfHour());
        Assert.assertEquals(date.getMinuteOfHour(), toMinute.getMinuteOfHour());
        Assert.assertEquals(date.getMinuteOfHour(), toSecond.getMinuteOfHour());
        // second
        Assert.assertEquals(0, toYear.getSecondOfMinute());
        Assert.assertEquals(0, toMonth.getSecondOfMinute());
        Assert.assertEquals(0, toDay.getSecondOfMinute());
        Assert.assertEquals(0, toHour.getSecondOfMinute());
        Assert.assertEquals(0, toMinute.getSecondOfMinute());
        Assert.assertEquals(date.getSecondOfMinute(), toSecond.getSecondOfMinute());
    }

    @Test
    public void dateTime() {
        SQLQuery<?> query = query().from(Constants.employee).orderBy(Constants.employee.id.asc());
        Assert.assertEquals(Integer.valueOf(10), query.select(Constants.employee.datefield.dayOfMonth()).fetchFirst());
        Assert.assertEquals(Integer.valueOf(2), query.select(Constants.employee.datefield.month()).fetchFirst());
        Assert.assertEquals(Integer.valueOf(2000), query.select(Constants.employee.datefield.year()).fetchFirst());
        Assert.assertEquals(Integer.valueOf(200002), query.select(Constants.employee.datefield.yearMonth()).fetchFirst());
    }

    @Test
    public void dateTime_to_date() {
        firstResult(SQLExpressions.date(DateTimeExpression.currentTimestamp()));
    }

    @Test
    public void distinct_count() {
        long count1 = query().from(Constants.employee).distinct().fetchCount();
        long count2 = query().from(Constants.employee).distinct().fetchCount();
        Assert.assertEquals(count1, count2);
    }

    @Test
    public void distinct_list() {
        List<Integer> lengths1 = query().from(Constants.employee).distinct().select(Constants.employee.firstname.length()).fetch();
        List<Integer> lengths2 = query().from(Constants.employee).distinct().select(Constants.employee.firstname.length()).fetch();
        Assert.assertEquals(lengths1, lengths2);
    }

    @Test
    public void duplicate_columns() {
        Assert.assertEquals(10, query().from(Constants.employee).select(Constants.employee.id, Constants.employee.id).fetch().size());
    }

    @Test
    public void duplicate_columns_In_Subquery() {
        QEmployee employee2 = new QEmployee("e2");
        Assert.assertEquals(10, query().from(Constants.employee).where(query().from(employee2).where(employee2.id.eq(Constants.employee.id)).select(employee2.id, employee2.id).exists()).fetchCount());
    }

    @Test
    public void factoryExpression_in_groupBy() {
        Expression<Employee> empBean = Projections.bean(Employee.class, Constants.employee.id, Constants.employee.superiorId);
        Assert.assertTrue(((query().from(Constants.employee).groupBy(empBean).select(empBean).fetchFirst()) != null));
    }

    @Test
    @ExcludeIn({ H2, SQLITE, DERBY, CUBRID, MYSQL })
    public void full_join() throws SQLException {
        Assert.assertEquals(18, query().from(Constants.employee).fullJoin(Constants.employee2).on(Constants.employee.superiorIdKey.on(Constants.employee2)).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    public void getResultSet() throws IOException, SQLException {
        ResultSet results = query().select(Constants.survey.id, Constants.survey.name).from(Constants.survey).getResults();
        while (results.next()) {
            Assert.assertNotNull(results.getObject(1));
            Assert.assertNotNull(results.getObject(2));
        } 
        results.close();
    }

    @Test
    public void groupBy_superior() {
        SQLQuery<?> qry = query().from(Constants.employee).innerJoin(Constants.employee._superiorIdKey, Constants.employee2);
        QTuple subordinates = Projections.tuple(Constants.employee2.id, Constants.employee2.firstname, Constants.employee2.lastname);
        Map<Integer, Group> results = qry.transform(GroupBy.groupBy(Constants.employee.id).as(Constants.employee.firstname, Constants.employee.lastname, GroupBy.map(Constants.employee2.id, subordinates)));
        Assert.assertEquals(2, results.size());
        // Mike Smith
        Group group = results.get(1);
        Assert.assertEquals("Mike", group.getOne(Constants.employee.firstname));
        Assert.assertEquals("Smith", group.getOne(Constants.employee.lastname));
        Map<Integer, Tuple> emps = group.getMap(Constants.employee2.id, subordinates);
        Assert.assertEquals(4, emps.size());
        Assert.assertEquals("Steve", emps.get(12).get(Constants.employee2.firstname));
        // Mary Smith
        group = results.get(2);
        Assert.assertEquals("Mary", group.getOne(Constants.employee.firstname));
        Assert.assertEquals("Smith", group.getOne(Constants.employee.lastname));
        emps = group.getMap(Constants.employee2.id, subordinates);
        Assert.assertEquals(4, emps.size());
        Assert.assertEquals("Mason", emps.get(21).get(Constants.employee2.lastname));
    }

    @Test
    public void groupBy_yearMonth() {
        Assert.assertEquals(Arrays.asList(10L), query().from(Constants.employee).groupBy(Constants.employee.datefield.yearMonth()).orderBy(Constants.employee.datefield.yearMonth().asc()).select(Constants.employee.id.count()).fetch());
    }

    @Test
    @ExcludeIn({ H2, DB2, DERBY, ORACLE, SQLSERVER })
    public void groupBy_validate() {
        NumberPath<BigDecimal> alias = Expressions.numberPath(BigDecimal.class, "alias");
        Assert.assertEquals(8, query().from(Constants.employee).groupBy(alias).select(Constants.employee.salary.multiply(100).as(alias), Constants.employee.salary.avg()).fetch().size());
    }

    @Test
    @ExcludeIn({ FIREBIRD })
    public void groupBy_count() {
        List<Integer> ids = query().from(Constants.employee).groupBy(Constants.employee.id).select(Constants.employee.id).fetch();
        long count = query().from(Constants.employee).groupBy(Constants.employee.id).fetchCount();
        QueryResults<Integer> results = query().from(Constants.employee).groupBy(Constants.employee.id).limit(1).select(Constants.employee.id).fetchResults();
        Assert.assertEquals(10, ids.size());
        Assert.assertEquals(10, count);
        Assert.assertEquals(1, results.getResults().size());
        Assert.assertEquals(10, results.getTotal());
    }

    @Test
    @ExcludeIn({ FIREBIRD, SQLSERVER, TERADATA })
    public void groupBy_Distinct_count() {
        List<Integer> ids = query().from(Constants.employee).groupBy(Constants.employee.id).distinct().select(ONE).fetch();
        QueryResults<Integer> results = query().from(Constants.employee).groupBy(Constants.employee.id).limit(1).distinct().select(ONE).fetchResults();
        Assert.assertEquals(1, ids.size());
        Assert.assertEquals(1, results.getResults().size());
        Assert.assertEquals(1, results.getTotal());
    }

    @Test
    @ExcludeIn({ FIREBIRD })
    public void having_count() {
        // Produces empty resultset https://github.com/querydsl/querydsl/issues/1055
        query().from(Constants.employee).innerJoin(Constants.employee2).on(Constants.employee.id.eq(Constants.employee2.id)).groupBy(Constants.employee.id).having(count.eq(4L)).select(Constants.employee.id, Constants.employee.firstname).fetchResults();
    }

    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void illegalUnion() throws SQLException {
        SubQueryExpression<Integer> sq1 = query().from(Constants.employee).select(Constants.employee.id.max());
        SubQueryExpression<Integer> sq2 = query().from(Constants.employee).select(Constants.employee.id.max());
        Assert.assertEquals(0, query().from(Constants.employee).union(sq1, sq2).list().size());
    }

    @Test
    public void in() {
        Assert.assertEquals(2, query().from(Constants.employee).where(Constants.employee.id.in(Arrays.asList(1, 2))).select(Constants.employee).fetch().size());
    }

    @Test
    @ExcludeIn({ DERBY, FIREBIRD, SQLITE, SQLSERVER, TERADATA })
    public void in_long_list() {
        List<Integer> ids = Lists.newArrayList();
        for (int i = 0; i < 20000; i++) {
            ids.add(i);
        }
        Assert.assertEquals(query().from(Constants.employee).fetchCount(), query().from(Constants.employee).where(Constants.employee.id.in(ids)).fetchCount());
    }

    @Test
    @ExcludeIn({ DERBY, FIREBIRD, SQLITE, SQLSERVER, TERADATA })
    public void notIn_long_list() {
        List<Integer> ids = Lists.newArrayList();
        for (int i = 0; i < 20000; i++) {
            ids.add(i);
        }
        Assert.assertEquals(0, query().from(Constants.employee).where(Constants.employee.id.notIn(ids)).fetchCount());
    }

    @Test
    public void in_empty() {
        Assert.assertEquals(0, query().from(Constants.employee).where(Constants.employee.id.in(ImmutableList.<Integer>of())).fetchCount());
    }

    @Test
    @ExcludeIn(DERBY)
    public void in_null() {
        Assert.assertEquals(1, query().from(Constants.employee).where(Constants.employee.id.in(1, null)).fetchCount());
    }

    @Test
    @ExcludeIn({ MYSQL, TERADATA })
    public void in_subqueries() {
        QEmployee e1 = new QEmployee("e1");
        QEmployee e2 = new QEmployee("e2");
        Assert.assertEquals(2, query().from(Constants.employee).where(Constants.employee.id.in(query().from(e1).where(e1.firstname.eq("Mike")).select(e1.id), query().from(e2).where(e2.firstname.eq("Mary")).select(e2.id))).fetchCount());
    }

    @Test
    public void notIn_empty() {
        long count = query().from(Constants.employee).fetchCount();
        Assert.assertEquals(count, query().from(Constants.employee).where(Constants.employee.id.notIn(ImmutableList.<Integer>of())).fetchCount());
    }

    @Test
    public void inner_join() throws SQLException {
        Assert.assertEquals(8, query().from(Constants.employee).innerJoin(Constants.employee2).on(Constants.employee.superiorIdKey.on(Constants.employee2)).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    public void inner_join_2Conditions() {
        Assert.assertEquals(8, query().from(Constants.employee).innerJoin(Constants.employee2).on(Constants.employee.superiorIdKey.on(Constants.employee2)).on(Constants.employee2.firstname.isNotNull()).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    public void join() throws Exception {
        for (String name : query().from(Constants.survey, Constants.survey2).where(Constants.survey.id.eq(Constants.survey2.id)).select(Constants.survey.name).fetch()) {
            Assert.assertNotNull(name);
        }
    }

    @Test
    public void joins() throws SQLException {
        for (Tuple row : query().from(Constants.employee).innerJoin(Constants.employee2).on(Constants.employee.superiorId.eq(Constants.employee2.superiorId)).where(Constants.employee2.id.eq(10)).select(Constants.employee.id, Constants.employee2.id).fetch()) {
            Assert.assertNotNull(row.get(Constants.employee.id));
            Assert.assertNotNull(row.get(Constants.employee2.id));
        }
    }

    @Test
    public void left_join() throws SQLException {
        Assert.assertEquals(10, query().from(Constants.employee).leftJoin(Constants.employee2).on(Constants.employee.superiorIdKey.on(Constants.employee2)).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    public void like() {
        Assert.assertEquals(0, query().from(Constants.employee).where(Constants.employee.firstname.like("\\")).fetchCount());
        Assert.assertEquals(0, query().from(Constants.employee).where(Constants.employee.firstname.like("\\\\")).fetchCount());
    }

    @Test
    public void like_ignore_case() {
        Assert.assertEquals(3, query().from(Constants.employee).where(Constants.employee.firstname.likeIgnoreCase("%m%")).fetchCount());
    }

    @Test
    @ExcludeIn(FIREBIRD)
    public void like_escape() {
        List<String> strs = ImmutableList.of("%a", "a%", "%a%", "_a", "a_", "_a_", "[C-P]arsen", "a\nb");
        for (String str : strs) {
            Assert.assertTrue(str, ((query().from(Constants.employee).where(Expressions.predicate(STRING_CONTAINS, Expressions.constant(str), Expressions.constant(str))).fetchCount()) > 0));
        }
    }

    @Test
    @ExcludeIn({ DB2, DERBY })
    public void like_number() {
        Assert.assertEquals(5, query().from(Constants.employee).where(Constants.employee.id.like("1%")).fetchCount());
    }

    @Test
    public void limit() throws SQLException {
        Assert.assertEquals(Arrays.asList(23, 22, 21, 20), query().from(Constants.employee).orderBy(Constants.employee.firstname.asc()).limit(4).select(Constants.employee.id).fetch());
    }

    @Test
    public void limit_and_offset() throws SQLException {
        Assert.assertEquals(Arrays.asList(20, 13, 10, 2), query().from(Constants.employee).orderBy(Constants.employee.firstname.asc()).limit(4).offset(3).select(Constants.employee.id).fetch());
    }

    @Test
    public void limit_and_offset_Group() {
        Assert.assertEquals(9, query().from(Constants.employee).orderBy(Constants.employee.id.asc()).limit(100).offset(1).transform(GroupBy.groupBy(Constants.employee.id).as(Constants.employee)).size());
    }

    @Test
    public void limit_and_offset_and_Order() {
        List<String> names2 = Arrays.asList("Helen", "Jennifer", "Jim", "Joe");
        Assert.assertEquals(names2, query().from(Constants.employee).orderBy(Constants.employee.firstname.asc()).limit(4).offset(2).select(Constants.employee.firstname).fetch());
    }

    @Test
    @IncludeIn(DERBY)
    public void limit_and_offset_In_Derby() throws SQLException {
        expectedQuery = "select e.ID from EMPLOYEE e offset 3 rows fetch next 4 rows only";
        query().from(Constants.employee).limit(4).offset(3).select(Constants.employee.id).fetch();
        // limit
        expectedQuery = "select e.ID from EMPLOYEE e fetch first 4 rows only";
        query().from(Constants.employee).limit(4).select(Constants.employee.id).fetch();
        // offset
        expectedQuery = "select e.ID from EMPLOYEE e offset 3 rows";
        query().from(Constants.employee).offset(3).select(Constants.employee.id).fetch();
    }

    @Test
    @IncludeIn(ORACLE)
    @SkipForQuoted
    public void limit_and_offset_In_Oracle() throws SQLException {
        if (configuration.getUseLiterals()) {
            return;
        }
        // limit
        expectedQuery = "select * from (   select e.ID from EMPLOYEE e ) where rownum <= ?";
        query().from(Constants.employee).limit(4).select(Constants.employee.id).fetch();
        // offset
        expectedQuery = "select * from (  select a.*, rownum rn from (   select e.ID from EMPLOYEE e  ) a) where rn > ?";
        query().from(Constants.employee).offset(3).select(Constants.employee.id).fetch();
        // limit offset
        expectedQuery = "select * from (  select a.*, rownum rn from (   select e.ID from EMPLOYEE e  ) a) where rn > 3 and rownum <= 4";
        query().from(Constants.employee).limit(4).offset(3).select(Constants.employee.id).fetch();
    }

    @Test
    @ExcludeIn({ ORACLE, DB2, DERBY, FIREBIRD, SQLSERVER, CUBRID, TERADATA })
    @SkipForQuoted
    public void limit_and_offset2() throws SQLException {
        // limit
        expectedQuery = "select e.ID from EMPLOYEE e limit ?";
        query().from(Constants.employee).limit(4).select(Constants.employee.id).fetch();
        // limit offset
        expectedQuery = "select e.ID from EMPLOYEE e limit ? offset ?";
        query().from(Constants.employee).limit(4).offset(3).select(Constants.employee.id).fetch();
    }

    @Test
    public void limit_and_order() {
        List<String> names1 = Arrays.asList("Barbara", "Daisy", "Helen", "Jennifer");
        Assert.assertEquals(names1, query().from(Constants.employee).orderBy(Constants.employee.firstname.asc()).limit(4).select(Constants.employee.firstname).fetch());
    }

    @Test
    public void listResults() {
        QueryResults<Integer> results = query().from(Constants.employee).limit(10).offset(1).orderBy(Constants.employee.id.asc()).select(Constants.employee.id).fetchResults();
        Assert.assertEquals(10, results.getTotal());
    }

    @Test
    public void listResults2() {
        QueryResults<Integer> results = query().from(Constants.employee).limit(2).offset(10).orderBy(Constants.employee.id.asc()).select(Constants.employee.id).fetchResults();
        Assert.assertEquals(10, results.getTotal());
    }

    @Test
    public void listResults_factoryExpression() {
        QueryResults<Employee> results = query().from(Constants.employee).limit(10).offset(1).orderBy(Constants.employee.id.asc()).select(Constants.employee).fetchResults();
        Assert.assertEquals(10, results.getTotal());
    }

    @Test
    @ExcludeIn({ DB2, DERBY })
    public void literals() {
        Assert.assertEquals(1L, firstResult(ConstantImpl.create(1)).intValue());
        Assert.assertEquals(2L, firstResult(ConstantImpl.create(2L)).longValue());
        Assert.assertEquals(3.0, firstResult(ConstantImpl.create(3.0)), 0.001);
        Assert.assertEquals(4.0F, firstResult(ConstantImpl.create(4.0F)), 0.001);
        Assert.assertEquals(true, firstResult(ConstantImpl.create(true)));
        Assert.assertEquals(false, firstResult(ConstantImpl.create(false)));
        Assert.assertEquals("abc", firstResult(ConstantImpl.create("abc")));
        Assert.assertEquals("'", firstResult(ConstantImpl.create("'")));
        Assert.assertEquals("\"", firstResult(ConstantImpl.create("\"")));
        Assert.assertEquals("\n", firstResult(ConstantImpl.create("\n")));
        Assert.assertEquals("\r\n", firstResult(ConstantImpl.create("\r\n")));
        Assert.assertEquals("\t", firstResult(ConstantImpl.create("\t")));
    }

    @Test
    public void literals_literals() {
        if (configuration.getUseLiterals()) {
            literals();
        }
    }

    @Test
    @ExcludeIn({ SQLITE, DERBY })
    public void lPad() {
        Assert.assertEquals("  ab", firstResult(StringExpressions.lpad(ConstantImpl.create("ab"), 4)));
        Assert.assertEquals("!!ab", firstResult(StringExpressions.lpad(ConstantImpl.create("ab"), 4, '!')));
    }

    // @Test
    // public void map() {
    // Map<Integer, String> idToName = query().from(employee).map(employee.id.as("id"), employee.firstname);
    // for (Map.Entry<Integer, String> entry : idToName.entrySet()) {
    // assertNotNull(entry.getKey());
    // assertNotNull(entry.getValue());
    // }
    // }
    @Test
    @SuppressWarnings("serial")
    public void mappingProjection() {
        List<Pair<String, String>> pairs = query().from(Constants.employee).select(new MappingProjection<Pair<String, String>>(Pair.class, Constants.employee.firstname, Constants.employee.lastname) {
            @Override
            protected Pair<String, String> map(Tuple row) {
                return Pair.of(row.get(Constants.employee.firstname), row.get(Constants.employee.lastname));
            }
        }).fetch();
        for (Pair<String, String> pair : pairs) {
            Assert.assertNotNull(pair.getFirst());
            Assert.assertNotNull(pair.getSecond());
        }
    }

    @Test
    public void math() {
        math(Expressions.numberTemplate(Double.class, "0.50"));
    }

    // FIXME
    @Test
    @ExcludeIn(FIREBIRD)
    public void math2() {
        math(Expressions.constant(0.5));
    }

    // Derby doesn't support mod with decimal operands
    @Test
    @ExcludeIn(DERBY)
    public void math3() {
        // 1.0 + 2.0 * 3.0 - 4.0 / 5.0 + 6.0 % 3.0
        NumberTemplate<Double> one = Expressions.numberTemplate(Double.class, "1.0");
        NumberTemplate<Double> two = Expressions.numberTemplate(Double.class, "2.0");
        NumberTemplate<Double> three = Expressions.numberTemplate(Double.class, "3.0");
        NumberTemplate<Double> four = Expressions.numberTemplate(Double.class, "4.0");
        NumberTemplate<Double> five = Expressions.numberTemplate(Double.class, "5.0");
        NumberTemplate<Double> six = Expressions.numberTemplate(Double.class, "6.0");
        Double num = query().select(one.add(two.multiply(three)).subtract(four.divide(five)).add(six.mod(three))).fetchFirst();
        Assert.assertEquals(6.2, num, 0.001);
    }

    @Test
    public void nested_tuple_projection() {
        Concatenation concat = new Concatenation(Constants.employee.firstname, Constants.employee.lastname);
        List<Tuple> tuples = query().from(Constants.employee).select(Constants.employee.firstname, Constants.employee.lastname, concat).fetch();
        Assert.assertFalse(tuples.isEmpty());
        for (Tuple tuple : tuples) {
            String firstName = tuple.get(Constants.employee.firstname);
            String lastName = tuple.get(Constants.employee.lastname);
            Assert.assertEquals((firstName + lastName), tuple.get(concat));
        }
    }

    @Test
    public void no_from() {
        Assert.assertNotNull(firstResult(DateExpression.currentDate()));
    }

    @Test
    public void nullif() {
        query().from(Constants.employee).select(Constants.employee.firstname.nullif(Constants.employee.lastname)).fetch();
    }

    @Test
    public void nullif_constant() {
        query().from(Constants.employee).select(Constants.employee.firstname.nullif("xxx")).fetch();
    }

    @Test
    public void num_cast() {
        query().from(Constants.employee).select(Constants.employee.id.castToNum(Long.class)).fetch();
        query().from(Constants.employee).select(Constants.employee.id.castToNum(Float.class)).fetch();
        query().from(Constants.employee).select(Constants.employee.id.castToNum(Double.class)).fetch();
    }

    @Test
    public void num_cast2() {
        NumberExpression<Integer> num = Expressions.numberTemplate(Integer.class, "0");
        firstResult(num.castToNum(Byte.class));
        firstResult(num.castToNum(Short.class));
        firstResult(num.castToNum(Integer.class));
        firstResult(num.castToNum(Long.class));
        firstResult(num.castToNum(Float.class));
        firstResult(num.castToNum(Double.class));
    }

    @Test
    public void num_date_operation() {
        long result = query().select(Constants.employee.datefield.year().mod(1)).from(Constants.employee).fetchFirst();
        Assert.assertEquals(0, result);
    }

    @Test
    @ExcludeIn({ DERBY, FIREBIRD, POSTGRESQL })
    public void number_as_boolean() {
        QNumberTest numberTest = QNumberTest.numberTest;
        delete(numberTest).execute();
        insert(numberTest).set(numberTest.col1Boolean, true).execute();
        insert(numberTest).set(numberTest.col1Number, ((byte) (1))).execute();
        Assert.assertEquals(2, query().from(numberTest).select(numberTest.col1Boolean).fetch().size());
        Assert.assertEquals(2, query().from(numberTest).select(numberTest.col1Number).fetch().size());
    }

    @Test
    public void number_as_boolean_Null() {
        QNumberTest numberTest = QNumberTest.numberTest;
        delete(numberTest).execute();
        insert(numberTest).setNull(numberTest.col1Boolean).execute();
        insert(numberTest).setNull(numberTest.col1Number).execute();
        Assert.assertEquals(2, query().from(numberTest).select(numberTest.col1Boolean).fetch().size());
        Assert.assertEquals(2, query().from(numberTest).select(numberTest.col1Number).fetch().size());
    }

    @Test
    public void offset_only() {
        Assert.assertEquals(Arrays.asList(20, 13, 10, 2, 1, 11, 12), query().from(Constants.employee).orderBy(Constants.employee.firstname.asc()).offset(3).select(Constants.employee.id).fetch());
    }

    @Test
    public void operation_in_constant_list() {
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.charAt(0).in(Arrays.asList('a'))).fetchCount());
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.charAt(0).in(Arrays.asList('a', 'b'))).fetchCount());
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.charAt(0).in(Arrays.asList('a', 'b', 'c'))).fetchCount());
    }

    @Test
    public void order_nullsFirst() {
        Assert.assertEquals(Arrays.asList("Hello World"), query().from(Constants.survey).orderBy(Constants.survey.name.asc().nullsFirst()).select(Constants.survey.name).fetch());
    }

    @Test
    public void order_nullsLast() {
        Assert.assertEquals(Arrays.asList("Hello World"), query().from(Constants.survey).orderBy(Constants.survey.name.asc().nullsLast()).select(Constants.survey.name).fetch());
    }

    @Test
    public void params() {
        Param<String> name = new Param<String>(String.class, "name");
        Assert.assertEquals("Mike", query().from(Constants.employee).where(Constants.employee.firstname.eq(name)).set(name, "Mike").select(Constants.employee.firstname).fetchFirst());
    }

    @Test
    public void params_anon() {
        Param<String> name = new Param<String>(String.class);
        Assert.assertEquals("Mike", query().from(Constants.employee).where(Constants.employee.firstname.eq(name)).set(name, "Mike").select(Constants.employee.firstname).fetchFirst());
    }

    @Test(expected = ParamNotSetException.class)
    public void params_not_set() {
        Param<String> name = new Param<String>(String.class, "name");
        Assert.assertEquals("Mike", query().from(Constants.employee).where(Constants.employee.firstname.eq(name)).select(Constants.employee.firstname).fetchFirst());
    }

    @Test
    @ExcludeIn({ DB2, DERBY, FIREBIRD, HSQLDB, ORACLE, SQLSERVER })
    @SkipForQuoted
    public void path_alias() {
        expectedQuery = "select e.LASTNAME, sum(e.SALARY) as salarySum " + ("from EMPLOYEE e " + "group by e.LASTNAME having salarySum > ?");
        NumberExpression<BigDecimal> salarySum = Constants.employee.salary.sum().as("salarySum");
        query().from(Constants.employee).groupBy(Constants.employee.lastname).having(salarySum.gt(10000)).select(Constants.employee.lastname, salarySum).fetch();
    }

    @Test
    public void path_in_constant_list() {
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.in(Arrays.asList("a"))).fetchCount());
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.in(Arrays.asList("a", "b"))).fetchCount());
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.in(Arrays.asList("a", "b", "c"))).fetchCount());
    }

    @Test
    public void precedence() {
        StringPath fn = Constants.employee.firstname;
        StringPath ln = Constants.employee.lastname;
        Predicate where = fn.eq("Mike").and(ln.eq("Smith")).or(fn.eq("Joe").and(ln.eq("Divis")));
        Assert.assertEquals(2L, query().from(Constants.employee).where(where).fetchCount());
    }

    @Test
    public void precedence2() {
        StringPath fn = Constants.employee.firstname;
        StringPath ln = Constants.employee.lastname;
        Predicate where = fn.eq("Mike").and(ln.eq("Smith").or(fn.eq("Joe")).and(ln.eq("Divis")));
        Assert.assertEquals(0L, query().from(Constants.employee).where(where).fetchCount());
    }

    @Test
    public void projection() throws IOException {
        CloseableIterator<Tuple> results = query().from(Constants.survey).select(Constants.survey.all()).iterate();
        Assert.assertTrue(results.hasNext());
        while (results.hasNext()) {
            Assert.assertEquals(3, results.next().size());
        } 
        results.close();
    }

    @Test
    public void projection_and_twoColumns() {
        // projection and two columns
        for (Tuple row : query().from(Constants.survey).select(new QIdName(Constants.survey.id, Constants.survey.name), Constants.survey.id, Constants.survey.name).fetch()) {
            Assert.assertEquals(3, row.size());
            Assert.assertEquals(IdName.class, row.get(0, Object.class).getClass());
            Assert.assertEquals(Integer.class, row.get(1, Object.class).getClass());
            Assert.assertEquals(String.class, row.get(2, Object.class).getClass());
        }
    }

    @Test
    public void projection2() throws IOException {
        CloseableIterator<Tuple> results = query().from(Constants.survey).select(Constants.survey.id, Constants.survey.name).iterate();
        Assert.assertTrue(results.hasNext());
        while (results.hasNext()) {
            Assert.assertEquals(2, results.next().size());
        } 
        results.close();
    }

    @Test
    public void projection3() throws IOException {
        CloseableIterator<String> names = query().from(Constants.survey).select(Constants.survey.name).iterate();
        Assert.assertTrue(names.hasNext());
        while (names.hasNext()) {
            System.out.println(names.next());
        } 
        names.close();
    }

    @Test
    public void qBeanUsage() {
        PathBuilder<Object[]> sq = new PathBuilder<Object[]>(Object[].class, "sq");
        List<Survey> surveys = query().from(query().from(Constants.survey).select(Constants.survey.all()).as("sq")).select(Projections.bean(Survey.class, Collections.singletonMap("name", sq.get(Constants.survey.name)))).fetch();
        Assert.assertFalse(surveys.isEmpty());
    }

    @Test
    public void query_with_constant() throws Exception {
        for (Tuple row : query().from(Constants.survey).where(Constants.survey.id.eq(1)).select(Constants.survey.id, Constants.survey.name).fetch()) {
            Assert.assertNotNull(row.get(Constants.survey.id));
            Assert.assertNotNull(row.get(Constants.survey.name));
        }
    }

    @Test
    public void query1() throws Exception {
        for (String s : query().from(Constants.survey).select(Constants.survey.name).fetch()) {
            Assert.assertNotNull(s);
        }
    }

    @Test
    public void query2() throws Exception {
        for (Tuple row : query().from(Constants.survey).select(Constants.survey.id, Constants.survey.name).fetch()) {
            Assert.assertNotNull(row.get(Constants.survey.id));
            Assert.assertNotNull(row.get(Constants.survey.name));
        }
    }

    @Test
    public void random() {
        firstResult(MathExpressions.random());
    }

    @Test
    @ExcludeIn({ FIREBIRD, ORACLE, POSTGRESQL, SQLITE, TERADATA })
    public void random2() {
        firstResult(MathExpressions.random(10));
    }

    @Test
    public void relationalPath_projection() {
        List<Tuple> results = query().from(Constants.employee, Constants.employee2).where(Constants.employee.id.eq(Constants.employee2.id)).select(Constants.employee, Constants.employee2).fetch();
        Assert.assertFalse(results.isEmpty());
        for (Tuple row : results) {
            Employee e1 = row.get(Constants.employee);
            Employee e2 = row.get(Constants.employee2);
            Assert.assertEquals(e1.getId(), e2.getId());
        }
    }

    @Test
    public void relationalPath_eq() {
        Assert.assertEquals(10, query().from(Constants.employee, Constants.employee2).where(Constants.employee.eq(Constants.employee2)).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    public void relationalPath_ne() {
        Assert.assertEquals(90, query().from(Constants.employee, Constants.employee2).where(Constants.employee.ne(Constants.employee2)).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    public void relationalPath_eq2() {
        Assert.assertEquals(1, query().from(Constants.survey, Constants.survey2).where(eq(Constants.survey2)).select(Constants.survey.id, Constants.survey2.id).fetch().size());
    }

    @Test
    public void relationalPath_ne2() {
        Assert.assertEquals(0, query().from(Constants.survey, Constants.survey2).where(ne(Constants.survey2)).select(Constants.survey.id, Constants.survey2.id).fetch().size());
    }

    @Test
    @ExcludeIn(SQLITE)
    public void right_join() throws SQLException {
        Assert.assertEquals(16, query().from(Constants.employee).rightJoin(Constants.employee2).on(Constants.employee.superiorIdKey.on(Constants.employee2)).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    @ExcludeIn(DERBY)
    public void round() {
        Expression<Double> expr = Expressions.numberTemplate(Double.class, "1.32");
        Assert.assertEquals(Double.valueOf(1.0), firstResult(MathExpressions.round(expr)));
        Assert.assertEquals(Double.valueOf(1.3), firstResult(MathExpressions.round(expr, 1)));
    }

    @Test
    @ExcludeIn({ SQLITE, DERBY })
    public void rpad() {
        Assert.assertEquals("ab  ", firstResult(StringExpressions.rpad(ConstantImpl.create("ab"), 4)));
        Assert.assertEquals("ab!!", firstResult(StringExpressions.rpad(ConstantImpl.create("ab"), 4, '!')));
    }

    @Test
    public void select_booleanExpr3() {
        Assert.assertTrue(query().select(TRUE).fetchFirst());
        Assert.assertFalse(query().select(FALSE).fetchFirst());
    }

    @Test
    public void select_concat() throws SQLException {
        for (Tuple row : query().from(Constants.survey).select(Constants.survey.name, Constants.survey.name.append("Hello World")).fetch()) {
            Assert.assertEquals(((row.get(Constants.survey.name)) + "Hello World"), row.get(Constants.survey.name.append("Hello World")));
        }
    }

    @Test
    @ExcludeIn({ SQLITE, CUBRID, TERADATA })
    public void select_for_update() {
        Assert.assertEquals(1, query().from(Constants.survey).forUpdate().select(Constants.survey.id).fetch().size());
    }

    @Test
    @ExcludeIn({ SQLITE, CUBRID, TERADATA })
    public void select_for_update_Where() {
        Assert.assertEquals(1, query().from(Constants.survey).forUpdate().where(Constants.survey.id.isNotNull()).select(Constants.survey.id).fetch().size());
    }

    @Test
    @ExcludeIn({ SQLITE, CUBRID, TERADATA })
    public void select_for_update_UniqueResult() {
        query().from(Constants.survey).forUpdate().select(Constants.survey.id).fetchOne();
    }

    @Test
    public void select_for_share() {
        if (configuration.getTemplates().isForShareSupported()) {
            Assert.assertEquals(1, query().from(Constants.survey).forShare().where(Constants.survey.id.isNotNull()).select(Constants.survey.id).fetch().size());
        } else {
            try {
                query().from(Constants.survey).forShare().where(Constants.survey.id.isNotNull()).select(Constants.survey.id).fetch().size();
                Assert.fail();
            } catch (QueryException e) {
                Assert.assertTrue(e.getMessage().equals("Using forShare() is not supported"));
            }
        }
    }

    @Test
    @SkipForQuoted
    public void serialization() {
        SQLQuery<?> query = query();
        query.from(Constants.survey);
        Assert.assertEquals("from SURVEY s", query.toString());
        query.from(Constants.survey2);
        Assert.assertEquals("from SURVEY s, SURVEY s2", query.toString());
    }

    @Test
    public void serialization2() throws Exception {
        List<Tuple> rows = query().from(Constants.survey).select(Constants.survey.id, Constants.survey.name).fetch();
        serialize(rows);
    }

    @Test
    public void single() {
        Assert.assertNotNull(query().from(Constants.survey).select(Constants.survey.name).fetchFirst());
    }

    @Test
    public void single_array() {
        Assert.assertNotNull(query().from(Constants.survey).select(new Expression<?>[]{ Constants.survey.name }).fetchFirst());
    }

    @Test
    public void single_column() {
        // single column
        for (String s : query().from(Constants.survey).select(Constants.survey.name).fetch()) {
            Assert.assertNotNull(s);
        }
    }

    @Test
    public void single_column_via_Object_type() {
        for (Object s : query().from(Constants.survey).select(ExpressionUtils.path(Object.class, Constants.survey.name.getMetadata())).fetch()) {
            Assert.assertEquals(String.class, s.getClass());
        }
    }

    @Test
    public void specialChars() {
        Assert.assertEquals(0, query().from(Constants.survey).where(Constants.survey.name.in("\n", "\r", "\\", "\'", "\"")).fetchCount());
    }

    @Test
    public void standardTest() {
        standardTest.runBooleanTests(Constants.employee.firstname.isNull(), Constants.employee2.lastname.isNotNull());
        // datetime
        standardTest.runDateTests(Constants.employee.datefield, Constants.employee2.datefield, Constants.date);
        // numeric
        standardTest.runNumericCasts(Constants.employee.id, Constants.employee2.id, 1);
        standardTest.runNumericTests(Constants.employee.id, Constants.employee2.id, 1);
        // BigDecimal
        standardTest.runNumericTests(Constants.employee.salary, Constants.employee2.salary, new BigDecimal("30000.00"));
        standardTest.runStringTests(Constants.employee.firstname, Constants.employee2.firstname, "Jennifer");
        Target.Target target = Connections.getTarget();
        if ((target != (SQLITE)) && (target != (SQLSERVER))) {
            // jTDS driver does not support TIME SQL data type
            standardTest.runTimeTests(Constants.employee.timefield, Constants.employee2.timefield, Constants.time);
        }
        standardTest.report();
    }

    @Test
    @IncludeIn(H2)
    public void standardTest_turkish() {
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr", "TR"));
        try {
            standardTest();
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @Test
    @ExcludeIn(SQLITE)
    public void string() {
        StringExpression str = Expressions.stringTemplate("'  abcd  '");
        Assert.assertEquals("abcd  ", firstResult(StringExpressions.ltrim(str)));
        Assert.assertEquals(Integer.valueOf(3), firstResult(str.locate("a")));
        Assert.assertEquals(Integer.valueOf(0), firstResult(str.locate("a", 4)));
        Assert.assertEquals(Integer.valueOf(4), firstResult(str.locate("b", 2)));
        Assert.assertEquals("  abcd", firstResult(StringExpressions.rtrim(str)));
        Assert.assertEquals("abc", firstResult(str.substring(2, 5)));
    }

    @Test
    @ExcludeIn(SQLITE)
    public void string_withTemplate() {
        StringExpression str = Expressions.stringTemplate("'  abcd  '");
        NumberExpression<Integer> four = Expressions.numberTemplate(Integer.class, "4");
        NumberExpression<Integer> two = Expressions.numberTemplate(Integer.class, "2");
        NumberExpression<Integer> five = Expressions.numberTemplate(Integer.class, "5");
        Assert.assertEquals("abcd  ", firstResult(StringExpressions.ltrim(str)));
        Assert.assertEquals(Integer.valueOf(3), firstResult(str.locate("a")));
        Assert.assertEquals(Integer.valueOf(0), firstResult(str.locate("a", four)));
        Assert.assertEquals(Integer.valueOf(4), firstResult(str.locate("b", two)));
        Assert.assertEquals("  abcd", firstResult(StringExpressions.rtrim(str)));
        Assert.assertEquals("abc", firstResult(str.substring(two, five)));
    }

    @Test
    @ExcludeIn({ POSTGRESQL, SQLITE })
    public void string_indexOf() {
        StringExpression str = Expressions.stringTemplate("'  abcd  '");
        Assert.assertEquals(Integer.valueOf(2), firstResult(str.indexOf("a")));
        Assert.assertEquals(Integer.valueOf((-1)), firstResult(str.indexOf("a", 4)));
        Assert.assertEquals(Integer.valueOf(3), firstResult(str.indexOf("b", 2)));
    }

    @Test
    public void stringFunctions2() throws SQLException {
        for (BooleanExpression where : Arrays.asList(Constants.employee.firstname.startsWith("a"), Constants.employee.firstname.startsWithIgnoreCase("a"), Constants.employee.firstname.endsWith("a"), Constants.employee.firstname.endsWithIgnoreCase("a"))) {
            query().from(Constants.employee).where(where).select(Constants.employee.firstname).fetch();
        }
    }

    @Test
    @ExcludeIn(SQLITE)
    public void string_left() {
        Assert.assertEquals("John", query().from(Constants.employee).where(Constants.employee.lastname.eq("Johnson")).select(SQLExpressions.left(Constants.employee.lastname, 4)).fetchFirst());
    }

    @Test
    @ExcludeIn({ DERBY, SQLITE })
    public void string_right() {
        Assert.assertEquals("son", query().from(Constants.employee).where(Constants.employee.lastname.eq("Johnson")).select(SQLExpressions.right(Constants.employee.lastname, 3)).fetchFirst());
    }

    @Test
    @ExcludeIn({ DERBY, SQLITE })
    public void string_left_Right() {
        Assert.assertEquals("hn", query().from(Constants.employee).where(Constants.employee.lastname.eq("Johnson")).select(SQLExpressions.right(SQLExpressions.left(Constants.employee.lastname, 4), 2)).fetchFirst());
    }

    @Test
    @ExcludeIn({ DERBY, SQLITE })
    public void string_right_Left() {
        Assert.assertEquals("ns", query().from(Constants.employee).where(Constants.employee.lastname.eq("Johnson")).select(SQLExpressions.left(SQLExpressions.right(Constants.employee.lastname, 4), 2)).fetchFirst());
    }

    @Test
    @ExcludeIn({ DB2, DERBY, FIREBIRD })
    public void substring() {
        // SELECT * FROM account where SUBSTRING(name, -x, 1) = SUBSTRING(name, -y, 1)
        query().from(Constants.employee).where(Constants.employee.firstname.substring((-3), 1).eq(Constants.employee.firstname.substring((-2), 1))).select(Constants.employee.id).fetch();
    }

    @Test
    public void syntax_for_employee() throws SQLException {
        Assert.assertEquals(3, query().from(Constants.employee).groupBy(Constants.employee.superiorId).orderBy(Constants.employee.superiorId.asc()).select(Constants.employee.salary.avg(), Constants.employee.id.max()).fetch().size());
        Assert.assertEquals(2, query().from(Constants.employee).groupBy(Constants.employee.superiorId).having(Constants.employee.id.max().gt(5)).orderBy(Constants.employee.superiorId.asc()).select(Constants.employee.salary.avg(), Constants.employee.id.max()).fetch().size());
        Assert.assertEquals(2, query().from(Constants.employee).groupBy(Constants.employee.superiorId).having(Constants.employee.superiorId.isNotNull()).orderBy(Constants.employee.superiorId.asc()).select(Constants.employee.salary.avg(), Constants.employee.id.max()).fetch().size());
    }

    @Test
    public void templateExpression() {
        NumberExpression<Integer> one = Expressions.numberTemplate(Integer.class, "1");
        Assert.assertEquals(Arrays.asList(1), query().from(Constants.survey).select(one.as("col1")).fetch());
    }

    @Test
    public void transform_groupBy() {
        QEmployee employee = new QEmployee("employee");
        QEmployee employee2 = new QEmployee("employee2");
        Map<Integer, Map<Integer, Employee>> results = query().from(employee, employee2).transform(GroupBy.groupBy(employee.id).as(GroupBy.map(employee2.id, employee2)));
        int count = ((int) (query().from(employee).fetchCount()));
        Assert.assertEquals(count, results.size());
        for (Map.Entry<Integer, Map<Integer, Employee>> entry : results.entrySet()) {
            Map<Integer, Employee> employees = entry.getValue();
            Assert.assertEquals(count, employees.size());
        }
    }

    @Test
    public void tuple_projection() {
        List<Tuple> tuples = query().from(Constants.employee).select(Constants.employee.firstname, Constants.employee.lastname).fetch();
        Assert.assertFalse(tuples.isEmpty());
        for (Tuple tuple : tuples) {
            Assert.assertNotNull(tuple.get(Constants.employee.firstname));
            Assert.assertNotNull(tuple.get(Constants.employee.lastname));
        }
    }

    @Test
    @ExcludeIn({ DB2, DERBY })
    public void tuple2() {
        Assert.assertEquals(10, query().from(Constants.employee).select(Expressions.as(ConstantImpl.create("1"), "code"), Constants.employee.id).fetch().size());
    }

    @Test
    public void twoColumns() {
        // two columns
        for (Tuple row : query().from(Constants.survey).select(Constants.survey.id, Constants.survey.name).fetch()) {
            Assert.assertEquals(2, row.size());
            Assert.assertEquals(Integer.class, row.get(0, Object.class).getClass());
            Assert.assertEquals(String.class, row.get(1, Object.class).getClass());
        }
    }

    @Test
    public void twoColumns_and_projection() {
        // two columns and projection
        for (Tuple row : query().from(Constants.survey).select(Constants.survey.id, Constants.survey.name, new QIdName(Constants.survey.id, Constants.survey.name)).fetch()) {
            Assert.assertEquals(3, row.size());
            Assert.assertEquals(Integer.class, row.get(0, Object.class).getClass());
            Assert.assertEquals(String.class, row.get(1, Object.class).getClass());
            Assert.assertEquals(IdName.class, row.get(2, Object.class).getClass());
        }
    }

    @Test
    public void unique_Constructor_projection() {
        IdName idAndName = query().from(Constants.survey).limit(1).select(new QIdName(Constants.survey.id, Constants.survey.name)).fetchFirst();
        Assert.assertNotNull(idAndName);
        Assert.assertNotNull(idAndName.getId());
        Assert.assertNotNull(idAndName.getName());
    }

    @Test
    public void unique_single() {
        String s = query().from(Constants.survey).limit(1).select(Constants.survey.name).fetchFirst();
        Assert.assertNotNull(s);
    }

    @Test
    public void unique_wildcard() {
        // unique wildcard
        Tuple row = query().from(Constants.survey).limit(1).select(Constants.survey.all()).fetchFirst();
        Assert.assertNotNull(row);
        Assert.assertEquals(3, row.size());
        Assert.assertNotNull(row.get(0, Object.class));
        Assert.assertNotNull(((row.get(0, Object.class)) + " is not null"), row.get(1, Object.class));
    }

    @Test(expected = NonUniqueResultException.class)
    public void uniqueResultContract() {
        query().from(Constants.employee).select(Constants.employee.all()).fetchOne();
    }

    @Test
    public void various() throws SQLException {
        for (String s : query().from(Constants.survey).select(Constants.survey.name.lower()).fetch()) {
            Assert.assertEquals(s, s.toLowerCase());
        }
        for (String s : query().from(Constants.survey).select(Constants.survey.name.append("abc")).fetch()) {
            Assert.assertTrue(s.endsWith("abc"));
        }
        System.out.println(query().from(Constants.survey).select(Constants.survey.id.sqrt()).fetch());
    }

    @Test
    public void where_exists() throws SQLException {
        SQLQuery<Integer> sq1 = query().from(Constants.employee).select(Constants.employee.id.max());
        Assert.assertEquals(10, query().from(Constants.employee).where(sq1.exists()).fetchCount());
    }

    @Test
    public void where_exists_Not() throws SQLException {
        SQLQuery<Integer> sq1 = query().from(Constants.employee).select(Constants.employee.id.max());
        Assert.assertEquals(0, query().from(Constants.employee).where(sq1.exists().not()).fetchCount());
    }

    @Test
    @IncludeIn({ HSQLDB, ORACLE, POSTGRESQL })
    public void with() {
        Assert.assertEquals(10, query().with(Constants.employee2, query().from(Constants.employee).where(Constants.employee.firstname.eq("Jim")).select(all)).from(Constants.employee, Constants.employee2).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    @IncludeIn({ HSQLDB, ORACLE, POSTGRESQL })
    public void with2() {
        QEmployee employee3 = new QEmployee("e3");
        Assert.assertEquals(100, query().with(Constants.employee2, query().from(Constants.employee).where(Constants.employee.firstname.eq("Jim")).select(all)).with(Constants.employee2, query().from(Constants.employee).where(Constants.employee.firstname.eq("Jim")).select(all)).from(Constants.employee, Constants.employee2, employee3).select(Constants.employee.id, Constants.employee2.id, employee3.id).fetch().size());
    }

    @Test
    @IncludeIn({ HSQLDB, ORACLE, POSTGRESQL })
    public void with3() {
        Assert.assertEquals(10, query().with(Constants.employee2, Constants.employee2.all()).as(query().from(Constants.employee).where(Constants.employee.firstname.eq("Jim")).select(all)).from(Constants.employee, Constants.employee2).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    @IncludeIn({ HSQLDB, ORACLE, POSTGRESQL })
    public void with_limit() {
        Assert.assertEquals(5, query().with(Constants.employee2, Constants.employee2.all()).as(query().from(Constants.employee).where(Constants.employee.firstname.eq("Jim")).select(all)).from(Constants.employee, Constants.employee2).limit(5).orderBy(Constants.employee.id.asc(), Constants.employee2.id.asc()).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    @IncludeIn({ HSQLDB, ORACLE, POSTGRESQL })
    public void with_limitOffset() {
        Assert.assertEquals(5, query().with(Constants.employee2, Constants.employee2.all()).as(query().from(Constants.employee).where(Constants.employee.firstname.eq("Jim")).select(all)).from(Constants.employee, Constants.employee2).limit(10).offset(5).orderBy(Constants.employee.id.asc(), Constants.employee2.id.asc()).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    @IncludeIn({ ORACLE, POSTGRESQL })
    public void with_recursive() {
        Assert.assertEquals(10, query().withRecursive(Constants.employee2, query().from(Constants.employee).where(Constants.employee.firstname.eq("Jim")).select(all)).from(Constants.employee, Constants.employee2).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    @IncludeIn({ ORACLE, POSTGRESQL })
    public void with_recursive2() {
        Assert.assertEquals(10, query().withRecursive(Constants.employee2, Constants.employee2.all()).as(query().from(Constants.employee).where(Constants.employee.firstname.eq("Jim")).select(all)).from(Constants.employee, Constants.employee2).select(Constants.employee.id, Constants.employee2.id).fetch().size());
    }

    @Test
    public void wildcard() {
        // wildcard
        for (Tuple row : query().from(Constants.survey).select(Constants.survey.all()).fetch()) {
            Assert.assertNotNull(row);
            Assert.assertEquals(3, row.size());
            Assert.assertNotNull(row.get(0, Object.class));
            Assert.assertNotNull(((row.get(0, Object.class)) + " is not null"), row.get(1, Object.class));
        }
    }

    @Test
    @SkipForQuoted
    public void wildcard_all() {
        expectedQuery = "select * from EMPLOYEE e";
        query().from(Constants.employee).select(all).fetch();
    }

    @Test
    public void wildcard_all2() {
        Assert.assertEquals(10, query().from(new RelationalPathBase<Object>(Object.class, "employee", "public", "EMPLOYEE")).select(all).fetch().size());
    }

    @Test
    public void wildcard_and_qTuple() {
        // wildcard and QTuple
        for (Tuple tuple : query().from(Constants.survey).select(Constants.survey.all()).fetch()) {
            Assert.assertNotNull(tuple.get(Constants.survey.id));
            Assert.assertNotNull(tuple.get(Constants.survey.name));
        }
    }

    @Test
    @IncludeIn(ORACLE)
    public void withinGroup() {
        List<WithinGroup<?>> exprs = new ArrayList<WithinGroup<?>>();
        NumberPath<Integer> path = Constants.survey.id;
        // two args
        add(exprs, SQLExpressions.cumeDist(2, 3));
        add(exprs, SQLExpressions.denseRank(4, 5));
        add(exprs, SQLExpressions.listagg(path, ","));
        add(exprs, SQLExpressions.percentRank(6, 7));
        add(exprs, SQLExpressions.rank(8, 9));
        for (WithinGroup<?> wg : exprs) {
            query().from(Constants.survey).select(wg.withinGroup().orderBy(Constants.survey.id, Constants.survey.id)).fetch();
            query().from(Constants.survey).select(wg.withinGroup().orderBy(Constants.survey.id.asc(), Constants.survey.id.asc())).fetch();
        }
        // one arg
        exprs.clear();
        add(exprs, SQLExpressions.percentileCont(0.1));
        add(exprs, SQLExpressions.percentileDisc(0.9));
        for (WithinGroup<?> wg : exprs) {
            query().from(Constants.survey).select(wg.withinGroup().orderBy(Constants.survey.id)).fetch();
            query().from(Constants.survey).select(wg.withinGroup().orderBy(Constants.survey.id.asc())).fetch();
        }
    }

    @Test
    @ExcludeIn({ DB2, DERBY, H2 })
    public void yearWeek() {
        SQLQuery<?> query = query().from(Constants.employee).orderBy(Constants.employee.id.asc());
        Assert.assertEquals(Integer.valueOf(200006), query.select(Constants.employee.datefield.yearWeek()).fetchFirst());
    }

    @Test
    @IncludeIn({ H2 })
    public void yearWeek_h2() {
        SQLQuery<?> query = query().from(Constants.employee).orderBy(Constants.employee.id.asc());
        Assert.assertEquals(Integer.valueOf(200007), query.select(Constants.employee.datefield.yearWeek()).fetchFirst());
    }

    @Test
    public void statementOptions() {
        StatementOptions options = StatementOptions.builder().setFetchSize(15).setMaxRows(150).build();
        SQLQuery<?> query = query().from(Constants.employee).orderBy(Constants.employee.id.asc());
        query.setStatementOptions(options);
        query.addListener(new SQLBaseListener() {
            public void preExecute(SQLListenerContext context) {
                try {
                    Assert.assertEquals(15, context.getPreparedStatement().getFetchSize());
                    Assert.assertEquals(150, context.getPreparedStatement().getMaxRows());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        query.select(Constants.employee.id).fetch();
    }

    @Test
    public void getResults() throws InterruptedException, SQLException {
        final AtomicLong endCalled = new AtomicLong(0);
        SQLQuery<Integer> query = query().select(Constants.employee.id).from(Constants.employee);
        query.addListener(new SQLBaseListener() {
            @Override
            public void end(SQLListenerContext context) {
                endCalled.set(System.currentTimeMillis());
            }
        });
        ResultSet results = query.getResults(Constants.employee.id);
        long getResultsCalled = System.currentTimeMillis();
        Thread.sleep(100);
        results.close();
        Assert.assertTrue((((endCalled.get()) - getResultsCalled) >= 100));
    }

    @Test
    @ExcludeIn(DERBY)
    public void groupConcat() {
        List<String> expected = ImmutableList.of("Mike,Mary", "Joe,Peter,Steve,Jim", "Jennifer,Helen,Daisy,Barbara");
        if ((Connections.getTarget()) == (POSTGRESQL)) {
            expected = ImmutableList.of("Steve,Jim,Joe,Peter", "Barbara,Helen,Daisy,Jennifer", "Mary,Mike");
        }
        Assert.assertEquals(expected, query().select(SQLExpressions.groupConcat(Constants.employee.firstname)).from(Constants.employee).groupBy(Constants.employee.superiorId).fetch());
    }

    @Test
    @ExcludeIn(DERBY)
    public void groupConcat2() {
        List<String> expected = ImmutableList.of("Mike-Mary", "Joe-Peter-Steve-Jim", "Jennifer-Helen-Daisy-Barbara");
        if ((Connections.getTarget()) == (POSTGRESQL)) {
            expected = ImmutableList.of("Steve-Jim-Joe-Peter", "Barbara-Helen-Daisy-Jennifer", "Mary-Mike");
        }
        Assert.assertEquals(expected, query().select(SQLExpressions.groupConcat(Constants.employee.firstname, "-")).from(Constants.employee).groupBy(Constants.employee.superiorId).fetch());
    }
}

/**
 * CHECKSTYLERULE:ON: FileLength
 */
