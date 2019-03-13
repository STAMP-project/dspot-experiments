/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.jdo;


import com.querydsl.jdo.test.domain.QProduct;
import org.junit.Assert;
import org.junit.Test;


public class AggregateTest extends AbstractJDOTest {
    private final QProduct product = QProduct.product;

    @Test
    public void unique() {
        double min = 200.0;
        double avg = 400.0;
        double max = 600.0;
        Assert.assertEquals(Double.valueOf(min), query().from(product).select(product.price.min()).fetchOne());
        Assert.assertEquals(Double.valueOf(avg), query().from(product).select(product.price.avg()).fetchOne());
        Assert.assertEquals(Double.valueOf(max), query().from(product).select(product.price.max()).fetchOne());
    }

    @Test
    public void list() {
        double min = 200.0;
        double avg = 400.0;
        double max = 600.0;
        Assert.assertEquals(Double.valueOf(min), query().from(product).select(product.price.min()).fetch().get(0));
        Assert.assertEquals(Double.valueOf(avg), query().from(product).select(product.price.avg()).fetch().get(0));
        Assert.assertEquals(Double.valueOf(max), query().from(product).select(product.price.max()).fetch().get(0));
    }
}

