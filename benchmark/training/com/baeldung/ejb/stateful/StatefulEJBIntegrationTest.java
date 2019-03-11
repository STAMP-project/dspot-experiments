package com.baeldung.ejb.stateful;


import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class StatefulEJBIntegrationTest {
    @Inject
    private EJBClient1 ejbClient1;

    @Inject
    private EJBClient2 ejbClient2;

    @Test
    public void givenOneStatefulBean_whenTwoClientsSetValueOnBean_thenClientStateIsMaintained() {
        // act
        ejbClient1.statefulEJB.name = "Client 1";
        ejbClient2.statefulEJB.name = "Client 2";
        // assert
        Assert.assertNotEquals(ejbClient1.statefulEJB.name, ejbClient2.statefulEJB.name);
        Assert.assertEquals("Client 1", ejbClient1.statefulEJB.name);
        Assert.assertEquals("Client 2", ejbClient2.statefulEJB.name);
    }
}

