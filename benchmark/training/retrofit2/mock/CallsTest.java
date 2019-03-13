/**
 * Copyright (C) 2017 Square, Inc.
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
package retrofit2.mock;


import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;


public final class CallsTest {
    @Test
    public void bodyExecute() throws IOException {
        Call<String> taco = Calls.response("Taco");
        Assert.assertEquals("Taco", taco.execute().body());
    }

    @Test
    public void bodyEnqueue() throws IOException {
        Call<String> taco = Calls.response("Taco");
        final AtomicReference<Response<String>> responseRef = new AtomicReference<>();
        taco.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                responseRef.set(response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Assert.fail();
            }
        });
        assertThat(responseRef.get().body()).isEqualTo("Taco");
    }

    @Test
    public void responseExecute() throws IOException {
        Response<String> response = Response.success("Taco");
        Call<String> taco = Calls.response(response);
        Assert.assertFalse(taco.isExecuted());
        Assert.assertSame(response, taco.execute());
        Assert.assertTrue(taco.isExecuted());
        try {
            taco.execute();
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Already executed");
        }
    }

    @Test
    public void responseEnqueue() {
        Response<String> response = Response.success("Taco");
        Call<String> taco = Calls.response(response);
        Assert.assertFalse(taco.isExecuted());
        final AtomicReference<Response<String>> responseRef = new AtomicReference<>();
        taco.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                responseRef.set(response);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Assert.fail();
            }
        });
        Assert.assertSame(response, responseRef.get());
        Assert.assertTrue(taco.isExecuted());
        try {
            taco.enqueue(new retrofit2.Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Assert.fail();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Assert.fail();
                }
            });
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Already executed");
        }
    }

    @Test
    public void enqueueNullThrows() {
        Call<String> taco = Calls.response("Taco");
        try {
            taco.enqueue(null);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("callback == null");
        }
    }

    @Test
    public void responseCancelExecute() {
        Call<String> taco = Calls.response(Response.success("Taco"));
        Assert.assertFalse(taco.isCanceled());
        taco.cancel();
        Assert.assertTrue(taco.isCanceled());
        try {
            taco.execute();
            Assert.fail();
        } catch (IOException e) {
            assertThat(e).hasMessage("canceled");
        }
    }

    @Test
    public void responseCancelEnqueue() throws IOException {
        Call<String> taco = Calls.response(Response.success("Taco"));
        Assert.assertFalse(taco.isCanceled());
        taco.cancel();
        Assert.assertTrue(taco.isCanceled());
        final AtomicReference<Throwable> failureRef = new AtomicReference<>();
        taco.enqueue(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Assert.fail();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                failureRef.set(t);
            }
        });
        assertThat(failureRef.get()).isInstanceOf(IOException.class).hasMessage("canceled");
    }

    @Test
    public void failureExecute() {
        IOException failure = new IOException("Hey");
        Call<Object> taco = Calls.failure(failure);
        Assert.assertFalse(taco.isExecuted());
        try {
            taco.execute();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertSame(failure, e);
        }
        Assert.assertTrue(taco.isExecuted());
    }

    @Test
    public void failureExecuteCheckedException() {
        CertificateException failure = new CertificateException("Hey");
        Call<Object> taco = Calls.failure(failure);
        Assert.assertFalse(taco.isExecuted());
        try {
            taco.execute();
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertSame(failure, e);
        }
        Assert.assertTrue(taco.isExecuted());
    }

    @Test
    public void failureEnqueue() {
        IOException failure = new IOException("Hey");
        Call<Object> taco = Calls.failure(failure);
        Assert.assertFalse(taco.isExecuted());
        final AtomicReference<Throwable> failureRef = new AtomicReference<>();
        taco.enqueue(new retrofit2.Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Assert.fail();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                failureRef.set(t);
            }
        });
        Assert.assertSame(failure, failureRef.get());
        Assert.assertTrue(taco.isExecuted());
    }

    @Test
    public void cloneHasOwnState() throws IOException {
        Call<String> taco = Calls.response("Taco");
        Assert.assertEquals("Taco", taco.execute().body());
        Call<String> anotherTaco = taco.clone();
        Assert.assertFalse(anotherTaco.isExecuted());
        Assert.assertEquals("Taco", anotherTaco.execute().body());
        Assert.assertTrue(anotherTaco.isExecuted());
    }

    @Test
    public void deferredReturnExecute() throws IOException {
        Call<Integer> counts = Calls.defer(new Callable<Call<Integer>>() {
            private int count = 0;

            @Override
            public Call<Integer> call() throws Exception {
                return Calls.response((++(count)));
            }
        });
        Call<Integer> a = counts.clone();
        Call<Integer> b = counts.clone();
        Assert.assertEquals(1, b.execute().body().intValue());
        Assert.assertEquals(2, a.execute().body().intValue());
    }

    @Test
    public void deferredReturnEnqueue() {
        Call<Integer> counts = Calls.defer(new Callable<Call<Integer>>() {
            private int count = 0;

            @Override
            public Call<Integer> call() throws Exception {
                return Calls.response((++(count)));
            }
        });
        Call<Integer> a = counts.clone();
        Call<Integer> b = counts.clone();
        final AtomicReference<Response<Integer>> responseRef = new AtomicReference<>();
        retrofit2.Callback<Integer> callback = new retrofit2.Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                responseRef.set(response);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Assert.fail();
            }
        };
        b.enqueue(callback);
        Assert.assertEquals(1, responseRef.get().body().intValue());
        a.enqueue(callback);
        Assert.assertEquals(2, responseRef.get().body().intValue());
    }

    @Test
    public void deferredThrowExecute() throws IOException {
        final IOException failure = new IOException("Hey");
        Call<Object> failing = Calls.defer(new Callable<Call<Object>>() {
            @Override
            public Call<Object> call() throws Exception {
                throw failure;
            }
        });
        try {
            failing.execute();
            Assert.fail();
        } catch (IOException e) {
            Assert.assertSame(failure, e);
        }
    }

    @Test
    public void deferredThrowEnqueue() {
        final IOException failure = new IOException("Hey");
        Call<Object> failing = Calls.defer(new Callable<Call<Object>>() {
            @Override
            public Call<Object> call() throws Exception {
                throw failure;
            }
        });
        final AtomicReference<Throwable> failureRef = new AtomicReference<>();
        failing.enqueue(new retrofit2.Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Assert.fail();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                failureRef.set(t);
            }
        });
        Assert.assertSame(failure, failureRef.get());
    }

    @Test
    public void deferredThrowUncheckedExceptionEnqueue() {
        final RuntimeException failure = new RuntimeException("Hey");
        final AtomicReference<Throwable> failureRef = new AtomicReference<>();
        Calls.failure(failure).enqueue(new retrofit2.Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Assert.fail();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                failureRef.set(t);
            }
        });
        Assert.assertSame(failure, failureRef.get());
    }
}

