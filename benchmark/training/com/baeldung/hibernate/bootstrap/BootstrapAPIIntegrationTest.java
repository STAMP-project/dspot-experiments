package com.baeldung.hibernate.bootstrap;


import com.baeldung.hibernate.pojo.Movie;
import java.io.IOException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.junit.Assert;
import org.junit.Test;


public class BootstrapAPIIntegrationTest {
    SessionFactory sessionFactory = null;

    @Test
    public void whenServiceRegistryAndMetadata_thenSessionFactory() throws IOException {
        BootstrapServiceRegistry bootstrapRegistry = new BootstrapServiceRegistryBuilder().build();
        ServiceRegistry standardRegistry = // No need for hibernate.cfg.xml file, an hibernate.properties is sufficient.
        // .configure()
        build();
        MetadataSources metadataSources = new MetadataSources(standardRegistry);
        metadataSources.addAnnotatedClass(Movie.class);
        Metadata metadata = metadataSources.getMetadataBuilder().build();
        sessionFactory = metadata.buildSessionFactory();
        Assert.assertNotNull(sessionFactory);
        sessionFactory.close();
    }
}

