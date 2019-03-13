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
package org.apache.camel.component.schematron;


import Constants.FAILED;
import Constants.SUCCESS;
import Constants.VALIDATION_STATUS;
import javax.xml.transform.sax.SAXSource;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.xml.sax.InputSource;


/**
 * Schematron Producer Unit Test.
 */
public class SchematronProducerTest extends CamelTestSupport {
    private static SchematronProducer producer;

    @Test
    public void testProcessValidXML() throws Exception {
        Exchange exc = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        exc.getIn().setBody(ClassLoader.getSystemResourceAsStream("xml/article-1.xml"));
        // process xml payload
        SchematronProducerTest.producer.process(exc);
        // assert
        assertTrue(exc.getOut().getHeader(VALIDATION_STATUS).equals(SUCCESS));
    }

    @Test
    public void testProcessInValidXML() throws Exception {
        Exchange exc = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        exc.getIn().setBody(ClassLoader.getSystemResourceAsStream("xml/article-2.xml"));
        // process xml payload
        SchematronProducerTest.producer.process(exc);
        // assert
        assertTrue(exc.getOut().getHeader(VALIDATION_STATUS).equals(FAILED));
    }

    @Test
    public void testProcessValidXMLAsSource() throws Exception {
        Exchange exc = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        exc.getIn().setBody(new SAXSource(SchematronProducerTest.getXMLReader(), new InputSource(ClassLoader.getSystemResourceAsStream("xml/article-1.xml"))));
        // process xml payload
        SchematronProducerTest.producer.process(exc);
        // assert
        assertTrue(exc.getOut().getHeader(VALIDATION_STATUS).equals(SUCCESS));
    }

    @Test
    public void testProcessInValidXMLAsSource() throws Exception {
        Exchange exc = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        exc.getIn().setBody(new SAXSource(SchematronProducerTest.getXMLReader(), new InputSource(ClassLoader.getSystemResourceAsStream("xml/article-2.xml"))));
        // process xml payload
        SchematronProducerTest.producer.process(exc);
        // assert
        assertTrue(exc.getOut().getHeader(VALIDATION_STATUS).equals(FAILED));
    }
}

