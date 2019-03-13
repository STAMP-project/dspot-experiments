package com.baeldung.cxf.introduction;


import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import org.junit.Assert;
import org.junit.Test;


public class StudentLiveTest {
    private static QName SERVICE_NAME = new QName("http://introduction.cxf.baeldung.com/", "Baeldung");

    private static QName PORT_NAME = new QName("http://introduction.cxf.baeldung.com/", "BaeldungPort");

    private Service service;

    private Baeldung baeldungProxy;

    private BaeldungImpl baeldungImpl;

    {
        service = Service.create(StudentLiveTest.SERVICE_NAME);
        final String endpointAddress = "http://localhost:8080/baeldung";
        service.addPort(StudentLiveTest.PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
    }

    @Test
    public void whenUsingHelloMethod_thenCorrect() {
        final String endpointResponse = baeldungProxy.hello("Baeldung");
        final String localResponse = baeldungImpl.hello("Baeldung");
        Assert.assertEquals(localResponse, endpointResponse);
    }

    @Test
    public void whenUsingHelloStudentMethod_thenCorrect() {
        final Student student = new StudentImpl("John Doe");
        final String endpointResponse = baeldungProxy.helloStudent(student);
        final String localResponse = baeldungImpl.helloStudent(student);
        Assert.assertEquals(localResponse, endpointResponse);
    }

    @Test
    public void usingGetStudentsMethod_thenCorrect() {
        final Student student1 = new StudentImpl("Adam");
        baeldungProxy.helloStudent(student1);
        final Student student2 = new StudentImpl("Eve");
        baeldungProxy.helloStudent(student2);
        final Map<Integer, Student> students = baeldungProxy.getStudents();
        Assert.assertEquals("Adam", students.get(1).getName());
        Assert.assertEquals("Eve", students.get(2).getName());
    }
}

