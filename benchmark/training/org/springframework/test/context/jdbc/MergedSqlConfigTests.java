/**
 * Copyright 2002-2014 the original author or authors.
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


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link MergedSqlConfig}.
 *
 * @author Sam Brannen
 * @since 4.1
 */
public class MergedSqlConfigTests {
    @Test
    public void localConfigWithDefaults() throws Exception {
        Method method = getClass().getMethod("localConfigMethodWithDefaults");
        SqlConfig localSqlConfig = method.getAnnotation(Sql.class).config();
        MergedSqlConfig cfg = new MergedSqlConfig(localSqlConfig, getClass());
        assertDefaults(cfg);
    }

    @Test
    public void globalConfigWithDefaults() throws Exception {
        Method method = MergedSqlConfigTests.GlobalConfigWithDefaultsClass.class.getMethod("globalConfigMethod");
        SqlConfig localSqlConfig = method.getAnnotation(Sql.class).config();
        MergedSqlConfig cfg = new MergedSqlConfig(localSqlConfig, MergedSqlConfigTests.GlobalConfigWithDefaultsClass.class);
        assertDefaults(cfg);
    }

    @Test
    public void localConfigWithCustomValues() throws Exception {
        Method method = getClass().getMethod("localConfigMethodWithCustomValues");
        SqlConfig localSqlConfig = method.getAnnotation(Sql.class).config();
        MergedSqlConfig cfg = new MergedSqlConfig(localSqlConfig, getClass());
        Assert.assertNotNull(cfg);
        Assert.assertEquals("dataSource", "ds", cfg.getDataSource());
        Assert.assertEquals("transactionManager", "txMgr", cfg.getTransactionManager());
        Assert.assertEquals("transactionMode", ISOLATED, cfg.getTransactionMode());
        Assert.assertEquals("encoding", "enigma", cfg.getEncoding());
        Assert.assertEquals("separator", "\n", cfg.getSeparator());
        Assert.assertEquals("commentPrefix", "`", cfg.getCommentPrefix());
        Assert.assertEquals("blockCommentStartDelimiter", "<<", cfg.getBlockCommentStartDelimiter());
        Assert.assertEquals("blockCommentEndDelimiter", ">>", cfg.getBlockCommentEndDelimiter());
        Assert.assertEquals("errorMode", IGNORE_FAILED_DROPS, cfg.getErrorMode());
    }

    @Test
    public void localConfigWithContinueOnError() throws Exception {
        Method method = getClass().getMethod("localConfigMethodWithContinueOnError");
        SqlConfig localSqlConfig = method.getAnnotation(Sql.class).config();
        MergedSqlConfig cfg = new MergedSqlConfig(localSqlConfig, getClass());
        Assert.assertNotNull(cfg);
        Assert.assertEquals("errorMode", CONTINUE_ON_ERROR, cfg.getErrorMode());
    }

    @Test
    public void localConfigWithIgnoreFailedDrops() throws Exception {
        Method method = getClass().getMethod("localConfigMethodWithIgnoreFailedDrops");
        SqlConfig localSqlConfig = method.getAnnotation(Sql.class).config();
        MergedSqlConfig cfg = new MergedSqlConfig(localSqlConfig, getClass());
        Assert.assertNotNull(cfg);
        Assert.assertEquals("errorMode", IGNORE_FAILED_DROPS, cfg.getErrorMode());
    }

    @Test
    public void globalConfig() throws Exception {
        Method method = MergedSqlConfigTests.GlobalConfigClass.class.getMethod("globalConfigMethod");
        SqlConfig localSqlConfig = method.getAnnotation(Sql.class).config();
        MergedSqlConfig cfg = new MergedSqlConfig(localSqlConfig, MergedSqlConfigTests.GlobalConfigClass.class);
        Assert.assertNotNull(cfg);
        Assert.assertEquals("dataSource", "", cfg.getDataSource());
        Assert.assertEquals("transactionManager", "", cfg.getTransactionManager());
        Assert.assertEquals("transactionMode", INFERRED, cfg.getTransactionMode());
        Assert.assertEquals("encoding", "global", cfg.getEncoding());
        Assert.assertEquals("separator", "\n", cfg.getSeparator());
        Assert.assertEquals("commentPrefix", DEFAULT_COMMENT_PREFIX, cfg.getCommentPrefix());
        Assert.assertEquals("blockCommentStartDelimiter", DEFAULT_BLOCK_COMMENT_START_DELIMITER, cfg.getBlockCommentStartDelimiter());
        Assert.assertEquals("blockCommentEndDelimiter", DEFAULT_BLOCK_COMMENT_END_DELIMITER, cfg.getBlockCommentEndDelimiter());
        Assert.assertEquals("errorMode", IGNORE_FAILED_DROPS, cfg.getErrorMode());
    }

    @Test
    public void globalConfigWithLocalOverrides() throws Exception {
        Method method = MergedSqlConfigTests.GlobalConfigClass.class.getMethod("globalConfigWithLocalOverridesMethod");
        SqlConfig localSqlConfig = method.getAnnotation(Sql.class).config();
        MergedSqlConfig cfg = new MergedSqlConfig(localSqlConfig, MergedSqlConfigTests.GlobalConfigClass.class);
        Assert.assertNotNull(cfg);
        Assert.assertEquals("dataSource", "", cfg.getDataSource());
        Assert.assertEquals("transactionManager", "", cfg.getTransactionManager());
        Assert.assertEquals("transactionMode", INFERRED, cfg.getTransactionMode());
        Assert.assertEquals("encoding", "local", cfg.getEncoding());
        Assert.assertEquals("separator", "@@", cfg.getSeparator());
        Assert.assertEquals("commentPrefix", DEFAULT_COMMENT_PREFIX, cfg.getCommentPrefix());
        Assert.assertEquals("blockCommentStartDelimiter", DEFAULT_BLOCK_COMMENT_START_DELIMITER, cfg.getBlockCommentStartDelimiter());
        Assert.assertEquals("blockCommentEndDelimiter", DEFAULT_BLOCK_COMMENT_END_DELIMITER, cfg.getBlockCommentEndDelimiter());
        Assert.assertEquals("errorMode", CONTINUE_ON_ERROR, cfg.getErrorMode());
    }

    @SqlConfig
    public static class GlobalConfigWithDefaultsClass {
        @Sql("foo.sql")
        public void globalConfigMethod() {
        }
    }

    @SqlConfig(encoding = "global", separator = "\n", errorMode = IGNORE_FAILED_DROPS)
    public static class GlobalConfigClass {
        @Sql("foo.sql")
        public void globalConfigMethod() {
        }

        @Sql(scripts = "foo.sql", config = @SqlConfig(encoding = "local", separator = "@@", errorMode = CONTINUE_ON_ERROR))
        public void globalConfigWithLocalOverridesMethod() {
        }
    }
}

