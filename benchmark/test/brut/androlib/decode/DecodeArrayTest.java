package brut.androlib.decode;


import brut.androlib.ApkDecoder;
import brut.androlib.BaseTest;
import brut.androlib.res.data.ResTable;
import brut.androlib.res.data.value.ResArrayValue;
import brut.androlib.res.data.value.ResValue;
import brut.common.BrutException;
import java.io.File;
import junit.framework.Assert;
import org.junit.Test;


public class DecodeArrayTest extends BaseTest {
    @Test
    public void decodeStringArray() throws BrutException {
        String apk = "issue1994.apk";
        ApkDecoder apkDecoder = new ApkDecoder(new File((((BaseTest.sTmpDir) + (File.separator)) + apk)));
        ResTable resTable = apkDecoder.getResTable();
        ResValue value = resTable.getResSpec(2130837505).getDefaultResource().getValue();
        Assert.assertTrue(("Not a ResArrayValue. Found: " + (value.getClass())), (value instanceof ResArrayValue));
    }

    @Test
    public void decodeArray() throws BrutException {
        String apk = "issue1994.apk";
        ApkDecoder apkDecoder = new ApkDecoder(new File((((BaseTest.sTmpDir) + (File.separator)) + apk)));
        ResTable resTable = apkDecoder.getResTable();
        ResValue value = resTable.getResSpec(2130837504).getDefaultResource().getValue();
        Assert.assertTrue(("Not a ResArrayValue. Found: " + (value.getClass())), (value instanceof ResArrayValue));
    }
}

