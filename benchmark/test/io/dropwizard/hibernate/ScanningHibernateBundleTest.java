package io.dropwizard.hibernate;


import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ScanningHibernateBundleTest {
    @Test
    public void testFindEntityClassesFromDirectory() {
        // given
        String packageWithEntities = "io.dropwizard.hibernate.fake.entities.pckg";
        // when
        List<Class<?>> findEntityClassesFromDirectory = ScanningHibernateBundle.findEntityClassesFromDirectory(new String[]{ packageWithEntities });
        // then
        Assertions.assertFalse(findEntityClassesFromDirectory.isEmpty());
        Assertions.assertEquals(4, findEntityClassesFromDirectory.size());
    }

    @Test
    public void testFindEntityClassesFromMultipleDirectories() {
        // given
        String packageWithEntities = "io.dropwizard.hibernate.fake.entities.pckg";
        String packageWithEntities2 = "io.dropwizard.hibernate.fake2.entities.pckg";
        // when
        List<Class<?>> findEntityClassesFromDirectory = ScanningHibernateBundle.findEntityClassesFromDirectory(new String[]{ packageWithEntities, packageWithEntities2 });
        // then
        Assertions.assertFalse(findEntityClassesFromDirectory.isEmpty());
        Assertions.assertEquals(8, findEntityClassesFromDirectory.size());
    }
}

