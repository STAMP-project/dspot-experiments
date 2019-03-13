package com.bumptech.glide.request;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


@RunWith(JUnit4.class)
public class ThumbnailRequestCoordinatorTest {
    @Mock
    private Request full;

    @Mock
    private Request thumb;

    @Mock
    private RequestCoordinator parent;

    private ThumbnailRequestCoordinator coordinator;

    @Test
    public void testIsRunningIsFalseIfNeitherRequestIsRunning() {
        Assert.assertFalse(coordinator.isRunning());
    }

    @Test
    public void testIsRunningIsTrueIfFullIsRunning() {
        Mockito.when(full.isRunning()).thenReturn(true);
        Assert.assertTrue(coordinator.isRunning());
    }

    @Test
    public void testIsNotRunningIfFullIsNotRunningButThumbIs() {
        Mockito.when(full.isRunning()).thenReturn(false);
        Mockito.when(thumb.isRunning()).thenReturn(true);
        Assert.assertFalse(coordinator.isRunning());
    }

    @Test
    public void testStartsFullOnRunIfNotRunning() {
        Mockito.when(full.isRunning()).thenReturn(false);
        coordinator.begin();
        Mockito.verify(full).begin();
    }

    @Test
    public void testStartsThumbOnRunIfNotRunning() {
        Mockito.when(thumb.isRunning()).thenReturn(false);
        coordinator.begin();
        Mockito.verify(thumb).begin();
    }

    @Test
    public void testDoesNotStartFullOnRunIfRunning() {
        Mockito.when(full.isRunning()).thenReturn(true);
        coordinator.begin();
        Mockito.verify(full, Mockito.never()).begin();
    }

    @Test
    public void testDoesNotStartThumbOnRunIfRunning() {
        Mockito.when(thumb.isRunning()).thenReturn(true);
        coordinator.begin();
        Mockito.verify(thumb, Mockito.never()).begin();
    }

    @Test
    public void begin_whenFullIsComplete_startsFull() {
        Mockito.when(full.isComplete()).thenReturn(true);
        coordinator.begin();
        Mockito.verify(full).begin();
    }

    @Test
    public void begin_whenFullIsComplete_doesNotBeginThumb() {
        Mockito.when(full.isComplete()).thenReturn(true);
        coordinator.begin();
        Mockito.verify(thumb, Mockito.never()).begin();
    }

    @Test
    public void begin_whenFullIsComplete_doesNotSetRunning() {
        Mockito.when(full.isComplete()).thenReturn(true);
        coordinator.begin();
        assertThat(coordinator.isRunning()).isFalse();
    }

    @Test
    public void testDoesNotStartFullIfClearedByThumb() {
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                coordinator.clear();
                return null;
            }
        }).when(thumb).begin();
        coordinator.begin();
        Mockito.verify(full, Mockito.never()).begin();
    }

    @Test
    public void testCallsClearOnRequestsWhenCleared() {
        coordinator.clear();
        InOrder order = Mockito.inOrder(thumb, full);
        order.verify(thumb).clear();
        order.verify(full).clear();
    }

    @Test
    public void testRecyclesRequestsWhenRecycled() {
        coordinator.recycle();
        Mockito.verify(thumb).recycle();
        Mockito.verify(full).recycle();
    }

    @Test
    public void testCanSetImageReturnsTrueForFullRequestIfCoordinatorIsNull() {
        coordinator = new ThumbnailRequestCoordinator();
        coordinator.setRequests(full, thumb);
        Assert.assertTrue(coordinator.canSetImage(full));
    }

    @Test
    public void testCanSetImageReturnsTrueForFullRequestIfParentAllowsSetImage() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        Mockito.when(parent.canSetImage(ArgumentMatchers.eq(coordinator))).thenReturn(true);
        Assert.assertTrue(coordinator.canSetImage(full));
    }

    @Test
    public void testCanSetImageReturnsFalseForFullRequestIfParentDoesNotAllowSetImage() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        Mockito.when(parent.canSetImage(ArgumentMatchers.eq(coordinator))).thenReturn(false);
        Assert.assertFalse(coordinator.canSetImage(full));
    }

    @Test
    public void testCanSetImageReturnsTrueForThumbRequestIfParentIsNullAndFullDoesNotHaveResourceSet() {
        Mockito.when(full.isResourceSet()).thenReturn(false);
        Assert.assertTrue(coordinator.canSetImage(thumb));
    }

    @Test
    public void testCanSetImageReturnsFalseForThumbRequestIfParentIsNullAndFullHasResourceSet() {
        Mockito.when(full.isResourceSet()).thenReturn(true);
        Assert.assertFalse(coordinator.canSetImage(thumb));
    }

    @Test
    public void testCanNotSetImageForThumbIfNotAllowedByParentAndFullDoesNotHaveResourceSet() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        Mockito.when(parent.canSetImage(ArgumentMatchers.eq(coordinator))).thenReturn(false);
        Mockito.when(full.isResourceSet()).thenReturn(false);
        Assert.assertFalse(coordinator.canSetImage(thumb));
    }

    @Test
    public void testCanNotifyStatusChangedIfFullAndNoRequestsAreComplete() {
        Assert.assertTrue(coordinator.canNotifyStatusChanged(full));
    }

    @Test
    public void testCanNotNotifyStatusChangedIfThumb() {
        Assert.assertFalse(coordinator.canNotifyStatusChanged(thumb));
    }

    @Test
    public void testCanNotNotifyStatusChangedIfFullHasResourceSet() {
        Mockito.when(full.isResourceSet()).thenReturn(true);
        Assert.assertFalse(coordinator.canNotifyStatusChanged(full));
    }

    @Test
    public void testCanNotNotifyStatusChangedIfThumbHasResourceSet() {
        Mockito.when(thumb.isResourceSet()).thenReturn(true);
        Assert.assertFalse(coordinator.canNotifyStatusChanged(full));
    }

    @Test
    public void testCanNotNotifyStatusChangedIfParentHasResourceSet() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        Mockito.when(parent.isAnyResourceSet()).thenReturn(true);
        Assert.assertFalse(coordinator.canNotifyStatusChanged(full));
    }

    @Test
    public void testCanNotifyStatusChangedIfParentAllowsNotify() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        Mockito.when(parent.canNotifyStatusChanged(ArgumentMatchers.eq(coordinator))).thenReturn(true);
        Assert.assertTrue(coordinator.canNotifyStatusChanged(full));
    }

    @Test
    public void testCanNotNotifyStatusChangedIfParentDoesNotAllowNotify() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        Mockito.when(parent.canNotifyStatusChanged(ArgumentMatchers.eq(coordinator))).thenReturn(false);
        Assert.assertFalse(coordinator.canNotifyStatusChanged(full));
    }

    @Test
    public void testIsAnyResourceSetIsFalseIfNeitherRequestHasResourceSet() {
        Mockito.when(full.isResourceSet()).thenReturn(false);
        Mockito.when(thumb.isResourceSet()).thenReturn(false);
        Assert.assertFalse(coordinator.isAnyResourceSet());
    }

    @Test
    public void testIsAnyResourceSetIsTrueIfFullHasResourceSet() {
        Mockito.when(full.isResourceSet()).thenReturn(true);
        Mockito.when(thumb.isResourceSet()).thenReturn(false);
        Assert.assertTrue(coordinator.isAnyResourceSet());
    }

    @Test
    public void testIsAnyResourceSetIsTrueIfThumbHasResourceSet() {
        Mockito.when(full.isResourceSet()).thenReturn(false);
        Mockito.when(thumb.isResourceSet()).thenReturn(true);
        Assert.assertTrue(coordinator.isAnyResourceSet());
    }

    @Test
    public void testIsAnyResourceSetIsTrueIfParentIsNonNullAndParentHasResourceSet() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        Mockito.when(parent.isAnyResourceSet()).thenReturn(true);
        Mockito.when(full.isResourceSet()).thenReturn(false);
        Mockito.when(thumb.isResourceSet()).thenReturn(false);
        Assert.assertTrue(coordinator.isAnyResourceSet());
    }

    @Test
    public void testIsNotCompleteIfNeitherRequestIsComplete() {
        Assert.assertFalse(coordinator.isComplete());
    }

    @Test
    public void testIsCompleteIfFullIsComplete() {
        Mockito.when(full.isComplete()).thenReturn(true);
        Assert.assertTrue(coordinator.isComplete());
    }

    @Test
    public void testIsCompleteIfThumbIsComplete() {
        Mockito.when(thumb.isComplete()).thenReturn(true);
        Assert.assertTrue(coordinator.isComplete());
    }

    @Test
    public void testIsResourceSetIsFalseIfNeitherRequestHasResourceSet() {
        Assert.assertFalse(coordinator.isResourceSet());
    }

    @Test
    public void testIsResourceSetIsTrueIfFullRequestHasResourceSet() {
        Mockito.when(full.isResourceSet()).thenReturn(true);
        Assert.assertTrue(coordinator.isResourceSet());
    }

    @Test
    public void testIsResourceSetIsTrueIfThumbRequestHasResourceSet() {
        Mockito.when(thumb.isResourceSet()).thenReturn(true);
        Assert.assertTrue(coordinator.isResourceSet());
    }

    @Test
    public void testClearsThumbRequestOnFullRequestComplete_withNullParent() {
        coordinator.onRequestSuccess(full);
        Mockito.verify(thumb).clear();
    }

    @Test
    public void testNotifiesParentOnFullRequestComplete_withNonNullParent() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        coordinator.onRequestSuccess(full);
        Mockito.verify(parent).onRequestSuccess(ArgumentMatchers.eq(coordinator));
    }

    @Test
    public void testClearsThumbRequestOnFullRequestComplete_withNonNullParent() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        coordinator.onRequestSuccess(full);
        Mockito.verify(thumb).clear();
    }

    @Test
    public void testDoesNotClearThumbOnThumbRequestComplete() {
        coordinator.onRequestSuccess(thumb);
        Mockito.verify(thumb, Mockito.never()).clear();
    }

    @Test
    public void testDoesNotClearThumbOnFullComplete_whenThumbIsComplete() {
        Mockito.when(thumb.isComplete()).thenReturn(true);
        coordinator.onRequestSuccess(full);
        Mockito.verify(thumb, Mockito.never()).clear();
    }

    @Test
    public void testDoesNotNotifyParentOnThumbRequestComplete() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        coordinator.onRequestSuccess(thumb);
        Mockito.verify(parent, Mockito.never()).onRequestSuccess(ArgumentMatchers.any(Request.class));
    }

    @Test
    public void canNotifyCleared_withThumbRequest_returnsFalse() {
        assertThat(coordinator.canNotifyCleared(thumb)).isFalse();
    }

    @Test
    public void canNotifyCleared_withFullRequest_andNullParent_returnsTrue() {
        assertThat(coordinator.canNotifyCleared(full)).isTrue();
    }

    @Test
    public void canNotifyCleared_withFullRequest_nonNullParent_parentCanClear_returnsTrue() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        Mockito.when(parent.canNotifyCleared(coordinator)).thenReturn(true);
        assertThat(coordinator.canNotifyCleared(full)).isTrue();
    }

    @Test
    public void canNotifyCleared_withFullRequest_nonNullParent_parentCanNotClear_returnsFalse() {
        coordinator = new ThumbnailRequestCoordinator(parent);
        coordinator.setRequests(full, thumb);
        Mockito.when(parent.canNotifyCleared(coordinator)).thenReturn(false);
        assertThat(coordinator.canNotifyCleared(full)).isFalse();
    }

    @Test
    public void testIsEquivalentTo() {
        ThumbnailRequestCoordinator first = new ThumbnailRequestCoordinator();
        Mockito.when(full.isEquivalentTo(full)).thenReturn(true);
        Mockito.when(thumb.isEquivalentTo(thumb)).thenReturn(true);
        first.setRequests(full, thumb);
        Assert.assertTrue(first.isEquivalentTo(first));
        ThumbnailRequestCoordinator second = new ThumbnailRequestCoordinator();
        second.setRequests(full, full);
        Assert.assertTrue(second.isEquivalentTo(second));
        Assert.assertFalse(second.isEquivalentTo(first));
        Assert.assertFalse(first.isEquivalentTo(second));
        ThumbnailRequestCoordinator third = new ThumbnailRequestCoordinator();
        third.setRequests(thumb, thumb);
        Assert.assertTrue(third.isEquivalentTo(third));
        Assert.assertFalse(third.isEquivalentTo(first));
        Assert.assertFalse(first.isEquivalentTo(third));
    }
}

