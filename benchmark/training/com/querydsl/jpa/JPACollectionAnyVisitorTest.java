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
package com.querydsl.jpa;


import QCat.cat.kittens;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QCompany;
import com.querydsl.jpa.domain.QDomesticCat;
import com.querydsl.jpa.domain.QEmployee;
import org.junit.Assert;
import org.junit.Test;


public class JPACollectionAnyVisitorTest {
    private QCat cat = QCat.cat;

    @Test
    public void path() {
        Assert.assertEquals("cat_kittens_0", serialize(cat.kittens.any()));
    }

    @Test
    public void longer_path() {
        Assert.assertEquals("cat_kittens_0.name", serialize(cat.kittens.any().name));
    }

    @Test
    public void nested_any_booleanOperation() {
        QCompany company = QCompany.company;
        Predicate predicate = company.departments.any().employees.any().firstName.eq("Bob");
        Assert.assertEquals(("exists (select 1\n" + (("from company.departments as company_departments_0\n" + "  inner join company_departments_0.employees as company_departments_0_employees_1\n") + "where company_departments_0_employees_1.firstName = ?1)")), serialize(predicate));
    }

    @Test
    public void simple_booleanOperation() {
        Predicate predicate = cat.kittens.any().name.eq("Ruth123");
        Assert.assertEquals(("exists (select 1\n" + ("from cat.kittens as cat_kittens_0\n" + "where cat_kittens_0.name = ?1)")), serialize(predicate));
    }

    @Test
    public void simple_booleanOperation_longPath() {
        Predicate predicate = cat.kittens.any().kittens.any().name.eq("Ruth123");
        Assert.assertEquals(("exists (select 1\n" + (("from cat.kittens as cat_kittens_0\n" + "  inner join cat_kittens_0.kittens as cat_kittens_0_kittens_1\n") + "where cat_kittens_0_kittens_1.name = ?1)")), serialize(predicate));
    }

    @Test
    public void simple_booleanOperation_elementCollection() {
        QEmployee employee = QEmployee.employee;
        Predicate predicate = employee.jobFunctions.any().stringValue().eq("CODER");
        Assert.assertEquals(("exists (select 1\n" + (("from Employee employee_1463394548\n" + "  inner join employee_1463394548.jobFunctions as employee_jobFunctions_0\n") + "where employee_1463394548 = employee and str(employee_jobFunctions_0) = ?1)")), serialize(predicate));
    }

    @Test
    public void simple_stringOperation() {
        Predicate predicate = cat.kittens.any().name.substring(1).eq("uth123");
        Assert.assertEquals(("exists (select 1\n" + ("from cat.kittens as cat_kittens_0\n" + "where substring(cat_kittens_0.name,2) = ?1)")), serialize(predicate));
    }

    @Test
    public void and_operation() {
        Predicate predicate = cat.kittens.any().name.eq("Ruth123").and(cat.kittens.any().bodyWeight.gt(10.0));
        Assert.assertEquals(("exists (select 1\n" + ((("from cat.kittens as cat_kittens_0\n" + "where cat_kittens_0.name = ?1) and exists (select 1\n") + "from cat.kittens as cat_kittens_1\n") + "where cat_kittens_1.bodyWeight > ?2)")), serialize(predicate));
    }

    @Test
    public void template() {
        Expression<Boolean> templateExpr = ExpressionUtils.template(Boolean.class, "{0} = {1}", cat.kittens.any().name, ConstantImpl.create("Ruth123"));
        Assert.assertEquals(("exists (select 1\n" + ("from cat.kittens as cat_kittens_0\n" + "where cat_kittens_0.name = ?1)")), serialize(templateExpr));
    }

    @Test
    public void cast() {
        // JPAQuery query = new JPAQuery(em).from(QPerson.person);
        // QDog anyDog = QPerson.person.animals.any().as(QDog.class);
        // query.where(anyDog.gender.eq("M"));
        // List<Person> foundOwners = query.fetch(QPerson.person);
        QDomesticCat anyCat = kittens.any().as(QDomesticCat.class);
        Predicate predicate = anyCat.name.eq("X");
        Assert.assertEquals(("exists (select 1\n" + ("from cat.kittens as cat_kittens_0\n" + "where cat_kittens_0.name = ?1)")), serialize(predicate));
    }
}

