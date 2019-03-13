package com.pushtorefresh.storio3.contentresolver.impl;


import BackpressureStrategy.MISSING;
import Build.VERSION_CODES;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;
import com.pushtorefresh.storio3.contentresolver.BuildConfig;
import com.pushtorefresh.storio3.contentresolver.Changes;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RxChangesObserverTest {
    @Test
    public void constructorShouldBePrivateAndThrowException() {
        PrivateConstructorChecker.forClass(RxChangesObserver.class).expectedTypeOfException(IllegalStateException.class).expectedExceptionMessage("No instances please.").check();
    }

    @Test
    public void contentObserverShouldReturnFalseOnDeliverSelfNotificationsOnAllSdkVersions() {
        for (int sdkVersion = MIN_SDK_VERSION; sdkVersion < (MAX_SDK_VERSION); sdkVersion++) {
            ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
            Uri uri = Mockito.mock(Uri.class);
            final AtomicReference<ContentObserver> contentObserver = new AtomicReference<ContentObserver>();
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    contentObserver.set(((ContentObserver) (invocation.getArguments()[2])));
                    return null;
                }
            }).when(contentResolver).registerContentObserver(ArgumentMatchers.same(uri), ArgumentMatchers.eq(true), ArgumentMatchers.any(ContentObserver.class));
            Handler handler = Mockito.mock(Handler.class);
            Flowable<Changes> flowable = RxChangesObserver.observeChanges(contentResolver, Collections.singleton(uri), handler, sdkVersion, MISSING);
            Disposable disposable = flowable.subscribe();
            assertThat(contentObserver.get().deliverSelfNotifications()).isFalse();
            disposable.dispose();
        }
    }

    @Test
    public void shouldRegisterOnlyOneContentObserverAfterSubscribingToFlowableOnSdkVersionGreaterThan15() {
        for (int sdkVersion = 16; sdkVersion < (MAX_SDK_VERSION); sdkVersion++) {
            ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
            final AtomicReference<ContentObserver> contentObserver = new AtomicReference<ContentObserver>();
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    // Save reference to ContentObserver only once to assert that it was created once
                    if ((contentObserver.get()) == null) {
                        contentObserver.set(((ContentObserver) (invocation.getArguments()[2])));
                    } else
                        if ((contentObserver.get()) != (invocation.getArguments()[2])) {
                            throw new AssertionError("More than one ContentObserver was created");
                        }

                    return null;
                }
            }).when(contentResolver).registerContentObserver(ArgumentMatchers.any(Uri.class), ArgumentMatchers.eq(true), ArgumentMatchers.any(ContentObserver.class));
            Set<Uri> uris = new HashSet<Uri>(3);
            uris.add(Mockito.mock(Uri.class));
            uris.add(Mockito.mock(Uri.class));
            uris.add(Mockito.mock(Uri.class));
            Flowable<Changes> flowable = RxChangesObserver.observeChanges(contentResolver, uris, Mockito.mock(Handler.class), sdkVersion, MISSING);
            // Should not register ContentObserver before subscribing to Flowable
            Mockito.verify(contentResolver, Mockito.times(0)).registerContentObserver(ArgumentMatchers.any(Uri.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ContentObserver.class));
            Disposable disposable = flowable.subscribe();
            for (Uri uri : uris) {
                // Assert that same ContentObserver was registered for all uris
                Mockito.verify(contentResolver).registerContentObserver(ArgumentMatchers.same(uri), ArgumentMatchers.eq(true), ArgumentMatchers.same(contentObserver.get()));
            }
            disposable.dispose();
        }
    }

    @Test
    public void shouldRegisterObserverForEachPassedUriAfterSubscribingToFlowableOnSdkVersionLowerThan15() {
        for (int sdkVersion = MIN_SDK_VERSION; sdkVersion < 16; sdkVersion++) {
            ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
            final Map<Uri, ContentObserver> contentObservers = new HashMap<Uri, ContentObserver>(3);
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    contentObservers.put(((Uri) (invocation.getArguments()[0])), ((ContentObserver) (invocation.getArguments()[2])));
                    return null;
                }
            }).when(contentResolver).registerContentObserver(ArgumentMatchers.any(Uri.class), ArgumentMatchers.eq(true), ArgumentMatchers.any(ContentObserver.class));
            Set<Uri> uris = new HashSet<Uri>(3);
            uris.add(Mockito.mock(Uri.class));
            uris.add(Mockito.mock(Uri.class));
            uris.add(Mockito.mock(Uri.class));
            Flowable<Changes> flowable = RxChangesObserver.observeChanges(contentResolver, uris, Mockito.mock(Handler.class), sdkVersion, MISSING);
            // Should not register ContentObserver before subscribing to Flowable
            Mockito.verify(contentResolver, Mockito.times(0)).registerContentObserver(ArgumentMatchers.any(Uri.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ContentObserver.class));
            Disposable disposable = flowable.subscribe();
            for (Uri uri : uris) {
                // Assert that new ContentObserver was registered for each uri
                Mockito.verify(contentResolver).registerContentObserver(ArgumentMatchers.same(uri), ArgumentMatchers.eq(true), ArgumentMatchers.same(contentObservers.get(uri)));
            }
            assertThat(contentObservers).hasSameSizeAs(uris);
            disposable.dispose();
        }
    }

    @Test
    public void shouldUnregisterContentObserverAfterDisposingFromFlowableOnSdkVersionGreaterThan15() {
        for (int sdkVersion = 16; sdkVersion < (MAX_SDK_VERSION); sdkVersion++) {
            ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
            Set<Uri> uris = new HashSet<Uri>(3);
            uris.add(Mockito.mock(Uri.class));
            uris.add(Mockito.mock(Uri.class));
            uris.add(Mockito.mock(Uri.class));
            Disposable disposable = RxChangesObserver.observeChanges(contentResolver, uris, Mockito.mock(Handler.class), sdkVersion, MISSING).subscribe();
            // Should not unregister before dispose from Disposable
            Mockito.verify(contentResolver, Mockito.times(0)).unregisterContentObserver(ArgumentMatchers.any(ContentObserver.class));
            disposable.dispose();
            // Should unregister ContentObserver after dispose from Disposable
            Mockito.verify(contentResolver).unregisterContentObserver(ArgumentMatchers.any(ContentObserver.class));
        }
    }

    @Test
    public void shouldUnregisterContentObserversForEachUriAfterDisposingFromFlowableOnSdkVersionLowerThan16() {
        for (int sdkVersion = MIN_SDK_VERSION; sdkVersion < 16; sdkVersion++) {
            ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
            Set<Uri> uris = new HashSet<Uri>(3);
            uris.add(Mockito.mock(Uri.class));
            uris.add(Mockito.mock(Uri.class));
            uris.add(Mockito.mock(Uri.class));
            final Map<Uri, ContentObserver> contentObservers = new HashMap<Uri, ContentObserver>(3);
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    contentObservers.put(((Uri) (invocation.getArguments()[0])), ((ContentObserver) (invocation.getArguments()[2])));
                    return null;
                }
            }).when(contentResolver).registerContentObserver(ArgumentMatchers.any(Uri.class), ArgumentMatchers.eq(true), ArgumentMatchers.any(ContentObserver.class));
            Disposable disposable = RxChangesObserver.observeChanges(contentResolver, uris, Mockito.mock(Handler.class), sdkVersion, MISSING).subscribe();
            // Should not unregister before dispose from Disposable
            Mockito.verify(contentResolver, Mockito.never()).unregisterContentObserver(ArgumentMatchers.any(ContentObserver.class));
            disposable.dispose();
            for (Uri uri : uris) {
                // Assert that ContentObserver for each uri was unregistered
                Mockito.verify(contentResolver).unregisterContentObserver(contentObservers.get(uri));
            }
        }
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Test
    public void shouldEmitChangesOnSdkVersionGreaterThan15() {
        for (int sdkVersion = 16; sdkVersion < (MAX_SDK_VERSION); sdkVersion++) {
            ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
            final AtomicReference<ContentObserver> contentObserver = new AtomicReference<ContentObserver>();
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    // Save reference to ContentObserver only once to assert that it was created once
                    if ((contentObserver.get()) == null) {
                        contentObserver.set(((ContentObserver) (invocation.getArguments()[2])));
                    } else
                        if ((contentObserver.get()) != (invocation.getArguments()[2])) {
                            throw new AssertionError("More than one ContentObserver was created");
                        }

                    return null;
                }
            }).when(contentResolver).registerContentObserver(ArgumentMatchers.any(Uri.class), ArgumentMatchers.eq(true), ArgumentMatchers.any(ContentObserver.class));
            TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
            Uri uri1 = Mockito.mock(Uri.class);
            Uri uri2 = Mockito.mock(Uri.class);
            Set<Uri> uris = new HashSet<Uri>(2);
            uris.add(uri1);
            uris.add(uri2);
            RxChangesObserver.observeChanges(contentResolver, uris, Mockito.mock(Handler.class), sdkVersion, MISSING).subscribe(testSubscriber);
            testSubscriber.assertNotTerminated();
            testSubscriber.assertNoValues();
            // RxChangesObserver should ignore call to onChange() without Uri on sdkVersion >= 16
            contentObserver.get().onChange(false);
            testSubscriber.assertNoValues();
            // Emulate change of Uris, Flowable should react and emit Changes objects
            contentObserver.get().onChange(false, uri1);
            contentObserver.get().onChange(false, uri2);
            testSubscriber.assertValues(Changes.newInstance(uri1), Changes.newInstance(uri2));
            testSubscriber.dispose();
            testSubscriber.assertNoErrors();
        }
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    @Test
    public void shouldEmitChangesOnSdkVersionLowerThan16() {
        for (int sdkVersion = MIN_SDK_VERSION; sdkVersion < 16; sdkVersion++) {
            ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
            final Map<Uri, ContentObserver> contentObservers = new HashMap<Uri, ContentObserver>(3);
            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    contentObservers.put(((Uri) (invocation.getArguments()[0])), ((ContentObserver) (invocation.getArguments()[2])));
                    return null;
                }
            }).when(contentResolver).registerContentObserver(ArgumentMatchers.any(Uri.class), ArgumentMatchers.eq(true), ArgumentMatchers.any(ContentObserver.class));
            TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();
            Uri uri1 = Mockito.mock(Uri.class);
            Uri uri2 = Mockito.mock(Uri.class);
            Set<Uri> uris = new HashSet<Uri>(2);
            uris.add(uri1);
            uris.add(uri2);
            RxChangesObserver.observeChanges(contentResolver, uris, Mockito.mock(Handler.class), sdkVersion, MISSING).subscribe(testSubscriber);
            testSubscriber.assertNotTerminated();
            testSubscriber.assertNoValues();
            // Emulate change of Uris, Flowable should react and emit Changes objects
            contentObservers.get(uri1).onChange(false);
            contentObservers.get(uri2).onChange(false);
            testSubscriber.assertValues(Changes.newInstance(uri1), Changes.newInstance(uri2));
            testSubscriber.dispose();
            testSubscriber.assertNoErrors();
        }
    }

    @Test
    public void shouldDoNothingIfUriListEmpty() {
        for (int sdkVersion = MIN_SDK_VERSION; sdkVersion < (MAX_SDK_VERSION); sdkVersion++) {
            ContentResolver contentResolver = Mockito.mock(ContentResolver.class);
            Disposable disposable = RxChangesObserver.observeChanges(contentResolver, Collections.<Uri>emptySet(), Mockito.mock(Handler.class), sdkVersion, MISSING).subscribe();
            disposable.dispose();
            Mockito.verify(contentResolver, Mockito.never()).registerContentObserver(ArgumentMatchers.any(Uri.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(ContentObserver.class));
            Mockito.verify(contentResolver, Mockito.never()).unregisterContentObserver(ArgumentMatchers.any(ContentObserver.class));
        }
    }
}

