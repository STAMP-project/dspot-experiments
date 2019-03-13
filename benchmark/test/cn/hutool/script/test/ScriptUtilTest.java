package cn.hutool.script.test;


import cn.hutool.script.ScriptRuntimeException;
import cn.hutool.script.ScriptUtil;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import org.junit.Test;


/**
 * ???????
 *
 * @author looly
 */
public class ScriptUtilTest {
    @Test
    public void compileTest() {
        CompiledScript script = ScriptUtil.compile("print('Script test!');");
        try {
            script.eval();
        } catch (ScriptException e) {
            throw new ScriptRuntimeException(e);
        }
    }

    @Test
    public void evalTest() {
        ScriptUtil.eval("print('Script test!');");
    }
}

