package com.pedrogomez.renderers;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pedrogomez.renderers.exception.NeedsPrototypesException;
import com.pedrogomez.renderers.exception.NullContentException;
import com.pedrogomez.renderers.exception.NullLayoutInflaterException;
import com.pedrogomez.renderers.exception.NullParentException;
import com.pedrogomez.renderers.exception.NullPrototypeClassException;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Test class created to check the correct behaviour of RendererBuilder
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class RendererBuilderTest {
    private ObjectRendererBuilder rendererBuilder;

    private List<Renderer<Object>> prototypes;

    private ObjectRenderer objectRenderer;

    private SubObjectRenderer subObjectRenderer;

    @Mock
    private View mockedConvertView;

    @Mock
    private ViewGroup mockedParent;

    @Mock
    private LayoutInflater mockedLayoutInflater;

    @Mock
    private Object mockedContent;

    @Mock
    private View mockedRendererdView;

    @Test(expected = NeedsPrototypesException.class)
    public void shouldThrowNeedsPrototypeExceptionIfPrototypesIsNull() {
        rendererBuilder = new ObjectRendererBuilder(null);
    }

    @Test(expected = NullContentException.class)
    public void shouldThrowNullContentExceptionIfBuildRendererWithoutContent() {
        buildRenderer(null, mockedConvertView, mockedParent, mockedLayoutInflater);
    }

    @Test(expected = NullParentException.class)
    public void shouldThrowNullParentExceptionIfBuildRendererWithoutParent() {
        buildRenderer(mockedContent, mockedConvertView, null, mockedLayoutInflater);
    }

    @Test(expected = NullPrototypeClassException.class)
    public void shouldThrowNullPrototypeClassExceptionIfRendererBuilderImplementationReturnsNullPrototypeClassAndGetItemViewType() {
        Mockito.when(rendererBuilder.getPrototypeClass(mockedContent)).thenReturn(null);
        buildRenderer(mockedContent, mockedConvertView, mockedParent, mockedLayoutInflater);
        getItemViewType(mockedContent);
    }

    @Test(expected = NullPrototypeClassException.class)
    public void shouldThrowNullPrototypeClassExceptionIfRendererBuilderImplementationReturnsNullPrototypeClassAndBuildOneRenderer() {
        Mockito.when(rendererBuilder.getPrototypeClass(mockedContent)).thenReturn(null);
        buildRenderer(mockedContent, mockedConvertView, mockedParent, mockedLayoutInflater);
        build();
    }

    @Test(expected = NullLayoutInflaterException.class)
    public void shouldThrowNullParentExceptionIfBuildARendererWithoutLayoutInflater() {
        buildRenderer(mockedContent, mockedConvertView, mockedParent, null);
    }

    @Test
    public void shouldReturnCreatedRenderer() {
        Mockito.when(rendererBuilder.getPrototypeClass(mockedContent)).thenReturn(ObjectRenderer.class);
        Renderer<Object> renderer = buildRenderer(mockedContent, null, mockedParent, mockedLayoutInflater);
        Assert.assertEquals(objectRenderer.getClass(), renderer.getClass());
    }

    @Test
    public void shouldReturnRecycledRenderer() {
        Mockito.when(rendererBuilder.getPrototypeClass(mockedContent)).thenReturn(ObjectRenderer.class);
        Mockito.when(mockedConvertView.getTag()).thenReturn(objectRenderer);
        Renderer<Object> renderer = buildRenderer(mockedContent, mockedConvertView, mockedParent, mockedLayoutInflater);
        Assert.assertEquals(objectRenderer, renderer);
    }

    @Test
    public void shouldCreateRendererEvenIfTagInConvertViewIsNotNull() {
        Mockito.when(rendererBuilder.getPrototypeClass(mockedContent)).thenReturn(ObjectRenderer.class);
        Mockito.when(mockedConvertView.getTag()).thenReturn(subObjectRenderer);
        Renderer<Object> renderer = buildRenderer(mockedContent, mockedConvertView, mockedParent, mockedLayoutInflater);
        Assert.assertEquals(objectRenderer.getClass(), renderer.getClass());
    }

    @Test
    public void shouldReturnPrototypeSizeOnGetViewTypeCount() {
        Assert.assertEquals(prototypes.size(), getViewTypeCount());
    }

    @Test(expected = NeedsPrototypesException.class)
    public void shouldNotAcceptNullPrototypes() {
        RendererBuilder<Object> rendererBuilder = new RendererBuilder<Object>();
        rendererBuilder.withPrototypes(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullKeysBindingAPrototype() {
        RendererBuilder<Object> rendererBuilder = new RendererBuilder<Object>();
        rendererBuilder.bind(null, new ObjectRenderer());
    }

    @Test
    public void shouldAddPrototypeAndConfigureRendererBinding() {
        RendererBuilder<Object> rendererBuilder = new RendererBuilder<Object>();
        rendererBuilder.bind(Object.class, new ObjectRenderer());
        Assert.assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test
    public void shouldAddDescendantPrototypeAndConfigureRendererBinding() {
        RendererBuilder<Object> rendererBuilder = new RendererBuilder<Object>();
        rendererBuilder.bind(String.class, new StringRenderer());
        rendererBuilder.bind(Integer.class, new IntegerRenderer());
        Assert.assertEquals(StringRenderer.class, rendererBuilder.getPrototypeClass(""));
        Assert.assertEquals(IntegerRenderer.class, rendererBuilder.getPrototypeClass(0));
    }

    @Test
    public void shouldAddPrototypeAndConfigureBindingByClass() {
        RendererBuilder<Object> rendererBuilder = new RendererBuilder<Object>();
        rendererBuilder.withPrototype(new ObjectRenderer()).bind(Object.class, ObjectRenderer.class);
        Assert.assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test
    public void shouldAddDescendantPrototypesAndConfigureBindingByClass() {
        RendererBuilder<Object> rendererBuilder = new RendererBuilder<Object>();
        rendererBuilder.withPrototype(new StringRenderer()).bind(String.class, StringRenderer.class).withPrototype(new IntegerRenderer()).bind(Integer.class, IntegerRenderer.class);
        Assert.assertEquals(StringRenderer.class, rendererBuilder.getPrototypeClass(""));
        Assert.assertEquals(IntegerRenderer.class, rendererBuilder.getPrototypeClass(0));
    }

    @Test
    public void shouldAddDescendantPrototypesBySetterAndConfigureBindingByClass() {
        RendererBuilder<Object> rendererBuilder = new RendererBuilder<Object>();
        rendererBuilder.setPrototypes(Arrays.asList(new StringRenderer(), new IntegerRenderer()));
        rendererBuilder.bind(String.class, StringRenderer.class);
        rendererBuilder.bind(Integer.class, IntegerRenderer.class);
        Assert.assertEquals(StringRenderer.class, rendererBuilder.getPrototypeClass(""));
        Assert.assertEquals(IntegerRenderer.class, rendererBuilder.getPrototypeClass(0));
    }

    @Test
    public void shouldAddDescendantPrototypesByConstructionAndConfigureBindingByClass() {
        RendererBuilder<Object> rendererBuilder = new RendererBuilder<Object>(Arrays.asList(new StringRenderer(), new IntegerRenderer()));
        rendererBuilder.bind(String.class, StringRenderer.class);
        rendererBuilder.bind(Integer.class, IntegerRenderer.class);
        Assert.assertEquals(StringRenderer.class, rendererBuilder.getPrototypeClass(""));
        Assert.assertEquals(IntegerRenderer.class, rendererBuilder.getPrototypeClass(0));
    }

    @Test
    public void shouldAddPrototyeAndconfigureBindingOnConstruction() {
        RendererBuilder<Object> rendererBuilder = new RendererBuilder<Object>(new ObjectRenderer());
        Assert.assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }
}

