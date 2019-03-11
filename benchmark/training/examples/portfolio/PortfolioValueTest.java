/**
 * Copyright 2012-2018 Chronicle Map Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package examples.portfolio;


import java.util.concurrent.ExecutionException;
import net.openhft.chronicle.core.values.LongValue;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.junit.Test;


public class PortfolioValueTest {
    private static final boolean useIterator = true;

    private static final long nAssets = 10000000;

    private static final int nThreads = Runtime.getRuntime().availableProcessors();

    private static final int nRepetitions = 11;// 10 for computing average, throwing away the first one (warmup)


    @Test
    public void test() throws InterruptedException, ExecutionException {
        ChronicleMapBuilder<LongValue, PortfolioAssetInterface> mapBuilder = ChronicleMapBuilder.of(LongValue.class, PortfolioAssetInterface.class).entries(PortfolioValueTest.nAssets);
        try (ChronicleMap<LongValue, PortfolioAssetInterface> cache = mapBuilder.create()) {
            createData(cache);
            // Compute multiple times to get an reasonable average compute time
            for (int i = 0; i < (PortfolioValueTest.nRepetitions); i++) {
                PortfolioValueTest.computeValue(cache);
            }
        }
    }
}

