/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;


/**
 * Tests behavior of Drools Fusion when several events are being
 * fed into the engine by thread A while the engine had been started by
 * fireUntilHalt by thread B.
 */
public class FireUntilHaltAccumulateTest {
    private KieSession statefulSession;

    private FireUntilHaltAccumulateTest.StockFactory stockFactory;

    private static String drl = "package org.drools.integrationtests\n" + (((((((((((((((("\n" + "import java.util.List;\n") + "\n") + "declare Stock\n") + "    @role( event )\n") + "    @expires( 1s ) // setting to a large value causes the test to pass\n") + "    name : String\n") + "    value : Double\n") + "end\n") + "\n") + "rule \"collect events\"\n") + "when\n") + "    stocks := List()\n") + "        from accumulate( $zeroStock : Stock( value == 0.0 );\n") + "                         collectList( $zeroStock ) )\n") + "then\n") + "end");

    /**
     * Events are being collected through accumulate while
     * separate thread inserts other events.
     * <p/>
     * The engine runs in fireUntilHalt mode started in a separate thread.
     * Events may expire during the evaluation of accumulate.
     */
    @Test
    public void testFireUntilHaltWithAccumulateAndExpires() throws Exception {
        // thread for firing until halt
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future sessionFuture = executor.submit(((Runnable) (statefulSession::fireUntilHalt)));
        try {
            for (int iteration = 0; iteration < 100; iteration++) {
                this.populateSessionWithStocks();
            }
            // let the engine finish its job
            Thread.sleep(2000);
        } finally {
            statefulSession.halt();
            // not to swallow possible exception
            sessionFuture.get();
            statefulSession.dispose();
            executor.shutdownNow();
        }
    }

    /**
     * Factory creating events used in the test.
     */
    private static class StockFactory {
        private static final String DRL_PACKAGE_NAME = "org.drools.integrationtests";

        private static final String DRL_FACT_NAME = "Stock";

        private final KieBase kbase;

        public StockFactory(final KieBase kbase) {
            this.kbase = kbase;
        }

        public Object createStock(final String name, final Double value) {
            try {
                return this.createDRLStock(name, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to create Stock instance defined in DRL", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Unable to create Stock instance defined in DRL", e);
            }
        }

        private Object createDRLStock(final String name, final Double value) throws IllegalAccessException, InstantiationException {
            final FactType stockType = kbase.getFactType(FireUntilHaltAccumulateTest.StockFactory.DRL_PACKAGE_NAME, FireUntilHaltAccumulateTest.StockFactory.DRL_FACT_NAME);
            final Object stock = stockType.newInstance();
            stockType.set(stock, "name", name);
            stockType.set(stock, "value", value);
            return stock;
        }
    }
}

