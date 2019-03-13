/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jpa;


import JpaConstants.ENTITY_MANAGER;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.examples.SendEmail;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;


public class JpaTest extends Assert {
    private static final Logger LOG = LoggerFactory.getLogger(JpaTest.class);

    protected CamelContext camelContext = new DefaultCamelContext();

    protected ProducerTemplate template;

    protected JpaEndpoint endpoint;

    protected EntityManager entityManager;

    protected TransactionTemplate transactionTemplate;

    protected Consumer consumer;

    protected Exchange receivedExchange;

    protected CountDownLatch latch = new CountDownLatch(1);

    protected String entityName = SendEmail.class.getName();

    protected String queryText = ("select o from " + (entityName)) + " o";

    @Test
    public void testProducerInsertsIntoDatabaseThenConsumerFiresMessageExchange() throws Exception {
        transactionTemplate.execute(new org.springframework.transaction.support.TransactionCallback<Object>() {
            public Object doInTransaction(TransactionStatus status) {
                entityManager.joinTransaction();
                // lets delete any exiting records before the test
                entityManager.createQuery(("delete from " + (entityName))).executeUpdate();
                return null;
            }
        });
        List<?> results = entityManager.createQuery(queryText).getResultList();
        Assert.assertEquals(("Should have no results: " + results), 0, results.size());
        // lets produce some objects
        template.send(endpoint, new Processor() {
            public void process(Exchange exchange) {
                exchange.getIn().setBody(new SendEmail("foo@bar.com"));
            }
        });
        // now lets assert that there is a result
        results = entityManager.createQuery(queryText).getResultList();
        Assert.assertEquals(("Should have results: " + results), 1, results.size());
        SendEmail mail = ((SendEmail) (results.get(0)));
        Assert.assertEquals("address property", "foo@bar.com", mail.getAddress());
        // now lets create a consumer to consume it
        consumer = endpoint.createConsumer(new Processor() {
            public void process(Exchange e) {
                JpaTest.LOG.info(("Received exchange: " + (e.getIn())));
                receivedExchange = e;
                // should have a EntityManager
                EntityManager entityManager = e.getIn().getHeader(ENTITY_MANAGER, EntityManager.class);
                Assert.assertNotNull("Should have a EntityManager as header", entityManager);
                latch.countDown();
            }
        });
        consumer.start();
        Assert.assertTrue(latch.await(50, TimeUnit.SECONDS));
        Assert.assertNotNull(receivedExchange);
        SendEmail result = receivedExchange.getIn().getBody(SendEmail.class);
        Assert.assertNotNull("Received a POJO", result);
        Assert.assertEquals("address property", "foo@bar.com", result.getAddress());
    }
}

