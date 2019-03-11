package com.baeldung.hibernate.interceptors;


import com.baeldung.hibernate.interceptors.entity.User;
import java.io.Serializable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;


public class HibernateInterceptorUnitTest {
    private static SessionFactory sessionFactory;

    private static Serializable userId;

    @Test
    public void givenHibernateInterceptorAndSessionScoped_whenUserCreated_shouldSucceed() {
        Session session = HibernateInterceptorUnitTest.sessionFactory.withOptions().interceptor(new CustomInterceptor()).openSession();
        User user = new User("Benjamin Franklin");
        Transaction transaction = session.beginTransaction();
        HibernateInterceptorUnitTest.userId = session.save(user);
        transaction.commit();
        session.close();
    }

    @Test
    public void givenHibernateInterceptorAndSessionFactoryScoped_whenUserModified_shouldSucceed() {
        Session session = HibernateInterceptorUnitTest.sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        User user = session.load(User.class, HibernateInterceptorUnitTest.userId);
        if (user != null) {
            user.setAbout("I am a scientist.");
            session.update(user);
        }
        transaction.commit();
        session.close();
    }
}

