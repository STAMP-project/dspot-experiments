/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.examples.logging.snippets;


import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Sink;
import com.google.cloud.logging.testing.RemoteLoggingHelper;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class ITSinkSnippets {
    private static final String SINK_NAME = RemoteLoggingHelper.formatForTest("it_sink_snippets");

    private static final String SINK_FILTER = "severity>=ERROR";

    private static final String UPDATED_SINK_FILTER = "severity<=ERROR";

    private static final String DESTINATION = "dataset";

    private static Logging logging;

    private static SinkSnippets sinkSnippets;

    @Test
    public void testSink() throws InterruptedException, ExecutionException {
        Sink sink = ITSinkSnippets.sinkSnippets.reload();
        Assert.assertNotNull(sink);
        Sink updatedSink = ITSinkSnippets.sinkSnippets.update();
        Assert.assertEquals(ITSinkSnippets.UPDATED_SINK_FILTER, updatedSink.getFilter());
        updatedSink = ITSinkSnippets.sinkSnippets.reloadAsync();
        Assert.assertNotNull(updatedSink);
        Assert.assertEquals(ITSinkSnippets.UPDATED_SINK_FILTER, updatedSink.getFilter());
        sink.update();
        updatedSink = ITSinkSnippets.sinkSnippets.updateAsync();
        Assert.assertEquals(ITSinkSnippets.UPDATED_SINK_FILTER, updatedSink.getFilter());
        Assert.assertTrue(ITSinkSnippets.sinkSnippets.delete());
        Assert.assertFalse(ITSinkSnippets.sinkSnippets.deleteAsync());
    }
}

