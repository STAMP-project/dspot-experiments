/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.alibaba.dubbo.http.matcher;


import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_JSON_VALUE;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;


/**
 * {@link AbstractMediaTypeExpression} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 */
public abstract class AbstractMediaTypeExpressionTest<T extends AbstractMediaTypeExpression> {
    @Test
    public void testGetMediaTypeAndNegated() {
        // Normal
        AbstractMediaTypeExpression expression = createExpression(APPLICATION_JSON_VALUE);
        Assert.assertEquals(APPLICATION_JSON, expression.getMediaType());
        Assert.assertFalse(expression.isNegated());
        // Negated
        expression = createExpression(("!" + (MediaType.APPLICATION_JSON_VALUE)));
        Assert.assertEquals(APPLICATION_JSON, expression.getMediaType());
        Assert.assertTrue(expression.isNegated());
    }

    @Test
    public void testEqualsAndHashCode() {
        Assert.assertEquals(createExpression(APPLICATION_JSON_VALUE), createExpression(APPLICATION_JSON_VALUE));
        Assert.assertEquals(createExpression(APPLICATION_JSON_VALUE).hashCode(), createExpression(APPLICATION_JSON_VALUE).hashCode());
    }

    @Test
    public void testCompareTo() {
        Assert.assertEquals(0, createExpression(APPLICATION_JSON_VALUE).compareTo(createExpression(APPLICATION_JSON_VALUE)));
    }
}

