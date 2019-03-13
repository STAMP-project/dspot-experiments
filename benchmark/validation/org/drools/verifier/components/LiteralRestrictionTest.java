/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.verifier.components;


import Field.BOOLEAN;
import Field.DATE;
import Field.DOUBLE;
import Field.INT;
import Field.STRING;
import Field.UNKNOWN;
import org.drools.verifier.VerifierComponentMockFactory;
import org.junit.Assert;
import org.junit.Test;


public class LiteralRestrictionTest {
    @Test
    public void testSetValue() {
        Pattern pattern1 = VerifierComponentMockFactory.createPattern1();
        LiteralRestriction literalBooleanRestriction = LiteralRestriction.createRestriction(pattern1, "true");
        Assert.assertTrue((literalBooleanRestriction instanceof BooleanRestriction));
        BooleanRestriction booleanRestriction = ((BooleanRestriction) (literalBooleanRestriction));
        Assert.assertEquals(BOOLEAN, booleanRestriction.getValueType());
        Assert.assertEquals(true, booleanRestriction.getValue());
        LiteralRestriction intLiteralRestriction = LiteralRestriction.createRestriction(pattern1, "1");
        Assert.assertTrue((intLiteralRestriction instanceof NumberRestriction));
        NumberRestriction intRestriction = ((NumberRestriction) (intLiteralRestriction));
        Assert.assertTrue(intRestriction.isInt());
        Assert.assertEquals(INT, intRestriction.getValueType());
        Assert.assertEquals(1, intRestriction.getValue());
        LiteralRestriction doubleLiteralRestriction = LiteralRestriction.createRestriction(pattern1, "1.0");
        Assert.assertTrue((doubleLiteralRestriction instanceof NumberRestriction));
        NumberRestriction doubleRestriction = ((NumberRestriction) (doubleLiteralRestriction));
        Assert.assertEquals(DOUBLE, doubleRestriction.getValueType());
        Assert.assertEquals(1.0, doubleRestriction.getValue());
        LiteralRestriction dateLiteralRestriction = LiteralRestriction.createRestriction(pattern1, "11-jan-2008");
        Assert.assertTrue((dateLiteralRestriction instanceof DateRestriction));
        DateRestriction dateRestriction = ((DateRestriction) (dateLiteralRestriction));
        Assert.assertEquals(DATE, dateRestriction.getValueType());
        LiteralRestriction stringRestriction = LiteralRestriction.createRestriction(pattern1, "test test");
        Assert.assertEquals(STRING, stringRestriction.getValueType());
        Assert.assertEquals("test test", stringRestriction.getValueAsString());
        LiteralRestriction nullRestriction = LiteralRestriction.createRestriction(pattern1, null);
        Assert.assertTrue((nullRestriction instanceof StringRestriction));
        Assert.assertEquals(UNKNOWN, nullRestriction.getValueType());
        Assert.assertEquals("", nullRestriction.getValueAsString());
    }
}

