/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.job.entries.http;


import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.job.Job;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class JobEntryHTTP_PDI208_Test {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    public static final String HTTP_HOST = "localhost";

    public static final int HTTP_PORT = 9998;

    public static final String HTTP_SERVER_BASEURL = "http://localhost:9998";

    private static HttpServer httpServer;

    @Test
    public void testHTTPResultDefaultRows() throws IOException {
        File localFileForUpload = getInputFile("existingFile1", ".tmp");
        File tempFileForDownload = File.createTempFile("downloadedFile1", ".tmp");
        localFileForUpload.deleteOnExit();
        tempFileForDownload.deleteOnExit();
        Object[] r = new Object[]{ (JobEntryHTTP_PDI208_Test.HTTP_SERVER_BASEURL) + "/uploadFile", localFileForUpload.getCanonicalPath(), tempFileForDownload.getCanonicalPath() };
        RowMeta rowMetaDefault = new RowMeta();
        rowMetaDefault.addValueMeta(new ValueMetaString("URL"));
        rowMetaDefault.addValueMeta(new ValueMetaString("UPLOAD"));
        rowMetaDefault.addValueMeta(new ValueMetaString("DESTINATION"));
        List<RowMetaAndData> rows = new ArrayList<RowMetaAndData>();
        rows.add(new RowMetaAndData(rowMetaDefault, r));
        Result previousResult = new Result();
        previousResult.setRows(rows);
        JobEntryHTTP http = new JobEntryHTTP();
        http.setParentJob(new Job());
        http.setRunForEveryRow(true);
        http.setAddFilenameToResult(false);
        http.execute(previousResult, 0);
        Assert.assertTrue(FileUtils.contentEquals(localFileForUpload, tempFileForDownload));
    }

    @Test
    public void testHTTPResultCustomRows() throws IOException {
        File localFileForUpload = getInputFile("existingFile2", ".tmp");
        File tempFileForDownload = File.createTempFile("downloadedFile2", ".tmp");
        localFileForUpload.deleteOnExit();
        tempFileForDownload.deleteOnExit();
        Object[] r = new Object[]{ (JobEntryHTTP_PDI208_Test.HTTP_SERVER_BASEURL) + "/uploadFile", localFileForUpload.getCanonicalPath(), tempFileForDownload.getCanonicalPath() };
        RowMeta rowMetaDefault = new RowMeta();
        rowMetaDefault.addValueMeta(new ValueMetaString("MyURL"));
        rowMetaDefault.addValueMeta(new ValueMetaString("MyUpload"));
        rowMetaDefault.addValueMeta(new ValueMetaString("MyDestination"));
        List<RowMetaAndData> rows = new ArrayList<RowMetaAndData>();
        rows.add(new RowMetaAndData(rowMetaDefault, r));
        Result previousResult = new Result();
        previousResult.setRows(rows);
        JobEntryHTTP http = new JobEntryHTTP();
        http.setParentJob(new Job());
        http.setRunForEveryRow(true);
        http.setAddFilenameToResult(false);
        http.setUrlFieldname("MyURL");
        http.setUploadFieldname("MyUpload");
        http.setDestinationFieldname("MyDestination");
        http.execute(previousResult, 0);
        Assert.assertTrue(FileUtils.contentEquals(localFileForUpload, tempFileForDownload));
    }
}

