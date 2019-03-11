package net.openhft.chronicle.map;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;
import net.openhft.chronicle.hash.serialization.SizeMarshaller;
import org.junit.Assert;
import org.junit.Test;


public class ValueAlignmentRelocationTest {
    @Test
    public void testValueAlignmentRelocation() throws IOException {
        File file = Files.createTempFile("test", "cm3").toFile();
        ChronicleMap<byte[], byte[]> map = ChronicleMapBuilder.of(byte[].class, byte[].class).averageKeySize(5).averageValueSize(5).keySizeMarshaller(SizeMarshaller.stopBit()).valueSizeMarshaller(SizeMarshaller.stopBit()).entryAndValueOffsetAlignment(8).actualSegments(1).actualChunkSize(2).entries(10).createPersistedTo(file);
        Random r = new Random(0);
        for (int firstKeySize = 1; firstKeySize < 10; firstKeySize++) {
            byte[] firstKey = new byte[firstKeySize];
            byte[] firstValue = new byte[8];
            r.nextBytes(firstKey);
            r.nextBytes(firstValue);
            for (int secondKeySize = 1; secondKeySize < 10; secondKeySize++) {
                byte[] secondKey = new byte[secondKeySize];
                r.nextBytes(secondKey);
                while (Arrays.equals(secondKey, firstKey)) {
                    r.nextBytes(secondKey);
                } 
                byte[] secondValue = new byte[1];
                r.nextBytes(secondValue);
                map.clear();
                map.put(firstKey, firstValue);
                map.put(secondKey, secondValue);
                byte[] thirdValue = new byte[16];
                r.nextBytes(thirdValue);
                map.put(firstKey, thirdValue);
                for (int i = 0; i < 10; i++) {
                    map.put(new byte[]{ ((byte) (i)) }, new byte[]{ ((byte) (i)) });
                    map.put(("Hello" + i).getBytes(), "world".getBytes());
                }
                System.out.println(((("firstKeySize=" + firstKeySize) + ",second key=") + secondKeySize));
                Assert.assertTrue(Arrays.equals(map.get(firstKey), thirdValue));
            }
        }
    }

    @Test
    public void testValueAlignmentRelocationNoRandomTest() throws IOException {
        File file = Files.createTempFile("test", "cm3").toFile();
        ChronicleMap<byte[], byte[]> map = ChronicleMapBuilder.of(byte[].class, byte[].class).averageKeySize(5).averageValueSize(5).keySizeMarshaller(SizeMarshaller.stopBit()).valueSizeMarshaller(SizeMarshaller.stopBit()).entryAndValueOffsetAlignment(8).actualSegments(1).actualChunkSize(2).entries(10).createPersistedTo(file);
        byte[] firstKey = "austi".getBytes(StandardCharsets.ISO_8859_1);
        byte[] firstValue = "12345678".getBytes(StandardCharsets.ISO_8859_1);
        byte[] secondKey = "h".getBytes(StandardCharsets.ISO_8859_1);
        byte[] secondValue = "a".getBytes(StandardCharsets.ISO_8859_1);
        map.put(firstKey, firstValue);
        map.put(secondKey, secondValue);
        byte[] thirdValue = "1234567890123456".getBytes(StandardCharsets.ISO_8859_1);
        map.put(firstKey, thirdValue);
        map.put("Hello".getBytes(StandardCharsets.ISO_8859_1), "world".getBytes(StandardCharsets.ISO_8859_1));
        String actual = toString(map.get(firstKey));
        String expected = toString(thirdValue);
        Assert.assertEquals(expected, actual);
    }
}

