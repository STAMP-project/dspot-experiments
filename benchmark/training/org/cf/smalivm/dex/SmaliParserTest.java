package org.cf.smalivm.dex;


import java.util.List;
import org.cf.smalivm.VMTester;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.junit.Assert;
import org.junit.Test;


public class SmaliParserTest {
    @Test
    public void canParseSmaliDirectory() throws Exception {
        List<BuilderClassDef> classDefs = SmaliParser.parse(VMTester.TEST_CLASS_PATH);
        Assert.assertTrue(((classDefs.size()) > 0));
    }
}

