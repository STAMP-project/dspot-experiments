package io.searchbox.cluster;


import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import com.google.common.collect.ImmutableMap;
import io.searchbox.client.JestResult;
import io.searchbox.common.AbstractIntegrationTest;
import io.searchbox.indices.reindex.Reindex;
import java.io.IOException;
import org.junit.Test;


@ClusterScope(scope = Scope.TEST, numDataNodes = 1)
public class TaskInformationIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void shouldReturnTaskInformation() throws IOException {
        String sourceIndex = "test_source_index";
        String destIndex = "test_dest_index";
        String documentType = "test_type";
        String documentId = "test_id";
        createIndex(sourceIndex);
        index(sourceIndex, documentType, documentId, "{}");
        flushAndRefresh(sourceIndex);
        ImmutableMap<String, Object> source = ImmutableMap.of("index", sourceIndex);
        ImmutableMap<String, Object> dest = ImmutableMap.of("index", destIndex);
        Reindex reindex = new Reindex.Builder(source, dest).refresh(true).waitForCompletion(false).build();
        JestResult result = client.execute(reindex);
        String task = ((String) (result.getValue("task")));
        assertNotNull(task);
        JestResult taskInformation = client.execute(new TasksInformation.Builder().task(task).build());
        Boolean completed = ((Boolean) (taskInformation.getValue("completed")));
        assertNotNull(completed);
    }
}

