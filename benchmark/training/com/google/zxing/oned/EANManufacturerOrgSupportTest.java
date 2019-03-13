/**
 * Copyright (C) 2010 ZXing authors
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
package com.google.zxing.oned;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link EANManufacturerOrgSupport}.
 *
 * @author Sean Owen
 */
public final class EANManufacturerOrgSupportTest extends Assert {
    @Test
    public void testLookup() {
        EANManufacturerOrgSupport support = new EANManufacturerOrgSupport();
        Assert.assertNull(support.lookupCountryIdentifier("472000"));
        Assert.assertEquals("US/CA", support.lookupCountryIdentifier("000000"));
        Assert.assertEquals("MO", support.lookupCountryIdentifier("958000"));
        Assert.assertEquals("GB", support.lookupCountryIdentifier("500000"));
        Assert.assertEquals("GB", support.lookupCountryIdentifier("509000"));
    }
}

