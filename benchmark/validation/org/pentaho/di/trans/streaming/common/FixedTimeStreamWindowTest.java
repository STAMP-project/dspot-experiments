/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.streaming.common;


import io.reactivex.Flowable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.SubtransExecutor;


@RunWith(MockitoJUnitRunner.class)
public class FixedTimeStreamWindowTest {
    @Mock
    private SubtransExecutor subtransExecutor;

    @Test
    public void emptyResultShouldNotThrowException() throws KettleException {
        Mockito.when(subtransExecutor.execute(ArgumentMatchers.any())).thenReturn(Optional.empty());
        RowMetaInterface rowMeta = new RowMeta();
        rowMeta.addValueMeta(new ValueMetaString("field"));
        FixedTimeStreamWindow<List> window = new FixedTimeStreamWindow(subtransExecutor, rowMeta, 0, 2, 1);
        window.buffer(Flowable.fromIterable(Collections.singletonList(Arrays.asList("v1", "v2")))).forEach(( result) -> {
        });
    }

    @Test
    public void resultsComeBackToParent() throws KettleException {
        RowMetaInterface rowMeta = new RowMeta();
        rowMeta.addValueMeta(new ValueMetaString("field"));
        Result mockResult = new Result();
        mockResult.setRows(Arrays.asList(new org.pentaho.di.core.RowMetaAndData(rowMeta, "queen"), new org.pentaho.di.core.RowMetaAndData(rowMeta, "king")));
        Mockito.when(subtransExecutor.execute(ArgumentMatchers.any())).thenReturn(Optional.of(mockResult));
        FixedTimeStreamWindow<List> window = new FixedTimeStreamWindow(subtransExecutor, rowMeta, 0, 2, 1);
        window.buffer(Flowable.fromIterable(Collections.singletonList(Arrays.asList("v1", "v2")))).forEach(( result) -> assertEquals(mockResult, result));
    }

    @Test
    public void supportsPostProcessing() throws KettleException {
        RowMetaInterface rowMeta = new RowMeta();
        rowMeta.addValueMeta(new ValueMetaString("field"));
        Result mockResult = new Result();
        mockResult.setRows(Arrays.asList(new org.pentaho.di.core.RowMetaAndData(rowMeta, "queen"), new org.pentaho.di.core.RowMetaAndData(rowMeta, "king")));
        Mockito.when(subtransExecutor.execute(ArgumentMatchers.any())).thenReturn(Optional.of(mockResult));
        AtomicInteger count = new AtomicInteger();
        FixedTimeStreamWindow<List> window = new FixedTimeStreamWindow(subtransExecutor, rowMeta, 0, 2, 1, ( p) -> count.set(p.getKey().get(0).size()));
        window.buffer(Flowable.fromIterable(Collections.singletonList(Arrays.asList("v1", "v2")))).forEach(( result) -> assertEquals(mockResult, result));
        Assert.assertEquals(2, count.get());
    }

    @Test
    public void emptyResultsNotPostProcessed() throws KettleException {
        RowMetaInterface rowMeta = new RowMeta();
        rowMeta.addValueMeta(new ValueMetaString("field"));
        Result mockResult = new Result();
        mockResult.setRows(Arrays.asList(new org.pentaho.di.core.RowMetaAndData(rowMeta, "queen"), new org.pentaho.di.core.RowMetaAndData(rowMeta, "king")));
        Mockito.when(subtransExecutor.execute(ArgumentMatchers.any())).thenReturn(Optional.empty());
        AtomicInteger count = new AtomicInteger();
        FixedTimeStreamWindow<List> window = new FixedTimeStreamWindow(subtransExecutor, rowMeta, 0, 2, 1, ( p) -> count.set(p.getKey().get(0).size()));
        window.buffer(Flowable.fromIterable(Collections.singletonList(Arrays.asList("v1", "v2")))).forEach(( result) -> assertEquals(mockResult, result));
        Assert.assertEquals(0, count.get());
    }
}

