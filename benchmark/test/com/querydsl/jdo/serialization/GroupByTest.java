package com.querydsl.jdo.serialization;


import com.querydsl.jdo.models.company.QEmployee;
import org.junit.Assert;
import org.junit.Test;

import static com.querydsl.jdo.JDOExpressions.selectFrom;


public class GroupByTest extends AbstractTest {
    @Test
    public void groupBy() {
        QEmployee employee = QEmployee.employee;
        Assert.assertEquals(("SELECT FROM com.querydsl.jdo.models.company.Employee " + (("PARAMETERS java.lang.String a1 " + "GROUP BY this.emailAddress ") + "HAVING this.emailAddress != a1")), serialize(selectFrom(employee).groupBy(employee.emailAddress).having(employee.emailAddress.ne("XXX"))));
    }
}

