package org.kairosdb.core.http.rest;


import Level.OFF;
import ch.qos.logback.classic.Level;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.http.rest.json.ErrorResponse;
import org.kairosdb.core.http.rest.json.QueryParser;
import org.kairosdb.rollup.RollUpException;
import org.kairosdb.rollup.RollUpTasksStore;
import org.kairosdb.rollup.RollupTask;
import org.kairosdb.rollup.RollupTaskStatusStore;
import org.kairosdb.util.LoggingUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RollUpResourceTest {
    private static final String BEAN_VALIDATION_ERROR = "bean validation error";

    private static final String CONTEXT = "context";

    private static final String INTERNAL_EXCEPTION_MESSAGE = "Internal Exception";

    private RollUpResource resource;

    private RollUpTasksStore mockStore;

    private RollupTaskStatusStore mockStatusStore;

    private QueryParser mockQueryParser;

    private QueryParser queryParser;

    @Test(expected = NullPointerException.class)
    public void testCreate_nullJsonInvalid() {
        resource.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_emptyJsonInvalid() {
        resource.create("");
    }

    @Test
    public void testCreate_parseError() throws IOException, QueryException {
        Mockito.when(mockQueryParser.parseRollupTask(ArgumentMatchers.anyString())).thenThrow(createBeanException());
        Response response = resource.create("thejson");
        ErrorResponse errorResponse = ((ErrorResponse) (response.getEntity()));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(BAD_REQUEST.getStatusCode()));
        MatcherAssert.assertThat(errorResponse.getErrors().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(errorResponse.getErrors().get(0), CoreMatchers.equalTo(getBeanValidationMessage()));
    }

    @Test
    public void testCreate_internalError() throws IOException, QueryException {
        Level previousLogLevel = LoggingUtils.setLogLevel(OFF);
        try {
            Mockito.when(mockQueryParser.parseRollupTask(ArgumentMatchers.anyString())).thenThrow(createQueryException());
            Response response = resource.create("thejson");
            ErrorResponse errorResponse = ((ErrorResponse) (response.getEntity()));
            MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
            MatcherAssert.assertThat(errorResponse.getErrors().size(), CoreMatchers.equalTo(1));
            MatcherAssert.assertThat(errorResponse.getErrors().get(0), CoreMatchers.equalTo(RollUpResourceTest.INTERNAL_EXCEPTION_MESSAGE));
        } finally {
            LoggingUtils.setLogLevel(previousLogLevel);
        }
    }

    @Test
    public void testCreate() throws IOException, QueryException {
        resource = new RollUpResource(queryParser, mockStore, mockStatusStore);
        String json = Resources.toString(Resources.getResource("rolluptask1.json"), Charsets.UTF_8);
        RollupTask task = queryParser.parseRollupTask(json);
        Response response = resource.create(json);
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(OK.getStatusCode()));
        assertRollupResponse(((String) (response.getEntity())), task);
    }

    @Test
    public void testList() throws IOException, QueryException, RollUpException {
        resource = new RollUpResource(queryParser, mockStore, mockStatusStore);
        List<RollupTask> tasks = mockTasks(Resources.toString(Resources.getResource("rolluptasks.json"), Charsets.UTF_8));
        Response response = resource.list();
        List<RollupTask> responseTasks = queryParser.parseRollupTasks(((String) (response.getEntity())));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(OK.getStatusCode()));
        MatcherAssert.assertThat(responseTasks, Matchers.containsInAnyOrder(tasks.toArray()));
    }

    @Test
    public void testList_internalError() throws IOException, QueryException, RollUpException {
        Level previousLogLevel = LoggingUtils.setLogLevel(OFF);
        try {
            Mockito.when(mockStore.read()).thenThrow(createRollupException());
            Response response = resource.list();
            ErrorResponse errorResponse = ((ErrorResponse) (response.getEntity()));
            MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
            MatcherAssert.assertThat(errorResponse.getErrors().size(), CoreMatchers.equalTo(1));
            MatcherAssert.assertThat(errorResponse.getErrors().get(0), CoreMatchers.equalTo(RollUpResourceTest.INTERNAL_EXCEPTION_MESSAGE));
        } finally {
            LoggingUtils.setLogLevel(previousLogLevel);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testGet_nullIdInvalid() {
        resource.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGet_emptyIdInvalid() {
        resource.get("");
    }

    @Test
    public void testGet() throws IOException, QueryException, RollUpException {
        resource = new RollUpResource(queryParser, mockStore, mockStatusStore);
        String json = Resources.toString(Resources.getResource("rolluptasks.json"), Charsets.UTF_8);
        List<RollupTask> tasks = mockTasks(json);
        Response response = resource.get(tasks.get(1).getId());
        RollupTask responseTask = queryParser.parseRollupTask(((String) (response.getEntity())));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(OK.getStatusCode()));
        MatcherAssert.assertThat(responseTask, CoreMatchers.equalTo(tasks.get(1)));
    }

    @Test
    public void testGet_taskDoesNotExist() throws IOException, QueryException, RollUpException {
        resource = new RollUpResource(queryParser, mockStore, mockStatusStore);
        String json = Resources.toString(Resources.getResource("rolluptasks.json"), Charsets.UTF_8);
        mockTasks(json);
        Response response = resource.get("bogus");
        ErrorResponse errorResponse = ((ErrorResponse) (response.getEntity()));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(NOT_FOUND.getStatusCode()));
        MatcherAssert.assertThat(errorResponse.getErrors().get(0), CoreMatchers.equalTo("Resource not found for id bogus"));
    }

    @Test
    public void testGet_internalError() throws IOException, QueryException, RollUpException {
        Level previousLogLevel = LoggingUtils.setLogLevel(OFF);
        try {
            Mockito.when(mockStore.read(ArgumentMatchers.anyString())).thenThrow(createRollupException());
            Response response = resource.get("1");
            ErrorResponse errorResponse = ((ErrorResponse) (response.getEntity()));
            MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
            MatcherAssert.assertThat(errorResponse.getErrors().size(), CoreMatchers.equalTo(1));
            MatcherAssert.assertThat(errorResponse.getErrors().get(0), CoreMatchers.equalTo(RollUpResourceTest.INTERNAL_EXCEPTION_MESSAGE));
        } finally {
            LoggingUtils.setLogLevel(previousLogLevel);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testDelete_nullIdInvalid() {
        resource.delete(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelete_emptyIdInvalid() {
        resource.delete("");
    }

    @Test
    public void testDelete() throws IOException, QueryException, RollUpException {
        String json = Resources.toString(Resources.getResource("rolluptasks.json"), Charsets.UTF_8);
        List<RollupTask> tasks = mockTasks(json);
        resource = new RollUpResource(queryParser, mockStore, mockStatusStore);
        Response response = resource.delete(tasks.get(0).getId());
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(NO_CONTENT.getStatusCode()));
        Assert.assertNull(response.getEntity());
    }

    @Test
    public void testDelete_internalError() throws IOException, QueryException, RollUpException {
        Level previousLogLevel = LoggingUtils.setLogLevel(OFF);
        try {
            String json = Resources.toString(Resources.getResource("rolluptasks.json"), Charsets.UTF_8);
            List<RollupTask> tasks = mockTasks(json);
            Mockito.doThrow(createRollupException()).when(mockStore).remove(ArgumentMatchers.anyString());
            Response response = resource.delete(tasks.get(0).getId());
            ErrorResponse errorResponse = ((ErrorResponse) (response.getEntity()));
            MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
            MatcherAssert.assertThat(errorResponse.getErrors().size(), CoreMatchers.equalTo(1));
            MatcherAssert.assertThat(errorResponse.getErrors().get(0), CoreMatchers.equalTo(RollUpResourceTest.INTERNAL_EXCEPTION_MESSAGE));
        } finally {
            LoggingUtils.setLogLevel(previousLogLevel);
        }
    }

    @Test
    public void testDelete_resourceNotExists() throws IOException, QueryException, RollUpException {
        Mockito.when(mockStore.read()).thenReturn(ImmutableMap.of());
        Response response = resource.delete("1");
        ErrorResponse errorResponse = ((ErrorResponse) (response.getEntity()));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(NOT_FOUND.getStatusCode()));
        MatcherAssert.assertThat(errorResponse.getErrors().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(errorResponse.getErrors().get(0), CoreMatchers.equalTo("Resource not found for id 1"));
    }

    @Test(expected = NullPointerException.class)
    public void testUpdate_nullIdInvalid() {
        resource.update(null, "json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_emptyIdInvalid() {
        resource.update("", "json");
    }

    @Test(expected = NullPointerException.class)
    public void testUpdate_nullJsonInvalid() {
        resource.update("id", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_emptyJsonInvalid() {
        resource.update("id", "");
    }

    @Test
    public void testUpdate() throws IOException, QueryException, RollUpException {
        resource = new RollUpResource(queryParser, mockStore, mockStatusStore);
        String json = Resources.toString(Resources.getResource("rolluptasksExisting.json"), Charsets.UTF_8);
        List<RollupTask> tasks = mockTasks(json);
        // Replace task 1 with task 2
        Response response = resource.update(tasks.get(0).getId(), tasks.get(1).getJson());
        @SuppressWarnings("unchecked")
        Class<ArrayList<RollupTask>> listClass = ((Class<ArrayList<RollupTask>>) ((Class) (ArrayList.class)));
        ArgumentCaptor<ArrayList<RollupTask>> captor = ArgumentCaptor.forClass(listClass);
        Mockito.verify(mockStore, Mockito.times(1)).write(captor.capture());
        List<RollupTask> modifiedTasks = captor.getValue();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(OK.getStatusCode()));
        MatcherAssert.assertThat(modifiedTasks.size(), CoreMatchers.equalTo(1));
        RollupTask modifiedTask = modifiedTasks.get(0);
        MatcherAssert.assertThat(modifiedTask.getId(), CoreMatchers.equalTo(tasks.get(0).getId()));
        MatcherAssert.assertThat(modifiedTask.getName(), CoreMatchers.equalTo(tasks.get(1).getName()));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        assertRollupResponse(((String) (response.getEntity())), modifiedTasks.get(0));
        // since the id is stored in the json, verify that the id has not changed
        MatcherAssert.assertThat(new GsonBuilder().create().fromJson(modifiedTask.getJson(), RollupTask.class).getId(), CoreMatchers.equalTo(tasks.get(0).getId()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdate_internalError() throws IOException, QueryException, RollUpException {
        Level previousLogLevel = LoggingUtils.setLogLevel(OFF);
        try {
            resource = new RollUpResource(queryParser, mockStore, mockStatusStore);
            String json = Resources.toString(Resources.getResource("rolluptasks.json"), Charsets.UTF_8);
            List<RollupTask> tasks = mockTasks(json);
            // noinspection unchecked
            Mockito.doThrow(createRollupException()).when(mockStore).write(ArgumentMatchers.any());
            Response response = resource.update(tasks.get(0).getId(), tasks.get(0).getJson());
            ErrorResponse errorResponse = ((ErrorResponse) (response.getEntity()));
            MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
            MatcherAssert.assertThat(errorResponse.getErrors().size(), CoreMatchers.equalTo(1));
            MatcherAssert.assertThat(errorResponse.getErrors().get(0), CoreMatchers.equalTo(RollUpResourceTest.INTERNAL_EXCEPTION_MESSAGE));
        } finally {
            LoggingUtils.setLogLevel(previousLogLevel);
        }
    }

    @Test
    public void testUpdate_resourceNotExists() throws IOException, QueryException, RollUpException {
        Mockito.when(mockStore.read()).thenReturn(ImmutableMap.of());
        Response response = resource.update("1", "json");
        ErrorResponse errorResponse = ((ErrorResponse) (response.getEntity()));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(NOT_FOUND.getStatusCode()));
        MatcherAssert.assertThat(errorResponse.getErrors().size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(errorResponse.getErrors().get(0), CoreMatchers.equalTo("Resource not found for id 1"));
    }
}

