package com.baeldung.resource;


import com.baeldung.configuration.ApplicationContextTestResourceQualifier;
import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ApplicationContextTestResourceQualifier.class)
public class MethodByQualifierResourceIntegrationTest {
    private File arbDependency;

    private File anotherArbDependency;

    @Test
    public void givenResourceQualifier_WhenSetter_ThenValidDependencies() {
        Assert.assertNotNull(arbDependency);
        Assert.assertEquals("namedFile.txt", arbDependency.getName());
        Assert.assertNotNull(anotherArbDependency);
        Assert.assertEquals("defaultFile.txt", anotherArbDependency.getName());
    }
}

