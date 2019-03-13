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
package uk.gov.gchq.gaffer.hbasestore.serialisation;


import KeyValue.Type.Delete;
import org.apache.hadoop.hbase.Cell;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.exception.SerialisationException;


public class LazyElementCellTest {
    @Test
    public void shouldConstructLazyElementCell() throws SerialisationException {
        // Given
        final Cell cell = Mockito.mock(Cell.class);
        final ElementSerialisation serialisation = Mockito.mock(ElementSerialisation.class);
        final Element element = Mockito.mock(Element.class);
        BDDMockito.given(serialisation.getElement(cell, false)).willReturn(element);
        // When
        final LazyElementCell lazyElementCell = new LazyElementCell(cell, serialisation, false);
        // Then
        Assert.assertSame(cell, lazyElementCell.getCell());
        Assert.assertFalse(lazyElementCell.isElementLoaded());
        Assert.assertSame(element, lazyElementCell.getElement());
        Assert.assertTrue(lazyElementCell.isElementLoaded());
        Assert.assertSame(serialisation, lazyElementCell.getSerialisation());
    }

    @Test
    public void shouldNotBeAbleToDeserialiseCellIfCellIsMarkedForDeletion() throws SerialisationException {
        // Given
        final Cell cell = Mockito.mock(Cell.class);
        final ElementSerialisation serialisation = Mockito.mock(ElementSerialisation.class);
        final LazyElementCell lazyElementCell = new LazyElementCell(cell, serialisation, false);
        BDDMockito.given(cell.getTypeByte()).willReturn(Delete.getCode());
        // When / Then
        try {
            lazyElementCell.getElement();
            Assert.fail("Exception expected");
        } catch (final IllegalStateException e) {
            Assert.assertNotNull(e.getMessage());
        }
        Assert.assertTrue(lazyElementCell.isDeleted());
    }

    @Test
    public void shoulCacheLoadedElement() throws SerialisationException {
        // Given
        final Cell cell = Mockito.mock(Cell.class);
        final ElementSerialisation serialisation = Mockito.mock(ElementSerialisation.class);
        final Element element = Mockito.mock(Element.class);
        BDDMockito.given(serialisation.getElement(cell, false)).willReturn(element);
        // When
        final LazyElementCell lazyElementCell = new LazyElementCell(cell, serialisation, false);
        // Then
        Assert.assertFalse(lazyElementCell.isElementLoaded());
        Assert.assertSame(element, lazyElementCell.getElement());
        Assert.assertTrue(lazyElementCell.isElementLoaded());
        Assert.assertSame(element, lazyElementCell.getElement());
        Mockito.verify(serialisation, Mockito.times(1)).getElement(cell, false);
        Assert.assertSame(serialisation, lazyElementCell.getSerialisation());
    }

    @Test
    public void shouldCacheGroup() throws SerialisationException {
        // Given
        final Cell cell = Mockito.mock(Cell.class);
        final ElementSerialisation serialisation = Mockito.mock(ElementSerialisation.class);
        final String group = "a group";
        BDDMockito.given(serialisation.getGroup(cell)).willReturn(group);
        // When
        final LazyElementCell lazyElementCell = new LazyElementCell(cell, serialisation, false);
        // Then
        Assert.assertEquals(group, lazyElementCell.getGroup());
        Assert.assertEquals(group, lazyElementCell.getGroup());
        Assert.assertFalse(lazyElementCell.isElementLoaded());
        Mockito.verify(serialisation, Mockito.times(1)).getGroup(cell);
    }
}

