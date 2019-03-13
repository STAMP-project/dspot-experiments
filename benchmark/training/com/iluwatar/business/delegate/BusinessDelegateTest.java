/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.business.delegate;


import ServiceType.EJB;
import ServiceType.JMS;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * The Business Delegate pattern adds an abstraction layer between the presentation and business
 * tiers. By using the pattern we gain loose coupling between the tiers. The Business Delegate
 * encapsulates knowledge about how to locate, connect to, and interact with the business objects
 * that make up the application.
 *
 * <p>Some of the services the Business Delegate uses are instantiated directly, and some can be
 * retrieved through service lookups. The Business Delegate itself may contain business logic too
 * potentially tying together multiple service calls, exception handling, retrying etc.
 */
public class BusinessDelegateTest {
    private EjbService ejbService;

    private JmsService jmsService;

    private BusinessLookup businessLookup;

    private BusinessDelegate businessDelegate;

    /**
     * In this example the client ({@link Client}) utilizes a business delegate (
     * {@link BusinessDelegate}) to execute a task. The Business Delegate then selects the appropriate
     * service and makes the service call.
     */
    @Test
    public void testBusinessDelegate() {
        // setup a client object
        Client client = new Client(businessDelegate);
        // set the service type
        businessDelegate.setServiceType(EJB);
        // action
        client.doTask();
        // verifying that the businessDelegate was used by client during doTask() method.
        Mockito.verify(businessDelegate).doTask();
        Mockito.verify(ejbService).doProcessing();
        // set the service type
        businessDelegate.setServiceType(JMS);
        // action
        client.doTask();
        // verifying that the businessDelegate was used by client during doTask() method.
        Mockito.verify(businessDelegate, Mockito.times(2)).doTask();
        Mockito.verify(jmsService).doProcessing();
    }
}

