package com.baeldung.client;


import com.baeldung.api.CabBookingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = { BurlapClient.class, HessianClient.class })
@RunWith(SpringRunner.class)
public class CabBookingServiceIntegrationTest {
    static Logger log = LoggerFactory.getLogger(CabBookingServiceIntegrationTest.class);

    @Autowired
    @Qualifier("burlapInvoker")
    CabBookingService burlapClient;

    @Autowired
    @Qualifier("hessianInvoker")
    CabBookingService hessianClient;

    static Thread serverThread;

    @Test
    public void bookACabWithBurlapClient() throws InterruptedException {
        bookACab(this.burlapClient);
    }

    @Test
    public void bookACabWithHessianClient() throws InterruptedException {
        bookACab(this.hessianClient);
    }
}

