/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.test.context.jdbc;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.jdbc.SqlConfig.TransactionMode;


/**
 * Unit tests for {@link SqlScriptsTestExecutionListener}.
 *
 * @author Sam Brannen
 * @since 4.1
 */
public class SqlScriptsTestExecutionListenerTests {
    private final SqlScriptsTestExecutionListener listener = new SqlScriptsTestExecutionListener();

    private final TestContext testContext = Mockito.mock(TestContext.class);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void missingValueAndScriptsAndStatementsAtClassLevel() throws Exception {
        Class<?> clazz = SqlScriptsTestExecutionListenerTests.MissingValueAndScriptsAndStatementsAtClassLevel.class;
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(clazz);
        BDDMockito.given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("foo"));
        assertExceptionContains(((clazz.getSimpleName()) + ".sql"));
    }

    @Test
    public void missingValueAndScriptsAndStatementsAtMethodLevel() throws Exception {
        Class<?> clazz = SqlScriptsTestExecutionListenerTests.MissingValueAndScriptsAndStatementsAtMethodLevel.class;
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(clazz);
        BDDMockito.given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("foo"));
        assertExceptionContains((((clazz.getSimpleName()) + ".foo") + ".sql"));
    }

    @Test
    public void valueAndScriptsDeclared() throws Exception {
        Class<?> clazz = SqlScriptsTestExecutionListenerTests.ValueAndScriptsDeclared.class;
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(clazz);
        BDDMockito.given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("foo"));
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(either(containsString("attribute 'value' and its alias 'scripts'")).or(containsString("attribute 'scripts' and its alias 'value'")));
        exception.expectMessage(either(containsString("values of [{foo}] and [{bar}]")).or(containsString("values of [{bar}] and [{foo}]")));
        exception.expectMessage(containsString("but only one is permitted"));
        listener.beforeTestMethod(testContext);
    }

    @Test
    public void isolatedTxModeDeclaredWithoutTxMgr() throws Exception {
        ApplicationContext ctx = Mockito.mock(ApplicationContext.class);
        BDDMockito.given(ctx.getResource(ArgumentMatchers.anyString())).willReturn(Mockito.mock(Resource.class));
        BDDMockito.given(ctx.getAutowireCapableBeanFactory()).willReturn(Mockito.mock(AutowireCapableBeanFactory.class));
        Class<?> clazz = SqlScriptsTestExecutionListenerTests.IsolatedWithoutTxMgr.class;
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(clazz);
        BDDMockito.given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("foo"));
        BDDMockito.given(testContext.getApplicationContext()).willReturn(ctx);
        assertExceptionContains("cannot execute SQL scripts using Transaction Mode [ISOLATED] without a PlatformTransactionManager");
    }

    @Test
    public void missingDataSourceAndTxMgr() throws Exception {
        ApplicationContext ctx = Mockito.mock(ApplicationContext.class);
        BDDMockito.given(ctx.getResource(ArgumentMatchers.anyString())).willReturn(Mockito.mock(Resource.class));
        BDDMockito.given(ctx.getAutowireCapableBeanFactory()).willReturn(Mockito.mock(AutowireCapableBeanFactory.class));
        Class<?> clazz = SqlScriptsTestExecutionListenerTests.MissingDataSourceAndTxMgr.class;
        BDDMockito.<Class<?>>given(testContext.getTestClass()).willReturn(clazz);
        BDDMockito.given(testContext.getTestMethod()).willReturn(clazz.getDeclaredMethod("foo"));
        BDDMockito.given(testContext.getApplicationContext()).willReturn(ctx);
        assertExceptionContains("supply at least a DataSource or PlatformTransactionManager");
    }

    // -------------------------------------------------------------------------
    @Sql
    static class MissingValueAndScriptsAndStatementsAtClassLevel {
        public void foo() {
        }
    }

    static class MissingValueAndScriptsAndStatementsAtMethodLevel {
        @Sql
        public void foo() {
        }
    }

    static class ValueAndScriptsDeclared {
        @Sql(value = "foo", scripts = "bar")
        public void foo() {
        }
    }

    static class IsolatedWithoutTxMgr {
        @Sql(scripts = "foo.sql", config = @SqlConfig(transactionMode = TransactionMode.ISOLATED))
        public void foo() {
        }
    }

    static class MissingDataSourceAndTxMgr {
        @Sql("foo.sql")
        public void foo() {
        }
    }
}

