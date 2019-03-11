package com.sleekbyte.tailor.integration;


import com.sleekbyte.tailor.common.ExitCode;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests for {@link XcodeIntegratorTest}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class XcodeIntegratorTest {
    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test(expected = IOException.class)
    public void testGetAbsolutePathEmptyPath() throws IOException {
        XcodeIntegrator.getAbsolutePath("");
    }

    @Test(expected = IOException.class)
    public void testGetAbsolutePathNotXcodeprojFile() throws IOException {
        XcodeIntegrator.getAbsolutePath(createFolder("testDir").getPath());
    }

    @Test
    public void testGetAbsolutePathValidXcodeprojFile() throws IOException {
        String ret = XcodeIntegrator.getAbsolutePath(createFolder("test.xcodeproj").getPath());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testSetupXcodeInvalidXcodeprojFile() throws IOException {
        int ret = XcodeIntegrator.setupXcode(createFolder("testDir").getPath());
        Assert.assertEquals(ExitCode.failure(), ret);
    }

    @Test
    public void testCreateTempRubyScriptValidXcodeprojFile() throws IOException {
        File xcodeproj = createFolder("test.xcodeproj");
        File rubyScript = XcodeIntegrator.createTempRubyScript(xcodeproj.getAbsolutePath());
        Assert.assertNotNull(rubyScript);
        Assert.assertTrue(rubyScript.getName().contains("xcode_integrate"));
        if (!(rubyScript.delete())) {
            throw new FileNotFoundException(("Failed to delete file " + rubyScript));
        }
    }
}

