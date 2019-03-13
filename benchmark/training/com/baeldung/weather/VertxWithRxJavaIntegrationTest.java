package com.baeldung.weather;


import io.reactivex.Flowable;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.file.FileSystem;
import io.vertx.reactivex.core.http.HttpClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VertxWithRxJavaIntegrationTest {
    private Vertx vertx;

    private HttpClient httpClient;

    private FileSystem fileSystem;

    private static Logger log = LoggerFactory.getLogger(VertxWithRxJavaIntegrationTest.class);

    @Test
    public void shouldDisplayLightLenghts() throws InterruptedException {
        // read the file that contains one city name per line
        fileSystem.rxReadFile("cities.txt").toFlowable().doOnNext(( buffer) -> VertxWithRxJavaIntegrationTest.log.info("File buffer ---\n{}\n---", buffer)).flatMap(( buffer) -> Flowable.fromArray(buffer.toString().split("\\r?\\n"))).doOnNext(( city) -> VertxWithRxJavaIntegrationTest.log.info("City from file: '{}'", city)).filter(( city) -> !(city.startsWith("#"))).doOnNext(( city) -> VertxWithRxJavaIntegrationTest.log.info("City that survived filtering: '{}'", city)).flatMap(( city) -> searchByCityName(httpClient, city)).flatMap(HttpClientResponse::toFlowable).doOnNext(( buffer) -> VertxWithRxJavaIntegrationTest.log.info("JSON of city detail: '{}'", buffer)).map(VertxWithRxJavaIntegrationTest.extractingWoeid()).flatMap(( cityId) -> getDataByPlaceId(httpClient, cityId)).flatMap(VertxWithRxJavaIntegrationTest.toBufferFlowable()).doOnNext(( buffer) -> VertxWithRxJavaIntegrationTest.log.info("JSON of place detail: '{}'", buffer)).map(Buffer::toJsonObject).map(VertxWithRxJavaIntegrationTest.toCityAndDayLength()).subscribe(System.out::println, Throwable::printStackTrace);
        Thread.sleep(20000);// enough to give time to complete the execution

    }
}

