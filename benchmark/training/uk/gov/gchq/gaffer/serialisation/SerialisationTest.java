/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.serialisation;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.pair.Pair;


public abstract class SerialisationTest<INPUT, OUTPUT> {
    protected final Serialiser<INPUT, OUTPUT> serialiser;

    protected final Pair<INPUT, OUTPUT>[] historicSerialisationPairs;

    public SerialisationTest() {
        this.serialiser = getSerialisation();
        this.historicSerialisationPairs = getHistoricSerialisationPairs();
    }

    @Test
    public void shouldSerialiseWithHistoricValues() throws Exception {
        Assert.assertNotNull("historicSerialisationPairs should not be null.", historicSerialisationPairs);
        Assert.assertNotEquals("historicSerialisationPairs should not be empty.", 0, historicSerialisationPairs.length);
        for (final Pair<INPUT, OUTPUT> pair : historicSerialisationPairs) {
            Assert.assertNotNull("historicSerialisationPairs first value should not be null", pair.getFirst());
            serialiseFirst(pair);
            Assert.assertNotNull("historicSerialisationPairs second value should not be null", pair.getSecond());
            deserialiseSecond(pair);
        }
    }
}

