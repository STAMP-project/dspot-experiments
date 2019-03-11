/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.virtual;


import com.google.common.base.Supplier;
import java.util.List;
import org.apache.druid.common.guava.SettableSupplier;
import org.junit.Assert;
import org.junit.Test;


public class ExpressionColumnValueSelectorTest {
    @Test
    public void testSupplierFromDimensionSelector() {
        final SettableSupplier<String> settableSupplier = new SettableSupplier();
        final Supplier<Object> supplier = ExpressionSelectors.supplierFromDimensionSelector(ExpressionColumnValueSelectorTest.dimensionSelectorFromSupplier(settableSupplier));
        Assert.assertNotNull(supplier);
        Assert.assertEquals(null, supplier.get());
        settableSupplier.set(null);
        Assert.assertEquals(null, supplier.get());
        settableSupplier.set("1234");
        Assert.assertEquals("1234", supplier.get());
    }

    @Test
    public void testSupplierFromObjectSelectorObject() {
        final SettableSupplier<Object> settableSupplier = new SettableSupplier();
        final Supplier<Object> supplier = ExpressionSelectors.supplierFromObjectSelector(ExpressionColumnValueSelectorTest.objectSelectorFromSupplier(settableSupplier, Object.class));
        Assert.assertNotNull(supplier);
        Assert.assertEquals(null, supplier.get());
        settableSupplier.set(1.1F);
        Assert.assertEquals(1.1F, supplier.get());
        settableSupplier.set(1L);
        Assert.assertEquals(1L, supplier.get());
        settableSupplier.set("1234");
        Assert.assertEquals("1234", supplier.get());
        settableSupplier.set("1.234");
        Assert.assertEquals("1.234", supplier.get());
    }

    @Test
    public void testSupplierFromObjectSelectorNumber() {
        final SettableSupplier<Number> settableSupplier = new SettableSupplier();
        final Supplier<Object> supplier = ExpressionSelectors.supplierFromObjectSelector(ExpressionColumnValueSelectorTest.objectSelectorFromSupplier(settableSupplier, Number.class));
        Assert.assertNotNull(supplier);
        Assert.assertEquals(null, supplier.get());
        settableSupplier.set(1.1F);
        Assert.assertEquals(1.1F, supplier.get());
        settableSupplier.set(1L);
        Assert.assertEquals(1L, supplier.get());
    }

    @Test
    public void testSupplierFromObjectSelectorString() {
        final SettableSupplier<String> settableSupplier = new SettableSupplier();
        final Supplier<Object> supplier = ExpressionSelectors.supplierFromObjectSelector(ExpressionColumnValueSelectorTest.objectSelectorFromSupplier(settableSupplier, String.class));
        Assert.assertNotNull(supplier);
        Assert.assertEquals(null, supplier.get());
        settableSupplier.set("1.1");
        Assert.assertEquals("1.1", supplier.get());
        settableSupplier.set("1");
        Assert.assertEquals("1", supplier.get());
    }

    @Test
    public void testSupplierFromObjectSelectorList() {
        final SettableSupplier<List> settableSupplier = new SettableSupplier();
        final Supplier<Object> supplier = ExpressionSelectors.supplierFromObjectSelector(ExpressionColumnValueSelectorTest.objectSelectorFromSupplier(settableSupplier, List.class));
        // List can't be a number, so supplierFromObjectSelector should return null.
        Assert.assertNull(supplier);
    }
}

