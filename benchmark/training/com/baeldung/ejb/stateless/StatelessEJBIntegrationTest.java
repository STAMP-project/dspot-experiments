package com.baeldung.ejb.stateless;


import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class StatelessEJBIntegrationTest {
    @Inject
    private EJBClient1 ejbClient1;

    @Inject
    private EJBClient2 ejbClient2;

    @Test
    public void givenOneStatelessBean_whenStateIsSetInOneBean_secondBeanShouldHaveSameState() {
        // act
        ejbClient1.statelessEJB.name = "Client 1";
        // assert
        Assert.assertEquals("Client 1", ejbClient1.statelessEJB.name);
        Assert.assertEquals("Client 1", ejbClient2.statelessEJB.name);
    }

    @Test
    public void givenOneStatelessBean_whenStateIsSetInBothBeans_secondBeanShouldHaveSecondBeanState() {
        // act
        ejbClient1.statelessEJB.name = "Client 1";
        ejbClient2.statelessEJB.name = "Client 2";
        // assert
        Assert.assertEquals("Client 2", ejbClient2.statelessEJB.name);
    }
}

