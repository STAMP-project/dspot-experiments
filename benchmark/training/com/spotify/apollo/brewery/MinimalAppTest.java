package com.spotify.apollo.brewery;


import Status.IM_A_TEAPOT;
import Status.INTERNAL_SERVER_ERROR;
import com.spotify.apollo.Response;
import com.spotify.apollo.StatusType;
import com.spotify.apollo.test.ServiceHelper;
import com.spotify.apollo.test.StubClient;
import okio.ByteString;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MinimalAppTest {
    private static final String BREWERY_ORDER_URI = "http://brewery/order";

    private static final String ORDER_REPLY = "order0443";

    private static final ByteString ORDER_REPLY_BYTES = ByteString.encodeUtf8(MinimalAppTest.ORDER_REPLY);

    @Rule
    public ServiceHelper serviceHelper = ServiceHelper.create(MinimalApp::init, "test");

    public StubClient stubClient = serviceHelper.stubClient();

    @Test
    public void shouldRespondWithOrderId() throws Exception {
        stubClient.respond(Response.forPayload(MinimalAppTest.ORDER_REPLY_BYTES)).to(MinimalAppTest.BREWERY_ORDER_URI);
        String reply = serviceHelper.request("GET", "/beer").toCompletableFuture().get().payload().get().utf8();
        Assert.assertThat(reply, Matchers.is(("your order is " + (MinimalAppTest.ORDER_REPLY))));
    }

    @Test
    public void shouldRespondWithStatusCode() throws Exception {
        stubClient.respond(Response.of(IM_A_TEAPOT, MinimalAppTest.ORDER_REPLY_BYTES)).to(MinimalAppTest.BREWERY_ORDER_URI);
        StatusType status = serviceHelper.request("GET", "/beer").toCompletableFuture().get().status();
        Assert.assertThat(status.code(), Matchers.is(INTERNAL_SERVER_ERROR.code()));
    }

    @Test
    public void shouldBeOkWithPayload() throws Exception {
        stubClient.respond(Response.of(IM_A_TEAPOT, MinimalAppTest.ORDER_REPLY_BYTES)).to(MinimalAppTest.BREWERY_ORDER_URI);
        StatusType status = serviceHelper.request("POST", "/beer", ByteString.encodeUtf8("{\"key\": \"value\"}")).toCompletableFuture().get().status();
        Assert.assertThat(status.code(), Matchers.is(INTERNAL_SERVER_ERROR.code()));
    }
}

