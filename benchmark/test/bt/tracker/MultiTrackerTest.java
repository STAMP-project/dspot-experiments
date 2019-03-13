/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
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
package bt.tracker;


import bt.metainfo.TorrentId;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import org.junit.Test;


public class MultiTrackerTest {
    private static final String trackerUrl1;

    private static final String trackerUrl2;

    private static final String trackerUrl3;

    private static final String backupUrl1;

    private static final String backupUrl2;

    static {
        trackerUrl1 = "http://tracker1.org/ann";
        trackerUrl2 = "http://tracker2.org/ann";
        trackerUrl3 = "http://tracker3.org/ann";
        backupUrl1 = "http://backup1.org/ann";
        backupUrl2 = "http://backup2.org/ann";
    }

    private AnnounceKey announceKey;

    private TorrentId torrentId;

    private ITrackerService trackerService;

    private List<Tracker> accessLog;

    private MultiTrackerTest.StoppableTracker tracker1;

    private MultiTrackerTest.StoppableTracker tracker2;

    private MultiTrackerTest.StoppableTracker tracker3;

    private MultiTrackerTest.StoppableTracker backup1;

    private MultiTrackerTest.StoppableTracker backup2;

    @Test
    public void testMultiTracker_FirstTrackerReachable() {
        MultiTracker tracker = new MultiTracker(trackerService, announceKey, false);
        tracker.request(torrentId).start();
        assertLogHasTrackers(tracker1);
    }

    @Test
    public void testMultiTracker_UnreachableTrackers_MainTier() {
        MultiTracker tracker = new MultiTracker(trackerService, announceKey, false);
        tracker1.shutdown();
        tracker.request(torrentId).start();
        assertLogHasTrackers(tracker1, tracker2);
        clearLog();
        tracker.request(torrentId).query();
        assertLogHasTrackers(tracker2);
        clearLog();
        tracker2.shutdown();
        tracker.request(torrentId).query();
        assertLogHasTrackers(tracker2, tracker1, tracker3);
        clearLog();
        tracker3.shutdown();
        tracker1.startup();
        tracker.request(torrentId).query();
        assertLogHasTrackers(tracker3, tracker2, tracker1);
        clearLog();
        tracker.request(torrentId).stop();
        assertLogHasTrackers(tracker1);
    }

    @Test
    public void testMultiTracker_UnreachableTrackers_Backups() {
        MultiTracker tracker = new MultiTracker(trackerService, announceKey, false);
        tracker1.shutdown();
        tracker2.shutdown();
        tracker3.shutdown();
        tracker.request(torrentId).start();
        assertLogHasTrackers(tracker1, tracker2, tracker3, backup1);
        clearLog();
        backup1.shutdown();
        tracker.request(torrentId).query();
        assertLogHasTrackers(tracker1, tracker2, tracker3, backup1, backup2);
        clearLog();
        tracker.request(torrentId).stop();
        assertLogHasTrackers(tracker1, tracker2, tracker3, backup2);
    }

    private static class StoppableTracker implements Tracker {
        private Tracker instance;

        private final String url;

        private TrackerRequestBuilder requestBuilder;

        private boolean shutdown;

        public StoppableTracker(String url, TorrentId torrentId, Consumer<Tracker> accessLog) {
            instance = this;
            this.url = url;
            requestBuilder = new TrackerRequestBuilder(torrentId) {
                @Override
                public TrackerResponse start() {
                    return logAndResponse();
                }

                @Override
                public TrackerResponse stop() {
                    return logAndResponse();
                }

                @Override
                public TrackerResponse complete() {
                    return logAndResponse();
                }

                @Override
                public TrackerResponse query() {
                    return logAndResponse();
                }

                private TrackerResponse logAndResponse() {
                    accessLog.accept(instance);
                    return shutdown ? TrackerResponse.exceptional(new IOException("shutdown")) : TrackerResponse.ok();
                }
            };
        }

        @Override
        public TrackerRequestBuilder request(TorrentId torrentId) {
            return requestBuilder;
        }

        public void startup() {
            shutdown = false;
        }

        public void shutdown() {
            shutdown = true;
        }

        @Override
        public String toString() {
            return url;
        }
    }
}

