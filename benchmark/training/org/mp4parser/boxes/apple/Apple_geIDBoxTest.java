package org.mp4parser.boxes.apple;


import com.googlecode.mp4parser.boxes.BoxWriteReadBase;
import java.io.IOException;
import org.junit.Test;
import org.mp4parser.IsoFile;
import org.mp4parser.tools.Hex;


public class Apple_geIDBoxTest extends BoxWriteReadBase<Apple_geIDBox> {
    @Test
    public void testRealLifeBox() throws IOException {
        Apple_geIDBox geid = ((Apple_geIDBox) (new IsoFile(new org.mp4parser.tools.ByteBufferByteChannel(Hex.decodeHex("0000001C67654944000000146461746100000015000000000000000A"))).getBoxes().get(0)));
        System.err.println(geid.getValue());
    }
}

