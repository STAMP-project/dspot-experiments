package com.thinkaurelius.titan.pkgtest;


import com.google.common.base.Joiner;
import com.thinkaurelius.titan.util.system.IOUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GraphOfTheGodsCompatIT extends AbstractTitanAssemblyIT {
    private static final Logger log = LoggerFactory.getLogger(GraphOfTheGodsCompatIT.class);

    private static final String BDB_ES = "bdb-es";

    /**
     * Download and open databases created by previous versions of Titan.
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
                String archiveurl = tokens[2].trim();
                if (version.isEmpty()) {
                    GraphOfTheGodsCompatIT.log.warn("Ignoring line {} due to empty version field", lineNumber);
                    continue;
                }
                if (config.isEmpty()) {
                    GraphOfTheGodsCompatIT.log.warn("Ignoring line {} due to empty config field", lineNumber);
                    continue;
                }
                if (archiveurl.isEmpty()) {
                    GraphOfTheGodsCompatIT.log.warn("Ignoring line {} due to empty archiveurl field", lineNumber);
                    continue;
                }
                if (!(config.equals(GraphOfTheGodsCompatIT.BDB_ES))) {
                    GraphOfTheGodsCompatIT.log.warn("Ignoring line {} with unknown config string {} (only {} is supported)", lineNumber, config, GraphOfTheGodsCompatIT.BDB_ES);
                }
                downloadAndTest(archiveurl);
                tested++;
            } 
        } finally {
            IOUtils.closeQuietly(compatReader);
        }
        GraphOfTheGodsCompatIT.log.info("Read compat manifest with {} lines ({} tested, {} ignored)", lineNumber, tested, (lineNumber - tested));
    }
}

