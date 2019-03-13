/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.server.resources;


import Errors.ERROR_CODE_SERVER_ERROR;
import Response.Status.INTERNAL_SERVER_ERROR;
import io.confluent.ksql.rest.entity.KsqlErrorMessage;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class KsqlExceptionMapperTest {
    KsqlExceptionMapper exceptionMapper = new KsqlExceptionMapper();

    @Test
    public void shouldReturnEmbeddedResponseForKsqlRestException() {
        final Response response = Response.status(400).build();
        Assert.assertThat(exceptionMapper.toResponse(new KsqlRestException(response)), CoreMatchers.sameInstance(response));
    }

    @Test
    public void shouldReturnCorrectResponseForWebAppException() {
        final WebApplicationException webApplicationException = new WebApplicationException("error msg", 403);
        final Response response = exceptionMapper.toResponse(webApplicationException);
        Assert.assertThat(response.getEntity(), CoreMatchers.instanceOf(KsqlErrorMessage.class));
        final KsqlErrorMessage errorMessage = ((KsqlErrorMessage) (response.getEntity()));
        Assert.assertThat(errorMessage.getMessage(), CoreMatchers.equalTo("error msg"));
        Assert.assertThat(errorMessage.getErrorCode(), CoreMatchers.equalTo(40300));
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(403));
    }

    @Test
    public void shouldReturnCorrectResponseForUnspecificException() {
        final Response response = exceptionMapper.toResponse(new Exception("error msg"));
        Assert.assertThat(response.getEntity(), CoreMatchers.instanceOf(KsqlErrorMessage.class));
        final KsqlErrorMessage errorMessage = ((KsqlErrorMessage) (response.getEntity()));
        Assert.assertThat(errorMessage.getMessage(), CoreMatchers.equalTo("error msg"));
        Assert.assertThat(errorMessage.getErrorCode(), CoreMatchers.equalTo(ERROR_CODE_SERVER_ERROR));
        Assert.assertThat(response.getStatus(), CoreMatchers.equalTo(INTERNAL_SERVER_ERROR.getStatusCode()));
    }
}

