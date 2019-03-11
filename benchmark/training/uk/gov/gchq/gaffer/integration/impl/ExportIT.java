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


import TestGroups.EDGE;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationChain.Builder;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.data.generator.EntityIdExtractor;
import uk.gov.gchq.gaffer.operation.impl.DiscardOutput;
import uk.gov.gchq.gaffer.operation.impl.export.resultcache.ExportToGafferResultCache;
import uk.gov.gchq.gaffer.operation.impl.export.resultcache.GetGafferResultCacheExport;
import uk.gov.gchq.gaffer.operation.impl.export.set.GetSetExport;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;


public class ExportIT extends AbstractStoreIT {
    @Test
    public void shouldExportResultsInSet() throws OperationException {
        // Given
        final View edgesView = new View.Builder().edge(EDGE).build();
        final OperationChain<Iterable<?>> exportOpChain = new Builder().first(new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_DIR_0)).view(edgesView).build()).then(new uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet()).then(new GenerateObjects.Builder<uk.gov.gchq.gaffer.data.element.id.EntityId>().generator(new EntityIdExtractor()).build()).then(new GetElements.Builder().view(edgesView).build()).then(new uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet()).then(new DiscardOutput()).then(new GetSetExport()).build();
        // When
        final Iterable<?> export = AbstractStoreIT.graph.execute(exportOpChain, getUser());
        // Then
        Assert.assertEquals(2, Sets.newHashSet(export).size());
    }

    @Test
    public void shouldExportResultsToGafferCache() throws OperationException {
        Assume.assumeTrue("Gaffer result cache has not been enabled for this store.", AbstractStoreIT.graph.isSupported(ExportToGafferResultCache.class));
        // Given
        final View edgesView = new View.Builder().edge(EDGE).build();
        final OperationChain<? extends Iterable<?>> exportOpChain = new Builder().first(new GetElements.Builder().input(new EntitySeed(AbstractStoreIT.SOURCE_DIR_0)).view(edgesView).build()).then(new ExportToGafferResultCache()).then(new GenerateObjects.Builder<uk.gov.gchq.gaffer.data.element.id.EntityId>().generator(new EntityIdExtractor()).build()).then(new GetElements.Builder().view(edgesView).build()).then(new ExportToGafferResultCache()).then(new DiscardOutput()).then(new GetGafferResultCacheExport()).build();
        // When
        final Iterable<?> export = AbstractStoreIT.graph.execute(exportOpChain, getUser());
        // Then
        Assert.assertEquals(2, Sets.newHashSet(export).size());
    }
}

