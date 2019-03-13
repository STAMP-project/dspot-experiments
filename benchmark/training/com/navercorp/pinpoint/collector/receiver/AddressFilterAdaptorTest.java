/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.collector.receiver;


import com.navercorp.pinpoint.collector.receiver.thrift.AddressFilterAdaptor;
import com.navercorp.pinpoint.common.server.util.AddressFilter;
import com.navercorp.pinpoint.common.server.util.IgnoreAddressFilter;
import java.net.InetAddress;
import java.util.Arrays;
import org.apache.hadoop.hbase.shaded.com.google.common.net.InetAddresses;
import org.jboss.netty.channel.Channel;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class AddressFilterAdaptorTest {
    private final String ignoreString = "10.0.0.1";

    private final InetAddress ignore = InetAddresses.forString(ignoreString);

    @Test
    public void accept_accept() {
        AddressFilter filter = Mockito.mock(AddressFilter.class);
        Mockito.when(filter.accept(ArgumentMatchers.any())).thenReturn(true);
        Channel ignoreChannel = mockChannel(ignore);
        AddressFilterAdaptor adaptor = new AddressFilterAdaptor(filter);
        Assert.assertTrue(adaptor.accept(ignoreChannel));
    }

    @Test
    public void accept_reject() {
        String ignoreString = "10.0.0.1";
        AddressFilter ignoreAddressFilter = new IgnoreAddressFilter(Arrays.asList(ignoreString));
        Channel ignoreChannel = mockChannel(ignore);
        AddressFilterAdaptor adaptor = new AddressFilterAdaptor(ignoreAddressFilter);
        Assert.assertFalse(adaptor.accept(ignoreChannel));
    }
}

