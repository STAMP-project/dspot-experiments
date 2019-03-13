package org.mp4parser.boxes.apple;


import com.googlecode.mp4parser.boxes.BoxWriteReadBase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.mp4parser.IsoFile;


public class TimeCodeBoxTest extends BoxWriteReadBase<TimeCodeBox> {
    String tcmd = "00000026746D6364000000000000" + ("0001000000000000000000005DC00000" + "03E918B200000000");

    @Test
    public void checkRealLifeBox() throws IOException, DecoderException {
        File f = File.createTempFile("TimeCodeBoxTest", "checkRealLifeBox");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(Hex.decodeHex(tcmd.toCharArray()));
        fos.close();
        IsoFile isoFile = new IsoFile(new FileInputStream(f).getChannel());
        TimeCodeBox tcmd = ((TimeCodeBox) (isoFile.getBoxes().get(0)));
        System.err.println(tcmd);
        isoFile.close();
        f.delete();
    }
}

