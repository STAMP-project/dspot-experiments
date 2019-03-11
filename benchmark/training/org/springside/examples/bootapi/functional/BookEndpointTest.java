/**
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springside.examples.bootapi.functional;


import Book.STATUS_REQUEST;
import ErrorCode.NO_TOKEN.code;
import HttpStatus.BAD_REQUEST;
import HttpStatus.FORBIDDEN;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import java.util.ArrayList;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springside.examples.bootapi.api.support.ErrorResult;
import org.springside.examples.bootapi.domain.Book;
import org.springside.examples.bootapi.dto.BookDto;
import org.springside.examples.bootapi.repository.BookDao;
import org.springside.modules.utils.mapper.JsonMapper;


// ????????????JVM????????????????????????????????
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BookEndpointTest extends BaseFunctionalTest {
    // ??Spring Context??BookDao??????????????
    @Autowired
    private BookDao bookDao;

    private RestTemplate restTemplate;

    private JsonMapper jsonMapper = new JsonMapper();

    private String resourceUrl;

    private String loginUrl;

    private String logoutUrl;

    @Test
    public void listBook() {
        BookEndpointTest.BookList tasks = restTemplate.getForObject(resourceUrl, BookEndpointTest.BookList.class);
        assertThat(tasks).hasSize(3);
        BookDto firstBook = tasks.get(0);
        assertThat(firstBook.title).isEqualTo("Big Data???");
        assertThat(firstBook.owner.name).isEqualTo("Calvin");
        BookDto book = restTemplate.getForObject(((resourceUrl) + "/{id}"), BookDto.class, 1L);
        assertThat(book.title).isEqualTo("Big Data???");
        assertThat(book.owner.name).isEqualTo("Calvin");
    }

    @Test
    public void applyRequest() {
        String token = login("calvin.xiao@springside.io");
        ResponseEntity<String> response = restTemplate.getForEntity(((resourceUrl) + "/{id}/request?token={token}"), String.class, 3L, token);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        // ???????
        Book book = bookDao.findOne(3L);
        assertThat(book.borrower.id).isEqualTo(1L);
        assertThat(book.status).isEqualTo(STATUS_REQUEST);
        // ????
        response = restTemplate.getForEntity(((resourceUrl) + "/{id}/cancel?token={token}"), String.class, 3L, token);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        logout(token);
    }

    @Test
    public void applyRequestWithError() {
        // ???token
        ResponseEntity<String> response = restTemplate.getForEntity(((resourceUrl) + "/{id}/request"), String.class, 1L);
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
        ErrorResult errorResult = jsonMapper.fromJson(response.getBody(), ErrorResult.class);
        assertThat(errorResult.code).isEqualTo(code);
        Book book = bookDao.findOne(1L);
        assertThat(book.borrower).isNull();
        // ????token
        response = restTemplate.getForEntity(((resourceUrl) + "/{id}/request?token={token}"), String.class, 1L, "abc");
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
        errorResult = jsonMapper.fromJson(response.getBody(), ErrorResult.class);
        assertThat(errorResult.code).isEqualTo(ErrorCode.UNAUTHORIZED.code);
        book = bookDao.findOne(1L);
        assertThat(book.borrower).isNull();
        // ???????
        String token = login("calvin.xiao@springside.io");
        response = restTemplate.getForEntity(((resourceUrl) + "/{id}/request?token={token}"), String.class, 1L, token);
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
        errorResult = jsonMapper.fromJson(response.getBody(), ErrorResult.class);
        assertThat(errorResult.code).isEqualTo(ErrorCode.BOOK_OWNERSHIP_WRONG.code);
        book = bookDao.findOne(1L);
        assertThat(book.borrower).isNull();
        logout(token);
        // ???????????
        token = login("calvin.xiao@springside.io");
        response = restTemplate.getForEntity(((resourceUrl) + "/{id}/request?token={token}"), String.class, 3L, token);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        response = restTemplate.getForEntity(((resourceUrl) + "/{id}/request?token={token}"), String.class, 3L, token);
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        errorResult = jsonMapper.fromJson(response.getBody(), ErrorResult.class);
        assertThat(errorResult.code).isEqualTo(ErrorCode.BOOK_STATUS_WRONG.code);
        // ????
        response = restTemplate.getForEntity(((resourceUrl) + "/{id}/cancel?token={token}"), String.class, 3L, token);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        logout(token);
    }

    @Test
    public void fullBorrowProcess() {
        // ????
        String token = login("david.wang@springside.io");
        ResponseEntity<String> response = restTemplate.getForEntity(((resourceUrl) + "/{id}/request?token={token}"), String.class, 1L, token);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        logout(token);
        // ????
        token = login("calvin.xiao@springside.io");
        response = restTemplate.getForEntity(((resourceUrl) + "/{id}/confirm?token={token}"), String.class, 1L, token);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        // ????
        response = restTemplate.getForEntity(((resourceUrl) + "/{id}/return?token={token}"), String.class, 1L, token);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        logout(token);
    }

    // ArrayList<Task>?RestTemplate???????????????????????
    private static class BookList extends ArrayList<BookDto> {}
}

