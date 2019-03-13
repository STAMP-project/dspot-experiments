package org.baeldung.ex.mappingexception;


import java.io.IOException;
import org.baeldung.ex.mappingexception.cause4.persistence.model.Foo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;


public class Cause4MappingExceptionManualTest {
    // tests
    @Test
    public final void givenEntityIsPersisted_thenException() throws IOException {
        final SessionFactory sessionFactory = configureSessionFactory();
        final Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(new Foo());
        session.getTransaction().commit();
    }
}

