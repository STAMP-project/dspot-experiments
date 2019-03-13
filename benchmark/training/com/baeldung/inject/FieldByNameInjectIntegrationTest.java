package com.baeldung.inject;


import com.baeldung.configuration.ApplicationContextTestInjectName;
import com.baeldung.dependency.ArbitraryDependency;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ApplicationContextTestInjectName.class)
public class FieldByNameInjectIntegrationTest {
    @Inject
    @Named("yetAnotherFieldInjectDependency")
    private ArbitraryDependency yetAnotherFieldInjectDependency;

    @Test
    public void givenInjectQualifier_WhenSetOnField_ThenDependencyValid() {
        Assert.assertNotNull(yetAnotherFieldInjectDependency);
        Assert.assertEquals("Yet Another Arbitrary Dependency", yetAnotherFieldInjectDependency.toString());
    }
}

