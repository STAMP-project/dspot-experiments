/**
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.xml;


import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.xml.domain.QualifiedTrade;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;


public class Jaxb2NamespaceUnmarshallingTests {
    private StaxEventItemReader<QualifiedTrade> reader = new StaxEventItemReader();

    private Resource resource = new org.springframework.core.io.ClassPathResource(ClassUtils.addResourcePathToPackagePath(getClass(), "domain/trades.xml"));

    @Test
    public void testUnmarshal() throws Exception {
        QualifiedTrade trade = ((QualifiedTrade) (getUnmarshaller().unmarshal(new StreamSource(new StringReader(Jaxb2NamespaceUnmarshallingTests.TRADE_XML)))));
        Assert.assertEquals("XYZ0001", trade.getIsin());
        Assert.assertEquals(5, trade.getQuantity());
        Assert.assertEquals(new BigDecimal("11.39"), trade.getPrice());
        Assert.assertEquals("Customer1", trade.getCustomer());
    }

    @Test
    public void testRead() throws Exception {
        QualifiedTrade result;
        List<QualifiedTrade> results = new ArrayList<>();
        while ((result = reader.read()) != null) {
            results.add(result);
        } 
        checkResults(results);
    }

    private static String TRADE_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><trade xmlns=\"urn:org.springframework.batch.io.oxm.domain\">" + ("<customer>Customer1</customer><isin>XYZ0001</isin><price>11.39</price><quantity>5</quantity>" + "</trade>");
}

