package com.querydsl.sql;


import Wildcard.all;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.domain.Employee;
import com.querydsl.sql.domain.QEmployee;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static Configuration.DEFAULT;


public class SubqueriesBase extends AbstractBaseTest {
    @Test
    @ExcludeIn({ CUBRID, DERBY, FIREBIRD, H2, HSQLDB, SQLITE, SQLSERVER })
    public void keys() {
        QEmployee employee2 = new QEmployee("employee2");
        ForeignKey<Employee> nameKey1 = new ForeignKey<Employee>(Constants.employee, ImmutableList.of(Constants.employee.firstname, Constants.employee.lastname), ImmutableList.of("a", "b"));
        ForeignKey<Employee> nameKey2 = new ForeignKey<Employee>(Constants.employee, ImmutableList.of(Constants.employee.firstname, Constants.employee.lastname), ImmutableList.of("a", "b"));
        query().from(Constants.employee).where(nameKey1.in(query().from(employee2).select(nameKey2.getProjection()))).select(Constants.employee.id).fetch();
    }

    @Test
    @ExcludeIn({ CUBRID, DERBY, FIREBIRD, H2, HSQLDB, SQLITE, SQLSERVER })
    public void list_in_query() {
        QEmployee employee2 = new QEmployee("employee2");
        query().from(Constants.employee).where(Expressions.list(Constants.employee.id, Constants.employee.lastname).in(query().from(employee2).select(employee2.id, employee2.lastname))).select(Constants.employee.id).fetch();
    }

    // ID is reserved IN DB2
    @Test
    @SkipForQuoted
    @ExcludeIn(DB2)
    public void subQueries() throws SQLException {
        // subquery in where block
        expectedQuery = "select e.ID from EMPLOYEE e " + ("where e.ID = (select max(e.ID) " + "from EMPLOYEE e)");
        List<Integer> list = query().from(Constants.employee).where(Constants.employee.id.eq(query().from(Constants.employee).select(Constants.employee.id.max()))).select(Constants.employee.id).fetch();
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void subQuery_alias() {
        query().from(query().from(Constants.employee).select(all()).as(Constants.employee2)).select(all()).fetch();
    }

    @Test
    @ExcludeIn(SQLITE)
    public void subQuery_all() {
        query().from(Constants.employee).where(Constants.employee.id.gtAll(query().from(Constants.employee2).select(Constants.employee2.id))).fetchCount();
    }

    @Test
    @ExcludeIn(SQLITE)
    public void subQuery_any() {
        query().from(Constants.employee).where(Constants.employee.id.gtAny(query().from(Constants.employee2).select(Constants.employee2.id))).fetchCount();
    }

    @Test
    public void subQuery_innerJoin() {
        SubQueryExpression<Integer> sq = query().from(Constants.employee2).select(Constants.employee2.id);
        QEmployee sqEmp = new QEmployee("sq");
        query().from(Constants.employee).innerJoin(sq, sqEmp).on(sqEmp.id.eq(Constants.employee.id)).select(Constants.employee.id).fetch();
    }

    @Test
    public void subQuery_leftJoin() {
        SubQueryExpression<Integer> sq = query().from(Constants.employee2).select(Constants.employee2.id);
        QEmployee sqEmp = new QEmployee("sq");
        query().from(Constants.employee).leftJoin(sq, sqEmp).on(sqEmp.id.eq(Constants.employee.id)).select(Constants.employee.id).fetch();
    }

    @Test
    @ExcludeIn({ MYSQL, POSTGRESQL, DERBY, SQLSERVER, TERADATA })
    public void subQuery_params() {
        Param<String> aParam = new Param<String>(String.class, "param");
        SQLQuery<?> subQuery = SQLExpressions.select(all).from(Constants.employee).where(Constants.employee.firstname.eq(aParam));
        subQuery.set(aParam, "Mike");
        Assert.assertEquals(1, query().from(subQuery).fetchCount());
    }

    @Test
    @ExcludeIn(SQLITE)
    public void subQuery_rightJoin() {
        SubQueryExpression<Integer> sq = query().from(Constants.employee2).select(Constants.employee2.id);
        QEmployee sqEmp = new QEmployee("sq");
        query().from(Constants.employee).rightJoin(sq, sqEmp).on(sqEmp.id.eq(Constants.employee.id)).select(Constants.employee.id).fetch();
    }

    @Test
    public void subQuery_with_alias() {
        List<Integer> ids1 = query().from(Constants.employee).select(Constants.employee.id).fetch();
        List<Integer> ids2 = query().from(query().from(Constants.employee).select(Constants.employee.id), Constants.employee).select(Constants.employee.id).fetch();
        Assert.assertEquals(ids1, ids2);
    }

    @Test
    public void subQuery_with_alias2() {
        List<Integer> ids1 = query().from(Constants.employee).select(Constants.employee.id).fetch();
        List<Integer> ids2 = query().from(query().from(Constants.employee).select(Constants.employee.id).as(Constants.employee)).select(Constants.employee.id).fetch();
        Assert.assertEquals(ids1, ids2);
    }

    @Test
    @SkipForQuoted
    public void subQuerySerialization() {
        SQLQuery<?> query = query();
        query.from(Constants.survey);
        Assert.assertEquals("from SURVEY s", query.toString());
        query.from(Constants.survey2);
        Assert.assertEquals("from SURVEY s, SURVEY s2", query.toString());
    }

    @Test
    public void subQuerySerialization2() {
        NumberPath<BigDecimal> sal = Expressions.numberPath(BigDecimal.class, "sal");
        PathBuilder<Object[]> sq = new PathBuilder<Object[]>(Object[].class, "sq");
        SQLSerializer serializer = new SQLSerializer(DEFAULT);
        serializer.handle(query().from(Constants.employee).select(Constants.employee.salary.add(Constants.employee.salary).add(Constants.employee.salary).as(sal)).as(sq));
        Assert.assertEquals("(select (e.SALARY + e.SALARY + e.SALARY) as sal\nfrom EMPLOYEE e) as sq", serializer.toString());
    }

    @Test
    public void scalarSubQueryInClause() {
        SQLSerializer serializer = new SQLSerializer(DEFAULT);
        serializer.handle(this.query().from(Constants.employee).where(SQLExpressions.select(Constants.employee.firstname).from(Constants.employee).orderBy(Constants.employee.salary.asc()).limit(1).in(Arrays.asList("Mike", "Mary"))));
        expectedQuery = "(\nfrom EMPLOYEE e\n" + ((("where (select e.FIRSTNAME\n" + "from EMPLOYEE e\n") + "order by e.SALARY asc\n") + "limit ?) in (?, ?))");
        Assert.assertEquals(expectedQuery, serializer.toString());
    }

    @Test
    public void scalarSubQueryInClause2() {
        SQLSerializer serializer = new SQLSerializer(DEFAULT);
        serializer.handle(this.query().from(Constants.employee).where(SQLExpressions.select(Constants.employee.firstname).from(Constants.employee).orderBy(Constants.employee.salary.asc()).limit(1).in("Mike", "Mary")));
        expectedQuery = "(\nfrom EMPLOYEE e\n" + ((("where (select e.FIRSTNAME\n" + "from EMPLOYEE e\n") + "order by e.SALARY asc\n") + "limit ?) in (?, ?))");
        Assert.assertEquals(expectedQuery, serializer.toString());
    }
}

