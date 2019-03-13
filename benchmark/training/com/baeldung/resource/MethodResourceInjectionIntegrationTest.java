package com.baeldung.resource;


import com.baeldung.configuration.ApplicationContextTestResourceNameType;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ApplicationContextTestResourceNameType.class)
public class MethodResourceInjectionIntegrationTest {
    private File defaultFile;

    @Test
    public void givenResourceAnnotation_WhenSetter_ThenDependencyValid() {
        Assert.assertNotNull(defaultFile);
        Assert.assertEquals("namedFile.txt", defaultFile.getName());
    }
}

