package io.github.hidroh.materialistic.accounts;


import Response.Builder;
import RuntimeEnvironment.application;
import UserServices.Exception;
import android.accounts.Account;
import io.github.hidroh.materialistic.Preferences;
import io.github.hidroh.materialistic.test.TestRunner;
import java.io.IOException;
import java.net.HttpURLConnection;
import junit.framework.Assert;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.shadows.ShadowAccountManager;


@RunWith(TestRunner.class)
public class UserServicesClientTest {
    private UserServices userServices;

    private Call call;

    private Builder responseBuilder = createResponseBuilder();

    private Account account;

    @Captor
    ArgumentCaptor<Throwable> throwableCaptor;

    @Test
    public void testLoginSuccess() throws IOException {
        Mockito.when(call.execute()).thenReturn(responseBuilder.message("").code(HttpURLConnection.HTTP_MOVED_TEMP).build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.login("username", "password", false, callback);
        Mockito.verify(callback).onDone(ArgumentMatchers.eq(true));
    }

    @Test
    public void testRegisterFailed() throws IOException {
        Mockito.when(call.execute()).thenReturn(responseBuilder.body(ResponseBody.create(MediaType.parse("text/html"), "<body>Message<br/></body>")).message("").code(HttpURLConnection.HTTP_OK).build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.login("username", "password", true, callback);
        Mockito.verify(callback).onError(throwableCaptor.capture());
        // noinspection ThrowableResultOfMethodCallIgnored
        assertThat(throwableCaptor.getValue().getMessage()).contains("Message");
    }

    @Test
    public void testLoginError() throws IOException {
        Mockito.when(call.execute()).thenThrow(new IOException());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.login("username", "password", false, callback);
        Mockito.verify(callback).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testVoteSuccess() throws IOException {
        Mockito.when(call.execute()).thenReturn(responseBuilder.message("").code(HttpURLConnection.HTTP_MOVED_TEMP).build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        Assert.assertTrue(userServices.voteUp(application, "1", callback));
        Mockito.verify(callback).onDone(ArgumentMatchers.eq(true));
    }

    @Test
    public void testVoteFailed() throws IOException {
        Mockito.when(call.execute()).thenReturn(responseBuilder.message("").code(HttpURLConnection.HTTP_OK).build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        Assert.assertTrue(userServices.voteUp(application, "1", callback));
        Mockito.verify(callback).onDone(ArgumentMatchers.eq(false));
    }

    @Test
    public void testVoteError() throws IOException {
        Mockito.when(call.execute()).thenThrow(new IOException());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        Assert.assertTrue(userServices.voteUp(application, "1", callback));
        Mockito.verify(callback).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testVoteNoMatchingAccount() {
        Preferences.setUsername(application, "another");
        UserServices.Callback callback = Mockito.mock(Callback.class);
        Assert.assertFalse(userServices.voteUp(application, "1", callback));
        Mockito.verify(call, Mockito.never()).enqueue(ArgumentMatchers.any(Callback.class));
    }

    @Test
    public void testCommentSuccess() throws IOException {
        Mockito.when(call.execute()).thenReturn(responseBuilder.message("").code(HttpURLConnection.HTTP_MOVED_TEMP).build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.reply(application, "1", "reply", callback);
        Mockito.verify(callback).onDone(ArgumentMatchers.eq(true));
    }

    @Test
    public void testCommentNotLoggedIn() {
        Preferences.setUsername(application, null);
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.reply(application, "1", "reply", callback);
        Mockito.verify(call, Mockito.never()).enqueue(ArgumentMatchers.any(Callback.class));
        Mockito.verify(callback).onDone(ArgumentMatchers.eq(false));
    }

    @Test
    public void testVoteNoAccount() {
        ShadowAccountManager.get(application).removeAccount(account, null, null);
        UserServices.Callback callback = Mockito.mock(Callback.class);
        Assert.assertFalse(userServices.voteUp(application, "1", callback));
        Mockito.verify(call, Mockito.never()).enqueue(ArgumentMatchers.any(Callback.class));
    }

    @Test
    public void testSubmitNoAccount() {
        ShadowAccountManager.get(application).removeAccount(account, null, null);
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.submit(application, "title", "content", true, callback);
        Mockito.verify(call, Mockito.never()).enqueue(ArgumentMatchers.any(Callback.class));
        Mockito.verify(callback).onDone(ArgumentMatchers.eq(false));
    }

    @Test
    public void testSubmitSuccess() throws IOException {
        Mockito.when(call.execute()).thenReturn(createResponseBuilder().body(ResponseBody.create(MediaType.parse("text/html"), "<input \"name\"=\"fnid\" value=\"unique\">")).message("").code(HttpURLConnection.HTTP_OK).build()).thenReturn(createResponseBuilder().message("").code(HttpURLConnection.HTTP_MOVED_TEMP).header("location", "newest").build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.submit(application, "title", "content", false, callback);
        Mockito.verify(call, Mockito.times(2)).execute();
        Mockito.verify(callback).onDone(ArgumentMatchers.eq(true));
    }

    @Test
    public void testSubmitDuplicate() throws IOException {
        Mockito.when(call.execute()).thenReturn(createResponseBuilder().body(ResponseBody.create(MediaType.parse("text/html"), "<input \"name\"=\"fnid\" value=\"unique\">")).message("").code(HttpURLConnection.HTTP_OK).build()).thenReturn(createResponseBuilder().message("").code(HttpURLConnection.HTTP_MOVED_TEMP).header("location", "item?id=1234").build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.submit(application, "title", "url", true, callback);
        Mockito.verify(call, Mockito.times(2)).execute();
        Mockito.verify(callback).onError(ArgumentMatchers.isA(Exception.class));
    }

    @Test
    public void testSubmitError() throws IOException {
        Mockito.when(call.execute()).thenReturn(createResponseBuilder().body(ResponseBody.create(MediaType.parse("text/html"), "<input \"name\"=\"fnid\" value=\"unique\">")).message("").code(HttpURLConnection.HTTP_OK).build()).thenReturn(createResponseBuilder().message("").code(HttpURLConnection.HTTP_OK).build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.submit(application, "title", "url", true, callback);
        Mockito.verify(call, Mockito.times(2)).execute();
        Mockito.verify(callback).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSubmitNetworkError() throws IOException {
        Mockito.when(call.execute()).thenReturn(createResponseBuilder().body(ResponseBody.create(MediaType.parse("text/html"), "<input \"name\"=\"fnid\" value=\"unique\">")).message("").code(HttpURLConnection.HTTP_OK).build()).thenThrow(new IOException());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.submit(application, "title", "url", true, callback);
        Mockito.verify(call, Mockito.times(2)).execute();
        Mockito.verify(callback).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSubmitParsingNoInput() throws IOException {
        Mockito.when(call.execute()).thenReturn(createResponseBuilder().body(ResponseBody.create(MediaType.parse("text/html"), "")).message("").code(HttpURLConnection.HTTP_OK).build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.submit(application, "title", "url", true, callback);
        Mockito.verify(callback).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSubmitParsingNoFnid() throws IOException {
        Mockito.when(call.execute()).thenReturn(createResponseBuilder().body(ResponseBody.create(MediaType.parse("text/html"), "<input \"name\"=\"hiddenfield\" value=\"unique\">")).message("").code(HttpURLConnection.HTTP_OK).build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.submit(application, "title", "url", true, callback);
        Mockito.verify(callback).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSubmitParsingNoFnidValue() throws IOException {
        Mockito.when(call.execute()).thenReturn(createResponseBuilder().body(ResponseBody.create(MediaType.parse("text/html"), "<input \"name\"=\"fnid\">")).message("").code(HttpURLConnection.HTTP_OK).build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.submit(application, "title", "url", true, callback);
        Mockito.verify(callback).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSubmitLoginFailed() throws IOException {
        Mockito.when(call.execute()).thenReturn(createResponseBuilder().message("").code(HttpURLConnection.HTTP_MOVED_TEMP).build());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.submit(application, "title", "url", true, callback);
        Mockito.verify(callback).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSubmitLoginError() throws IOException {
        Mockito.when(call.execute()).thenThrow(new IOException());
        UserServices.Callback callback = Mockito.mock(Callback.class);
        userServices.submit(application, "title", "url", true, callback);
        Mockito.verify(callback).onError(ArgumentMatchers.any(Throwable.class));
    }
}

