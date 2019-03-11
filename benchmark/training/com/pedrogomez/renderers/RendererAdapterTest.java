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
 * Test class created to check the correct behaviour of RendererAdapter.
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
@Config(emulateSdk = 16)
@RunWith(RobolectricTestRunner.class)
public class RendererAdapterTest {
    private static final int ANY_SIZE = 11;

    private static final int ANY_POSITION = 2;

    private static final Object ANY_OBJECT = new Object();

    private static final int ANY_ITEM_VIEW_TYPE = 3;

    private static final int ANY_VIEW_TYPE_COUNT = 4;

    private static final Collection<Object> ANY_OBJECT_COLLECTION = new LinkedList<Object>();

    private RendererAdapter<Object> rendererAdapter;

    @Mock
    private LayoutInflater mockedLayoutInflater;

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

    @Test
    public void shouldReturnTheAdapteeCollection() {
        Assert.assertEquals(mockedCollection, rendererAdapter.getCollection());
    }

    @Test
    public void shouldReturnCollectionSizeOnGetCount() {
        Mockito.when(mockedCollection.size()).thenReturn(RendererAdapterTest.ANY_SIZE);
        Assert.assertEquals(RendererAdapterTest.ANY_SIZE, rendererAdapter.getCount());
    }

    @Test
    public void shouldReturnItemAtCollectionPositionOnGetItem() {
        Mockito.when(mockedCollection.get(RendererAdapterTest.ANY_POSITION)).thenReturn(RendererAdapterTest.ANY_OBJECT);
    }

    @Test
    public void shouldReturnPositionAsItemId() {
        Assert.assertEquals(RendererAdapterTest.ANY_POSITION, rendererAdapter.getItemId(RendererAdapterTest.ANY_POSITION));
    }

    @Test
    public void shouldDelegateIntoRendererBuilderToGetItemViewType() {
        Mockito.when(mockedCollection.get(RendererAdapterTest.ANY_POSITION)).thenReturn(RendererAdapterTest.ANY_OBJECT);
        Mockito.when(mockedRendererBuilder.getItemViewType(RendererAdapterTest.ANY_OBJECT)).thenReturn(RendererAdapterTest.ANY_ITEM_VIEW_TYPE);
        Assert.assertEquals(RendererAdapterTest.ANY_ITEM_VIEW_TYPE, rendererAdapter.getItemViewType(RendererAdapterTest.ANY_POSITION));
    }

    @Test
    public void shouldDelegateIntoRendererBuilderToGetViewTypeCount() {
        Mockito.when(mockedRendererBuilder.getViewTypeCount()).thenReturn(RendererAdapterTest.ANY_VIEW_TYPE_COUNT);
        Assert.assertEquals(RendererAdapterTest.ANY_VIEW_TYPE_COUNT, rendererAdapter.getViewTypeCount());
    }

    @Test
    public void shouldBuildRendererUsingAllNeededDependencies() {
        Mockito.when(mockedCollection.get(RendererAdapterTest.ANY_POSITION)).thenReturn(RendererAdapterTest.ANY_OBJECT);
        Mockito.when(mockedRendererBuilder.build()).thenReturn(mockedRenderer);
        rendererAdapter.getView(RendererAdapterTest.ANY_POSITION, mockedConvertView, mockedParent);
        Mockito.verify(mockedRendererBuilder).withContent(RendererAdapterTest.ANY_OBJECT);
        Mockito.verify(mockedRendererBuilder).withConvertView(mockedConvertView);
        Mockito.verify(mockedRendererBuilder).withParent(mockedParent);
        Mockito.verify(mockedRendererBuilder).withLayoutInflater(((LayoutInflater) (ArgumentMatchers.notNull())));
    }

    @Test
    public void shouldBuildRendererAndCallUpdateRendererExtraValues() {
        Mockito.when(mockedCollection.get(RendererAdapterTest.ANY_POSITION)).thenReturn(RendererAdapterTest.ANY_OBJECT);
        Mockito.when(mockedRendererBuilder.build()).thenReturn(mockedRenderer);
        rendererAdapter.getView(RendererAdapterTest.ANY_POSITION, mockedConvertView, mockedParent);
        Mockito.verify(rendererAdapter).updateRendererExtraValues(RendererAdapterTest.ANY_OBJECT, mockedRenderer, RendererAdapterTest.ANY_POSITION);
    }

    @Test(expected = NullRendererBuiltException.class)
    public void shouldThrowNullRendererBuiltException() {
        rendererAdapter.getView(RendererAdapterTest.ANY_POSITION, mockedConvertView, mockedParent);
    }

    @Test
    public void shouldRenderTheRendererBuilt() {
        Mockito.when(mockedRendererBuilder.build()).thenReturn(mockedRenderer);
        rendererAdapter.getView(RendererAdapterTest.ANY_POSITION, mockedConvertView, mockedParent);
        Mockito.verify(mockedRenderer).render();
    }

    @Test
    public void shouldReturnRendererRootView() {
        Mockito.when(mockedRendererBuilder.build()).thenReturn(mockedRenderer);
        Mockito.when(getRootView()).thenReturn(mockedView);
        View renderedView = rendererAdapter.getView(RendererAdapterTest.ANY_POSITION, mockedConvertView, mockedParent);
        Assert.assertEquals(mockedView, renderedView);
    }

    @Test
    public void shouldAddElementToAdapteeCollection() {
        rendererAdapter.add(RendererAdapterTest.ANY_OBJECT);
        Mockito.verify(mockedCollection).add(RendererAdapterTest.ANY_OBJECT);
    }

    @Test
    public void shouldAddAllElementsToAdapteeCollection() {
        rendererAdapter.addAll(RendererAdapterTest.ANY_OBJECT_COLLECTION);
        Mockito.verify(mockedCollection).addAll(RendererAdapterTest.ANY_OBJECT_COLLECTION);
    }

    @Test
    public void shouldRemoveElementFromAdapteeCollection() {
        rendererAdapter.remove(RendererAdapterTest.ANY_OBJECT);
        Mockito.verify(mockedCollection).remove(RendererAdapterTest.ANY_OBJECT);
    }

    @Test
    public void shouldRemoveAllElementsFromAdapteeCollection() {
        rendererAdapter.removeAll(RendererAdapterTest.ANY_OBJECT_COLLECTION);
        Mockito.verify(mockedCollection).removeAll(RendererAdapterTest.ANY_OBJECT_COLLECTION);
    }

    @Test
    public void shouldClearElementsFromAdapteeCollection() {
        rendererAdapter.clear();
        Mockito.verify(mockedCollection).clear();
    }

    @Test
    public void shouldSetAdapteeCollection() throws Exception {
        RendererAdapter<Object> adapter = new RendererAdapter<Object>(mockedRendererBuilder);
        adapter.setCollection(mockedCollection);
        Assert.assertEquals(mockedCollection, adapter.getCollection());
    }

    @Test
    public void shouldBeEmptyWhenItsCreatedWithJustARendererBuilder() {
        RendererAdapter<Object> adapter = new RendererAdapter<Object>(mockedRendererBuilder);
        Assert.assertEquals(0, adapter.getCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSetNullCollection() {
        RendererAdapter<Object> adapter = new RendererAdapter<Object>(mockedRendererBuilder);
        adapter.setCollection(null);
    }
}

