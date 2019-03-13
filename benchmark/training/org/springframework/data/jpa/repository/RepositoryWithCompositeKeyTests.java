/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.repository;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.sample.EmbeddedIdExampleDepartment;
import org.springframework.data.jpa.domain.sample.EmbeddedIdExampleEmployee;
import org.springframework.data.jpa.domain.sample.EmbeddedIdExampleEmployeePK;
import org.springframework.data.jpa.domain.sample.IdClassExampleDepartment;
import org.springframework.data.jpa.domain.sample.IdClassExampleEmployee;
import org.springframework.data.jpa.domain.sample.IdClassExampleEmployeePK;
import org.springframework.data.jpa.domain.sample.QEmbeddedIdExampleEmployee;
import org.springframework.data.jpa.domain.sample.QIdClassExampleEmployee;
import org.springframework.data.jpa.repository.sample.EmployeeRepositoryWithEmbeddedId;
import org.springframework.data.jpa.repository.sample.EmployeeRepositoryWithIdClass;
import org.springframework.data.jpa.repository.sample.SampleConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 * Tests some usage variants of composite keys with spring data jpa.
 *
 * @author Thomas Darimont
 * @author Mark Paluch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleConfig.class)
@Transactional
public class RepositoryWithCompositeKeyTests {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    EmployeeRepositoryWithIdClass employeeRepositoryWithIdClass;

    @Autowired
    EmployeeRepositoryWithEmbeddedId employeeRepositoryWithEmbeddedId;

    /**
     *
     *
     * @see <a href="download.oracle.com/otn-pub/jcp/persistence-2_1-fr-eval-spec/JavaPersistence.pdf">Final JPA 2.0
    Specification 2.4.1.3 Derived Identities Example 2</a>
     */
    // DATAJPA-269
    @Test
    public void shouldSupportSavingEntitiesWithCompositeKeyClassesWithIdClassAndDerivedIdentities() {
        IdClassExampleDepartment dep = new IdClassExampleDepartment();
        dep.setName("TestDepartment");
        dep.setDepartmentId((-1));
        IdClassExampleEmployee emp = new IdClassExampleEmployee();
        emp.setDepartment(dep);
        save(emp);
        IdClassExampleEmployeePK key = new IdClassExampleEmployeePK();
        key.setDepartment(dep.getDepartmentId());
        key.setEmpId(emp.getEmpId());
        IdClassExampleEmployee persistedEmp = employeeRepositoryWithIdClass.findById(key).get();
        Assert.assertThat(persistedEmp, is(notNullValue()));
        Assert.assertThat(persistedEmp.getDepartment(), is(notNullValue()));
        Assert.assertThat(persistedEmp.getDepartment().getName(), is(dep.getName()));
    }

    /**
     *
     *
     * @see <a href="download.oracle.com/otn-pub/jcp/persistence-2_1-fr-eval-spec/JavaPersistence.pdf">Final JPA 2.0
    Specification 2.4.1.3 Derived Identities Example 3</a>
     */
    // DATAJPA-269
    @Test
    public void shouldSupportSavingEntitiesWithCompositeKeyClassesWithEmbeddedIdsAndDerivedIdentities() {
        EmbeddedIdExampleDepartment dep = new EmbeddedIdExampleDepartment();
        dep.setName("TestDepartment");
        dep.setDepartmentId((-1L));
        EmbeddedIdExampleEmployee emp = new EmbeddedIdExampleEmployee();
        emp.setDepartment(dep);
        emp.setEmployeePk(new EmbeddedIdExampleEmployeePK());
        emp = employeeRepositoryWithEmbeddedId.save(emp);
        EmbeddedIdExampleEmployeePK key = new EmbeddedIdExampleEmployeePK();
        key.setDepartmentId(emp.getDepartment().getDepartmentId());
        key.setEmployeeId(emp.getEmployeePk().getEmployeeId());
        EmbeddedIdExampleEmployee persistedEmp = findById(key).get();
        Assert.assertThat(persistedEmp, is(notNullValue()));
        Assert.assertThat(persistedEmp.getDepartment(), is(notNullValue()));
        Assert.assertThat(persistedEmp.getDepartment().getName(), is(dep.getName()));
    }

    // DATAJPA-472, DATAJPA-912
    @Test
    public void shouldSupportFindAllWithPageableAndEntityWithIdClass() throws Exception {
        if (Package.getPackage("org.hibernate.cfg").getImplementationVersion().startsWith("4.1.")) {
            // we expect this test to fail on 4.1.x - due to a bug in hibernate - remove as soon as 4.1.x fixes the issue.
            expectedException.expect(InvalidDataAccessApiUsageException.class);
            expectedException.expectMessage("No supertype found");
        }
        IdClassExampleDepartment dep = new IdClassExampleDepartment();
        dep.setName("TestDepartment");
        dep.setDepartmentId((-1));
        IdClassExampleEmployee emp = new IdClassExampleEmployee();
        emp.setDepartment(dep);
        emp = employeeRepositoryWithIdClass.save(emp);
        Page<IdClassExampleEmployee> page = employeeRepositoryWithIdClass.findAll(PageRequest.of(0, 1));
        Assert.assertThat(page, is(notNullValue()));
        Assert.assertThat(page.getTotalElements(), is(1L));
    }

    // DATAJPA-497
    @Test
    public void sortByEmbeddedPkFieldInCompositePkWithEmbeddedIdInQueryDsl() {
        EmbeddedIdExampleDepartment dep1 = new EmbeddedIdExampleDepartment();
        dep1.setDepartmentId(1L);
        dep1.setName("Dep1");
        EmbeddedIdExampleDepartment dep2 = new EmbeddedIdExampleDepartment();
        dep2.setDepartmentId(2L);
        dep2.setName("Dep2");
        EmbeddedIdExampleEmployee emp1 = new EmbeddedIdExampleEmployee();
        emp1.setEmployeePk(new EmbeddedIdExampleEmployeePK(3L, null));
        emp1.setDepartment(dep2);
        emp1 = employeeRepositoryWithEmbeddedId.save(emp1);
        EmbeddedIdExampleEmployee emp2 = new EmbeddedIdExampleEmployee();
        emp2.setEmployeePk(new EmbeddedIdExampleEmployeePK(2L, null));
        emp2.setDepartment(dep1);
        emp2 = employeeRepositoryWithEmbeddedId.save(emp2);
        EmbeddedIdExampleEmployee emp3 = new EmbeddedIdExampleEmployee();
        emp3.setEmployeePk(new EmbeddedIdExampleEmployeePK(1L, null));
        emp3.setDepartment(dep2);
        emp3 = employeeRepositoryWithEmbeddedId.save(emp3);
        QEmbeddedIdExampleEmployee emp = QEmbeddedIdExampleEmployee.embeddedIdExampleEmployee;
        List<EmbeddedIdExampleEmployee> result = employeeRepositoryWithEmbeddedId.findAll(emp.employeePk.departmentId.eq(dep2.getDepartmentId()), emp.employeePk.employeeId.asc());
        Assert.assertThat(result, is(notNullValue()));
        Assert.assertThat(result, hasSize(2));
        Assert.assertThat(result.get(0), is(emp3));
        Assert.assertThat(result.get(1), is(emp1));
    }

    // DATAJPA-497
    @Test
    public void sortByEmbeddedPkFieldInCompositePkWithIdClassInQueryDsl() {
        IdClassExampleDepartment dep1 = new IdClassExampleDepartment();
        dep1.setDepartmentId(1L);
        dep1.setName("Dep1");
        IdClassExampleDepartment dep2 = new IdClassExampleDepartment();
        dep2.setDepartmentId(2L);
        dep2.setName("Dep2");
        IdClassExampleEmployee emp1 = new IdClassExampleEmployee();
        emp1.setEmpId(3L);
        emp1.setDepartment(dep2);
        emp1 = employeeRepositoryWithIdClass.save(emp1);
        IdClassExampleEmployee emp2 = new IdClassExampleEmployee();
        emp2.setEmpId(2L);
        emp2.setDepartment(dep1);
        emp2 = employeeRepositoryWithIdClass.save(emp2);
        IdClassExampleEmployee emp3 = new IdClassExampleEmployee();
        emp3.setEmpId(1L);
        emp3.setDepartment(dep2);
        emp3 = employeeRepositoryWithIdClass.save(emp3);
        QIdClassExampleEmployee emp = QIdClassExampleEmployee.idClassExampleEmployee;
        List<IdClassExampleEmployee> result = employeeRepositoryWithIdClass.findAll(emp.department.departmentId.eq(dep2.getDepartmentId()), emp.empId.asc());
        Assert.assertThat(result, is(notNullValue()));
        Assert.assertThat(result, hasSize(2));
        Assert.assertThat(result.get(0), is(emp3));
        Assert.assertThat(result.get(1), is(emp1));
    }

    // DATAJPA-527, DATAJPA-1148
    @Test
    public void testExistsWithIdClass() {
        IdClassExampleDepartment dep = new IdClassExampleDepartment();
        dep.setName("TestDepartment");
        dep.setDepartmentId((-1));
        IdClassExampleEmployee emp = new IdClassExampleEmployee();
        emp.setDepartment(dep);
        save(emp);
        IdClassExampleEmployeePK key = new IdClassExampleEmployeePK();
        key.setDepartment(dep.getDepartmentId());
        key.setEmpId(emp.getEmpId());
        Assert.assertThat(employeeRepositoryWithIdClass.existsById(key), is(true));
        Assert.assertThat(employeeRepositoryWithIdClass.existsById(new IdClassExampleEmployeePK(0L, 0L)), is(false));
    }

    // DATAJPA-527
    @Test
    public void testExistsWithEmbeddedId() {
        EmbeddedIdExampleDepartment dep1 = new EmbeddedIdExampleDepartment();
        dep1.setDepartmentId(1L);
        dep1.setName("Dep1");
        EmbeddedIdExampleEmployeePK key = new EmbeddedIdExampleEmployeePK();
        key.setDepartmentId(1L);
        key.setEmployeeId(1L);
        EmbeddedIdExampleEmployee emp = new EmbeddedIdExampleEmployee();
        emp.setDepartment(dep1);
        emp.setEmployeePk(key);
        emp = employeeRepositoryWithEmbeddedId.save(emp);
        key.setDepartmentId(emp.getDepartment().getDepartmentId());
        key.setEmployeeId(emp.getEmployeePk().getEmployeeId());
        Assert.assertThat(existsById(key), is(true));
    }

    // DATAJPA-611
    @Test
    public void shouldAllowFindAllWithIdsForEntitiesWithCompoundIdClassKeys() {
        IdClassExampleDepartment dep2 = new IdClassExampleDepartment();
        dep2.setDepartmentId(2L);
        dep2.setName("Dep2");
        IdClassExampleEmployee emp1 = new IdClassExampleEmployee();
        emp1.setEmpId(3L);
        emp1.setDepartment(dep2);
        emp1 = employeeRepositoryWithIdClass.save(emp1);
        IdClassExampleDepartment dep1 = new IdClassExampleDepartment();
        dep1.setDepartmentId(1L);
        dep1.setName("Dep1");
        IdClassExampleEmployee emp2 = new IdClassExampleEmployee();
        emp2.setEmpId(2L);
        emp2.setDepartment(dep1);
        emp2 = employeeRepositoryWithIdClass.save(emp2);
        IdClassExampleEmployeePK emp1PK = new IdClassExampleEmployeePK();
        emp1PK.setDepartment(2L);
        emp1PK.setEmpId(3L);
        IdClassExampleEmployeePK emp2PK = new IdClassExampleEmployeePK();
        emp2PK.setDepartment(1L);
        emp2PK.setEmpId(2L);
        List<IdClassExampleEmployee> result = findAllById(Arrays.asList(emp1PK, emp2PK));
        Assert.assertThat(result, hasSize(2));
    }

    // DATAJPA-920
    @Test
    public void shouldExecuteExistsQueryForEntitiesWithEmbeddedId() {
        EmbeddedIdExampleDepartment dep1 = new EmbeddedIdExampleDepartment();
        dep1.setDepartmentId(1L);
        dep1.setName("Dep1");
        EmbeddedIdExampleEmployeePK key = new EmbeddedIdExampleEmployeePK();
        key.setDepartmentId(1L);
        key.setEmployeeId(1L);
        EmbeddedIdExampleEmployee emp = new EmbeddedIdExampleEmployee();
        emp.setDepartment(dep1);
        emp.setEmployeePk(key);
        emp.setName("White");
        employeeRepositoryWithEmbeddedId.save(emp);
        Assert.assertThat(employeeRepositoryWithEmbeddedId.existsByName(emp.getName()), is(true));
    }

    // DATAJPA-920
    @Test
    public void shouldExecuteExistsQueryForEntitiesWithCompoundIdClassKeys() {
        IdClassExampleDepartment dep2 = new IdClassExampleDepartment();
        dep2.setDepartmentId(2L);
        dep2.setName("Dep2");
        IdClassExampleEmployee emp1 = new IdClassExampleEmployee();
        emp1.setEmpId(3L);
        emp1.setDepartment(dep2);
        emp1.setName("White");
        save(emp1);
        Assert.assertThat(employeeRepositoryWithIdClass.existsByName(emp1.getName()), is(true));
        Assert.assertThat(employeeRepositoryWithIdClass.existsByName("Walter"), is(false));
    }
}

