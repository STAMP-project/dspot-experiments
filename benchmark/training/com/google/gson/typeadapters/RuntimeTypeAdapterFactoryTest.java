/**
 * Copyright (C) 2011 Google Inc.
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
package com.google.gson.typeadapters;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import junit.framework.TestCase;


public final class RuntimeTypeAdapterFactoryTest extends TestCase {
    public void testRuntimeTypeAdapter() {
        RuntimeTypeAdapterFactory<RuntimeTypeAdapterFactoryTest.BillingInstrument> rta = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class).registerSubtype(RuntimeTypeAdapterFactoryTest.CreditCard.class);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(rta).create();
        RuntimeTypeAdapterFactoryTest.CreditCard original = new RuntimeTypeAdapterFactoryTest.CreditCard("Jesse", 234);
        TestCase.assertEquals("{\"type\":\"CreditCard\",\"cvv\":234,\"ownerName\":\"Jesse\"}", gson.toJson(original, RuntimeTypeAdapterFactoryTest.BillingInstrument.class));
        RuntimeTypeAdapterFactoryTest.BillingInstrument deserialized = gson.fromJson("{type:'CreditCard',cvv:234,ownerName:'Jesse'}", RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
        TestCase.assertEquals("Jesse", deserialized.ownerName);
        TestCase.assertTrue((deserialized instanceof RuntimeTypeAdapterFactoryTest.CreditCard));
    }

    public void testRuntimeTypeIsBaseType() {
        TypeAdapterFactory rta = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class).registerSubtype(RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(rta).create();
        RuntimeTypeAdapterFactoryTest.BillingInstrument original = new RuntimeTypeAdapterFactoryTest.BillingInstrument("Jesse");
        TestCase.assertEquals("{\"type\":\"BillingInstrument\",\"ownerName\":\"Jesse\"}", gson.toJson(original, RuntimeTypeAdapterFactoryTest.BillingInstrument.class));
        RuntimeTypeAdapterFactoryTest.BillingInstrument deserialized = gson.fromJson("{type:'BillingInstrument',ownerName:'Jesse'}", RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
        TestCase.assertEquals("Jesse", deserialized.ownerName);
    }

    public void testNullBaseType() {
        try {
            RuntimeTypeAdapterFactory.of(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testNullTypeFieldName() {
        try {
            RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testNullSubtype() {
        RuntimeTypeAdapterFactory<RuntimeTypeAdapterFactoryTest.BillingInstrument> rta = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
        try {
            rta.registerSubtype(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testNullLabel() {
        RuntimeTypeAdapterFactory<RuntimeTypeAdapterFactoryTest.BillingInstrument> rta = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
        try {
            rta.registerSubtype(RuntimeTypeAdapterFactoryTest.CreditCard.class, null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testDuplicateSubtype() {
        RuntimeTypeAdapterFactory<RuntimeTypeAdapterFactoryTest.BillingInstrument> rta = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
        rta.registerSubtype(RuntimeTypeAdapterFactoryTest.CreditCard.class, "CC");
        try {
            rta.registerSubtype(RuntimeTypeAdapterFactoryTest.CreditCard.class, "Visa");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testDuplicateLabel() {
        RuntimeTypeAdapterFactory<RuntimeTypeAdapterFactoryTest.BillingInstrument> rta = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
        rta.registerSubtype(RuntimeTypeAdapterFactoryTest.CreditCard.class, "CC");
        try {
            rta.registerSubtype(RuntimeTypeAdapterFactoryTest.BankTransfer.class, "CC");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testDeserializeMissingTypeField() {
        TypeAdapterFactory billingAdapter = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class).registerSubtype(RuntimeTypeAdapterFactoryTest.CreditCard.class);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(billingAdapter).create();
        try {
            gson.fromJson("{ownerName:'Jesse'}", RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
            TestCase.fail();
        } catch (JsonParseException expected) {
        }
    }

    public void testDeserializeMissingSubtype() {
        TypeAdapterFactory billingAdapter = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class).registerSubtype(RuntimeTypeAdapterFactoryTest.BankTransfer.class);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(billingAdapter).create();
        try {
            gson.fromJson("{type:'CreditCard',ownerName:'Jesse'}", RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
            TestCase.fail();
        } catch (JsonParseException expected) {
        }
    }

    public void testSerializeMissingSubtype() {
        TypeAdapterFactory billingAdapter = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class).registerSubtype(RuntimeTypeAdapterFactoryTest.BankTransfer.class);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(billingAdapter).create();
        try {
            gson.toJson(new RuntimeTypeAdapterFactoryTest.CreditCard("Jesse", 456), RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
            TestCase.fail();
        } catch (JsonParseException expected) {
        }
    }

    public void testSerializeCollidingTypeFieldName() {
        TypeAdapterFactory billingAdapter = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class, "cvv").registerSubtype(RuntimeTypeAdapterFactoryTest.CreditCard.class);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(billingAdapter).create();
        try {
            gson.toJson(new RuntimeTypeAdapterFactoryTest.CreditCard("Jesse", 456), RuntimeTypeAdapterFactoryTest.BillingInstrument.class);
            TestCase.fail();
        } catch (JsonParseException expected) {
        }
    }

    public void testSerializeWrappedNullValue() {
        TypeAdapterFactory billingAdapter = RuntimeTypeAdapterFactory.of(RuntimeTypeAdapterFactoryTest.BillingInstrument.class).registerSubtype(RuntimeTypeAdapterFactoryTest.CreditCard.class).registerSubtype(RuntimeTypeAdapterFactoryTest.BankTransfer.class);
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(billingAdapter).create();
        String serialized = gson.toJson(new RuntimeTypeAdapterFactoryTest.BillingInstrumentWrapper(null), RuntimeTypeAdapterFactoryTest.BillingInstrumentWrapper.class);
        RuntimeTypeAdapterFactoryTest.BillingInstrumentWrapper deserialized = gson.fromJson(serialized, RuntimeTypeAdapterFactoryTest.BillingInstrumentWrapper.class);
        TestCase.assertNull(deserialized.instrument);
    }

    static class BillingInstrumentWrapper {
        RuntimeTypeAdapterFactoryTest.BillingInstrument instrument;

        BillingInstrumentWrapper(RuntimeTypeAdapterFactoryTest.BillingInstrument instrument) {
            this.instrument = instrument;
        }
    }

    static class BillingInstrument {
        private final String ownerName;

        BillingInstrument(String ownerName) {
            this.ownerName = ownerName;
        }
    }

    static class CreditCard extends RuntimeTypeAdapterFactoryTest.BillingInstrument {
        int cvv;

        CreditCard(String ownerName, int cvv) {
            super(ownerName);
            this.cvv = cvv;
        }
    }

    static class BankTransfer extends RuntimeTypeAdapterFactoryTest.BillingInstrument {
        int bankAccount;

        BankTransfer(String ownerName, int bankAccount) {
            super(ownerName);
            this.bankAccount = bankAccount;
        }
    }
}

