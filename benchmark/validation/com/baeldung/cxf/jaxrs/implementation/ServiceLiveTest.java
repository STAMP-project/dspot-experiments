package com.baeldung.cxf.jaxrs.implementation;


import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Test;


public class ServiceLiveTest {
    private static final String BASE_URL = "http://localhost:8080/baeldung/courses/";

    private static CloseableHttpClient client;

    @Test
    public void whenUpdateNonExistentCourse_thenReceiveNotFoundResponse() throws IOException {
        final HttpPut httpPut = new HttpPut(((ServiceLiveTest.BASE_URL) + "3"));
        final InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("non_existent_course.xml");
        httpPut.setEntity(new InputStreamEntity(resourceStream));
        httpPut.setHeader("Content-Type", "text/xml");
        final HttpResponse response = ServiceLiveTest.client.execute(httpPut);
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
    }

    @Test
    public void whenUpdateUnchangedCourse_thenReceiveNotModifiedResponse() throws IOException {
        final HttpPut httpPut = new HttpPut(((ServiceLiveTest.BASE_URL) + "1"));
        final InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("unchanged_course.xml");
        httpPut.setEntity(new InputStreamEntity(resourceStream));
        httpPut.setHeader("Content-Type", "text/xml");
        final HttpResponse response = ServiceLiveTest.client.execute(httpPut);
        Assert.assertEquals(304, response.getStatusLine().getStatusCode());
    }

    @Test
    public void whenUpdateValidCourse_thenReceiveOKResponse() throws IOException {
        final HttpPut httpPut = new HttpPut(((ServiceLiveTest.BASE_URL) + "2"));
        final InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("changed_course.xml");
        httpPut.setEntity(new InputStreamEntity(resourceStream));
        httpPut.setHeader("Content-Type", "text/xml");
        final HttpResponse response = ServiceLiveTest.client.execute(httpPut);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        final Course course = getCourse(2);
        Assert.assertEquals(2, course.getId());
        Assert.assertEquals("Apache CXF Support for RESTful", course.getName());
    }

    @Test
    public void whenCreateConflictStudent_thenReceiveConflictResponse() throws IOException {
        final HttpPost httpPost = new HttpPost(((ServiceLiveTest.BASE_URL) + "1/students"));
        final InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("conflict_student.xml");
        httpPost.setEntity(new InputStreamEntity(resourceStream));
        httpPost.setHeader("Content-Type", "text/xml");
        final HttpResponse response = ServiceLiveTest.client.execute(httpPost);
        Assert.assertEquals(409, response.getStatusLine().getStatusCode());
    }

    @Test
    public void whenCreateValidStudent_thenReceiveOKResponse() throws IOException {
        final HttpPost httpPost = new HttpPost(((ServiceLiveTest.BASE_URL) + "2/students"));
        final InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("created_student.xml");
        httpPost.setEntity(new InputStreamEntity(resourceStream));
        httpPost.setHeader("Content-Type", "text/xml");
        final HttpResponse response = ServiceLiveTest.client.execute(httpPost);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        final Student student = getStudent(2, 3);
        Assert.assertEquals(3, student.getId());
        Assert.assertEquals("Student C", student.getName());
    }

    @Test
    public void whenDeleteInvalidStudent_thenReceiveNotFoundResponse() throws IOException {
        final HttpDelete httpDelete = new HttpDelete(((ServiceLiveTest.BASE_URL) + "1/students/3"));
        final HttpResponse response = ServiceLiveTest.client.execute(httpDelete);
        Assert.assertEquals(404, response.getStatusLine().getStatusCode());
    }

    @Test
    public void whenDeleteValidStudent_thenReceiveOKResponse() throws IOException {
        final HttpDelete httpDelete = new HttpDelete(((ServiceLiveTest.BASE_URL) + "1/students/1"));
        final HttpResponse response = ServiceLiveTest.client.execute(httpDelete);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        final Course course = getCourse(1);
        Assert.assertEquals(1, course.getStudents().size());
        Assert.assertEquals(2, course.getStudents().get(0).getId());
        Assert.assertEquals("Student B", course.getStudents().get(0).getName());
    }
}

