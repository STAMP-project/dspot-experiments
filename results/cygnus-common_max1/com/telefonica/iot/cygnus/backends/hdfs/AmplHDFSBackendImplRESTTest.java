

package com.telefonica.iot.cygnus.backends.hdfs;


@org.junit.runner.RunWith(value = org.mockito.runners.MockitoJUnitRunner.class)
public class AmplHDFSBackendImplRESTTest {
    private com.telefonica.iot.cygnus.backends.hdfs.HDFSBackendImplREST backend;

    @org.mockito.Mock
    private org.apache.http.client.HttpClient mockHttpClientCreateFile;

    @org.mockito.Mock
    private org.apache.http.client.HttpClient mockHttpClientAppend;

    @org.mockito.Mock
    private org.apache.http.client.HttpClient mockHttpClientExistsCreateDir;

    private final java.lang.String hdfsHost = "1.2.3.4";

    private final java.lang.String hdfsPort = "50070";

    private final java.lang.String user = "hdfs-user";

    private final java.lang.String password = "12345abcde";

    private final java.lang.String token = "erwfgwegwegewtgewtg";

    private final java.lang.String hiveServerVersion = "2";

    private final java.lang.String hiveHost = "1.2.3.4";

    private final java.lang.String hivePort = "10000";

    private final java.lang.String dirPath = "path/to/my/data";

    private final java.lang.String data = "this is a lot of data";

    private final int maxConns = 50;

    private final int maxConnsPerRoute = 10;

    @org.junit.Before
    public void setUp() throws java.lang.Exception {
        backend = new com.telefonica.iot.cygnus.backends.hdfs.HDFSBackendImplREST(hdfsHost, hdfsPort, user, password, token, hiveServerVersion, hiveHost, hivePort, false, null, null, null, null, false, maxConns, maxConnsPerRoute);
        org.apache.http.message.BasicHttpResponse resp200 = new org.apache.http.message.BasicHttpResponse(new org.apache.http.ProtocolVersion("HTTP", 1, 1), 200, "OK");
        resp200.addHeader("Content-Type", "application/json");
        org.apache.http.message.BasicHttpResponse resp201 = new org.apache.http.message.BasicHttpResponse(new org.apache.http.ProtocolVersion("HTTP", 1, 1), 201, "Created");
        resp201.addHeader("Content-Type", "application/json");
        org.apache.http.message.BasicHttpResponse resp307 = new org.apache.http.message.BasicHttpResponse(new org.apache.http.ProtocolVersion("HTTP", 1, 1), 307, "Temporary Redirect");
        resp307.addHeader("Content-Type", "application/json");
        resp307.addHeader(new org.apache.http.message.BasicHeader("Location", "http://localhost:14000/"));
        org.mockito.Mockito.when(mockHttpClientExistsCreateDir.execute(org.mockito.Mockito.any(org.apache.http.client.methods.HttpUriRequest.class))).thenReturn(resp200);
        org.mockito.Mockito.when(mockHttpClientCreateFile.execute(org.mockito.Mockito.any(org.apache.http.client.methods.HttpUriRequest.class))).thenReturn(resp307, resp201);
        org.mockito.Mockito.when(mockHttpClientAppend.execute(org.mockito.Mockito.any(org.apache.http.client.methods.HttpUriRequest.class))).thenReturn(resp307, resp200);
    }

    @org.junit.Test
    public void testCreateDir() {
        java.lang.System.out.println("Testing HDFSBackendImplREST.createDir");
        try {
            backend.setHttpClient(mockHttpClientExistsCreateDir);
            backend.createDir(dirPath);
        } catch (java.lang.Exception e) {
            org.junit.Assert.fail(e.getMessage());
        } finally {
            org.junit.Assert.assertTrue(true);
        }
    }

    @org.junit.Test
    public void testCreateFile() {
        java.lang.System.out.println("Testing HDFSBackendImplREST.createFile");
        try {
            backend.setHttpClient(mockHttpClientCreateFile);
            backend.createFile(dirPath, data);
        } catch (java.lang.Exception e) {
            org.junit.Assert.fail(e.getMessage());
        } finally {
            org.junit.Assert.assertTrue(true);
        }
    }

    @org.junit.Test
    public void testAppend() {
        java.lang.System.out.println("Testing HDFSBackendImplREST.append");
        try {
            backend.setHttpClient(mockHttpClientAppend);
            backend.append(dirPath, data);
        } catch (java.lang.Exception e) {
            org.junit.Assert.fail(e.getMessage());
        } finally {
            org.junit.Assert.assertTrue(true);
        }
    }

    @org.junit.Test
    public void testExists() {
        java.lang.System.out.println("Testing HDFSBackendImplREST.exists");
        try {
            backend.setHttpClient(mockHttpClientExistsCreateDir);
            backend.exists(dirPath);
        } catch (java.lang.Exception e) {
            org.junit.Assert.fail(e.getMessage());
        } finally {
            org.junit.Assert.assertTrue(true);
        }
    }
}

