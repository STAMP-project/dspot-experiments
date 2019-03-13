package com.vip.saturn.job.console.aop.aspect;


import com.vip.saturn.job.console.utils.AuditInfoContext;
import com.vip.saturn.job.console.utils.DummyAppender;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContextAOP.class)
public class AuditLogAspectTest {
    @Autowired
    private TestAspectClass testClass;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession httpSession;

    private DummyAppender dummyAppender = new DummyAppender();

    @Test
    public void testRESTRequestAuditLog() {
        prepareRequest("192.168.1.1", "/home/path", null);
        testClass.method1();
        Assert.assertTrue(AuditInfoContext.currentAuditInfo().isEmpty());
        Assert.assertEquals("log size should be 1", 1, dummyAppender.getEvents().size());
        Assert.assertEquals("log content is not equal", "[INFO] REST API:[method1] path:[/home/path] is called by IP:[192.168.1.1], result is success. Context info:{namespace=www.abc.com, jobName=jobA, jobNames=[jobB, jobC]}.", dummyAppender.getLastEvent().toString());
    }

    @Test
    public void testWEBRequestAuditLog() {
        prepareRequest("192.168.1.2", "/home/path2", "usera");
        testClass.method2();
        Assert.assertTrue(AuditInfoContext.currentAuditInfo().isEmpty());
        Assert.assertEquals("log size should be 1", 1, dummyAppender.getEvents().size());
        Assert.assertEquals("log content is not equal", "[INFO] GUI API:[method2] path:[/home/path2] is called by User:[usera] with IP:[192.168.1.2], result is success. Context info:{namespace=www.abc.com, jobName=jobA, jobNames=[jobB, jobC]}.", dummyAppender.getLastEvent().toString());
    }

    @Test
    public void testWebRequestAuditLogButFail() {
        prepareRequest("192.168.1.3", "/home/path3", "userb");
        try {
            testClass.method3();
        } catch (RuntimeException e) {
            // do nothing
        }
        Assert.assertTrue(AuditInfoContext.currentAuditInfo().isEmpty());
        Assert.assertEquals("log size should be 1", 1, dummyAppender.getEvents().size());
        Assert.assertEquals("log content is not equal", "[INFO] GUI API:[method3] path:[/home/path3] is called by User:[userb] with IP:[192.168.1.3], result is failed. Context info:{namespace=www.abc.com, jobName=jobA, jobNames=[jobB, jobC]}.", dummyAppender.getLastEvent().toString());
    }
}

