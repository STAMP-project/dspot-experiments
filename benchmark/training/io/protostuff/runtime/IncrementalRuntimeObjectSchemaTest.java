/**
 * ================================================================================
 */
/**
 * Copyright (c) 2012, David Yu
 */
/**
 * All rights reserved.
 */
/**
 * --------------------------------------------------------------------------------
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are met:
 */
/**
 * 1. Redistributions of source code must retain the above copyright notice,
 */
/**
 * this list of conditions and the following disclaimer.
 */
/**
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 */
/**
 * this list of conditions and the following disclaimer in the documentation
 */
/**
 * and/or other materials provided with the distribution.
 */
/**
 * 3. Neither the name of protostuff nor the names of its contributors may be used
 */
/**
 * to endorse or promote products derived from this software without
 */
/**
 * specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 */
/**
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 */
/**
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 */
/**
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 */
/**
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 */
/**
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 */
/**
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 */
/**
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 */
/**
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 */
/**
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 */
/**
 * POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * ================================================================================
 */
package io.protostuff.runtime;


import Bat.PIPE_SCHEMA;
import Bat.SCHEMA;
import CollectionSchema.MessageFactories.ArrayList;
import CollectionSchema.MessageFactories.HashSet;
import CustomArrayList.MESSAGE_FACTORY;
import IncrementalIdStrategy.Registry;
import MapSchema.MessageFactories.HashMap;
import MapSchema.MessageFactories.LinkedHashMap;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.AcousticGuitar;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.BassGuitar;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.GuitarPickup;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.Pojo;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.PojoWithArray;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.PojoWithArray2D;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.PojoWithCollection;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.PojoWithCustomArrayListAndHashMap;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.PojoWithMap;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.Size;
import io.protostuff.runtime.AbstractRuntimeObjectSchemaTest.WrapsBat;
import io.protostuff.runtime.SampleDelegates.ShortArrayDelegate;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import static RuntimeEnv.ID_STRATEGY;


/**
 * Test for {@link IncrementalIdStrategy}.
 *
 * @author David Yu
 * @unknown Mar 29, 2012
 */
public class IncrementalRuntimeObjectSchemaTest extends TestCase {
    static final boolean runTest;

    static {
        // check whether test/run from root module
        String strategy = System.getProperty("test_id_strategy");
        runTest = (strategy == null) || (strategy.equals("incremental"));
        if (IncrementalRuntimeObjectSchemaTest.runTest) {
            System.setProperty("protostuff.runtime.id_strategy_factory", "io.protostuff.runtime.IncrementalRuntimeObjectSchemaTest$IdStrategyFactory");
        }
    }

    public static class IdStrategyFactory implements IdStrategy.Factory {
        static int INSTANCE_COUNT = 0;

        Registry r = new IncrementalIdStrategy.Registry(20, 11, 20, 11, 20, 11, 80, 11);

        public IdStrategyFactory() {
            ++(IncrementalRuntimeObjectSchemaTest.IdStrategyFactory.INSTANCE_COUNT);
            System.out.println("@INCREMENTAL");
        }

        @Override
        public IdStrategy create() {
            return r.strategy;
        }

        @Override
        public void postCreate() {
            r.registerCollection(ArrayList, 1).registerCollection(HashSet, 2).registerCollection(MESSAGE_FACTORY, 3);
            r.registerMap(HashMap, 1).registerMap(LinkedHashMap, 2).registerMap(CustomHashMap.MESSAGE_FACTORY, 3);
            r.registerEnum(Size.class, 1).registerEnum(GuitarPickup.class, 2);
            r.registerPojo(AcousticGuitar.class, 1).registerPojo(BassGuitar.class, 2).registerPojo(Pojo.class, 3).registerPojo(PojoWithArray.class, 4).registerPojo(PojoWithArray2D.class, 5).registerPojo(PojoWithCollection.class, 6).registerPojo(PojoWithMap.class, 7).registerPojo(SCHEMA, PIPE_SCHEMA, 8).registerPojo(WrapsBat.class, 9).registerPojo(PojoWithCustomArrayListAndHashMap.class, 10);
            r.registerDelegate(new ShortArrayDelegate(), 1);
            r.registerDelegate(SampleDelegates.SINGLETON_DELEGATE, 2);
            r = null;
        }
    }

    public void testProtostuff() throws Exception {
        if ((IncrementalRuntimeObjectSchemaTest.runTest) && ((ID_STRATEGY) instanceof IncrementalIdStrategy)) {
            TestRunner tr = new TestRunner();
            tr.doRun(tr.getTest("io.protostuff.runtime.ProtostuffRuntimeObjectSchemaTest"), false);
            TestCase.assertTrue(((IncrementalRuntimeObjectSchemaTest.IdStrategyFactory.INSTANCE_COUNT) != 0));
        }
    }

    public void testProtobuf() throws Exception {
        if ((IncrementalRuntimeObjectSchemaTest.runTest) && ((ID_STRATEGY) instanceof IncrementalIdStrategy)) {
            TestRunner tr = new TestRunner();
            tr.doRun(tr.getTest("io.protostuff.runtime.ProtobufRuntimeObjectSchemaTest"), false);
            TestCase.assertTrue(((IncrementalRuntimeObjectSchemaTest.IdStrategyFactory.INSTANCE_COUNT) != 0));
        }
    }
}

