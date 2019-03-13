package com.baeldung.resource;


import com.baeldung.configuration.ApplicationContextTestResourceQualifier;
import java.io.File;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ApplicationContextTestResourceQualifier.class)
public class QualifierResourceInjectionIntegrationTest {
    @Resource
    @Qualifier("defaultFile")
    private File dependency1;

    @Resource
    @Qualifier("namedFile")
    private File dependency2;

    @Test
    public void givenResourceAnnotation_WhenField_ThenDependency1Valid() {
        Assert.assertNotNull(dependency1);
        Assert.assertEquals("defaultFile.txt", dependency1.getName());
    }

    @Test
    public void givenResourceQualifier_WhenField_ThenDependency2Valid() {
        Assert.assertNotNull(dependency2);
        Assert.assertEquals("namedFile.txt", dependency2.getName());
    }
}

