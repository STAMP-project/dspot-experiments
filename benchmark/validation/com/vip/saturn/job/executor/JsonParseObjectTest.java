package com.vip.saturn.job.executor;


import com.vip.saturn.job.SaturnJobReturn;
import com.vip.saturn.job.executor.utils.LogbackListAppender;
import com.vip.saturn.job.shell.ScriptJobRunner;
import java.util.HashMap;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Created by xiaopeng.he on 2016/9/20.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JsonParseObjectTest {
    @Test
    public void test_A_normal() throws Exception {
        LogbackListAppender logbackListAppender = new LogbackListAppender();
        logbackListAppender.addToLogger(ScriptJobRunner.class);
        start();
        SaturnJobReturn saturnJobReturn = readSaturnJobReturn("/SaturnJobReturnNormal");
        SaturnJobReturn expect = new SaturnJobReturn(500, "hello world", 200);
        HashMap<String, String> prop = new HashMap();
        prop.put("key", "value");
        expect.setProp(prop);
        assertThat(logbackListAppender.getLastMessage()).isNull();
        assertThat(saturnJobReturn).isNotNull().isEqualToComparingFieldByField(expect);
    }

    @Test
    public void test_B_overFields() throws Exception {
        LogbackListAppender logbackListAppender = new LogbackListAppender();
        logbackListAppender.addToLogger(ScriptJobRunner.class);
        start();
        SaturnJobReturn saturnJobReturn = readSaturnJobReturn("/SaturnJobReturnMoreFields");
        SaturnJobReturn expect = new SaturnJobReturn(500, "hello world", 200);
        HashMap<String, String> prop = new HashMap();
        prop.put("key", "value");
        expect.setProp(prop);
        assertThat(logbackListAppender.getLastMessage()).isNull();
        assertThat(saturnJobReturn).isNotNull().isEqualToComparingFieldByField(expect);
    }

    /**
     * ?errorGroup????200
     */
    @Test
    public void test_C_lessFieldErrorGroup() throws Exception {
        LogbackListAppender logbackListAppender = new LogbackListAppender();
        logbackListAppender.addToLogger(ScriptJobRunner.class);
        start();
        SaturnJobReturn saturnJobReturn = readSaturnJobReturn("/SaturnJobReturnLessFieldErrorGroup");
        SaturnJobReturn expect = new SaturnJobReturn(500, "hello world", 200);
        HashMap<String, String> prop = new HashMap();
        prop.put("key", "value");
        expect.setProp(prop);
        assertThat(logbackListAppender.getLastMessage()).isNull();
        assertThat(saturnJobReturn).isNotNull().isEqualToComparingFieldByField(expect);
    }

    /**
     * ?prop?????
     */
    @Test
    public void test_D_lessFieldProp() throws Exception {
        LogbackListAppender logbackListAppender = new LogbackListAppender();
        logbackListAppender.addToLogger(ScriptJobRunner.class);
        start();
        SaturnJobReturn saturnJobReturn = readSaturnJobReturn("/SaturnJobReturnLessFieldProp");
        SaturnJobReturn expect = new SaturnJobReturn(500, "hello world", 200);
        assertThat(logbackListAppender.getLastMessage()).isNull();
        assertThat(saturnJobReturn).isNotNull().isEqualToComparingFieldByField(expect);
    }

    /**
     * ?returnCode????0
     */
    @Test
    public void test_E_lessFieldReturnCode() throws Exception {
        LogbackListAppender logbackListAppender = new LogbackListAppender();
        logbackListAppender.addToLogger(ScriptJobRunner.class);
        start();
        SaturnJobReturn saturnJobReturn = readSaturnJobReturn("/SaturnJobReturnLessFieldReturnCode");
        SaturnJobReturn expect = new SaturnJobReturn(0, "hello world", 200);
        HashMap<String, String> prop = new HashMap();
        prop.put("key", "value");
        expect.setProp(prop);
        assertThat(logbackListAppender.getLastMessage()).isNull();
        assertThat(saturnJobReturn).isNotNull().isEqualToComparingFieldByField(expect);
    }

    /**
     * ?returnMsg?????
     */
    @Test
    public void test_F_lessFieldReturnMsg() throws Exception {
        LogbackListAppender logbackListAppender = new LogbackListAppender();
        logbackListAppender.addToLogger(ScriptJobRunner.class);
        start();
        SaturnJobReturn saturnJobReturn = readSaturnJobReturn("/SaturnJobReturnLessFieldReturnMsg");
        SaturnJobReturn expect = new SaturnJobReturn(500, null, 200);
        HashMap<String, String> prop = new HashMap();
        prop.put("key", "value");
        expect.setProp(prop);
        assertThat(logbackListAppender.getLastMessage()).isNull();
        assertThat(saturnJobReturn).isNotNull().isEqualToComparingFieldByField(expect);
    }

    /**
     * ????????{}
     */
    @Test
    public void test_G_NoFields() throws Exception {
        LogbackListAppender logbackListAppender = new LogbackListAppender();
        logbackListAppender.addToLogger(ScriptJobRunner.class);
        start();
        SaturnJobReturn saturnJobReturn = readSaturnJobReturn("/SaturnJobReturnNoFields");
        SaturnJobReturn expect = new SaturnJobReturn();
        assertThat(logbackListAppender.getLastMessage()).isNull();
        assertThat(saturnJobReturn).isNotNull().isEqualToComparingFieldByField(expect);
    }

    /**
     * ????
     */
    @Test
    public void test_H_blank() throws Exception {
        LogbackListAppender logbackListAppender = new LogbackListAppender();
        logbackListAppender.addToLogger(ScriptJobRunner.class);
        start();
        SaturnJobReturn saturnJobReturn = readSaturnJobReturn("/SaturnJobReturnBlank");
        assertThat(logbackListAppender.getLastMessage()).isNull();
        assertThat(saturnJobReturn).isNull();
    }

    @Test
    public void test_I_trim() throws Exception {
        LogbackListAppender logbackListAppender = new LogbackListAppender();
        logbackListAppender.addToLogger(ScriptJobRunner.class);
        start();
        SaturnJobReturn saturnJobReturn = readSaturnJobReturn("/SaturnJobReturnTrim");
        SaturnJobReturn expect = new SaturnJobReturn(500, "hello world", 200);
        HashMap<String, String> prop = new HashMap();
        prop.put("key", "value");
        expect.setProp(prop);
        assertThat(logbackListAppender.getLastMessage()).isNull();
        assertThat(saturnJobReturn).isNotNull().isEqualToComparingFieldByField(expect);
    }
}

