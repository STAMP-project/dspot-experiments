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


import com.querydsl.jdo.test.domain.Product;
import com.querydsl.jdo.test.domain.QProduct;
import com.querydsl.jdo.test.domain.QStore;
import org.junit.Test;


public class JDOQLMethodsTest extends AbstractJDOTest {
    private QProduct product = QProduct.product;

    private QStore store = QStore.store;

    @Test
    public void test() {
        Product p = query().from(product).limit(1).select(product).fetchOne();
        for (BooleanExpression f : getFilters(product.name, product.description, "A0", store.products, p, store.productsByName, "A0", p, product.amount)) {
            query().from(store, product).where(f).select(store, product);
        }
    }
}

