package com.baeldung.hibernate.proxy;


import org.hibernate.proxy.HibernateProxy;
import org.junit.Assert;
import org.junit.Test;


public class HibernateProxyUnitTest {
    private SessionFactory factory;

    private Session session;

    private Company workplace;

    private Employee albert;

    private Employee bob;

    private Employee charlotte;

    @Test
    public void givenAnInexistentEmployeeId_whenUseGetMethod_thenReturnNull() {
        Employee employee = session.get(Employee.class, 14L);
        Assert.assertNull(employee);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void givenAnNonExistentEmployeeId_whenUseLoadMethod_thenThrowObjectNotFoundException() {
        Employee employee = session.load(Employee.class, 999L);
        Assert.assertNotNull(employee);
        employee.getFirstName();
    }

    @Test
    public void givenAnNonExistentEmployeeId_whenUseLoadMethod_thenReturnAProxy() {
        Employee employee = session.load(Employee.class, 14L);
        Assert.assertNotNull(employee);
        Assert.assertTrue((employee instanceof HibernateProxy));
    }

    @Test
    public void givenAnEmployeeFromACompany_whenUseLoadMethod_thenCompanyIsAProxy() {
        Transaction tx = session.beginTransaction();
        this.workplace = new Company("Bizco");
        session.save(workplace);
        this.albert = new Employee(workplace, "Albert");
        session.save(albert);
        session.flush();
        session.clear();
        tx.commit();
        this.session = factory.openSession();
        Employee proxyAlbert = session.load(Employee.class, albert.getId());
        Assert.assertTrue((proxyAlbert instanceof HibernateProxy));
        // with many-to-one lazy-loading, associations remain proxies
        Assert.assertTrue(((proxyAlbert.getWorkplace()) instanceof HibernateProxy));
    }

    @Test
    public void givenACompanyWithEmployees_whenUseLoadMethod_thenEmployeesAreProxies() {
        Transaction tx = session.beginTransaction();
        this.workplace = new Company("Bizco");
        session.save(workplace);
        this.albert = new Employee(workplace, "Albert");
        session.save(albert);
        session.flush();
        session.clear();
        tx.commit();
        this.session = factory.openSession();
        Company proxyBizco = session.load(Company.class, workplace.getId());
        Assert.assertTrue((proxyBizco instanceof HibernateProxy));
        // with one-to-many, associations aren't proxies
        Assert.assertFalse(((proxyBizco.getEmployees().iterator().next()) instanceof HibernateProxy));
    }

    @Test
    public void givenThreeEmployees_whenLoadThemWithBatch_thenReturnAllOfThemWithOneQuery() {
        Transaction tx = session.beginTransaction();
        // We are saving 3 entities with one flush
        this.workplace = new Company("Bizco");
        session.save(workplace);
        this.albert = new Employee(workplace, "Albert");
        session.save(albert);
        this.bob = new Employee(workplace, "Bob");
        session.save(bob);
        this.charlotte = new Employee(workplace, "Charlotte");
        session.save(charlotte);
        session.flush();
        session.clear();
        tx.commit();
        session = factory.openSession();
        Employee proxyAlbert = session.load(Employee.class, this.albert.getId());
        Assert.assertNotNull(proxyAlbert);
        Assert.assertTrue((proxyAlbert instanceof HibernateProxy));
        Employee proxyBob = session.load(Employee.class, this.bob.getId());
        Assert.assertNotNull(proxyBob);
        Assert.assertTrue((proxyBob instanceof HibernateProxy));
        Employee proxyCharlotte = session.load(Employee.class, this.charlotte.getId());
        Assert.assertNotNull(proxyCharlotte);
        Assert.assertTrue((proxyCharlotte instanceof HibernateProxy));
        // Fetching from database 3 entities with one call
        // Select from log: where employee0_.id in (?, ?, ?)
        proxyAlbert.getFirstName();
        Assert.assertEquals(proxyAlbert, this.albert);
        Assert.assertEquals(proxyBob, this.bob);
        Assert.assertEquals(proxyCharlotte, this.charlotte);
    }
}

