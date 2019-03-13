package com.baeldung.hibernate.manytomany;


import com.baeldung.hibernate.manytomany.model.Project;
import com.baeldung.manytomany.spring.PersistenceConfig;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfig.class }, loader = AnnotationConfigContextLoader.class)
public class HibernateManyToManyAnnotationJavaConfigMainIntegrationTest {
    @Autowired
    private SessionFactory sessionFactory;

    private Session session;

    @Test
    public final void whenEntitiesAreCreated_thenNoExceptions() {
        Set<Project> projects = new HashSet<Project>();
        projects.add(new Project("IT Project"));
        projects.add(new Project("Networking Project"));
        session.persist(new com.baeldung.hibernate.manytomany.model.Employee("Peter", "Oven", projects));
        session.persist(new com.baeldung.hibernate.manytomany.model.Employee("Allan", "Norman", projects));
    }
}

