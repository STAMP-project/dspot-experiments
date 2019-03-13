/**
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.cache;


import IndexType.TRACK1;
import IndexType.TRACK2;
import java.io.File;
import java.io.IOException;
import net.runelite.cache.fs.Archive;
import net.runelite.cache.fs.Index;
import net.runelite.cache.fs.Storage;
import net.runelite.cache.fs.Store;
import net.runelite.cache.util.Djb2Manager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TrackDumperTest {
    private static final Logger logger = LoggerFactory.getLogger(TrackDumperTest.class);

    @Rule
    public TemporaryFolder folder = StoreLocation.getTemporaryFolder();

    private final Djb2Manager djb2 = new Djb2Manager();

    @Test
    public void test() throws IOException {
        File dumpDir1 = folder.newFolder();
        File dumpDir2 = folder.newFolder();
        int idx1 = 0;
        int idx2 = 0;
        djb2.load();
        try (Store store = new Store(StoreLocation.LOCATION)) {
            store.load();
            Storage storage = store.getStorage();
            Index index = store.getIndex(TRACK1);
            Index index2 = store.getIndex(TRACK2);
            for (Archive archive : index.getArchives()) {
                dumpTrackArchive(dumpDir1, storage, archive);
                ++idx1;
            }
            for (Archive archive : index2.getArchives()) {
                dumpTrackArchive(dumpDir2, storage, archive);
                ++idx2;
            }
        }
        TrackDumperTest.logger.info("Dumped {} sound tracks ({} idx1, {} idx2) to {} and {}", (idx1 + idx2), idx1, idx2, dumpDir1, dumpDir2);
    }
}

