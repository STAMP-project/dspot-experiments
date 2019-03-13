package com.baeldung.inject;


import com.baeldung.configuration.ApplicationContextTestInjectType;
import com.baeldung.dependency.ArbitraryDependency;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ApplicationContextTestInjectType.class)
public class FieldInjectIntegrationTest {
    @Inject
    private ArbitraryDependency fieldInjectDependency;

    @Test
    public void givenInjectAnnotation_WhenOnField_ThenValidDependency() {
        Assert.assertNotNull(fieldInjectDependency);
        Assert.assertEquals("Arbitrary Dependency", fieldInjectDependency.toString());
    }
}

