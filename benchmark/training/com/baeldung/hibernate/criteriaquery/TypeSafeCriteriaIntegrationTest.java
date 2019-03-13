package com.baeldung.hibernate.criteriaquery;


import Student_.gradYear;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Assert;
import org.junit.Test;


public class TypeSafeCriteriaIntegrationTest {
    private static SessionFactory sessionFactory;

    private Session session;

    @Test
    public void givenStudentData_whenUsingTypeSafeCriteriaQuery_thenSearchAllStudentsOfAGradYear() {
        prepareData();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> criteriaQuery = cb.createQuery(Student.class);
        Root<Student> root = criteriaQuery.from(Student.class);
        criteriaQuery.select(root).where(cb.equal(root.get(gradYear), 1965));
        Query<Student> query = session.createQuery(criteriaQuery);
        List<Student> results = query.getResultList();
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Student student = results.get(0);
        Assert.assertEquals("Ken", student.getFirstName());
        Assert.assertEquals("Thompson", student.getLastName());
        Assert.assertEquals(1965, student.getGradYear());
    }
}

