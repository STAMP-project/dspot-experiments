package com.baeldung.autowired;


import com.baeldung.configuration.ApplicationContextTestAutowiredType;
import com.baeldung.dependency.ArbitraryDependency;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ApplicationContextTestAutowiredType.class)
public class FieldAutowiredIntegrationTest {
    @Autowired
    private ArbitraryDependency fieldDependency;

    @Test
    public void givenAutowired_WhenSetOnField_ThenDependencyResolved() {
        Assert.assertNotNull(fieldDependency);
        Assert.assertEquals("Arbitrary Dependency", fieldDependency.toString());
    }
}

