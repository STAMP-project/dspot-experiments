package com.querydsl.sql;


import com.querydsl.core.Target;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.sql.domain.QEmployee;
import java.sql.SQLException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SelectOracleBase extends AbstractBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSQLQuery.class);

    @Test
    @IncludeIn(Target.ORACLE)
    @SkipForQuoted
    public void connectByPrior() throws SQLException {
        expectedQuery = "select e.ID, e.LASTNAME, e.SUPERIOR_ID " + ("from EMPLOYEE e " + "connect by prior e.ID = e.SUPERIOR_ID");
        oracleQuery().from(Constants.employee).connectByPrior(Constants.employee.id.eq(Constants.employee.superiorId)).select(Constants.employee.id, Constants.employee.lastname, Constants.employee.superiorId).fetch();
    }

    @Test
    @IncludeIn(Target.ORACLE)
    @SkipForQuoted
    public void connectByPrior2() throws SQLException {
        if (configuration.getUseLiterals()) {
            return;
        }
        expectedQuery = "select e.ID, e.LASTNAME, e.SUPERIOR_ID " + (("from EMPLOYEE e " + "start with e.ID = ? ") + "connect by prior e.ID = e.SUPERIOR_ID");
        oracleQuery().from(Constants.employee).startWith(Constants.employee.id.eq(1)).connectByPrior(Constants.employee.id.eq(Constants.employee.superiorId)).select(Constants.employee.id, Constants.employee.lastname, Constants.employee.superiorId).fetch();
    }

    @Test
    @IncludeIn(Target.ORACLE)
    @SkipForQuoted
    public void connectByPrior3() throws SQLException {
        if (configuration.getUseLiterals()) {
            return;
        }
        expectedQuery = "select e.ID, e.LASTNAME, e.SUPERIOR_ID " + ((("from EMPLOYEE e " + "start with e.ID = ? ") + "connect by prior e.ID = e.SUPERIOR_ID ") + "order siblings by e.LASTNAME");
        oracleQuery().from(Constants.employee).startWith(Constants.employee.id.eq(1)).connectByPrior(Constants.employee.id.eq(Constants.employee.superiorId)).orderSiblingsBy(Constants.employee.lastname).select(Constants.employee.id, Constants.employee.lastname, Constants.employee.superiorId).fetch();
    }

    @Test
    @IncludeIn(Target.ORACLE)
    @SkipForQuoted
    public void connectByPrior4() throws SQLException {
        if (configuration.getUseLiterals()) {
            return;
        }
        expectedQuery = "select e.ID, e.LASTNAME, e.SUPERIOR_ID " + ("from EMPLOYEE e " + "connect by nocycle prior e.ID = e.SUPERIOR_ID");
        oracleQuery().from(Constants.employee).connectByNocyclePrior(Constants.employee.id.eq(Constants.employee.superiorId)).select(Constants.employee.id, Constants.employee.lastname, Constants.employee.superiorId).fetch();
    }

    @Test
    @IncludeIn(Target.ORACLE)
    @SkipForQuoted
    public void sumOver() throws SQLException {
        // SQL> select deptno,
        // 2  ename,
        // 3  sal,
        // 4  sum(sal) over (partition by deptno
        // 5  order by sal,ename) CumDeptTot,
        // 6  sum(sal) over (partition by deptno) SalByDept,
        // 7  sum(sal) over (order by deptno, sal) CumTot,
        // 8  sum(sal) over () TotSal
        // 9  from emp
        // 10  order by deptno, sal;
        expectedQuery = "select e.LASTNAME, e.SALARY, " + (("sum(e.SALARY) over (partition by e.SUPERIOR_ID order by e.LASTNAME asc, e.SALARY asc), " + "sum(e.SALARY) over (order by e.SUPERIOR_ID asc, e.SALARY asc), ") + "sum(e.SALARY) over () from EMPLOYEE e order by e.SALARY asc, e.SUPERIOR_ID asc");
        oracleQuery().from(Constants.employee).orderBy(Constants.employee.salary.asc(), Constants.employee.superiorId.asc()).select(Constants.employee.lastname, Constants.employee.salary, SQLExpressions.sum(Constants.employee.salary).over().partitionBy(Constants.employee.superiorId).orderBy(Constants.employee.lastname, Constants.employee.salary), SQLExpressions.sum(Constants.employee.salary).over().orderBy(Constants.employee.superiorId, Constants.employee.salary), SQLExpressions.sum(Constants.employee.salary).over()).fetch();
        // shorter version
        QEmployee e = Constants.employee;
        oracleQuery().from(e).orderBy(e.salary.asc(), e.superiorId.asc()).select(e.lastname, e.salary, SQLExpressions.sum(e.salary).over().partitionBy(e.superiorId).orderBy(e.lastname, e.salary), SQLExpressions.sum(e.salary).over().orderBy(e.superiorId, e.salary), SQLExpressions.sum(e.salary).over()).fetch();
    }
}

