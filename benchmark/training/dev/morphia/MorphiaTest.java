package dev.morphia;


import dev.morphia.mapping.MappedClass;
import dev.morphia.testmappackage.SimpleEntity;
import dev.morphia.testmappackage.testmapsubpackage.SimpleEntityInSubPackage;
import dev.morphia.testmappackage.testmapsubpackage.testmapsubsubpackage.SimpleEntityInSubSubPackage;
import java.util.ArrayList;
import java.util.Collection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MorphiaTest extends TestBase {
    @Test
    public void shouldOnlyMapEntitiesInTheGivenPackage() {
        // when
        final Morphia morphia = new Morphia();
        morphia.mapPackage("dev.morphia.testmappackage");
        // then
        Collection<MappedClass> mappedClasses = morphia.getMapper().getMappedClasses();
        Assert.assertThat(mappedClasses.size(), CoreMatchers.is(1));
        Assert.assertEquals(mappedClasses.iterator().next().getClazz(), SimpleEntity.class);
    }

    @Test
    public void testSubPackagesMapping() {
        // when
        final Morphia morphia = new Morphia();
        morphia.getMapper().getOptions().setMapSubPackages(true);
        morphia.mapPackage("dev.morphia.testmappackage");
        // then
        Collection<MappedClass> mappedClasses = morphia.getMapper().getMappedClasses();
        Assert.assertThat(mappedClasses.size(), CoreMatchers.is(3));
        Collection<Class<?>> classes = new ArrayList<Class<?>>();
        for (MappedClass mappedClass : mappedClasses) {
            classes.add(mappedClass.getClazz());
        }
        Assert.assertTrue(classes.contains(SimpleEntity.class));
        Assert.assertTrue(classes.contains(SimpleEntityInSubPackage.class));
        Assert.assertTrue(classes.contains(SimpleEntityInSubSubPackage.class));
    }
}

