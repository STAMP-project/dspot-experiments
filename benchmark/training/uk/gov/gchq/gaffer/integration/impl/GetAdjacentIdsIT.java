/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.integration.impl;


import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.id.DirectedType;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.operation.graph.SeededGraphFilters.IncludeIncomingOutgoingType;


public class GetAdjacentIdsIT extends AbstractStoreIT {
    private static final List<String> SEEDS = Arrays.asList(AbstractStoreIT.SOURCE_1, AbstractStoreIT.DEST_2, AbstractStoreIT.SOURCE_3, AbstractStoreIT.DEST_3, AbstractStoreIT.SOURCE_DIR_1, AbstractStoreIT.DEST_DIR_2, AbstractStoreIT.SOURCE_DIR_3, AbstractStoreIT.DEST_DIR_3, "A1");

    @Test
    public void shouldGetEntityIds() throws Exception {
        final List<DirectedType> directedTypes = Lists.newArrayList(DirectedType.values());
        directedTypes.add(null);
        final List<IncludeIncomingOutgoingType> inOutTypes = Lists.newArrayList(IncludeIncomingOutgoingType.values());
        inOutTypes.add(null);
        for (final IncludeIncomingOutgoingType inOutType : inOutTypes) {
            for (final DirectedType directedType : directedTypes) {
                final List<String> expectedSeeds = new ArrayList<>();
                if ((DirectedType.DIRECTED) != directedType) {
                    expectedSeeds.add(AbstractStoreIT.DEST_1);
                    expectedSeeds.add(AbstractStoreIT.SOURCE_2);
                    expectedSeeds.add(AbstractStoreIT.DEST_3);
                    expectedSeeds.add(AbstractStoreIT.SOURCE_3);
                    expectedSeeds.add("A1");
                    expectedSeeds.add("B1");
                    expectedSeeds.add("C1");
                    expectedSeeds.add("D1");
                }
                if ((IncludeIncomingOutgoingType.INCOMING) != inOutType) {
                    if ((DirectedType.UNDIRECTED) != directedType) {
                        expectedSeeds.add(((AbstractStoreIT.DEST_DIR) + "1"));
                        expectedSeeds.add(AbstractStoreIT.DEST_DIR_3);
                        expectedSeeds.add("A1");
                        expectedSeeds.add("B1");
                        expectedSeeds.add("C1");
                        expectedSeeds.add("D1");
                    }
                }
                if ((IncludeIncomingOutgoingType.OUTGOING) != inOutType) {
                    if ((DirectedType.UNDIRECTED) != directedType) {
                        expectedSeeds.add(AbstractStoreIT.SOURCE_DIR_2);
                        expectedSeeds.add(AbstractStoreIT.SOURCE_DIR_3);
                    }
                }
                shouldGetEntityIds(expectedSeeds, inOutType, directedType);
            }
        }
    }
}

