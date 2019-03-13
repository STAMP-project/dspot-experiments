/**
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2.adapter.rxjava2;


import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.http.GET;


public final class CancelDisposeTestSync {
    @Rule
    public final MockWebServer server = new MockWebServer();

    interface Service {
        @GET("/")
        Observable<String> go();
    }

    private final OkHttpClient client = new OkHttpClient();

    private CancelDisposeTestSync.Service service;

    @Test
    public void disposeBeforeExecuteDoesNotEnqueue() {
        service.go().test(true);
        Assert.assertEquals(0, server.getRequestCount());
    }
}

