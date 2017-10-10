/**
 * Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.apache.ibatis.plugin;


public class AmplPluginTest {
    @org.junit.Test
    public void mapPluginShouldInterceptGet() {
        java.util.Map map = new java.util.HashMap();
        map = ((java.util.Map) (new org.apache.ibatis.plugin.AmplPluginTest.AlwaysMapPlugin().plugin(map)));
        org.junit.Assert.assertEquals("Always", map.get("Anything"));
    }

    @org.junit.Test
    public void shouldNotInterceptToString() {
        java.util.Map map = new java.util.HashMap();
        map = ((java.util.Map) (new org.apache.ibatis.plugin.AmplPluginTest.AlwaysMapPlugin().plugin(map)));
        org.junit.Assert.assertFalse("Always".equals(map.toString()));
    }

    @org.apache.ibatis.plugin.Intercepts(value = { @org.apache.ibatis.plugin.Signature(type = java.util.Map.class, method = "get", args = { java.lang.Object.class }) })
    public static class AlwaysMapPlugin implements org.apache.ibatis.plugin.Interceptor {
        @java.lang.Override
        public java.lang.Object intercept(org.apache.ibatis.plugin.Invocation invocation) throws java.lang.Throwable {
            return "Always";
        }

        @java.lang.Override
        public java.lang.Object plugin(java.lang.Object target) {
            return org.apache.ibatis.plugin.Plugin.wrap(target, this);
        }

        @java.lang.Override
        public void setProperties(java.util.Properties properties) {
        }
    }
}

