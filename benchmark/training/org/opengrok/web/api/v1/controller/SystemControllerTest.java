package org.opengrok.web.api.v1.controller;


import Response.Status.NO_CONTENT;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.configuration.Configuration;
import org.opengrok.indexer.configuration.RuntimeEnvironment;
import org.opengrok.indexer.util.IOUtils;
import org.opengrok.indexer.web.EftarFileReader;


public class SystemControllerTest extends JerseyTest {
    private RuntimeEnvironment env = RuntimeEnvironment.getInstance();

    /**
     * This method tests include files reload by testing it for one specific file out of the whole set.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testIncludeReload() throws IOException {
        // Set new include root directory.
        Path includeRootPath = Files.createTempDirectory("systemControllerTest");
        File includeRoot = includeRootPath.toFile();
        env.setIncludeRoot(includeRoot.getAbsolutePath());
        Assert.assertEquals(includeRoot.getCanonicalPath(), env.getIncludeRootPath());
        // Create footer include file.
        File footerFile = new File(includeRoot, Configuration.FOOTER_INCLUDE_FILE);
        String content = "foo";
        try (PrintWriter out = new PrintWriter(footerFile)) {
            out.println(content);
        }
        // Sanity check that getFooterIncludeFileContent() works since the test depends on it.
        String before = env.getIncludeFiles().getFooterIncludeFileContent(false);
        Assert.assertEquals(content, before.trim());
        // Modify the contents of the file.
        content = content + "bar";
        try (PrintWriter out = new PrintWriter(footerFile)) {
            out.println(content);
        }
        // Reload the contents via API call.
        Response r = target("system").path("includes").path("reload").request().put(Entity.text(""));
        Assert.assertEquals(NO_CONTENT.getStatusCode(), r.getStatus());
        // Check that the content was reloaded.
        String after = env.getIncludeFiles().getFooterIncludeFileContent(false);
        Assert.assertNotEquals(before, after);
        Assert.assertEquals(content, after.trim());
        // Cleanup
        IOUtils.removeRecursive(includeRootPath);
    }

    @Test
    public void testDtagsEftarReload() throws IOException {
        // The output file will be located in a directory under data root so create it first.
        Path dataRoot = Files.createTempDirectory("api_dtags_test");
        env.setDataRoot(dataRoot.toString());
        Paths.get(dataRoot.toString(), "index").toFile().mkdir();
        // Create path descriptions string.
        StringBuilder sb = new StringBuilder();
        String[][] descriptions = new String[][]{ new String[]{ "/path1", "foo foo" }, new String[]{ "/path2", "bar bar" } };
        for (int i = 0; i < (descriptions.length); i++) {
            sb.append(descriptions[i][0]);
            sb.append("\t");
            sb.append(descriptions[i][1]);
            sb.append("\n");
        }
        String input = sb.toString();
        // Reload the contents via API call.
        Response r = target("system").path("pathdesc").request().post(Entity.text(input));
        Assert.assertEquals(NO_CONTENT.getStatusCode(), r.getStatus());
        // Check
        Path eftarPath = env.getDtagsEftarPath();
        Assert.assertTrue(eftarPath.toFile().exists());
        try (EftarFileReader er = new EftarFileReader(eftarPath.toString())) {
            for (int i = 0; i < (descriptions.length); i++) {
                Assert.assertEquals(descriptions[i][1], er.get(descriptions[i][0]));
            }
        }
        // Cleanup
        IOUtils.removeRecursive(dataRoot);
    }
}

