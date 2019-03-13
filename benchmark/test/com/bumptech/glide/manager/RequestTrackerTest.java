package com.bumptech.glide.manager;


import com.bumptech.glide.request.Request;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RequestTrackerTest {
    private RequestTracker tracker;

    @Test
    public void clearRequests_doesNotRecycleRequests() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.addRequest(request);
        tracker.clearRequests();
        assertThat(request.isCleared()).isTrue();
        assertThat(request.isRecycled()).isFalse();
    }

    @Test
    public void clearRemoveAndRecycle_withRequestPreviouslyClearedInClearRequests_doesNothing() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.addRequest(request);
        tracker.clearRequests();
        tracker.clearRemoveAndRecycle(request);
        assertThat(request.isCleared()).isTrue();
        assertThat(request.isRecycled()).isFalse();
    }

    @Test
    public void clearRemoveAndRecycle_withNullRequest_doesNothingAndReturnsTrue() {
        assertThat(tracker.clearRemoveAndRecycle(null)).isTrue();
    }

    @Test
    public void clearRemoveAndRecycle_withUnTrackedRequest_doesNothingAndReturnsFalse() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        assertThat(tracker.clearRemoveAndRecycle(request)).isFalse();
        assertThat(request.isCleared()).isFalse();
        assertThat(request.isRecycled()).isFalse();
    }

    @Test
    public void clearRemoveAndRecycle_withTrackedRequest_clearsRecyclesAndReturnsTrue() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.addRequest(request);
        assertThat(tracker.clearRemoveAndRecycle(request)).isTrue();
        assertThat(request.isCleared()).isTrue();
        assertThat(request.isRecycled()).isTrue();
    }

    @Test
    public void clearRemoveAndRecycle_withAlreadyRemovedRequest_doesNothingAndReturnsFalse() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.addRequest(request);
        tracker.clearRemoveAndRecycle(request);
        assertThat(tracker.clearRemoveAndRecycle(request)).isFalse();
        assertThat(request.isCleared()).isTrue();
        assertThat(request.isRecycled()).isTrue();
    }

    @Test
    public void clearRequests_withPreviouslyClearedRequest_doesNotClearRequestAgain() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.addRequest(request);
        tracker.clearRemoveAndRecycle(request);
        tracker.clearRequests();
        assertThat(request.isCleared()).isTrue();
    }

    @Test
    public void clearRequests_withMultipleRequests_clearsAllRequests() {
        RequestTrackerTest.FakeRequest first = new RequestTrackerTest.FakeRequest();
        RequestTrackerTest.FakeRequest second = new RequestTrackerTest.FakeRequest();
        tracker.addRequest(first);
        tracker.addRequest(second);
        tracker.clearRequests();
        assertThat(first.isCleared()).isTrue();
        assertThat(second.isCleared()).isTrue();
    }

    @Test
    public void pauseRequest_withRunningRequest_pausesRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        request.setIsRunning();
        tracker.addRequest(request);
        tracker.pauseRequests();
        assertThat(request.isCleared()).isTrue();
    }

    @Test
    public void pauseRequests_withCompletedRequest_doesNotClearRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.addRequest(request);
        request.setIsComplete();
        tracker.pauseRequests();
        assertThat(request.isCleared()).isFalse();
    }

    @Test
    public void runRequest_startsRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.runRequest(request);
        assertThat(request.isRunning()).isTrue();
    }

    @Test
    public void runRequest_whenPaused_doesNotStartRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.pauseRequests();
        tracker.runRequest(request);
        assertThat(request.isRunning()).isFalse();
    }

    @Test
    public void runRequest_withAllRequestsPaused_doesNotStartRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.pauseAllRequests();
        tracker.runRequest(request);
        assertThat(request.isRunning()).isFalse();
    }

    @Test
    public void runRequest_afterPausingAndResuming_startsRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.pauseRequests();
        tracker.runRequest(request);
        tracker.resumeRequests();
        assertThat(request.isRunning()).isTrue();
    }

    @Test
    public void pauseRequests_withFailedRequest_doesNotClearRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        request.setIsFailed();
        tracker.addRequest(request);
        tracker.pauseRequests();
        assertThat(request.isCleared()).isFalse();
    }

    @Test
    public void resumeRequests_withRequestAddedWhilePaused_startsRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.addRequest(request);
        tracker.resumeRequests();
        assertThat(request.isRunning()).isTrue();
    }

    @Test
    public void resumeRequests_withCompletedRequest_doesNotRestartCompletedRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        request.setIsComplete();
        tracker.addRequest(request);
        tracker.resumeRequests();
        assertThat(request.isRunning()).isFalse();
    }

    @Test
    public void resumeRequests_withFailedRequest_restartsRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        request.setIsFailed();
        tracker.addRequest(request);
        tracker.resumeRequests();
        assertThat(request.isRunning()).isTrue();
    }

    @Test
    public void addRequest_withRunningRequest_doesNotRestartRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        request.setIsRunning();
        tracker.addRequest(request);
        tracker.resumeRequests();
        assertThat(request.isRunning()).isTrue();
    }

    @Test
    public void resumeRequests_withRequestThatClearsAnotherRequest_avoidsConcurrentModifications() {
        Request first = Mockito.mock(Request.class);
        Request second = Mockito.mock(Request.class);
        Mockito.doAnswer(new RequestTrackerTest.ClearAndRemoveRequest(second)).when(first).begin();
        tracker.addRequest(Mockito.mock(Request.class));
        tracker.addRequest(first);
        tracker.addRequest(second);
        tracker.resumeRequests();
    }

    @Test
    public void pauseRequests_withRequestThatClearsAnother_avoidsConcurrentModifications() {
        Request first = Mockito.mock(Request.class);
        Request second = Mockito.mock(Request.class);
        Mockito.when(first.isRunning()).thenReturn(true);
        Mockito.doAnswer(new RequestTrackerTest.ClearAndRemoveRequest(second)).when(first).clear();
        tracker.addRequest(Mockito.mock(Request.class));
        tracker.addRequest(first);
        tracker.addRequest(second);
        tracker.pauseRequests();
    }

    @Test
    public void clearRequests_withRequestThatClearsAnother_avoidsConcurrentModifications() {
        Request first = Mockito.mock(Request.class);
        Request second = Mockito.mock(Request.class);
        Mockito.doAnswer(new RequestTrackerTest.ClearAndRemoveRequest(second)).when(first).clear();
        tracker.addRequest(Mockito.mock(Request.class));
        tracker.addRequest(first);
        tracker.addRequest(second);
        tracker.clearRequests();
    }

    @Test
    public void restartRequests_withRequestThatClearsAnother_avoidsConcurrentModifications() {
        Request first = Mockito.mock(Request.class);
        Request second = Mockito.mock(Request.class);
        Mockito.doAnswer(new RequestTrackerTest.ClearAndRemoveRequest(second)).when(first).clear();
        tracker.addRequest(Mockito.mock(Request.class));
        tracker.addRequest(first);
        tracker.addRequest(second);
        tracker.restartRequests();
    }

    @Test
    public void restartRequests_withFailedRequest_restartsRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        request.setIsFailed();
        tracker.addRequest(request);
        tracker.restartRequests();
        assertThat(request.isRunning()).isTrue();
    }

    @Test
    public void restartRequests_withIncompleteRequest_restartsRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.addRequest(request);
        tracker.restartRequests();
        assertThat(request.isRunning()).isTrue();
    }

    @Test
    public void restartRequests_whenPaused_doesNotRestartRequests() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        request.setIsFailed();
        tracker.pauseRequests();
        tracker.addRequest(request);
        tracker.restartRequests();
        assertThat(request.isRunning()).isFalse();
    }

    @Test
    public void restartRequests_withFailedRequestAddedWhilePaused_clearsFailedRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        request.setIsFailed();
        tracker.pauseRequests();
        tracker.addRequest(request);
        tracker.restartRequests();
        assertThat(request.isCleared()).isTrue();
    }

    @Test
    public void restartRequests_withIncompleteRequestAddedWhilePaused_doesNotRestartRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.pauseRequests();
        tracker.addRequest(request);
        tracker.restartRequests();
        assertThat(request.isRunning()).isFalse();
    }

    @Test
    public void restartRequests_withIncompleteRequestAddedWhilePaused_clearsRequestOnRestart() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        tracker.pauseRequests();
        tracker.addRequest(request);
        tracker.restartRequests();
        assertThat(request.isCleared()).isTrue();
    }

    @Test
    public void testReturnsTrueFromIsPausedWhenPaused() {
        tracker.pauseRequests();
        Assert.assertTrue(tracker.isPaused());
    }

    @Test
    public void testReturnsFalseFromIsPausedWhenResumed() {
        tracker.resumeRequests();
        Assert.assertFalse(tracker.isPaused());
    }

    @Test
    public void testPauseAllRequests_returnsTrueFromIsPaused() {
        tracker.pauseAllRequests();
        Assert.assertTrue(tracker.isPaused());
    }

    @Test
    public void resumeRequests_afterRequestIsPausedViaPauseAllRequests_resumesRequest() {
        RequestTrackerTest.FakeRequest request = new RequestTrackerTest.FakeRequest();
        request.setIsComplete();
        tracker.addRequest(request);
        tracker.pauseAllRequests();
        assertThat(request.isCleared()).isTrue();
        // reset complete status.
        request.setIsComplete(false);
        tracker.resumeRequests();
        assertThat(request.isRunning()).isTrue();
    }

    private static final class FakeRequest implements Request {
        private boolean isRunning;

        private boolean isFailed;

        private boolean isCleared;

        private boolean isComplete;

        private boolean isRecycled;

        void setIsComplete() {
            setIsComplete(true);
        }

        void setIsComplete(boolean isComplete) {
            this.isComplete = isComplete;
        }

        void setIsFailed() {
            isFailed = true;
        }

        void setIsRunning() {
            isRunning = true;
        }

        boolean isRecycled() {
            return isRecycled;
        }

        @Override
        public void begin() {
            if (isRunning) {
                throw new IllegalStateException();
            }
            isRunning = true;
        }

        @Override
        public void clear() {
            if (isCleared) {
                throw new IllegalStateException();
            }
            isRunning = false;
            isFailed = false;
            isCleared = true;
        }

        @Override
        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public boolean isComplete() {
            return isComplete;
        }

        @Override
        public boolean isResourceSet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCleared() {
            return isCleared;
        }

        @Override
        public boolean isFailed() {
            return isFailed;
        }

        @Override
        public void recycle() {
            if (isRecycled) {
                throw new IllegalStateException();
            }
            isRecycled = true;
        }

        @Override
        public boolean isEquivalentTo(Request other) {
            throw new UnsupportedOperationException();
        }
    }

    private class ClearAndRemoveRequest implements Answer<Void> {
        private final Request toRemove;

        ClearAndRemoveRequest(Request toRemove) {
            this.toRemove = toRemove;
        }

        @Override
        public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
            tracker.clearRemoveAndRecycle(toRemove);
            return null;
        }
    }
}

