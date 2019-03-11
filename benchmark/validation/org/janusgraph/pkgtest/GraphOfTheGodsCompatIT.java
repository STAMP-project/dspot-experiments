/**
 * Copyright 2017 JanusGraph Authors
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
package org.janusgraph.pkgtest;


import com.google.common.base.Joiner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.janusgraph.util.system.IOUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GraphOfTheGodsCompatIT extends AbstractJanusGraphAssemblyIT {
    private static final Logger log = LoggerFactory.getLogger(GraphOfTheGodsCompatIT.class);

    private static final String BDB_ES = "bdb-es";

    /**
     * Download and open databases created by previous versions of JanusGraph.
     * A basic check for cluster-restart forward-compatibility.
     */
    @Test
    public void testOpenAndQueryCompatibleDatabaseFiles() throws Exception {
        String compatManifest = Joiner.on(File.separator).join("target", "test-classes", "compat.csv");
        BufferedReader compatReader = null;
        int lineNumber = 0;
        int tested = 0;
        try {
            GraphOfTheGodsCompatIT.log.info("Opening compat manifest: {}", compatManifest);
            compatReader = new BufferedReader(new FileReader(compatManifest));
            String l;
            while (null != (l = compatReader.readLine())) {
                lineNumber++;
                String[] tokens = l.split(",");
                if (3 != (tokens.length)) {
                    GraphOfTheGodsCompatIT.log.warn("Ignoring line {} (splitting on commas yielded {} tokens instead of the expected {}): {}", lineNumber, tokens.length, 3, l);
                    continue;
                }
                String version = tokens[0].trim();
                String config = tokens[1].trim();
                String archiveUrl = tokens[2].trim();
                if (version.isEmpty()) {
                    GraphOfTheGodsCompatIT.log.warn("Ignoring line {} due to empty version field", lineNumber);
                    continue;
                }
                if (config.isEmpty()) {
                    GraphOfTheGodsCompatIT.log.warn("Ignoring line {} due to empty config field", lineNumber);
                    continue;
                }
                if (archiveUrl.isEmpty()) {
                    GraphOfTheGodsCompatIT.log.warn("Ignoring line {} due to empty archiveurl field", lineNumber);
                    continue;
                }
                if (!(config.equals(GraphOfTheGodsCompatIT.BDB_ES))) {
                    GraphOfTheGodsCompatIT.log.warn("Ignoring line {} with unknown config string {} (only {} is supported)", lineNumber, config, GraphOfTheGodsCompatIT.BDB_ES);
                }
                downloadAndTest(archiveUrl);
                tested++;
            } 
        } finally {
            IOUtils.closeQuietly(compatReader);
        }
        GraphOfTheGodsCompatIT.log.info("Read compat manifest with {} lines ({} tested, {} ignored)", lineNumber, tested, (lineNumber - tested));
    }
}

