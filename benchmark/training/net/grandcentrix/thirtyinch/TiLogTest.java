/**
 * Copyright (C) 2017 grandcentrix GmbH
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.grandcentrix.thirtyinch;


import Log.DEBUG;
import Log.ERROR;
import Log.INFO;
import Log.VERBOSE;
import Log.WARN;
import TiLog.Logger;
import TiLog.TI_LOG;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class TiLogTest {
    @Test
    public void dontCrashForNullLogger() throws Exception {
        TiLog.setLogger(null);
        TiLog.v("tag", "msg");
        TiLog.i("tag", "msg");
        TiLog.d("tag", "msg");
        TiLog.e("tag", "msg");
        TiLog.w("tag", "msg");
        TiLog.log(VERBOSE, "tag", "msg");
    }

    @Test
    public void logDToLogger() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        TiLog.setLogger(logger);
        final ArgumentCaptor<Integer> levelCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        TiLog.d("tag", "msg");
        Mockito.verify(logger).log(levelCaptor.capture(), tagCaptor.capture(), msgCaptor.capture());
        assertThat(levelCaptor.getValue()).isEqualTo(DEBUG);
        assertThat(tagCaptor.getValue()).isEqualTo("tag");
        assertThat(msgCaptor.getValue()).isEqualTo("msg");
    }

    @Test
    public void logEToLogger() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        TiLog.setLogger(logger);
        final ArgumentCaptor<Integer> levelCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        TiLog.e("tag", "msg");
        Mockito.verify(logger).log(levelCaptor.capture(), tagCaptor.capture(), msgCaptor.capture());
        assertThat(levelCaptor.getValue()).isEqualTo(ERROR);
        assertThat(tagCaptor.getValue()).isEqualTo("tag");
        assertThat(msgCaptor.getValue()).isEqualTo("msg");
    }

    @Test
    public void logIToLogger() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        TiLog.setLogger(logger);
        final ArgumentCaptor<Integer> levelCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        TiLog.i("tag", "msg");
        Mockito.verify(logger).log(levelCaptor.capture(), tagCaptor.capture(), msgCaptor.capture());
        assertThat(levelCaptor.getValue()).isEqualTo(INFO);
        assertThat(tagCaptor.getValue()).isEqualTo("tag");
        assertThat(msgCaptor.getValue()).isEqualTo("msg");
    }

    @Test
    public void logVToLogger() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        TiLog.setLogger(logger);
        final ArgumentCaptor<Integer> levelCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        TiLog.v("tag", "msg");
        Mockito.verify(logger).log(levelCaptor.capture(), tagCaptor.capture(), msgCaptor.capture());
        assertThat(levelCaptor.getValue()).isEqualTo(VERBOSE);
        assertThat(tagCaptor.getValue()).isEqualTo("tag");
        assertThat(msgCaptor.getValue()).isEqualTo("msg");
    }

    @Test
    public void logWToLogger() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        TiLog.setLogger(logger);
        final ArgumentCaptor<Integer> levelCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        TiLog.w("tag", "msg");
        Mockito.verify(logger).log(levelCaptor.capture(), tagCaptor.capture(), msgCaptor.capture());
        assertThat(levelCaptor.getValue()).isEqualTo(WARN);
        assertThat(tagCaptor.getValue()).isEqualTo("tag");
        assertThat(msgCaptor.getValue()).isEqualTo("msg");
    }

    @Test
    public void loglogDToLogger() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        TiLog.setLogger(logger);
        final ArgumentCaptor<Integer> levelCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        TiLog.log(DEBUG, "tag", "msg");
        Mockito.verify(logger).log(levelCaptor.capture(), tagCaptor.capture(), msgCaptor.capture());
        assertThat(levelCaptor.getValue()).isEqualTo(DEBUG);
        assertThat(tagCaptor.getValue()).isEqualTo("tag");
        assertThat(msgCaptor.getValue()).isEqualTo("msg");
    }

    @Test
    public void loglogVToLogger() throws Exception {
        final TiLog.Logger logger = Mockito.mock(Logger.class);
        TiLog.setLogger(logger);
        final ArgumentCaptor<Integer> levelCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<String> tagCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        TiLog.log(VERBOSE, "tag", "msg");
        Mockito.verify(logger).log(levelCaptor.capture(), tagCaptor.capture(), msgCaptor.capture());
        assertThat(levelCaptor.getValue()).isEqualTo(VERBOSE);
        assertThat(tagCaptor.getValue()).isEqualTo("tag");
        assertThat(msgCaptor.getValue()).isEqualTo("msg");
    }

    @Test
    public void preventSettingRecursiveLogger() throws Exception {
        try {
            TiLog.setLogger(TI_LOG);
            fail("did not throw");
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Recursion");
        }
    }
}

