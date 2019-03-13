/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.domain;


import Level.DEBUG;
import Level.ERROR;
import Level.WARN;
import com.thoughtworks.go.util.HttpService;
import com.thoughtworks.go.util.LogFixture;
import com.thoughtworks.go.util.TestingClock;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mockito.Mockito;


public class DownloadActionTest {
    private TestingClock clock;

    private FetchHandler fetchHandler;

    private StubGoPublisher publisher;

    @Test
    public void shouldRetryWhenCreatingFolderZipCache() throws Exception {
        Mockito.when(fetchHandler.handleResult(200, publisher)).thenReturn(true);
        DownloadActionTest.MockCachingFetchZipHttpService httpService = new DownloadActionTest.MockCachingFetchZipHttpService(3);
        DownloadAction downloadAction = new DownloadAction(httpService, publisher, clock);
        downloadAction.perform("foo", fetchHandler);
        Assert.assertThat(httpService.timesCalled, Matchers.is(3));
        Assert.assertThat(clock.getSleeps(), JUnitMatchers.hasItems(5000L));
    }

    @Test
    public void shouldRetryThreeTimesWhenDownloadFails() throws Exception {
        Mockito.when(fetchHandler.handleResult(200, publisher)).thenReturn(true);
        try (LogFixture logging = LogFixture.logFixtureFor(DownloadAction.class, DEBUG)) {
            DownloadActionTest.FailSometimesHttpService httpService = new DownloadActionTest.FailSometimesHttpService(3);
            DownloadAction downloadAction = new DownloadAction(httpService, publisher, clock);
            downloadAction.perform("foo", fetchHandler);
            Assert.assertThat(httpService.timesCalled, Matchers.is(4));
            shouldHaveLogged(logging, WARN, "Could not fetch artifact foo.");
            shouldHaveLogged(logging, WARN, "Error was : Caught an exception 'Connection Reset'");
            assertBetween(clock.getSleeps().get(0), 10000L, 20000L);
            assertBetween(clock.getSleeps().get(1), 20000L, 30000L);
            assertBetween(clock.getSleeps().get(2), 30000L, 40000L);
            Assert.assertThat(clock.getSleeps().size(), Matchers.is(3));
        }
    }

    @Test
    public void shouldFailAfterFourthTryWhenDownloadFails() throws Exception {
        try (LogFixture logging = LogFixture.logFixtureFor(DownloadAction.class, DEBUG)) {
            DownloadActionTest.FailSometimesHttpService httpService = new DownloadActionTest.FailSometimesHttpService(99);
            try {
                new DownloadAction(httpService, new StubGoPublisher(), clock).perform("foo", fetchHandler);
                Assert.fail("Expected to throw exception after four tries");
            } catch (Exception e) {
                Assert.assertThat(httpService.timesCalled, Matchers.is(4));
                shouldHaveLogged(logging, ERROR, "Giving up fetching resource 'foo'. Tried 4 times and failed.");
            }
        }
    }

    @Test
    public void shouldReturnWithoutRetryingArtifactIsNotModified() throws Exception {
        fetchHandler = new FileHandler(new File(""), getSrc());
        HttpService httpService = Mockito.mock(HttpService.class);
        StubGoPublisher goPublisher = new StubGoPublisher();
        Mockito.when(httpService.download("foo", fetchHandler)).thenReturn(SC_NOT_MODIFIED);
        new DownloadAction(httpService, goPublisher, clock).perform("foo", fetchHandler);
        Mockito.verify(httpService).download("foo", this.fetchHandler);
        Mockito.verifyNoMoreInteractions(httpService);
        Assert.assertThat(goPublisher.getMessage(), Matchers.containsString("Artifact is not modified, skipped fetching it"));
    }

    private class MockCachingFetchZipHttpService extends HttpService {
        private final int count;

        private int timesCalled = 0;

        MockCachingFetchZipHttpService(int count) {
            this.count = count;
        }

        public int download(String url, FetchHandler handler) throws IOException {
            timesCalled += 1;
            if ((timesCalled) < (count)) {
                return SC_ACCEPTED;
            } else {
                return SC_OK;
            }
        }
    }

    private class FailSometimesHttpService extends HttpService {
        private int count;

        private int timesCalled = 0;

        public FailSometimesHttpService(int count) {
            this.count = count;
        }

        public int download(String url, FetchHandler handler) throws IOException {
            timesCalled += 1;
            if ((timesCalled) <= (count)) {
                throw new SocketException("Connection Reset");
            } else {
                return SC_OK;
            }
        }
    }
}

