package com.pushtorefresh.storio3.contentresolver.impl;


import DefaultStorIOContentResolver.Builder;
import DefaultStorIOContentResolver.CompleteBuilder;
import DefaultStorIOContentResolver.LowLevelImpl;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.TypeMappingFinder;
import com.pushtorefresh.storio3.contentresolver.BuildConfig;
import com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver;
import com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver;
import com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver;
import com.pushtorefresh.storio3.contentresolver.queries.Query;
import com.pushtorefresh.storio3.internal.TypeMappingFinderImpl;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.lang.reflect.Field;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DefaultStorIOContentResolverTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void nullContentResolver() {
        DefaultStorIOContentResolver.Builder builder = DefaultStorIOContentResolver.builder();
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify content resolver");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        builder.contentResolver(null);
    }

    @Test
    public void addTypeMappingNullType() {
        DefaultStorIOContentResolver.CompleteBuilder builder = DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class));
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify type");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions,unchecked
        builder.addTypeMapping(null, ContentResolverTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build());
    }

    @Test
    public void addTypeMappingNullMapping() {
        DefaultStorIOContentResolver.CompleteBuilder builder = DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class));
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify type mapping");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        builder.addTypeMapping(Object.class, null);
    }

    @Test
    public void nullTypeMappingFinder() {
        DefaultStorIOContentResolver.CompleteBuilder builder = DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class));
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify typeMappingFinder");
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        // noinspection ConstantConditions
        builder.typeMappingFinder(null);
    }

    @Test
    public void shouldUseSpecifiedTypeMappingFinder() throws IllegalAccessException, NoSuchFieldException {
        TypeMappingFinder typeMappingFinder = Mockito.mock(TypeMappingFinder.class);
        DefaultStorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).typeMappingFinder(typeMappingFinder).build();
        assertThat(DefaultStorIOContentResolverTest.getTypeMappingFinder(storIOContentResolver)).isEqualTo(typeMappingFinder);
    }

    @Test
    public void typeMappingShouldWorkWithoutSpecifiedTypeMappingFinder() {
        // noinspection unchecked
        ContentResolverTypeMapping<DefaultStorIOContentResolverTest.ClassEntity> typeMapping = ContentResolverTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).addTypeMapping(DefaultStorIOContentResolverTest.ClassEntity.class, typeMapping).build();
        assertThat(storIOContentResolver.lowLevel().typeMapping(DefaultStorIOContentResolverTest.ClassEntity.class)).isEqualTo(typeMapping);
    }

    @Test
    public void typeMappingShouldWorkWithSpecifiedTypeMappingFinder() {
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        // noinspection unchecked
        ContentResolverTypeMapping<DefaultStorIOContentResolverTest.ClassEntity> typeMapping = ContentResolverTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).typeMappingFinder(typeMappingFinder).addTypeMapping(DefaultStorIOContentResolverTest.ClassEntity.class, typeMapping).build();
        assertThat(storIOContentResolver.lowLevel().typeMapping(DefaultStorIOContentResolverTest.ClassEntity.class)).isEqualTo(typeMapping);
    }

    @Test
    public void typeMappingShouldWorkForMultipleTypes() {
        class AnotherEntity {}
        TypeMappingFinder typeMappingFinder = new TypeMappingFinderImpl();
        // noinspection unchecked
        ContentResolverTypeMapping<DefaultStorIOContentResolverTest.ClassEntity> typeMapping = ContentResolverTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
        // noinspection unchecked
        ContentResolverTypeMapping<AnotherEntity> anotherMapping = ContentResolverTypeMapping.builder().putResolver(Mockito.mock(PutResolver.class)).getResolver(Mockito.mock(GetResolver.class)).deleteResolver(Mockito.mock(DeleteResolver.class)).build();
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).typeMappingFinder(typeMappingFinder).addTypeMapping(DefaultStorIOContentResolverTest.ClassEntity.class, typeMapping).addTypeMapping(AnotherEntity.class, anotherMapping).build();
        assertThat(storIOContentResolver.lowLevel().typeMapping(DefaultStorIOContentResolverTest.ClassEntity.class)).isEqualTo(typeMapping);
        assertThat(storIOContentResolver.lowLevel().typeMapping(AnotherEntity.class)).isEqualTo(anotherMapping);
    }

    @Test
    public void shouldThrowExceptionIfContentResolverReturnsNull() {
        ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder().contentResolver(contentResolver).build();
        Query query = Query.builder().uri(Mockito.mock(Uri.class)).build();
        Mockito.when(contentResolver.query(ArgumentMatchers.any(Uri.class), ArgumentMatchers.any(String[].class), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), ArgumentMatchers.anyString())).thenReturn(null);// Notice, we return null instead of Cursor

        try {
            storIOContentResolver.lowLevel().query(query);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("Cursor returned by content provider is null");
        }
    }

    @Test
    public void shouldReturnSameContentResolver() {
        ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder().contentResolver(contentResolver).build();
        assertThat(storIOContentResolver.lowLevel().contentResolver()).isSameAs(contentResolver);
    }

    @Test
    public void deprecatedInternalImplShouldReturnSentToConstructorTypeMapping() throws IllegalAccessException, NoSuchFieldException {
        ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
        TypeMappingFinder typeMappingFinder = Mockito.mock(TypeMappingFinder.class);
        DefaultStorIOContentResolverTest.TestDefaultStorIOContentResolver storIOContentResolver = new DefaultStorIOContentResolverTest.TestDefaultStorIOContentResolver(contentResolver, Mockito.mock(Handler.class), typeMappingFinder);
        assertThat(storIOContentResolver.typeMappingFinder()).isSameAs(typeMappingFinder);
    }

    static class ClassEntity {}

    class TestDefaultStorIOContentResolver extends DefaultStorIOContentResolver {
        private final LowLevel lowLevel;

        TestDefaultStorIOContentResolver(@NonNull
        ContentResolver contentResolver, @NonNull
        Handler contentObserverHandler, @NonNull
        TypeMappingFinder typeMappingFinder) {
            super(contentResolver, contentObserverHandler, typeMappingFinder, null, Collections.<Interceptor>emptyList());
            lowLevel = new LowLevelImpl(typeMappingFinder);
        }

        @Nullable
        TypeMappingFinder typeMappingFinder() throws IllegalAccessException, NoSuchFieldException {
            Field field = LowLevelImpl.class.getDeclaredField("typeMappingFinder");
            field.setAccessible(true);
            return ((TypeMappingFinder) (field.get(lowLevel)));
        }
    }

    @Test
    public void defaultSchedulerReturnsIOSchedulerIfNotSpecified() {
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).build();
        assertThat(storIOContentResolver.defaultRxScheduler()).isSameAs(Schedulers.io());
    }

    @Test
    public void defaultRxSchedulerReturnsSpecifiedScheduler() {
        Scheduler scheduler = Mockito.mock(Scheduler.class);
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).defaultRxScheduler(scheduler).build();
        assertThat(storIOContentResolver.defaultRxScheduler()).isSameAs(scheduler);
    }

    @Test
    public void defaultRxSchedulerReturnsNullIfSpecifiedSchedulerNull() {
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder().contentResolver(Mockito.mock(ContentResolver.class)).defaultRxScheduler(null).build();
        assertThat(storIOContentResolver.defaultRxScheduler()).isNull();
    }

    @Test
    public void shouldUseCustomHandlerForContentObservers() {
        ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
        ArgumentCaptor<ContentObserver> observerArgumentCaptor = ArgumentCaptor.forClass(ContentObserver.class);
        Mockito.doNothing().when(contentResolver).registerContentObserver(ArgumentMatchers.any(Uri.class), ArgumentMatchers.anyBoolean(), observerArgumentCaptor.capture());
        Handler handler = Mockito.mock(Handler.class);
        StorIOContentResolver storIOContentResolver = DefaultStorIOContentResolver.builder().contentResolver(contentResolver).contentObserverHandler(handler).defaultRxScheduler(null).build();
        Disposable disposable = storIOContentResolver.observeChangesOfUri(Mockito.mock(Uri.class), BackpressureStrategy.LATEST).subscribe();
        assertThat(observerArgumentCaptor.getAllValues()).hasSize(1);
        ContentObserver contentObserver = observerArgumentCaptor.getValue();
        Object actualHandler = ReflectionHelpers.getField(contentObserver, "mHandler");
        assertThat(actualHandler).isEqualTo(handler);
        disposable.dispose();
    }
}

