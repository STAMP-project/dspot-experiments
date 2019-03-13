/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests.concurrency;


import java.math.BigDecimal;
import org.drools.compiler.integrationtests.facts.AnEnum;
import org.drools.compiler.integrationtests.facts.FactWithBigDecimal;
import org.drools.compiler.integrationtests.facts.FactWithBoolean;
import org.drools.compiler.integrationtests.facts.FactWithByte;
import org.drools.compiler.integrationtests.facts.FactWithCharacter;
import org.drools.compiler.integrationtests.facts.FactWithDouble;
import org.drools.compiler.integrationtests.facts.FactWithEnum;
import org.drools.compiler.integrationtests.facts.FactWithFloat;
import org.drools.compiler.integrationtests.facts.FactWithInteger;
import org.drools.compiler.integrationtests.facts.FactWithLong;
import org.drools.compiler.integrationtests.facts.FactWithShort;
import org.drools.compiler.integrationtests.facts.FactWithString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class DataTypeEvaluationConcurrentSessionsTest extends AbstractConcurrentTest {
    private static final Integer NUMBER_OF_THREADS = 10;

    private static final Integer NUMBER_OF_REPETITIONS = 1;

    public DataTypeEvaluationConcurrentSessionsTest(final boolean enforcedJitting, final boolean serializeKieBase, final boolean sharedKieBase, final boolean sharedKieSession) {
        super(enforcedJitting, serializeKieBase, sharedKieBase, sharedKieSession);
    }

    @Test(timeout = 20000)
    public void testBooleanPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithBoolean: FactWithBoolean(booleanValue == false) \n", new FactWithBoolean(false));
    }

    @Test(timeout = 20000)
    public void testBoolean() throws InterruptedException {
        testFactAttributeType("    $factWithBoolean: FactWithBoolean(booleanObjectValue == false) \n", new FactWithBoolean(false));
    }

    @Test(timeout = 20000)
    public void testBytePrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithByte: FactWithByte(byteValue == 15) \n", new FactWithByte(((byte) (15))));
    }

    @Test(timeout = 20000)
    public void testByte() throws InterruptedException {
        testFactAttributeType("    $factWithByte: FactWithByte(byteObjectValue == 15) \n", new FactWithByte(((byte) (15))));
    }

    @Test(timeout = 20000)
    public void testShortPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithShort: FactWithShort(shortValue == 15) \n", new FactWithShort(((short) (15))));
    }

    @Test(timeout = 20000)
    public void testShort() throws InterruptedException {
        testFactAttributeType("    $factWithShort: FactWithShort(shortObjectValue == 15) \n", new FactWithShort(((short) (15))));
    }

    @Test(timeout = 20000)
    public void testIntPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithInt: FactWithInteger(intValue == 15) \n", new FactWithInteger(15));
    }

    @Test(timeout = 20000)
    public void testInteger() throws InterruptedException {
        testFactAttributeType("    $factWithInteger: FactWithInteger(integerValue == 15) \n", new FactWithInteger(15));
    }

    @Test(timeout = 20000)
    public void testLongPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithLong: FactWithLong(longValue == 15) \n", new FactWithLong(15));
    }

    @Test(timeout = 20000)
    public void testLong() throws InterruptedException {
        testFactAttributeType("    $factWithLong: FactWithLong(longObjectValue == 15) \n", new FactWithLong(15));
    }

    @Test(timeout = 20000)
    public void testFloatPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithFloat: FactWithFloat(floatValue == 15.1) \n", new FactWithFloat(15.1F));
    }

    @Test(timeout = 20000)
    public void testFloat() throws InterruptedException {
        testFactAttributeType("    $factWithFloat: FactWithFloat(floatObjectValue == 15.1) \n", new FactWithFloat(15.1F));
    }

    @Test(timeout = 20000)
    public void testDoublePrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithDouble: FactWithDouble(doubleValue == 15.1) \n", new FactWithDouble(15.1));
    }

    @Test(timeout = 20000)
    public void testDouble() throws InterruptedException {
        testFactAttributeType("    $factWithDouble: FactWithDouble(doubleObjectValue == 15.1) \n", new FactWithDouble(15.1));
    }

    @Test(timeout = 20000)
    public void testBigDecimal() throws InterruptedException {
        testFactAttributeType("    $factWithBigDecimal: FactWithBigDecimal(bigDecimalValue == 10) \n", new FactWithBigDecimal(BigDecimal.TEN));
    }

    @Test(timeout = 20000)
    public void testCharPrimitive() throws InterruptedException {
        testFactAttributeType("    $factWithChar: FactWithCharacter(charValue == \'a\') \n", new FactWithCharacter('a'));
    }

    @Test(timeout = 20000)
    public void testCharacter() throws InterruptedException {
        testFactAttributeType("    $factWithChar: FactWithCharacter(characterValue == \'a\') \n", new FactWithCharacter('a'));
    }

    @Test(timeout = 20000)
    public void testString() throws InterruptedException {
        testFactAttributeType("    $factWithString: FactWithString(stringValue == \"test\") \n", new FactWithString("test"));
    }

    @Test(timeout = 20000)
    public void testEnum() throws InterruptedException {
        testFactAttributeType("    $factWithEnum: FactWithEnum(enumValue == AnEnum.FIRST) \n", new FactWithEnum(AnEnum.FIRST));
    }
}

