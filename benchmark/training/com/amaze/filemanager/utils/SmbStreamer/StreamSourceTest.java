package com.amaze.filemanager.utils.SmbStreamer;


import com.amaze.filemanager.BuildConfig;
import com.amaze.filemanager.shadows.jcifs.smb.ShadowSmbFile;
import java.io.IOException;
import java.util.Arrays;
import jcifs.smb.SmbFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.multidex.ShadowMultiDex;


/**
 * Created by Rustam Khadipash on 30/3/2018.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, shadows = { ShadowMultiDex.class, ShadowSmbFile.class })
public class StreamSourceTest {
    private SmbFile file;

    private StreamSource ss;

    private byte[] text;

    /* From now on ssEmpty will not be used since StreamSource()
    constructor does not initialize any internal variables
     */
    /**
     * Purpose: Open an existing file
     * Input: no
     * Expected:
     *          cs.read() = 1
     *          buff[0] = text[0]
     */
    @Test
    public void openExisting() throws IOException {
        ss.open();
        byte[] buff = new byte[1];
        Assert.assertEquals(buff.length, ss.read(buff));
        Assert.assertEquals(text[0], buff[0]);
    }

    /**
     * Purpose: Read content of length equal to the buffer size from the file
     * Input: read(buffer)
     * Expected:
     *          buffer = text
     *          n = len(buffer)
     */
    @Test
    public void read() throws IOException {
        ss.open();
        byte[] buff = new byte[10];
        int n = ss.read(buff);
        byte[] temp = Arrays.copyOfRange(text, 0, buff.length);
        Assert.assertArrayEquals(temp, buff);
        Assert.assertEquals(buff.length, n);
    }

    /**
     * Purpose: Read content from the file with the buffer size bigger than length
     * of the text in the file
     * Input: read(buffer)
     * Expected:
     *          buffer = text
     *          n = len
     */
    @Test
    public void readExceed() throws IOException {
        ss.open();
        byte[] buff = new byte[100];
        int n = ss.read(buff);
        // erase dummy values in the end of buffer
        byte[] buffer = Arrays.copyOfRange(buff, 0, n);
        Assert.assertArrayEquals(text, buffer);
        Assert.assertEquals(text.length, n);
    }

    /**
     * Purpose: Throw an exception when reading happen on a closed file
     * Input: read(buffer)
     * Expected:
     *          IOException is thrown
     */
    @Test(expected = IOException.class)
    public void readClosedException() throws IOException {
        ss.open();
        ss.close();
        byte[] buff = new byte[text.length];
        int n = ss.read(buff);
    }

    /**
     * Purpose: Read content in certain positions of the buffer from the file
     * Input: read(buffer, startPosition, endPosition)
     * Expected:
     *          buffer = text
     *          n = endPosition
     */
    @Test
    public void readStartEnd() throws IOException {
        ss.open();
        byte[] buff = new byte[100];
        int start = 5;
        int end = 10;
        int n = ss.read(buff, start, end);
        byte[] file = Arrays.copyOfRange(text, 0, (end - start));
        byte[] buffer = Arrays.copyOfRange(buff, start, end);
        Assert.assertArrayEquals(file, buffer);
        Assert.assertEquals(end, n);
    }

    /**
     * Purpose: Throw an exception when start and/or end positions for writing in
     * the buffer exceed size of the buffer
     * Input: read(buffer, startPosition, endPosition)
     * Expected:
     *          IndexOutOfBoundsException is thrown
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void readStartEndExceedException() throws IOException {
        ss.open();
        byte[] buff = new byte[100];
        int start = 95;
        int end = 110;
        ss.read(buff, start, end);
    }

    /**
     * Purpose: Throw an exception when reading happen on a closed file
     * Input: read(buffer, startPosition, endPosition)
     * Expected:
     *          IOException is thrown
     */
    @Test(expected = IOException.class)
    public void readStartEndClosedException() throws IOException {
        ss.open();
        ss.close();
        byte[] buff = new byte[100];
        int start = 5;
        int end = 10;
        int n = ss.read(buff, start, end);
    }

    /**
     * Purpose: Read content of the file from a certain position
     * Input: moveTo(readPosition), read(buff)
     * Expected:
     *          buff = text[readPosition]
     *          n = buff.length
     */
    @Test
    public void moveTo() throws IOException {
        int readPosition = (text.length) - 10;
        byte[] buff = new byte[1];
        ss.moveTo(readPosition);
        ss.open();
        int n = ss.read(buff);
        Assert.assertEquals(text[readPosition], buff[0]);
        Assert.assertEquals(buff.length, n);
    }

    /**
     * Purpose: Throw an exception when a reading position in the file is incorrect
     * Input: moveTo(wrongPosition)
     * Expected:
     *          IllegalArgumentException is thrown
     */
    @Test(expected = IllegalArgumentException.class)
    public void moveToException() throws IOException, IllegalArgumentException {
        ss.open();
        ss.moveTo((-1));
    }

    /**
     * Purpose: Close file after successful reading
     * Input: no
     * Expected:
     *          Stream is closed and reading from the file is unavailable
     */
    @Test
    public void close() throws IOException {
        ss.open();
        ss.close();
        int n = -1;
        try {
            byte[] buff = new byte[1];
            n = ss.read(buff);
        } catch (IOException ignored) {
        }
        Assert.assertEquals((-1), n);
    }

    /**
     * Purpose: Get MIME type
     * Input: no
     * Expected:
     *          return "txt"
     */
    @Test
    public void getMimeType() {
        Assert.assertEquals("txt", ss.getMimeType());
    }

    /**
     * Purpose: Get length of the text from a file
     * Input: no
     * Expected:
     *          return len
     */
    @Test
    public void length() {
        Assert.assertEquals(text.length, ss.length());
    }

    /**
     * Purpose: Get name of a file
     * Input: no
     * Expected:
     *          return "Test.txt"
     */
    @Test
    public void getName() {
        Assert.assertEquals(file.getName(), ss.getName());
    }

    /**
     * Purpose: Get available to read remain amount of text from a file
     * Input: no
     * Expected:
     *          return amount
     */
    @Test
    public void available() throws IOException {
        int amount = 12;
        ss.moveTo(((text.length) - amount));
        Assert.assertEquals(amount, ss.availableExact());
    }

    /**
     * Purpose: Move reading position to the beginning of a file
     * Input: no
     * Expected:
     *          return len
     */
    @Test
    public void reset() throws IOException {
        ss.moveTo(10);
        Assert.assertEquals(((text.length) - 10), ss.availableExact());
        ss.reset();
        Assert.assertEquals(text.length, ss.availableExact());
    }

    /**
     * Purpose: Get a file object
     * Input: no
     * Expected:
     *          return SmbFile
     */
    @Test
    public void getFile() {
        Assert.assertEquals(file, ss.getFile());
    }
}

