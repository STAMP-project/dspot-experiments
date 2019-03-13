package uk.co.real_logic.sbe.generation.rust;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.co.real_logic.sbe.generation.CodeGenerator;


public class RustTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test(expected = NullPointerException.class)
    public void nullIRParamShouldTossNPE() {
        new Rust().newInstance(null, temporaryFolder.toString());
    }

    @Test(expected = NullPointerException.class)
    public void nullOutputDirParamShouldTossNPE() {
        new Rust().newInstance(RustTest.minimalDummyIr(), null);
    }

    @Test
    public void happyPathRustGeneratorThatThrowsNoExceptions() throws IOException {
        final File newFolder = temporaryFolder.newFolder();
        final CodeGenerator codeGenerator = new Rust().newInstance(RustTest.minimalDummyIr(), newFolder.toString());
        Assert.assertNotNull(codeGenerator);
        codeGenerator.generate();
        newFolder.setWritable(true, false);
    }
}

