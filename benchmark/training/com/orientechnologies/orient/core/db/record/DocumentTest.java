/**
 * *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.core.db.record;


import OType.STRING;
import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.junit.Assert;
import org.junit.Test;


public class DocumentTest {
    private ODatabaseDocumentTx db;

    @Test
    public void testConversionOnTypeSet() {
        ODocument doc = new ODocument();
        doc.field("some", 3);
        doc.setFieldType("some", STRING);
        Assert.assertEquals(doc.fieldType("some"), STRING);
        Assert.assertEquals(doc.field("some"), "3");
    }

    @Test
    public void testEval() {
        ODocument doc = new ODocument();
        doc.field("amount", 300);
        Number amountPlusVat = ((Number) (doc.eval("amount * 120 / 100")));
        Assert.assertEquals(amountPlusVat.longValue(), 360L);
    }

    @Test
    public void testEvalInContext() {
        ODocument doc = new ODocument();
        doc.field("amount", 300);
        OCommandContext context = new OBasicCommandContext().setVariable("vat", 20);
        Number amountPlusVat = ((Number) (doc.eval("amount * (100 + $vat) / 100", context)));
        Assert.assertEquals(amountPlusVat.longValue(), 360L);
    }
}

