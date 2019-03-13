/**
 * * 2015 December 06
 * *
 * * The author disclaims copyright to this source code. In place of
 * * a legal notice, here is a blessing:
 * *    May you do good and not evil.
 * *    May you find forgiveness for yourself and forgive others.
 * *    May you share freely, never taking more than you give.
 */
package info.ata4.test.junity;


import com.google.common.io.CountingOutputStream;
import info.ata4.junity.bundle.Bundle;
import info.ata4.junity.bundle.BundleHeader;
import info.ata4.junity.bundle.BundleReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
@RunWith(Parameterized.class)
public class BundleTest {
    private final Path file;

    private Bundle bundle;

    private final BundleReader reader;

    public BundleTest(Path file) throws IOException {
        this.file = file;
        this.reader = new BundleReader(file);
        System.out.println(file);
    }

    @Test
    public void headerValid() throws IOException {
        BundleHeader header = bundle.header();
        long fileSize = Files.size(file);
        Assert.assertTrue("Number of levels to download must be equal to number of levels or 1", (((header.numberOfLevelsToDownload()) == (header.numberOfLevels())) || ((header.numberOfLevelsToDownload()) == 1)));
        Assert.assertTrue("Signatures should be valid", header.hasValidSignature());
        Assert.assertTrue("Minimum streamed bytes must be smaller than or equal to file size", ((header.minimumStreamedBytes()) <= fileSize));
        Assert.assertTrue("Header size must be smaller than file size", ((header.headerSize()) < fileSize));
        if ((header.streamVersion()) >= 2) {
            Assert.assertEquals("Header file size and actual file size must be equal", header.completeFileSize(), fileSize);
        }
        Assert.assertEquals("Number of levels must match number of level end offsets", header.numberOfLevelsToDownload(), header.levelByteEnd().size());
        header.levelByteEnd().forEach(( lbe) -> {
            assertTrue("Compressed offset must be smaller or equal to uncompressed offset", ((lbe.getLeft()) <= (lbe.getRight())));
        });
    }

    @Test
    public void entriesValid() {
        Assert.assertEquals("Bundle entry lists must match in size", bundle.entries().size(), bundle.entryInfos().size());
        bundle.entries().forEach(uncheck(( entry) -> {
            CountingOutputStream cos = new CountingOutputStream(new NullOutputStream());
            IOUtils.copy(entry.inputStream(), cos);
            assertEquals("Entry size must match size of InputStream", entry.size(), cos.getCount());
        }));
    }
}

