/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.registry.support.command;


import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author chengya1
 */
public class RpcCommandUtilTest {
    @Test
    public void testPathMatch() {
        Assert.assertFalse(RpcCommandUtil.match("b*  & !bc \n | a* & !ac \t\n | c*", "bc"));
        Assert.assertFalse(RpcCommandUtil.match("b*  & !bc* \n | a* & !ac \t\n | c*", "bcd"));
        Assert.assertFalse(RpcCommandUtil.match("b*  & !bc \n | a* & !ac \t\n | c*", "ac"));
        Assert.assertFalse(RpcCommandUtil.match("b*  & !bc \n | (a* & !ac) \t\n | c*", "bc"));
        Assert.assertFalse(RpcCommandUtil.match("b*  & !bc* \n | (a* & !ac) \t\n | c*", "bcd"));
        Assert.assertFalse(RpcCommandUtil.match("b*  & !bc \n | a* & !ac \t\n | c*", "ac"));
        Assert.assertFalse(RpcCommandUtil.match("b*&!bc&!bd", "bc"));
        Assert.assertFalse(RpcCommandUtil.match("b*&!bc&!bd", "bd"));
        Assert.assertFalse(RpcCommandUtil.match("((a*&!aa)|b*)", "aa"));
        Assert.assertTrue(RpcCommandUtil.match("b*  & !bc \n | a* & !ac \t\n | c*", "ba"));
        Assert.assertTrue(RpcCommandUtil.match("b*  & !bc \n | a* & !ac \t\n | c*", "aaa"));
        Assert.assertTrue(RpcCommandUtil.match("b*  & !bc \n | a* & !ac \t\n | c*", "acc"));
        Assert.assertTrue(RpcCommandUtil.match("b*  & !bc \n | a* & !ac \t\n | c*", "cel"));
        Assert.assertTrue(RpcCommandUtil.match("b*  & !bc \n | (a* & !ac) \t\n | c*", "ba"));
        Assert.assertTrue(RpcCommandUtil.match("b*  & !bc \n | a* & !ac \t\n | c*", "aaa"));
        Assert.assertTrue(RpcCommandUtil.match("b*  & !bc \n | a* & !ac \t\n | c*", "acc"));
        Assert.assertTrue(RpcCommandUtil.match("b*  & !bc \n | a* & !ac \t\n | c*", "cel"));
        Assert.assertTrue(RpcCommandUtil.match("((b*&!bc) | (a*&!ab))", "ba"));
        Assert.assertTrue(RpcCommandUtil.match("a*", "a"));
        Assert.assertTrue(RpcCommandUtil.match("b* | (a* & !ab) | (c* & !cc)", "accc"));
    }

    @Test
    public void testRouteRuleMath() {
        Pattern p = Pattern.compile("^!?[0-9.]+\\*?$");
        Assert.assertTrue(p.matcher("10.75.0.180").find());
        Assert.assertTrue(p.matcher("!10.75.0.180").find());
        Assert.assertTrue(p.matcher("10.75.0*").find());
        Assert.assertTrue(p.matcher("10.75.0.*").find());
        Assert.assertTrue(p.matcher("!10.75.0.*").find());
        Assert.assertTrue(p.matcher("!10.75.0*").find());
        Assert.assertFalse(p.matcher("!!10.75.0.180").find());
        Assert.assertFalse(p.matcher("a").find());
        Assert.assertFalse(p.matcher("10.75.**").find());
    }

    @Test
    public void testCodec() {
        String commandString = Constants.commandString1;
        RpcCommand command = RpcCommandUtil.stringToCommand(commandString);
        Assert.assertNotNull(command);
        String temp = RpcCommandUtil.commandToString(command);
        Assert.assertEquals(commandString, temp);
    }
}

