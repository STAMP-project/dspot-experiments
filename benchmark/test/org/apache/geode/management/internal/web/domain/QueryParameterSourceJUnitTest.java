/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.web.domain;


import java.io.IOException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import org.apache.geode.internal.util.IOUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * The QueryParameterSourceJUnitTest class is a test suite of test cases testing the contract and
 * functionality of the QueryParameterSource class.
 * <p/>
 *
 * @see org.apache.geode.management.internal.web.domain.QueryParameterSource
 * @see org.junit.Assert
 * @see org.junit.Test
 * @since GemFire 8.0
 */
public class QueryParameterSourceJUnitTest {
    @Test
    public void testCreateQueryParameterSource() throws MalformedObjectNameException {
        final ObjectName expectedObjectName = ObjectName.getInstance("GemFire:type=Member,*");
        final QueryExp expectedQueryExpression = Query.eq(Query.attr("id"), Query.value("12345"));
        final QueryParameterSource query = new QueryParameterSource(expectedObjectName, expectedQueryExpression);
        Assert.assertNotNull(query);
        Assert.assertSame(expectedObjectName, query.getObjectName());
        Assert.assertSame(expectedQueryExpression, query.getQueryExpression());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException, MalformedObjectNameException {
        final ObjectName expectedObjectName = ObjectName.getInstance("GemFire:type=Member,*");
        final QueryExp expectedQueryExpression = Query.or(Query.eq(Query.attr("name"), Query.value("myName")), Query.eq(Query.attr("id"), Query.value("myId")));
        final QueryParameterSource expectedQuery = new QueryParameterSource(expectedObjectName, expectedQueryExpression);
        Assert.assertNotNull(expectedQuery);
        Assert.assertSame(expectedObjectName, expectedQuery.getObjectName());
        Assert.assertSame(expectedQueryExpression, expectedQuery.getQueryExpression());
        final byte[] queryBytes = IOUtils.serializeObject(expectedQuery);
        Assert.assertNotNull(queryBytes);
        Assert.assertTrue(((queryBytes.length) != 0));
        final Object queryObj = IOUtils.deserializeObject(queryBytes);
        Assert.assertTrue((queryObj instanceof QueryParameterSource));
        final QueryParameterSource actualQuery = ((QueryParameterSource) (queryObj));
        Assert.assertNotSame(expectedQuery, actualQuery);
        Assert.assertNotNull(actualQuery.getObjectName());
        Assert.assertEquals(expectedQuery.getObjectName().toString(), actualQuery.getObjectName().toString());
        Assert.assertNotNull(actualQuery.getQueryExpression());
        Assert.assertEquals(expectedQuery.getQueryExpression().toString(), actualQuery.getQueryExpression().toString());
    }
}

