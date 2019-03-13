/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.concurrency;


import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.pentaho.di.trans.Trans;


/**
 * In this test we add new elements to shared transformation concurrently
 * and get added elements from this transformation concurrently.
 *
 * When working with {@link java.util.HashMap} with default loadFactor this test will fail
 * when HashMap will try to rearrange it's elements (it will happen when number of elements in map will be equal to
 * capacity/loadFactor).
 *
 * Map will be in inconsistent state, because in the same time, when rearrange happens other threads will be adding
 * new elements to map.
 * This will lead to unpredictable result of executing {@link java.util.HashMap#size()} method (as a result there
 * would be an error in {@link Getter#call()} ).
 */
public class ActiveSubTransformationsConcurrencyTest {
    private static final int NUMBER_OF_GETTERS = 10;

    private static final int NUMBER_OF_CREATES = 10;

    private static final int NUMBER_OF_CREATE_CYCLES = 20;

    private static final int INITIAL_NUMBER_OF_TRANS = 100;

    private static final String TRANS_NAME = "transformation";

    private final Object lock = new Object();

    @Test
    public void getAndCreateConcurrently() throws Exception {
        AtomicBoolean condition = new AtomicBoolean(true);
        Trans trans = new Trans();
        createSubTransformations(trans);
        List<ActiveSubTransformationsConcurrencyTest.Getter> getters = generateGetters(trans, condition);
        List<ActiveSubTransformationsConcurrencyTest.Creator> creators = generateCreators(trans, condition);
        ConcurrencyTestRunner.runAndCheckNoExceptionRaised(creators, getters, condition);
    }

    private class Getter extends StopOnErrorCallable<Object> {
        private final Trans trans;

        private final Random random;

        Getter(Trans trans, AtomicBoolean condition) {
            super(condition);
            this.trans = trans;
            random = new Random();
        }

        @Override
        Object doCall() throws Exception {
            while (condition.get()) {
                final String activeSubTransName = createTransName(random.nextInt(ActiveSubTransformationsConcurrencyTest.INITIAL_NUMBER_OF_TRANS));
                Trans subTrans = trans.getActiveSubTransformation(activeSubTransName);
                if (subTrans == null) {
                    throw new IllegalStateException(String.format("Returned transformation must not be null. Transformation name = %s", activeSubTransName));
                }
            } 
            return null;
        }
    }

    private class Creator extends StopOnErrorCallable<Object> {
        private final Trans trans;

        private final Random random;

        Creator(Trans trans, AtomicBoolean condition) {
            super(condition);
            this.trans = trans;
            random = new Random();
        }

        @Override
        Object doCall() throws Exception {
            for (int i = 0; i < (ActiveSubTransformationsConcurrencyTest.NUMBER_OF_CREATE_CYCLES); i++) {
                synchronized(lock) {
                    String transName = createTransName(randomInt(ActiveSubTransformationsConcurrencyTest.INITIAL_NUMBER_OF_TRANS, Integer.MAX_VALUE));
                    trans.addActiveSubTransformation(transName, new Trans());
                }
            }
            return null;
        }

        private int randomInt(int min, int max) {
            return (random.nextInt((max - min))) + min;
        }
    }
}

