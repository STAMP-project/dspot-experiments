/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.autoconfigure.jooq;


import java.sql.SQLException;
import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.SQLDialect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jdbc.BadSqlGrammarException;


/**
 * Tests for {@link JooqExceptionTranslator}
 *
 * @author Andy Wilkinson
 */
@RunWith(Parameterized.class)
public class JooqExceptionTranslatorTests {
    private final JooqExceptionTranslator exceptionTranslator = new JooqExceptionTranslator();

    private final SQLDialect dialect;

    private final SQLException sqlException;

    public JooqExceptionTranslatorTests(SQLDialect dialect, SQLException sqlException) {
        this.dialect = dialect;
        this.sqlException = sqlException;
    }

    @Test
    public void exceptionTranslation() {
        ExecuteContext context = Mockito.mock(ExecuteContext.class);
        Configuration configuration = Mockito.mock(Configuration.class);
        BDDMockito.given(context.configuration()).willReturn(configuration);
        BDDMockito.given(configuration.dialect()).willReturn(this.dialect);
        BDDMockito.given(context.sqlException()).willReturn(this.sqlException);
        this.exceptionTranslator.exception(context);
        ArgumentCaptor<RuntimeException> captor = ArgumentCaptor.forClass(RuntimeException.class);
        Mockito.verify(context).exception(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(BadSqlGrammarException.class);
    }
}

