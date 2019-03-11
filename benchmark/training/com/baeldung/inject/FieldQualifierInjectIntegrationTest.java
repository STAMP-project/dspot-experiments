package com.baeldung.inject;


import com.baeldung.configuration.ApplicationContextTestInjectQualifier;
import com.baeldung.dependency.ArbitraryDependency;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ApplicationContextTestInjectQualifier.class)
public class FieldQualifierInjectIntegrationTest {
    @Inject
    @Qualifier("defaultFile")
    private ArbitraryDependency defaultDependency;

    @Inject
    @Qualifier("namedFile")
    private ArbitraryDependency namedDependency;

    @Test
    public void givenInjectQualifier_WhenOnField_ThenDefaultFileValid() {
        Assert.assertNotNull(defaultDependency);
        Assert.assertEquals("Arbitrary Dependency", defaultDependency.toString());
    }

    @Test
    public void givenInjectQualifier_WhenOnField_ThenNamedFileValid() {
        Assert.assertNotNull(defaultDependency);
        Assert.assertEquals("Another Arbitrary Dependency", namedDependency.toString());
    }
}

