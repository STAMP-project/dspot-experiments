package com.querydsl.sql;


import Expressions.ONE;
import Expressions.TWO;
import com.google.common.collect.ImmutableList;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Tuple;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.domain.Employee;
import com.querydsl.sql.domain.QEmployee;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class UnionBase extends AbstractBaseTest {
    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn({ MYSQL, TERADATA })
    public void in_union() {
        Assert.assertTrue(((query().from(Constants.employee).where(Constants.employee.id.in(query().union(query().select(ONE), query().select(TWO)))).select(ONE).fetchFirst()) != null));
    }

    // order is not properly supported
    @Test
    @SuppressWarnings("unchecked")
    @ExcludeIn(FIREBIRD)
    public void union() throws SQLException {
        SubQueryExpression<Integer> sq1 = query().from(Constants.employee).select(Constants.employee.id.max().as("ID"));
        SubQueryExpression<Integer> sq2 = query().from(Constants.employee).select(Constants.employee.id.min().as("ID"));
        Assert.assertEquals(ImmutableList.of(query().select(Constants.employee.id.min()).from(Constants.employee).fetchFirst(), query().select(Constants.employee.id.max()).from(Constants.employee).fetchFirst()), query().union(sq1, sq2).orderBy(Constants.employee.id.asc()).fetch());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void union_list() throws SQLException {
        SubQueryExpression<Integer> sq1 = query().from(Constants.employee).select(Constants.employee.id.max());
        SubQueryExpression<Integer> sq2 = query().from(Constants.employee).select(Constants.employee.id.min());
        Assert.assertEquals(query().union(sq1, sq2).fetch(), query().union(sq1, sq2).list());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void union_all() {
        SubQueryExpression<Integer> sq1 = query().from(Constants.employee).select(Constants.employee.id.max());
        SubQueryExpression<Integer> sq2 = query().from(Constants.employee).select(Constants.employee.id.min());
        List<Integer> list = query().unionAll(sq1, sq2).fetch();
        Assert.assertFalse(list.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union_multiple_columns() throws SQLException {
        SubQueryExpression<Tuple> sq1 = query().from(Constants.employee).select(Constants.employee.firstname, Constants.employee.lastname);
        SubQueryExpression<Tuple> sq2 = query().from(Constants.employee).select(Constants.employee.lastname, Constants.employee.firstname);
        List<Tuple> list = query().union(sq1, sq2).fetch();
        Assert.assertFalse(list.isEmpty());
        for (Tuple row : list) {
            Assert.assertNotNull(row.get(0, Object.class));
            Assert.assertNotNull(row.get(1, Object.class));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn(DERBY)
    public void union_multiple_columns2() throws SQLException {
        SubQueryExpression<Tuple> sq1 = query().from(Constants.employee).select(Constants.employee.firstname, Constants.employee.lastname);
        SubQueryExpression<Tuple> sq2 = query().from(Constants.employee).select(Constants.employee.firstname, Constants.employee.lastname);
        SQLQuery<?> query = query();
        query.union(sq1, sq2);
        List<String> list = query.select(Constants.employee.firstname).fetch();
        Assert.assertFalse(list.isEmpty());
        for (String row : list) {
            Assert.assertNotNull(row);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn(DERBY)
    public void union_multiple_columns3() throws SQLException {
        SubQueryExpression<Tuple> sq1 = query().from(Constants.employee).select(Constants.employee.firstname, Constants.employee.lastname);
        SubQueryExpression<Tuple> sq2 = query().from(Constants.employee).select(Constants.employee.firstname, Constants.employee.lastname);
        SQLQuery<?> query = query();
        query.union(sq1, sq2);
        List<Tuple> list = query.select(Constants.employee.lastname, Constants.employee.firstname).fetch();
        Assert.assertFalse(list.isEmpty());
        for (Tuple row : list) {
            System.out.println((((row.get(0, String.class)) + " ") + (row.get(1, String.class))));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void union_empty_result() throws SQLException {
        SubQueryExpression<Integer> sq1 = query().from(Constants.employee).where(Constants.employee.firstname.eq("XXX")).select(Constants.employee.id);
        SubQueryExpression<Integer> sq2 = query().from(Constants.employee).where(Constants.employee.firstname.eq("YYY")).select(Constants.employee.id);
        List<Integer> list = query().union(sq1, sq2).fetch();
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void union2() throws SQLException {
        List<Integer> list = query().union(query().from(Constants.employee).select(Constants.employee.id.max()), query().from(Constants.employee).select(Constants.employee.id.min())).fetch();
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void union3() throws SQLException {
        SubQueryExpression<Tuple> sq3 = query().from(Constants.employee).select(new Expression[]{ Constants.employee.id.max() });
        SubQueryExpression<Tuple> sq4 = query().from(Constants.employee).select(new Expression[]{ Constants.employee.id.min() });
        List<Tuple> list2 = query().union(sq3, sq4).fetch();
        Assert.assertFalse(list2.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn({ DERBY })
    public void union4() {
        SubQueryExpression<Tuple> sq1 = query().from(Constants.employee).select(Constants.employee.id, Constants.employee.firstname);
        SubQueryExpression<Tuple> sq2 = query().from(Constants.employee).select(Constants.employee.id, Constants.employee.firstname);
        Assert.assertEquals(1, query().union(Constants.employee, sq1, sq2).select(Constants.employee.id.count()).fetch().size());
    }

    // The ORDER BY clause must contain only integer constants.
    @Test
    @ExcludeIn({ FIREBIRD, TERADATA })
    @SuppressWarnings("unchecked")
    public void union_with_order() throws SQLException {
        SubQueryExpression<Integer> sq1 = query().from(Constants.employee).select(Constants.employee.id);
        SubQueryExpression<Integer> sq2 = query().from(Constants.employee).select(Constants.employee.id);
        List<Integer> list = query().union(sq1, sq2).orderBy(Constants.employee.id.asc()).fetch();
        Assert.assertFalse(list.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn(FIREBIRD)
    public void union_multi_column_projection_list() throws IOException {
        SubQueryExpression<Tuple> sq1 = query().from(Constants.employee).select(Constants.employee.id.max(), Constants.employee.id.max().subtract(1));
        SubQueryExpression<Tuple> sq2 = query().from(Constants.employee).select(Constants.employee.id.min(), Constants.employee.id.min().subtract(1));
        List<Tuple> list = query().union(sq1, sq2).list();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(((list.get(0)) != null));
        Assert.assertTrue(((list.get(1)) != null));
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn(FIREBIRD)
    public void union_multi_column_projection_iterate() throws IOException {
        SubQueryExpression<Tuple> sq1 = query().from(Constants.employee).select(Constants.employee.id.max(), Constants.employee.id.max().subtract(1));
        SubQueryExpression<Tuple> sq2 = query().from(Constants.employee).select(Constants.employee.id.min(), Constants.employee.id.min().subtract(1));
        CloseableIterator<Tuple> iterator = query().union(sq1, sq2).iterate();
        try {
            Assert.assertTrue(iterator.hasNext());
            Assert.assertTrue(((iterator.next()) != null));
            Assert.assertTrue(((iterator.next()) != null));
            Assert.assertFalse(iterator.hasNext());
        } finally {
            iterator.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union_single_column_projections_list() throws IOException {
        SubQueryExpression<Integer> sq1 = query().from(Constants.employee).select(Constants.employee.id.max());
        SubQueryExpression<Integer> sq2 = query().from(Constants.employee).select(Constants.employee.id.min());
        List<Integer> list = query().union(sq1, sq2).list();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(((list.get(0)) != null));
        Assert.assertTrue(((list.get(1)) != null));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union_single_column_projections_iterate() throws IOException {
        SubQueryExpression<Integer> sq1 = query().from(Constants.employee).select(Constants.employee.id.max());
        SubQueryExpression<Integer> sq2 = query().from(Constants.employee).select(Constants.employee.id.min());
        CloseableIterator<Integer> iterator = query().union(sq1, sq2).iterate();
        try {
            Assert.assertTrue(iterator.hasNext());
            Assert.assertTrue(((iterator.next()) != null));
            Assert.assertTrue(((iterator.next()) != null));
            Assert.assertFalse(iterator.hasNext());
        } finally {
            iterator.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void union_factoryExpression() {
        SubQueryExpression<Employee> sq1 = query().from(Constants.employee).select(Projections.constructor(Employee.class, Constants.employee.id));
        SubQueryExpression<Employee> sq2 = query().from(Constants.employee).select(Projections.constructor(Employee.class, Constants.employee.id));
        List<Employee> employees = query().union(sq1, sq2).list();
        for (Employee employee : employees) {
            Assert.assertNotNull(employee);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    @ExcludeIn({ DERBY, CUBRID })
    public void union_clone() {
        NumberPath<Integer> idAlias = Expressions.numberPath(Integer.class, "id");
        SubQueryExpression<Employee> sq1 = query().from(Constants.employee).select(Projections.constructor(Employee.class, Constants.employee.id.as(idAlias)));
        SubQueryExpression<Employee> sq2 = query().from(Constants.employee).select(Projections.constructor(Employee.class, Constants.employee.id.as(idAlias)));
        SQLQuery<?> query = query();
        query.union(sq1, sq2);
        Assert.assertEquals(10, query.clone().select(idAlias).fetch().size());
    }
}

