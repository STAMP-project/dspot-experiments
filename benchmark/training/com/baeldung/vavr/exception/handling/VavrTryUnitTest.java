package com.baeldung.vavr.exception.handling;


import com.baeldung.vavr.exception.handling.client.ClientException;
import com.baeldung.vavr.exception.handling.client.HttpClient;
import com.baeldung.vavr.exception.handling.client.Response;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Assert;
import org.junit.Test;


public class VavrTryUnitTest {
    @Test
    public void givenHttpClient_whenMakeACall_shouldReturnSuccess() {
        // given
        Integer defaultChainedResult = 1;
        String id = "a";
        HttpClient httpClient = () -> new Response(id);
        // when
        Try<Response> response = getResponse();
        Integer chainedResult = response.map(this::actionThatTakesResponse).getOrElse(defaultChainedResult);
        Stream<String> stream = response.toStream().map(( it) -> it.id);
        // then
        Assert.assertTrue((!(stream.isEmpty())));
        Assert.assertTrue(response.isSuccess());
        response.onSuccess(( r) -> assertEquals(id, r.id));
        response.andThen(( r) -> assertEquals(id, r.id));
        Assert.assertNotEquals(defaultChainedResult, chainedResult);
    }

    @Test
    public void givenHttpClientFailure_whenMakeACall_shouldReturnFailure() {
        // given
        Integer defaultChainedResult = 1;
        HttpClient httpClient = () -> {
            throw new ClientException("problem");
        };
        // when
        Try<Response> response = getResponse();
        Integer chainedResult = response.map(this::actionThatTakesResponse).getOrElse(defaultChainedResult);
        Option<Response> optionalResponse = response.toOption();
        // then
        Assert.assertTrue(optionalResponse.isEmpty());
        Assert.assertTrue(response.isFailure());
        response.onFailure(( ex) -> assertTrue((ex instanceof ClientException)));
        Assert.assertEquals(defaultChainedResult, chainedResult);
    }

    @Test
    public void givenHttpClientThatFailure_whenMakeACall_shouldReturnFailureAndNotRecover() {
        // given
        Response defaultResponse = new Response("b");
        HttpClient httpClient = () -> {
            throw new RuntimeException("critical problem");
        };
        // when
        Try<Response> recovered = new VavrTry(httpClient).getResponse().recover(( r) -> Match(r).of(Case($(instanceOf(.class)), defaultResponse)));
        // then
        Assert.assertTrue(recovered.isFailure());
        // recovered.getOrElseThrow(throwable -> {
        // throw new RuntimeException(throwable);
        // });
    }

    @Test
    public void givenHttpClientThatFailure_whenMakeACall_shouldReturnFailureAndRecover() {
        // given
        Response defaultResponse = new Response("b");
        HttpClient httpClient = () -> {
            throw new ClientException("non critical problem");
        };
        // when
        Try<Response> recovered = new VavrTry(httpClient).getResponse().recover(( r) -> Match(r).of(Case($(instanceOf(.class)), defaultResponse), Case($(instanceOf(.class)), defaultResponse)));
        // then
        Assert.assertTrue(recovered.isSuccess());
    }
}

