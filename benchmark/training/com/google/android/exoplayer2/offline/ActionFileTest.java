/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.offline;


import android.net.Uri;
import com.google.android.exoplayer2.offline.DownloadAction.Deserializer;
import com.google.android.exoplayer2.util.Util;
import data.length;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static ActionFile.VERSION;


/**
 * Unit tests for {@link ActionFile}.
 */
@RunWith(RobolectricTestRunner.class)
public class ActionFileTest {
    private File tempFile;

    @Test
    public void testLoadNoDataThrowsIOException() throws Exception {
        try {
            loadActions(new Object[]{  });
            Assert.fail();
        } catch (IOException e) {
            // Expected exception.
        }
    }

    @Test
    public void testLoadIncompleteHeaderThrowsIOException() throws Exception {
        try {
            loadActions(new Object[]{ VERSION });
            Assert.fail();
        } catch (IOException e) {
            // Expected exception.
        }
    }

    @Test
    public void testLoadCompleteHeaderZeroAction() throws Exception {
        DownloadAction[] actions = loadActions(new Object[]{ VERSION, 0 });
        assertThat(actions).isNotNull();
        assertThat(actions).hasLength(0);
    }

    @Test
    public void testLoadAction() throws Exception {
        byte[] data = Util.getUtf8Bytes("321");
        DownloadAction[] actions = loadActions(new Object[]{ VERSION, 1// Action count
        , "type2"// Action 1
        , ActionFileTest.FakeDownloadAction.VERSION, data }, new ActionFileTest.FakeDeserializer("type2"));
        assertThat(actions).isNotNull();
        assertThat(actions).hasLength(1);
        ActionFileTest.assertAction(actions[0], "type2", ActionFileTest.FakeDownloadAction.VERSION, data);
    }

    @Test
    public void testLoadActions() throws Exception {
        byte[] data1 = Util.getUtf8Bytes("123");
        byte[] data2 = Util.getUtf8Bytes("321");
        DownloadAction[] actions = loadActions(new Object[]{ VERSION, 2// Action count
        , "type1"// Action 1
        , ActionFileTest.FakeDownloadAction.VERSION, data1, "type2"// Action 2
        , ActionFileTest.FakeDownloadAction.VERSION, data2 }, new ActionFileTest.FakeDeserializer("type1"), new ActionFileTest.FakeDeserializer("type2"));
        assertThat(actions).isNotNull();
        assertThat(actions).hasLength(2);
        ActionFileTest.assertAction(actions[0], "type1", ActionFileTest.FakeDownloadAction.VERSION, data1);
        ActionFileTest.assertAction(actions[1], "type2", ActionFileTest.FakeDownloadAction.VERSION, data2);
    }

    @Test
    public void testLoadNotSupportedVersion() throws Exception {
        try {
            loadActions(new Object[]{ (VERSION) + 1, 1// Action count
            , "type2"// Action 1
            , ActionFileTest.FakeDownloadAction.VERSION, Util.getUtf8Bytes("321") }, new ActionFileTest.FakeDeserializer("type2"));
            Assert.fail();
        } catch (IOException e) {
            // Expected exception.
        }
    }

    @Test
    public void testLoadNotSupportedActionVersion() throws Exception {
        try {
            loadActions(new Object[]{ VERSION, 1// Action count
            , "type2"// Action 1
            , (ActionFileTest.FakeDownloadAction.VERSION) + 1, Util.getUtf8Bytes("321") }, new ActionFileTest.FakeDeserializer("type2"));
            Assert.fail();
        } catch (IOException e) {
            // Expected exception.
        }
    }

    @Test
    public void testLoadNotSupportedType() throws Exception {
        try {
            loadActions(new Object[]{ VERSION, 1// Action count
            , "type2"// Action 1
            , ActionFileTest.FakeDownloadAction.VERSION, Util.getUtf8Bytes("321") }, new ActionFileTest.FakeDeserializer("type1"));
            Assert.fail();
        } catch (DownloadException e) {
            // Expected exception.
        }
    }

    @Test
    public void testStoreAndLoadNoActions() throws Exception {
        doTestSerializationRoundTrip(new DownloadAction[0]);
    }

    @Test
    public void testStoreAndLoadActions() throws Exception {
        doTestSerializationRoundTrip(new DownloadAction[]{ new ActionFileTest.FakeDownloadAction("type1", Util.getUtf8Bytes("123")), new ActionFileTest.FakeDownloadAction("type2", Util.getUtf8Bytes("321")) }, new ActionFileTest.FakeDeserializer("type1"), new ActionFileTest.FakeDeserializer("type2"));
    }

    private static class FakeDeserializer extends Deserializer {
        FakeDeserializer(String type) {
            super(type, ActionFileTest.FakeDownloadAction.VERSION);
        }

        @Override
        public DownloadAction readFromStream(int version, DataInputStream input) throws IOException {
            int dataLength = input.readInt();
            byte[] data = new byte[dataLength];
            input.readFully(data);
            return new ActionFileTest.FakeDownloadAction(type, data);
        }
    }

    private static class FakeDownloadAction extends DownloadAction {
        public static final int VERSION = 0;

        private FakeDownloadAction(String type, byte[] data) {
            /* isRemoveAction= */
            super(type, ActionFileTest.FakeDownloadAction.VERSION, Uri.parse("http://test.com"), false, data);
        }

        @Override
        protected void writeToStream(DataOutputStream output) throws IOException {
            output.writeInt(length);
            output.write(data);
        }

        @Override
        public Downloader createDownloader(DownloaderConstructorHelper downloaderConstructorHelper) {
            return null;
        }
    }
}

