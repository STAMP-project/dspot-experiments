/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.data.jpa.repository.query;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.spel.standard.SpelExpressionParser;


/**
 * Unit tests for {@link ExpressionBasedStringQuery}.
 *
 * @author Thomas Darimont
 * @author Oliver Gierke
 */
@RunWith(MockitoJUnitRunner.class)
public class ExpressionBasedStringQueryUnitTests {
    @Mock
    JpaEntityMetadata<?> metadata;

    static final SpelExpressionParser SPEL_PARSER = new SpelExpressionParser();

    // DATAJPA-170
    @Test
    public void shouldReturnQueryWithDomainTypeExpressionReplacedWithSimpleDomainTypeName() {
        Mockito.when(metadata.getEntityName()).thenReturn("User");
        String source = "select from #{#entityName} u where u.firstname like :firstname";
        StringQuery query = new ExpressionBasedStringQuery(source, metadata, ExpressionBasedStringQueryUnitTests.SPEL_PARSER);
        Assert.assertThat(query.getQueryString(), Matchers.is("select from User u where u.firstname like :firstname"));
    }

    // DATAJPA-424
    @Test
    public void renderAliasInExpressionQueryCorrectly() {
        Mockito.when(metadata.getEntityName()).thenReturn("User");
        StringQuery query = new ExpressionBasedStringQuery("select u from #{#entityName} u", metadata, ExpressionBasedStringQueryUnitTests.SPEL_PARSER);
        Assert.assertThat(query.getAlias(), Matchers.is("u"));
        Assert.assertThat(query.getQueryString(), Matchers.is("select u from User u"));
    }
}

