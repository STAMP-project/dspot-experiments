package com.bumptech.glide.load.model;


import MultiModelLoaderFactory.Factory;
import android.support.v4.util.Pools.Pool;
import com.bumptech.glide.Registry.NoModelLoaderAvailableException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// containsExactly produces a spurious warning.
@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class MultiModelLoaderFactoryTest {
    @Mock
    private ModelLoaderFactory<String, String> firstFactory;

    @Mock
    private ModelLoader<String, String> firstModelLoader;

    @Mock
    private Factory multiModelLoaderFactory;

    @Mock
    private ModelLoaderFactory<String, String> secondFactory;

    @Mock
    private ModelLoader<String, String> secondModelLoader;

    private Pool<List<Throwable>> throwableListPool;

    private MultiModelLoaderFactory multiFactory;

    @Test
    public void testAppend_addsModelLoaderForModelClass() {
        multiFactory.append(String.class, String.class, firstFactory);
        List<ModelLoader<String, ?>> modelLoaders = multiFactory.build(String.class);
        assertThat(modelLoaders).containsExactly(firstModelLoader);
    }

    @Test
    public void testAppend_addsModelLoaderForModelAndDataClass() {
        multiFactory.append(String.class, String.class, firstFactory);
        ModelLoader<String, String> modelLoader = multiFactory.build(String.class, String.class);
        assertThat(modelLoader).isEqualTo(firstModelLoader);
    }

    @Test
    public void testPrepend_addsModelLoaderForModelClass() {
        multiFactory.prepend(String.class, String.class, firstFactory);
        List<ModelLoader<String, ?>> modelLoaders = multiFactory.build(String.class);
        assertThat(modelLoaders).containsExactly(firstModelLoader);
    }

    @Test
    public void testPrepend_addsModelLoaderForModelAndDataClass() {
        multiFactory.prepend(String.class, String.class, firstFactory);
        ModelLoader<String, String> modelLoader = multiFactory.build(String.class, String.class);
        assertThat(modelLoader).isEqualTo(firstModelLoader);
    }

    @Test
    public void testReplace_addsModelLoaderForModelClass() {
        multiFactory.replace(String.class, String.class, firstFactory);
        List<ModelLoader<String, ?>> modelLoaders = multiFactory.build(String.class);
        assertThat(modelLoaders).containsExactly(firstModelLoader);
    }

    @Test
    public void testReplace_addsModelLoaderForModelAndDataClasses() {
        multiFactory.replace(String.class, String.class, firstFactory);
        ModelLoader<String, String> modelLoader = multiFactory.build(String.class, String.class);
        assertThat(modelLoader).isEqualTo(firstModelLoader);
    }

    @Test
    public void testReplace_returnsPreviouslyRegisteredFactories_withModelAndDataClasses() {
        ModelLoaderFactory<String, String> firstOtherFactory = MultiModelLoaderFactoryTest.mockFactory();
        ModelLoaderFactory<String, String> secondOtherFactory = MultiModelLoaderFactoryTest.mockFactory();
        multiFactory.append(String.class, String.class, firstOtherFactory);
        multiFactory.append(String.class, String.class, secondOtherFactory);
        List<ModelLoaderFactory<? extends String, ? extends String>> removed = multiFactory.replace(String.class, String.class, firstFactory);
        assertThat(removed).containsExactly(firstOtherFactory, secondOtherFactory);
    }

    @Test
    public void testReplace_removesPreviouslyRegisteredFactories_withModelAndDataClasses() {
        appendFactoryFor(String.class, String.class);
        appendFactoryFor(String.class, String.class);
        multiFactory.replace(String.class, String.class, firstFactory);
        List<ModelLoader<String, ?>> modelLoaders = multiFactory.build(String.class);
        assertThat(modelLoaders).containsExactly(firstModelLoader);
    }

    @Test
    public void testRemove_returnsPreviouslyRegisteredFactories_withModelAndDataClasses() {
        ModelLoaderFactory<String, String> other = MultiModelLoaderFactoryTest.mockFactory();
        multiFactory.append(String.class, String.class, other);
        multiFactory.append(String.class, String.class, firstFactory);
        List<ModelLoaderFactory<? extends String, ? extends String>> removed = multiFactory.remove(String.class, String.class);
        assertThat(removed).containsExactly(firstFactory, other);
    }

    @Test
    public void testRemove_removesPreviouslyRegisteredFactories_withModelAndDataClasses() {
        appendFactoryFor(String.class, String.class);
        appendFactoryFor(String.class, String.class);
        multiFactory.remove(String.class, String.class);
        List<ModelLoader<String, ?>> modelLoaders = multiFactory.build(String.class);
        assertThat(modelLoaders).isEmpty();
    }

    @Test
    public void testBuild_withModelClass_returnsMultipleModelLoaders_ofGivenModelAndDataClasses() {
        ModelLoader<String, String> otherLoader = appendFactoryFor(String.class, String.class);
        multiFactory.append(String.class, String.class, firstFactory);
        List<ModelLoader<String, ?>> modelLoaders = multiFactory.build(String.class);
        assertThat(modelLoaders).containsExactly(otherLoader, firstModelLoader);
    }

    @Test
    public void testBuild_withModelClass_returnsMultipleModelLoaders_ofGivenModelClassWithDifferentDataClasses() {
        ModelLoader<String, Integer> otherLoader = appendFactoryFor(String.class, Integer.class);
        multiFactory.append(String.class, String.class, firstFactory);
        List<ModelLoader<String, ?>> modelLoaders = multiFactory.build(String.class);
        assertThat(modelLoaders).containsExactly(otherLoader, firstModelLoader);
    }

    @Test
    public void testBuild_withModelClass_excludesModelLoadersForOtherModelClasses() {
        multiFactory.append(String.class, String.class, firstFactory);
        List<ModelLoader<Integer, ?>> modelLoaders = multiFactory.build(Integer.class);
        assertThat(modelLoaders).doesNotContain(firstModelLoader);
    }

    @Test
    public void testBuild_withModelAndDataClasses_returnsMultipleModelLoaders_ofGivenModelAndDataClasses() {
        ModelLoader<String, String> otherLoader = appendFactoryFor(String.class, String.class);
        multiFactory.append(String.class, String.class, firstFactory);
        List<ModelLoader<String, String>> modelLoaders = buildModelLoaders(String.class, String.class);
        assertThat(modelLoaders).containsExactly(otherLoader, firstModelLoader);
    }

    @Test
    public void testBuild_withModelAndDataClasses_excludesModelLoadersForOtherDataClasses() {
        multiFactory.append(String.class, String.class, firstFactory);
        Assert.assertThrows(NoModelLoaderAvailableException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                multiFactory.build(String.class, Integer.class);
            }
        });
    }

    @Test
    public void testBuild_withModelAndDataClasses_excludesModelLoadersForOtherModelClasses() {
        multiFactory.append(String.class, String.class, firstFactory);
        Assert.assertThrows(NoModelLoaderAvailableException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                multiFactory.build(Integer.class, String.class);
            }
        });
    }

    @Test
    public void testBuild_withModelClass_doesNotMatchSubclassesOfModelClass() {
        ModelLoader<String, Object> subclass = appendFactoryFor(String.class, Object.class);
        List<ModelLoader<Object, ?>> modelLoaders = multiFactory.build(Object.class);
        assertThat(modelLoaders).doesNotContain(subclass);
    }

    @Test
    public void testBuild_withModelClass_matchesSuperclassesOfModelClass() {
        ModelLoader<Object, Object> superclass = appendFactoryFor(Object.class, Object.class);
        List<ModelLoader<String, ?>> modelLoaders = multiFactory.build(String.class);
        assertThat(modelLoaders).contains(superclass);
    }

    @Test
    public void testBuild_withModelAndDataClass_doesNotMatchSubclassesOfModelClass() {
        appendFactoryFor(String.class, Object.class);
        Assert.assertThrows(NoModelLoaderAvailableException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                multiFactory.build(Object.class, Object.class);
            }
        });
    }

    @Test
    public void testBuild_withModelAndDataClass_doesNotMatchSubclassesOfDataClass() {
        appendFactoryFor(Object.class, String.class);
        Assert.assertThrows(NoModelLoaderAvailableException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                multiFactory.build(Object.class, Object.class);
            }
        });
    }

    @Test
    public void testBuild_withModelAndDataClass_doesMatchSuperclassesOfModelClass() {
        ModelLoader<Object, Object> firstSuperClass = appendFactoryFor(Object.class, Object.class);
        ModelLoader<Object, Object> secondSuperClass = appendFactoryFor(Object.class, Object.class);
        List<ModelLoader<String, Object>> modelLoaders = buildModelLoaders(String.class, Object.class);
        assertThat(modelLoaders).containsExactly(firstSuperClass, secondSuperClass);
    }

    @Test
    public void testBuild_withModelAndDataClass_matchesSuperclassesOfDataClass() {
        ModelLoader<Object, Object> firstSuperClass = appendFactoryFor(Object.class, Object.class);
        ModelLoader<Object, Object> secondSuperClass = appendFactoryFor(Object.class, Object.class);
        List<ModelLoader<Object, String>> modelLoaders = buildModelLoaders(Object.class, String.class);
        assertThat(modelLoaders).containsExactly(firstSuperClass, secondSuperClass);
    }

    @Test
    public void testBuild_withModelAndDataClass_matchesSuperclassOfModelAndDataClass() {
        ModelLoader<Object, Object> firstSuperclass = appendFactoryFor(Object.class, Object.class);
        ModelLoader<Object, Object> secondSuperclass = appendFactoryFor(Object.class, Object.class);
        List<ModelLoader<String, String>> modelLoaders = buildModelLoaders(String.class, String.class);
        assertThat(modelLoaders).containsExactly(firstSuperclass, secondSuperclass);
    }

    @Test
    public void testBuild_respectsAppendOrder() {
        ModelLoader<String, String> first = appendFactoryFor(String.class, String.class);
        ModelLoader<String, String> second = appendFactoryFor(String.class, String.class);
        ModelLoader<String, String> third = appendFactoryFor(String.class, String.class);
        List<ModelLoader<String, String>> modelLoaders = buildModelLoaders(String.class, String.class);
        assertThat(modelLoaders).containsExactly(first, second, third).inOrder();
    }

    @Test
    public void testBuild_respectsPrependOrder() {
        ModelLoader<String, String> first = prependFactoryFor(String.class, String.class);
        ModelLoader<String, String> second = prependFactoryFor(String.class, String.class);
        ModelLoader<String, String> third = prependFactoryFor(String.class, String.class);
        List<ModelLoader<String, String>> modelLoaders = buildModelLoaders(String.class, String.class);
        assertThat(modelLoaders).containsExactly(third, second, first).inOrder();
    }
}

