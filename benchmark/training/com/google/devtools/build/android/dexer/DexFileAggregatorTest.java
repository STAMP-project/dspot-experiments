/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.dexer;


import com.android.dex.Dex;
import com.android.dx.command.dexer.DxContext;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.zip.ZipEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static MultidexStrategy.BEST_EFFORT;
import static MultidexStrategy.MINIMAL;
import static MultidexStrategy.OFF;


/**
 * Tests for {@link DexFileAggregator}.
 */
@RunWith(JUnit4.class)
public class DexFileAggregatorTest {
    /**
     * Standard .dex file limit on methods and fields.
     */
    private static final int DEX_LIMIT = 265 * 265;

    private static final int WASTE = 1;

    @Mock
    private DexFileArchive dest;

    @Captor
    private ArgumentCaptor<Dex> written;

    private Dex dex;

    @Test
    public void testClose_emptyWritesNothing() throws Exception {
        DexFileAggregator dexer = /* forceJumbo= */
        new DexFileAggregator(new DxContext(), dest, MoreExecutors.newDirectExecutorService(), MINIMAL, false, DexFileAggregatorTest.DEX_LIMIT, DexFileAggregatorTest.WASTE, DexFileMergerTest.DEX_PREFIX);
        dexer.close();
        Mockito.verify(dest, Mockito.times(0)).addFile(ArgumentMatchers.any(ZipEntry.class), ArgumentMatchers.any(Dex.class));
    }

    @Test
    public void testAddAndClose_singleInputWritesThatInput() throws Exception {
        DexFileAggregator dexer = /* forceJumbo= */
        new DexFileAggregator(new DxContext(), dest, MoreExecutors.newDirectExecutorService(), MINIMAL, false, 0, DexFileAggregatorTest.WASTE, DexFileMergerTest.DEX_PREFIX);
        dexer.add(dex);
        dexer.close();
        Mockito.verify(dest).addFile(ArgumentMatchers.any(ZipEntry.class), ArgumentMatchers.eq(dex));
    }

    @Test
    public void testAddAndClose_forceJumboRewrites() throws Exception {
        DexFileAggregator dexer = /* forceJumbo= */
        new DexFileAggregator(new DxContext(), dest, MoreExecutors.newDirectExecutorService(), MINIMAL, true, 0, DexFileAggregatorTest.WASTE, DexFileMergerTest.DEX_PREFIX);
        dexer.add(dex);
        try {
            dexer.close();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("--forceJumbo flag not supported");
            System.err.println("Skipping this test due to missing --forceJumbo support in Android SDK.");
            e.printStackTrace();
            return;
        }
        Mockito.verify(dest).addFile(ArgumentMatchers.any(ZipEntry.class), written.capture());
        assertThat(written.getValue()).isNotEqualTo(dex);
        assertThat(written.getValue().getLength()).isGreaterThan(dex.getLength());
    }

    @Test
    public void testMultidex_underLimitWritesOneShard() throws Exception {
        DexFileAggregator dexer = /* forceJumbo= */
        new DexFileAggregator(new DxContext(), dest, MoreExecutors.newDirectExecutorService(), BEST_EFFORT, false, DexFileAggregatorTest.DEX_LIMIT, DexFileAggregatorTest.WASTE, DexFileMergerTest.DEX_PREFIX);
        Dex dex2 = DexFiles.toDex(DexFileAggregatorTest.convertClass(ByteStreams.class));
        dexer.add(dex);
        dexer.add(dex2);
        Mockito.verify(dest, Mockito.times(0)).addFile(ArgumentMatchers.any(ZipEntry.class), ArgumentMatchers.any(Dex.class));
        dexer.close();
        Mockito.verify(dest).addFile(ArgumentMatchers.any(ZipEntry.class), written.capture());
        assertThat(Iterables.size(written.getValue().classDefs())).isEqualTo(2);
    }

    @Test
    public void testMultidex_overLimitWritesSecondShard() throws Exception {
        DexFileAggregator dexer = /* forceJumbo= */
        /* dex has more than 2 methods and fields */
        new DexFileAggregator(new DxContext(), dest, MoreExecutors.newDirectExecutorService(), BEST_EFFORT, false, 2, DexFileAggregatorTest.WASTE, DexFileMergerTest.DEX_PREFIX);
        Dex dex2 = DexFiles.toDex(DexFileAggregatorTest.convertClass(ByteStreams.class));
        dexer.add(dex);// classFile is already over limit but we take anything in empty shard

        dexer.add(dex2);// this should start a new shard

        // Make sure there was one file written and that file is dex
        Mockito.verify(dest).addFile(ArgumentMatchers.any(ZipEntry.class), written.capture());
        assertThat(written.getValue()).isSameAs(dex);
        dexer.close();
        Mockito.verify(dest).addFile(ArgumentMatchers.any(ZipEntry.class), ArgumentMatchers.eq(dex2));
    }

    @Test
    public void testMonodex_alwaysWritesSingleShard() throws Exception {
        DexFileAggregator dexer = /* forceJumbo= */
        /* dex has more than 2 methods and fields */
        new DexFileAggregator(new DxContext(), dest, MoreExecutors.newDirectExecutorService(), OFF, false, 2, DexFileAggregatorTest.WASTE, DexFileMergerTest.DEX_PREFIX);
        Dex dex2 = DexFiles.toDex(DexFileAggregatorTest.convertClass(ByteStreams.class));
        dexer.add(dex);
        dexer.add(dex2);
        Mockito.verify(dest, Mockito.times(0)).addFile(ArgumentMatchers.any(ZipEntry.class), ArgumentMatchers.any(Dex.class));
        dexer.close();
        Mockito.verify(dest).addFile(ArgumentMatchers.any(ZipEntry.class), written.capture());
        assertThat(Iterables.size(written.getValue().classDefs())).isEqualTo(2);
    }
}

