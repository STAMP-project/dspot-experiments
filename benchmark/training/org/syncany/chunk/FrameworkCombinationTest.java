/**
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2016 Philipp C. Heckel <philipp.heckel@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.chunk;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.junit.Test;
import org.syncany.config.Logging;
import org.syncany.crypto.SaltedSecretKey;
import org.syncany.database.ChunkEntry.ChunkChecksum;
import org.syncany.database.MultiChunkEntry.MultiChunkId;
import org.syncany.tests.util.TestFileUtil;


public class FrameworkCombinationTest {
    private static final Logger logger = Logger.getLogger(FrameworkCombinationTest.class.getSimpleName());

    private File tempDir;

    private List<FrameworkCombinationTest.FrameworkCombination> combinations;

    private SaltedSecretKey masterKey;

    static {
        Logging.init();
    }

    @Test
    public void testBlackBoxCombinationsWith50KBInputFile() throws Exception {
        // Setup
        setup();
        // Test
        List<File> inputFiles = TestFileUtil.createRandomFilesInDirectory(tempDir, (10 * 1024), 5);
        for (FrameworkCombinationTest.FrameworkCombination combination : combinations) {
            FrameworkCombinationTest.logger.info("");
            FrameworkCombinationTest.logger.info((("Testing framework combination " + (combination.name)) + " ..."));
            FrameworkCombinationTest.logger.info("---------------------------------------------------------------");
            testBlackBoxCombination(inputFiles, combination);
        }
        // Tear down (if success)
        teardown();
    }

    private static class FrameworkCombination {
        private String name;

        private Chunker chunker;

        private MultiChunker multiChunker;

        private Transformer transformer;

        public FrameworkCombination(String name, Chunker chunker, MultiChunker multiChunker, Transformer transformerChain) {
            this.name = name;
            this.chunker = chunker;
            this.multiChunker = multiChunker;
            transformer = transformerChain;
        }
    }

    private static class ChunkIndex {
        private Map<File, List<ChunkChecksum>> inputFileToChunkIDs = new HashMap<File, List<ChunkChecksum>>();

        private Map<ChunkChecksum, MultiChunkId> chunkIDToMultiChunkID = new HashMap<ChunkChecksum, MultiChunkId>();

        private List<File> outputMultiChunkFiles = new ArrayList<File>();
    }
}

