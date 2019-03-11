package com.baeldung.ejb.setup.test;


import com.baeldung.ejb.client.EJBClient;
import com.baeldung.ejb.tutorial.HelloWorldBean;
import org.junit.Assert;
import org.junit.Test;


public class EJBSetupIntegrationTest {
    @Test
    public void EJBClientTest() {
        EJBClient ejbClient = new EJBClient();
        HelloWorldBean bean = new HelloWorldBean();
        Assert.assertEquals(bean.getHelloWorld(), ejbClient.getEJBRemoteMessage());
    }
}

