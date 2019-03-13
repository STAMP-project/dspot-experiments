package com.baeldung.hibernate.findall;


import com.baeldung.hibernate.pojo.Student;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;


public class FindAllUnitTest {
    private Session session;

    private Transaction transaction;

    private FindAll findAll;

    @Test
    public void givenCriteriaQuery_WhenFindAll_ThenGetAllPersons() {
        List<Student> list = findAll.findAllWithCriteriaQuery();
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void givenJpql_WhenFindAll_ThenGetAllPersons() {
        List<Student> list = findAll.findAllWithJpql();
        Assert.assertEquals(3, list.size());
    }
}

