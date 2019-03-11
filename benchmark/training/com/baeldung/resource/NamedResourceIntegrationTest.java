package com.baeldung.resource;


import com.baeldung.configuration.ApplicationContextTestResourceNameType;
import java.io.File;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = ApplicationContextTestResourceNameType.class)
public class NamedResourceIntegrationTest {
    @Resource(name = "namedFile")
    private File testFile;

    @Test
    public void givenResourceAnnotation_WhenOnField_THEN_DEPENDENCY_Found() {
        Assert.assertNotNull(testFile);
        Assert.assertEquals("namedFile.txt", testFile.getName());
    }
}

