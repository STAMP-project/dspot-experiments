/**
 * Copyright (C) 2014 Pedro Vicente G?mez S?nchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pedrogomez.renderers;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pedrogomez.renderers.exception.NullRendererBuiltException;
import java.util.Collection;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Test class created to check the correct behaviour of RVRendererAdapter.
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
@Config(emulateSdk = 16)
@RunWith(RobolectricTestRunner.class)
public class RVRendererAdapterTest {
    private static final int ANY_SIZE = 11;

    private static final int ANY_POSITION = 2;

    private static final Object ANY_OBJECT = new Object();

    private static final Collection<Object> ANY_OBJECT_COLLECTION = new LinkedList<Object>();

    private static final int ANY_ITEM_VIEW_TYPE = 3;

    private RVRendererAdapter<Object> adapter;

    @Mock
    private RendererBuilder mockedRendererBuilder;

    @Mock
    private AdapteeCollection<Object> mockedCollection;

    @Mock
    private View mockedConvertView;

    @Mock
    private ViewGroup mockedParent;

    @Mock
    private ObjectRenderer mockedRenderer;

    @Mock
    private View mockedView;

    @Mock
    private RendererViewHolder mockedRendererViewHolder;

    @Test
    public void shouldReturnTheAdapteeCollection() {
        Assert.assertEquals(mockedCollection, adapter.getCollection());
    }

    @Test
    public void shouldReturnCollectionSizeOnGetCount() {
        Mockito.when(mockedCollection.size()).thenReturn(RVRendererAdapterTest.ANY_SIZE);
        Assert.assertEquals(RVRendererAdapterTest.ANY_SIZE, adapter.getItemCount());
    }

    @Test
    public void shouldReturnItemAtCollectionPositionOnGetItem() {
        Mockito.when(mockedCollection.get(RVRendererAdapterTest.ANY_POSITION)).thenReturn(RVRendererAdapterTest.ANY_OBJECT);
    }

    @Test
    public void shouldReturnPositionAsItemId() {
        Assert.assertEquals(RVRendererAdapterTest.ANY_POSITION, adapter.getItemId(RVRendererAdapterTest.ANY_POSITION));
    }

    @Test
    public void shouldDelegateIntoRendererBuilderToGetItemViewType() {
        Mockito.when(mockedCollection.get(RVRendererAdapterTest.ANY_POSITION)).thenReturn(RVRendererAdapterTest.ANY_OBJECT);
        Mockito.when(mockedRendererBuilder.getItemViewType(RVRendererAdapterTest.ANY_OBJECT)).thenReturn(RVRendererAdapterTest.ANY_ITEM_VIEW_TYPE);
        Assert.assertEquals(RVRendererAdapterTest.ANY_ITEM_VIEW_TYPE, adapter.getItemViewType(RVRendererAdapterTest.ANY_POSITION));
    }

    @Test
    public void shouldBuildRendererUsingAllNeededDependencies() {
        Mockito.when(mockedCollection.get(RVRendererAdapterTest.ANY_POSITION)).thenReturn(RVRendererAdapterTest.ANY_OBJECT);
        Mockito.when(mockedRendererBuilder.buildRendererViewHolder()).thenReturn(mockedRendererViewHolder);
        adapter.onCreateViewHolder(mockedParent, RVRendererAdapterTest.ANY_ITEM_VIEW_TYPE);
        Mockito.verify(mockedRendererBuilder).withParent(mockedParent);
        Mockito.verify(mockedRendererBuilder).withLayoutInflater(((LayoutInflater) (ArgumentMatchers.notNull())));
        Mockito.verify(mockedRendererBuilder).withViewType(RVRendererAdapterTest.ANY_ITEM_VIEW_TYPE);
        Mockito.verify(mockedRendererBuilder).buildRendererViewHolder();
    }

    @Test
    public void shouldGetRendererFromViewHolderAndCallUpdateRendererExtraValuesOnBind() {
        Mockito.when(mockedCollection.get(RVRendererAdapterTest.ANY_POSITION)).thenReturn(RVRendererAdapterTest.ANY_OBJECT);
        Mockito.when(mockedRendererViewHolder.getRenderer()).thenReturn(mockedRenderer);
        adapter.onBindViewHolder(mockedRendererViewHolder, RVRendererAdapterTest.ANY_POSITION);
        Mockito.verify(adapter).updateRendererExtraValues(RVRendererAdapterTest.ANY_OBJECT, mockedRenderer, RVRendererAdapterTest.ANY_POSITION);
    }

    @Test(expected = NullRendererBuiltException.class)
    public void shouldThrowNullRendererBuiltException() {
        adapter.onCreateViewHolder(mockedParent, RVRendererAdapterTest.ANY_ITEM_VIEW_TYPE);
    }

    @Test
    public void shouldAddElementToAdapteeCollection() {
        adapter.add(RVRendererAdapterTest.ANY_OBJECT);
        Mockito.verify(mockedCollection).add(RVRendererAdapterTest.ANY_OBJECT);
    }

    @Test
    public void shouldAddAllElementsToAdapteeCollection() {
        adapter.addAll(RVRendererAdapterTest.ANY_OBJECT_COLLECTION);
        Mockito.verify(mockedCollection).addAll(RVRendererAdapterTest.ANY_OBJECT_COLLECTION);
    }

    @Test
    public void shouldRemoveElementFromAdapteeCollection() {
        adapter.remove(RVRendererAdapterTest.ANY_OBJECT);
        Mockito.verify(mockedCollection).remove(RVRendererAdapterTest.ANY_OBJECT);
    }

    @Test
    public void shouldRemoveAllElementsFromAdapteeCollection() {
        adapter.removeAll(RVRendererAdapterTest.ANY_OBJECT_COLLECTION);
        Mockito.verify(mockedCollection).removeAll(RVRendererAdapterTest.ANY_OBJECT_COLLECTION);
    }

    @Test
    public void shouldClearElementsFromAdapteeCollection() {
        adapter.clear();
        Mockito.verify(mockedCollection).clear();
    }

    @Test
    public void shouldGetRendererFromViewHolderAndUpdateContentOnBind() {
        Mockito.when(mockedCollection.get(RVRendererAdapterTest.ANY_POSITION)).thenReturn(RVRendererAdapterTest.ANY_OBJECT);
        Mockito.when(mockedRendererViewHolder.getRenderer()).thenReturn(mockedRenderer);
        adapter.onBindViewHolder(mockedRendererViewHolder, RVRendererAdapterTest.ANY_POSITION);
        setContent(RVRendererAdapterTest.ANY_OBJECT);
    }

    @Test
    public void shouldGetRendererFromViewHolderAndRenderItOnBind() {
        Mockito.when(mockedCollection.get(RVRendererAdapterTest.ANY_POSITION)).thenReturn(RVRendererAdapterTest.ANY_OBJECT);
        Mockito.when(mockedRendererViewHolder.getRenderer()).thenReturn(mockedRenderer);
        adapter.onBindViewHolder(mockedRendererViewHolder, RVRendererAdapterTest.ANY_POSITION);
        Mockito.verify(mockedRenderer).render();
    }

    @Test
    public void shouldSetAdapteeCollection() throws Exception {
        RVRendererAdapter<Object> adapter = new RVRendererAdapter<Object>(mockedRendererBuilder);
        adapter.setCollection(mockedCollection);
        Assert.assertEquals(mockedCollection, adapter.getCollection());
    }

    @Test
    public void shouldBeEmptyWhenItsCreatedWithJustARendererBuilder() {
        RVRendererAdapter<Object> adapter = new RVRendererAdapter<Object>(mockedRendererBuilder);
        Assert.assertEquals(0, adapter.getItemCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSetNullCollection() {
        RVRendererAdapter<Object> adapter = new RVRendererAdapter<Object>(mockedRendererBuilder);
        adapter.setCollection(null);
    }
}

