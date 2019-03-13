package io.searchbox.snapshot;


import ElasticsearchVersion.UNKNOWN;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author happyprg(hongsgo@gmail.com)
 */
public class CreateSnapshotTest {
    private String repository = "leeseohoo";

    private String snapshot = "leeseola";

    @Test
    public void testSnapshot() {
        CreateSnapshot createSnapshot = new CreateSnapshot.Builder(repository, snapshot).waitForCompletion(true).build();
        Assert.assertEquals("PUT", createSnapshot.getRestMethodName());
        Assert.assertEquals("/_snapshot/leeseohoo/leeseola?wait_for_completion=true", createSnapshot.getURI(UNKNOWN));
    }

    @Test
    public void testSnapshotWithSettings() throws JSONException {
        Map<String, String> repositorySettings = new HashMap<>();
        repositorySettings.put("indices", "index_1,index_2");
        repositorySettings.put("ignore_unavailable", "true");
        repositorySettings.put("include_global_state", "false");
        CreateSnapshot createSnapshot = new CreateSnapshot.Builder(repository, snapshot).settings(repositorySettings).waitForCompletion(true).build();
        Assert.assertEquals("PUT", createSnapshot.getRestMethodName());
        Assert.assertEquals("/_snapshot/leeseohoo/leeseola?wait_for_completion=true", createSnapshot.getURI(UNKNOWN));
        String settings = createSnapshot.getData(new Gson());
        String expectedJSON = "{\n" + ((("\t\"ignore_unavailable\": \"true\",\n" + "\t\"include_global_state\": \"false\",\n") + "\t\"indices\": \"index_1,index_2\"\n") + "}");
        JSONAssert.assertEquals(expectedJSON, settings, false);
    }
}

