/**
 * Copyright 2015 the original author or authors.
 *  @https://github.com/scouter-project/scouter
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package scouter.server.http.model;


import DeltaType.BOTH;
import DeltaType.DELTA;
import java.util.HashMap;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import scouter.lang.Counter;
import scouter.server.Configure;
import scouter.server.ScouterTgMtConfig;


/**
 *
 *
 * @author Gun Lee (gunlee01@gmail.com) on 2018. 7. 22.
 */
public class InfluxSingleLineTest {
    // mem,host=vm0.us,os=aix,key=memory1 used=11656097792i,free=1467994112i,cached=0i,buffered=0i,wired=2481405952i,slab=0i,available_percent=32.152581214904785,total=17179869184i,available=5523771392i,active=8165543936i,inactive=4055777280i,used_percent=67.84741878509521 1532269780000000000
    // cpu,cpu=cpu0,host=vm0.us usage_user=23.923923923923923,usage_idle=64.46446446446447,usage_iowait=0,usage_irq=0,usage_softirq=0,usage_guest=0,usage_system=11.611611611611611,usage_nice=0,usage_steal=0,usage_guest_nice=0 1532269780000000000
    // cpu,cpu=cpu1,host=vm0.us usage_nice=0,usage_irq=0,usage_user=12.574850299401197,usage_system=5.289421157684631,usage_idle=82.13572854291417,usage_guest=0,usage_guest_nice=0,usage_iowait=0,usage_softirq=0,usage_steal=0 1532269780000000000
    // cpu,cpu=cpu2,host=vm0.us usage_system=9.3,usage_iowait=0,usage_softirq=0,usage_guest=0,usage_guest_nice=0,usage_user=24.4,usage_idle=66.3,usage_nice=0,usage_irq=0,usage_steal=0 1532269780000000000
    // cpu,cpu=cpu3,host=vm0.us usage_guest_nice=0,usage_user=12.387612387612387,usage_idle=82.51748251748252,usage_iowait=0,usage_guest=0,usage_steal=0,usage_system=5.094905094905095,usage_nice=0,usage_irq=0,usage_softirq=0 1532269780000000000
    // cpu,cpu=cpu-total,host=vm0.us usage_user=18.315842078960518,usage_iowait=0,usage_irq=0,usage_softirq=0,usage_guest=0,usage_system=7.8210894552723635,usage_idle=73.86306846576711,usage_nice=0,usage_steal=0,usage_guest_nice=0 1532269780000000000
    @Test
    public void of_test() {
        InfluxSingleLine.of("mem,host=GunMac.skbroadband,region=seoul used=11656097792i,free=1467994112i,cached=0i,buffered=0i,wired=2481405952i,slab=0i,available_percent=32.152581214904785,total=17179869184i,available=5523771392i,active=8165543936i,inactive=4055777280i,used_percent=67.84741878509521 1532269780000000000", Configure.getInstance(), System.currentTimeMillis());
    }

    // System.setProperty("input_telegraf_$cpu$_counter_mappings", "usage_user:tg-cpu-user:user cpu:%:false,usage_system:tg-cpu-sys:sys cpu:%:false");
    @Test
    public void of_test_InfluxSingleLine_can_parse_line_protocol_normal_case() {
        System.setProperty("input_telegraf_$mem$_counter_mappings", ("used:tg-mem-used" + (",free:tg-mem-free:memory free::false" + ",available_percent:tg-mem-free-pct:memory percent:%:false")));
        System.setProperty("input_telegraf_$mem$_objFamily_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_objType_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_objType_prepend_tags", "scouter_obj_type_prefix");
        System.setProperty("input_telegraf_$mem$_objName_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_host_tag", "host");
        Configure.newInstanceForTestCase();
        String protocol = "mem,host=vm0.us,os=aix,scouter_obj_type_prefix=TESTPREFIX,key=memory1" + (((" used=11656097792i,free=1467994112i,cached=0i,buffered=0i,wired=2481405952i" + ",slab=0i,available_percent=32.152581214904785,total=17179869184i,available=5523771392i") + ",active=8165543936i,inactive=4055777280i,used_percent=67.84741878509521") + " 1532269780000000000");
        InfluxSingleLine line = InfluxSingleLine.of(protocol, Configure.getInstance(), System.currentTimeMillis());
        Assert.assertEquals("TESTPREFIX_HOST-METRIC", line.getObjType());
        Assert.assertEquals("vm0.us", line.getHost());
        Assert.assertEquals((((("/" + (line.getHost())) + "/") + (ScouterTgMtConfig.getPrefix())) + "HOST-METRIC"), line.getObjName());
        TestCase.assertTrue(line.getNumberFields().keySet().contains(new CounterProtocol("tg-mem-used")));
        TestCase.assertTrue(line.getNumberFields().keySet().contains(new CounterProtocol("tg-mem-free")));
        TestCase.assertTrue(line.getNumberFields().keySet().contains(new CounterProtocol("tg-mem-free-pct")));
        for (Counter counter : line.getNumberFields().keySet()) {
            if (counter.getName().equals("tg-mem-used")) {
                Assert.assertEquals(true, counter.isTotal());
                Assert.assertEquals(line.getNumberFields().get(counter).longValue(), 11656097792L);
            } else
                if (counter.getName().equals("tg-mem-free")) {
                    Assert.assertEquals("memory free", counter.getDisplayName());
                    Assert.assertEquals("", counter.getUnit());
                    Assert.assertEquals(false, counter.isTotal());
                    Assert.assertEquals(line.getNumberFields().get(counter).longValue(), 1467994112L);
                } else
                    if (counter.getName().equals("tg-mem-free-pct")) {
                        Assert.assertEquals("memory percent", counter.getDisplayName());
                        Assert.assertEquals("%", counter.getUnit());
                        Assert.assertEquals(false, counter.isTotal());
                        Assert.assertEquals(line.getNumberFields().get(counter).floatValue(), 32.15258F, 1.0E-4);
                    }


        }
    }

    @Test
    public void of_test_InfluxSingleLine_can_parse_line_protocol_tagged_type_and_name_case() {
        System.setProperty("input_telegraf_$mem$_counter_mappings", ("used:tg-mem-used" + (",free:tg-mem-free:memory free::false" + ",available_percent:tg-mem-free-pct:memory percent:%:false")));
        System.setProperty("input_telegraf_$mem$_objFamily_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_objType_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_objType_append_tags", "os");
        System.setProperty("input_telegraf_$mem$_objName_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_objName_append_tags", "key");
        System.setProperty("input_telegraf_$mem$_host_tag", "host");
        System.setProperty("input_telegraf_$mem$_host_mappings", "vm1.us:myvm01,vm0.us:myvm0");
        Configure.newInstanceForTestCase();
        String protocol = "mem,host=vm0.us,os=aix,key=memory1" + (((" used=11656097792i,free=1467994112i,cached=0i,buffered=0i,wired=2481405952i" + ",slab=0i,available_percent=32.152581214904785,total=17179869184i,available=5523771392i") + ",active=8165543936i,inactive=4055777280i,used_percent=67.84741878509521") + " 1532269780000000000");
        InfluxSingleLine line = InfluxSingleLine.of(protocol, Configure.getInstance(), System.currentTimeMillis());
        Assert.assertEquals("HOST-METRIC_aix", line.getObjType());
        Assert.assertEquals("myvm0", line.getHost());
        Assert.assertEquals((((("/" + (line.getHost())) + "/") + (ScouterTgMtConfig.getPrefix())) + "HOST-METRIC_memory1"), line.getObjName());
    }

    @Test
    public void of_test_InfluxSingleLine_can_parse_line_protocol_with_tagged_counter() {
        Configure.newInstanceForTestCase();
        System.setProperty("input_telegraf_$cpu$_counter_mappings", ("usage_user:tg-$cpu$-user:$cpu$ user:%:false" + ",usage_system:tg-$cpu$-sys:$cpu$ sys:%:false"));
        System.setProperty("input_telegraf_$cpu$_objFamily_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_objType_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_objName_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_host_tag", "host");
        Configure.newInstanceForTestCase();
        String protocol = "cpu,cpu=cpu-total,host=vm0.us" + (((" usage_user=18.315842078960518,usage_iowait=0,usage_irq=0,usage_softirq=0" + ",usage_guest=0,usage_system=7.8210894552723635,usage_idle=73.86306846576711") + ",usage_nice=0,usage_steal=0,usage_guest_nice=0") + " 1532269780000000000");
        InfluxSingleLine line = InfluxSingleLine.of(protocol, Configure.getInstance(), System.currentTimeMillis());
        TestCase.assertTrue(line.getNumberFields().keySet().contains(new CounterProtocol("tg-*-user")));
        TestCase.assertTrue(line.getNumberFields().keySet().contains(new CounterProtocol("tg-*-sys")));
        // assertTrue(line.getNumberFields().keySet().contains(new CounterProtocol("tg-cpu-total-user")));
        // assertTrue(line.getNumberFields().keySet().contains(new CounterProtocol("tg-cpu-total-sys")));
        for (Counter counter : line.getNumberFields().keySet()) {
            if (counter.getName().equals("tg-cpu-total-user")) {
                Assert.assertEquals(line.getNumberFields().get(counter).floatValue(), 18.315842078960518, 0.001);
            } else
                if (counter.getName().equals("tg-cpu-total-sys")) {
                    Assert.assertEquals(line.getNumberFields().get(counter).floatValue(), 7.8210894552723635, 0.001);
                }

        }
    }

    @Test
    public void of_test_InfluxSingleLine_can_parse_line_protocol_with_tag_filter_unmatching_case() {
        Configure.newInstanceForTestCase();
        System.setProperty("input_telegraf_$cpu$_counter_mappings", ("usage_user:tg-$cpu$-user:$cpu$ user:%:false" + ",usage_system:tg-$cpu$-sys:$cpu$ sys:%:false"));
        System.setProperty("input_telegraf_$cpu$_objFamily_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_objType_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_objName_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_host_tag", "host");
        // tag filter => cpu:cpu-total,cpu:cpu2
        System.setProperty("input_telegraf_$cpu$_tag_filter", "cpu:cpu-total,cpu:cpu2");
        Configure.newInstanceForTestCase();
        // this protocol tag "cpu=cpu-0"
        String protocol = "cpu,cpu=cpu-0,host=vm0.us" + (((" usage_user=18.315842078960518,usage_iowait=0,usage_irq=0,usage_softirq=0" + ",usage_guest=0,usage_system=7.8210894552723635,usage_idle=73.86306846576711") + ",usage_nice=0,usage_steal=0,usage_guest_nice=0") + " 1532269780000000000");
        InfluxSingleLine line = InfluxSingleLine.of(protocol, Configure.getInstance(), System.currentTimeMillis());
        TestCase.assertNull(line);
    }

    @Test
    public void of_test_InfluxSingleLine_can_parse_line_protocol_with_tag_filter_unmatching_case_in_not_condition() {
        Configure.newInstanceForTestCase();
        System.setProperty("input_telegraf_$cpu$_counter_mappings", ("usage_user:tg-$cpu$-user:$cpu$ user:%:false" + ",usage_system:tg-$cpu$-sys:$cpu$ sys:%:false"));
        System.setProperty("input_telegraf_$cpu$_objFamily_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_objType_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_objName_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_host_tag", "host");
        // tag filter => cpu:cpu-total,cpu:cpu2
        System.setProperty("input_telegraf_$cpu$_tag_filter", "cpu:!cpu-0");
        Configure.newInstanceForTestCase();
        // this protocol tag "cpu=cpu-0"
        String protocol = "cpu,cpu=cpu-0,host=vm0.us" + (((" usage_user=18.315842078960518,usage_iowait=0,usage_irq=0,usage_softirq=0" + ",usage_guest=0,usage_system=7.8210894552723635,usage_idle=73.86306846576711") + ",usage_nice=0,usage_steal=0,usage_guest_nice=0") + " 1532269780000000000");
        InfluxSingleLine line = InfluxSingleLine.of(protocol, Configure.getInstance(), System.currentTimeMillis());
        TestCase.assertNull(line);
    }

    @Test
    public void of_test_InfluxSingleLine_can_parse_line_protocol_with_tag_filter_matching_case() {
        Configure.newInstanceForTestCase();
        System.setProperty("input_telegraf_$cpu$_counter_mappings", ("usage_user:tg-$cpu$-user:$cpu$ user:%:false" + ",usage_system:tg-$cpu$-sys:$cpu$ sys:%:false"));
        System.setProperty("input_telegraf_$cpu$_objFamily_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_objType_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_objName_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_host_tag", "host");
        // tag filter => cpu:cpu-total,cpu:cpu2
        System.setProperty("input_telegraf_$cpu$_tag_filter", "cpu:cpu-0,cpu:cpu-total");
        Configure.newInstanceForTestCase();
        // this protocol tag "cpu=cpu-0"
        String protocol = "cpu,cpu=cpu-total,host=vm0.us" + (((" usage_user=18.315842078960518,usage_iowait=0,usage_irq=0,usage_softirq=0" + ",usage_guest=0,usage_system=7.8210894552723635,usage_idle=73.86306846576711") + ",usage_nice=0,usage_steal=0,usage_guest_nice=0") + " 1532269780000000000");
        InfluxSingleLine line = InfluxSingleLine.of(protocol, Configure.getInstance(), System.currentTimeMillis());
        TestCase.assertNotNull(line);
    }

    @Test
    public void of_test_InfluxSingleLine_can_parse_line_protocol_with_tag_filter_matching_case_in_not_condition() {
        Configure.newInstanceForTestCase();
        System.setProperty("input_telegraf_$cpu$_counter_mappings", ("usage_user:tg-$cpu$-user:$cpu$ user:%:false" + ",usage_system:tg-$cpu$-sys:$cpu$ sys:%:false"));
        System.setProperty("input_telegraf_$cpu$_objFamily_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_objType_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_objName_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$cpu$_host_tag", "host");
        // tag filter => cpu:cpu-total,cpu:cpu2
        System.setProperty("input_telegraf_$cpu$_tag_filter", "cpu:!cpu-0");
        Configure.newInstanceForTestCase();
        // this protocol tag "cpu=cpu-0"
        String protocol = "cpu,cpu=cpu-total,host=vm0.us" + (((" usage_user=18.315842078960518,usage_iowait=0,usage_irq=0,usage_softirq=0" + ",usage_guest=0,usage_system=7.8210894552723635,usage_idle=73.86306846576711") + ",usage_nice=0,usage_steal=0,usage_guest_nice=0") + " 1532269780000000000");
        InfluxSingleLine line = InfluxSingleLine.of(protocol, Configure.getInstance(), System.currentTimeMillis());
        TestCase.assertNotNull(line);
    }

    @Test
    public void of_test_InfluxSingleLine_can_parse_line_protocol_with_delta_field() {
        System.setProperty("input_telegraf_$mem$_counter_mappings", ("used:tg-mem-used" + ((",&active:active:active:byte:false" + ",&&available:available:available:byte:false") + ",available_percent:tg-mem-free-pct:memory percent:%:false")));
        System.setProperty("input_telegraf_$mem$_objFamily_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_objType_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_objName_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_host_tag", "host");
        Configure.newInstanceForTestCase();
        String protocol = "mem,host=vm0.us,os=aix,key=memory1" + (((" used=11656097792i,free=1467994112i,cached=0i,buffered=0i,wired=2481405952i" + ",slab=0i,available_percent=32.152581214904785,total=17179869184i,available=5523771392i") + ",active=8165543936i,inactive=4055777280i,used_percent=67.84741878509521") + " 1532269780000000000");
        InfluxSingleLine line = InfluxSingleLine.of(protocol, Configure.getInstance(), System.currentTimeMillis());
        for (CounterProtocol counterProtocol : line.getNumberFields().keySet()) {
            HashMap<String, String> tagMap = new HashMap<String, String>();
            if (counterProtocol.getName().equals("active")) {
                Assert.assertEquals(DELTA, counterProtocol.getDeltaType());
                Assert.assertEquals(1, counterProtocol.toCounters(tagMap).size());
                TestCase.assertNull(counterProtocol.toNormalCounter(tagMap));
                TestCase.assertNotNull(counterProtocol.toDeltaCounter(tagMap));
                Counter deltaCounter = counterProtocol.toDeltaCounter(tagMap);
                Assert.assertEquals(deltaCounter.getName(), ((counterProtocol.getName()) + "_$delta"));
                Assert.assertEquals(deltaCounter.getUnit(), ((counterProtocol.getUnit()) + "/s"));
            } else
                if (counterProtocol.getName().equals("available")) {
                    Assert.assertEquals(BOTH, counterProtocol.getDeltaType());
                    Assert.assertEquals(2, counterProtocol.toCounters(tagMap).size());
                    TestCase.assertNotNull(counterProtocol.toNormalCounter(tagMap));
                    TestCase.assertNotNull(counterProtocol.toDeltaCounter(tagMap));
                    Counter normalCounter = counterProtocol.toNormalCounter(tagMap);
                    Assert.assertEquals(normalCounter.getName(), counterProtocol.getName());
                    Assert.assertEquals(normalCounter.getUnit(), counterProtocol.getUnit());
                    Counter deltaCounter = counterProtocol.toDeltaCounter(tagMap);
                    Assert.assertEquals(deltaCounter.getName(), ((counterProtocol.getName()) + "_$delta"));
                    Assert.assertEquals(deltaCounter.getUnit(), ((counterProtocol.getUnit()) + "/s"));
                }

        }
    }

    @Test
    public void of_test_InfluxSingleLine_can_parse_quoted_value() {
        System.setProperty("input_telegraf_$mem$_counter_mappings", ("used:tg-mem-used" + ((",active:active:active:byte:false" + ",available:available:available:byte:false") + ",available_percent:tg-mem-free-pct:memory percent:%:false")));
        System.setProperty("input_telegraf_$mem$_objFamily_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_objType_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_objName_base", "HOST-METRIC");
        System.setProperty("input_telegraf_$mem$_host_tag", "host");
        Configure.newInstanceForTestCase();
        String protocol = "mem,host=vm0.us,os=aix,key=memory1" + (((" used=11656097792i,free=1467994112i,quoted=\"It is quoted\",cached=0i,buffered=0i,wired=2481405952i" + ",slab=0i,available_percent=32.152581214904785,total=17179869184i,available=5523771392i") + ",active=8165543936i,inactive=4055777280i,used_percent=67.84741878509521") + " 1532269780000000000");
        InfluxSingleLine line = InfluxSingleLine.of(protocol, Configure.getInstance(), System.currentTimeMillis());
        Assert.assertEquals(line.timestampOrigin, 1.53226978E12, 1000);
    }
}

