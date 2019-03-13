/**
 * * 2015 December 02
 * *
 * * The author disclaims copyright to this source code. In place of
 * * a legal notice, here is a blessing:
 * *    May you do good and not evil.
 * *    May you find forgiveness for yourself and forgive others.
 * *    May you share freely, never taking more than you give.
 */
package info.ata4.test.junity;


import info.ata4.io.DataWriters;
import info.ata4.junity.serialize.SerializedFile;
import info.ata4.junity.serialize.SerializedFileHeader;
import info.ata4.junity.serialize.SerializedFileWriter;
import info.ata4.util.io.DataBlock;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.apache.commons.io.FileUtils;
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
public class SerializedFileTest {
    private final Path readFile;

    private SerializedFile asset;

    public SerializedFileTest(Path file) throws IOException {
        this.readFile = file;
        System.out.println(file);
    }

    @Test
    public void offsetsAndSizesValid() throws IOException {
        SerializedFileHeader header = asset.header();
        long fileSize = Files.size(readFile);
        Assert.assertEquals("Header file size and actual file size must be equal", header.fileSize(), fileSize);
        Assert.assertTrue("Data offset must be within file", ((header.dataOffset()) < (header.fileSize())));
        // allowing a difference of 3 bytes because of padding
        Assert.assertEquals("Metadata sizes in data block and header must match", header.metadataSize(), asset.metadataBlock().length(), 3);
        // check blocks
        List<DataBlock> blocks = asset.dataBlocks();
        blocks.forEach(( block1) -> blocks.forEach(( block2) -> {
            if (block1 != block2) {
                assertFalse(((("Blocks are overlapping: " + block1) + " / ") + block2), block1.isIntersecting(block2));
            }
        }));
    }

    @Test
    public void objectInfoValid() throws IOException {
        asset.metadata().objectInfoTable().infoMap().forEach(( path, objectInfo) -> {
            assertTrue("Object info offset must be within file", ((objectInfo.offset()) < (asset.header().fileSize())));
            assertTrue("Object info size must be smaller than object data block", ((objectInfo.length()) < (asset.objectDataBlock().length())));
            assertTrue("Object info class and type ID mismatch", (((objectInfo.typeID()) == (objectInfo.classID())) || (((objectInfo.classID()) == 114) && ((objectInfo.typeID()) < 0))));
        });
    }

    @Test
    public void typeTreeValid() {
        if (asset.metadata().typeTree().embedded()) {
            asset.metadata().typeTree().typeMap().forEach(( path, root) -> {
                root.nodes().forEachData(( type) -> {
                    assertNotNull("Name string must be set", type.fieldName());
                    assertNotNull("Type string must be set", type.typeName());
                    if (type.isArray()) {
                        assertTrue("isArray should be set only if the type is 'Array' or 'TypelessData'", ((type.typeName().equals("Array")) || (type.typeName().equals("TypelessData"))));
                    }
                });
            });
        }
    }

    @Test
    public void directCopyMatches() throws IOException {
        Path writeFile = Files.createTempFile("writeTest", ".assets");
        try {
            try (SerializedFileWriter writer = new SerializedFileWriter(DataWriters.forFile(writeFile, StandardOpenOption.WRITE))) {
                writer.write(asset);
            }
            Assert.assertEquals("Output file must match input file", FileUtils.checksumCRC32(readFile.toFile()), FileUtils.checksumCRC32(writeFile.toFile()));
        } finally {
            Files.deleteIfExists(writeFile);
        }
    }
}

