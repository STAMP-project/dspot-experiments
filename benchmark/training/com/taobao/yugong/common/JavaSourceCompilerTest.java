package com.taobao.yugong.common;


import com.taobao.yugong.common.model.record.Record;
import com.taobao.yugong.common.utils.compile.JdkCompiler;
import com.taobao.yugong.translator.DataTranslator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Test;


/**
 *
 *
 * @author agapple 2014?2?25? ??11:38:06
 * @since 1.0.0
 */
public class JavaSourceCompilerTest {
    @Test
    public void testSimple() {
        String javasource = null;
        try {
            List<String> lines = IOUtils.readLines(Thread.currentThread().getContextClassLoader().getResourceAsStream("compiler.txt"));
            javasource = StringUtils.join(lines, "\n");
        } catch (IOException e) {
            Assert.fail(ExceptionUtils.getFullStackTrace(e));
        }
        JdkCompiler compiler = new JdkCompiler();
        Class<?> clazz = compiler.compile(javasource);
        Assert.assertEquals("com.taobao.yugong.translator.TestDataTranslator", clazz.getName());
        try {
            DataTranslator translator = ((DataTranslator) (clazz.newInstance()));
            List<Record> result = translator.translator(new ArrayList());
            Assert.assertTrue(result.isEmpty());
        } catch (InstantiationException e) {
            Assert.fail(ExceptionUtils.getFullStackTrace(e));
        } catch (IllegalAccessException e) {
            Assert.fail(ExceptionUtils.getFullStackTrace(e));
        }
    }
}

