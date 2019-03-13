/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.rest;


import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.zeppelin.user.AuthenticationInfo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * NotebookRepo rest api test.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotebookRepoRestApiTest extends AbstractTestRestApi {
    Gson gson = new Gson();

    AuthenticationInfo anonymous;

    @Test
    public void thatCanGetNotebookRepositoiesSettings() throws IOException {
        List<Map<String, Object>> listOfRepositories = getListOfReposotiry();
        Assert.assertThat(listOfRepositories.size(), CoreMatchers.is(CoreMatchers.not(0)));
    }

    @Test
    public void reloadRepositories() throws IOException {
        GetMethod get = AbstractTestRestApi.httpGet("/notebook-repositories/reload");
        int status = get.getStatusCode();
        get.releaseConnection();
        Assert.assertThat(status, CoreMatchers.is(200));
    }

    @Test
    public void setNewDirectoryForLocalDirectory() throws IOException {
        List<Map<String, Object>> listOfRepositories = getListOfReposotiry();
        String localVfs = StringUtils.EMPTY;
        String className = StringUtils.EMPTY;
        for (int i = 0; i < (listOfRepositories.size()); i++) {
            if (listOfRepositories.get(i).get("name").equals("VFSNotebookRepo")) {
                localVfs = ((String) (((List<Map<String, Object>>) (listOfRepositories.get(i).get("settings"))).get(0).get("selected")));
                className = ((String) (listOfRepositories.get(i).get("className")));
                break;
            }
        }
        if (StringUtils.isBlank(localVfs)) {
            // no local VFS set...
            return;
        }
        String payload = (("{ \"name\": \"" + className) + "\", \"settings\" : ") + "{ \"Notebook Path\" : \"/tmp/newDir\" } }";
        updateNotebookRepoWithNewSetting(payload);
        // Verify
        listOfRepositories = getListOfReposotiry();
        String updatedPath = StringUtils.EMPTY;
        for (int i = 0; i < (listOfRepositories.size()); i++) {
            if (listOfRepositories.get(i).get("name").equals("VFSNotebookRepo")) {
                updatedPath = ((String) (((List<Map<String, Object>>) (listOfRepositories.get(i).get("settings"))).get(0).get("selected")));
                break;
            }
        }
        Assert.assertThat(updatedPath, CoreMatchers.anyOf(CoreMatchers.is("/tmp/newDir"), CoreMatchers.is("/tmp/newDir/")));
        // go back to normal
        payload = ((("{ \"name\": \"" + className) + "\", \"settings\" : { \"Notebook Path\" : \"") + localVfs) + "\" } }";
        updateNotebookRepoWithNewSetting(payload);
    }
}

