package com.baeldung.cxf.aegis;


import java.util.Date;
import org.apache.cxf.aegis.AegisContext;
import org.junit.Assert;
import org.junit.Test;


public class BaeldungIntegrationTest {
    private AegisContext context;

    private String fileName = "baeldung.xml";

    @Test
    public void whenMarshalingAndUnmarshalingCourseRepo_thenCorrect() throws Exception {
        initializeContext();
        CourseRepo inputRepo = initCourseRepo();
        marshalCourseRepo(inputRepo);
        CourseRepo outputRepo = unmarshalCourseRepo();
        Course restCourse = outputRepo.getCourses().get(1);
        Course securityCourse = outputRepo.getCourses().get(2);
        Assert.assertEquals("Welcome to Beldung!", outputRepo.getGreeting());
        Assert.assertEquals("REST with Spring", restCourse.getName());
        Assert.assertEquals(new Date(1234567890000L), restCourse.getEnrolmentDate());
        Assert.assertNull(restCourse.getInstructor());
        Assert.assertEquals("Learn Spring Security", securityCourse.getName());
        Assert.assertEquals(new Date(1456789000000L), securityCourse.getEnrolmentDate());
        Assert.assertNull(securityCourse.getInstructor());
    }
}

