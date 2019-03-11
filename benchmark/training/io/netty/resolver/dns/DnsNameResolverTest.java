/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.resolver.dns;


import DnsAttribute.DOMAIN_NAME;
import DnsAttribute.IP_ADDRESS;
import DnsRecord.CLASS_IN;
import DnsResponseCode.NOERROR;
import DnsSection.ANSWER;
import NetUtil.LOCALHOST4;
import NetUtil.LOCALHOST6;
import NoopDnsCache.INSTANCE;
import ResolvedAddressTypes.IPV4_ONLY;
import ResolvedAddressTypes.IPV4_PREFERRED;
import ResolvedAddressTypes.IPV6_ONLY;
import ResolvedAddressTypes.IPV6_PREFERRED;
import StringUtil.EMPTY_STRING;
import StringUtil.NEWLINE;
import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.resolver.HostsFileEntriesResolver;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.directory.server.dns.messages.DnsMessage;
import org.apache.directory.server.dns.messages.QuestionRecord;
import org.apache.directory.server.dns.messages.RecordType;
import org.apache.directory.server.dns.messages.ResourceRecord;
import org.apache.directory.server.dns.store.RecordStore;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static DefaultDnsServerAddressStreamProvider.DNS_PORT;
import static DnsNameResolver.DEFAULT_SEARCH_DOMAINS;
import static NoopDnsQueryLifecycleObserverFactory.INSTANCE;


public class DnsNameResolverTest {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(DnsNameResolver.class);

    private static final long DEFAULT_TEST_TIMEOUT_MS = 30000;

    // Using the top-100 web sites ranked in Alexa.com (Oct 2014)
    // Please use the following series of shell commands to get this up-to-date:
    // $ curl -O http://s3.amazonaws.com/alexa-static/top-1m.csv.zip
    // $ unzip -o top-1m.csv.zip top-1m.csv
    // $ head -100 top-1m.csv | cut -d, -f2 | cut -d/ -f1 | while read L; do echo '"'"$L"'",'; done > topsites.txt
    private static final Set<String> DOMAINS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("google.com", "youtube.com", "facebook.com", "baidu.com", "wikipedia.org", "yahoo.com", "reddit.com", "google.co.in", "qq.com", "amazon.com", "taobao.com", "tmall.com", "twitter.com", "vk.com", "live.com", "sohu.com", "instagram.com", "google.co.jp", "sina.com.cn", "jd.com", "weibo.com", "360.cn", "google.de", "google.co.uk", "google.com.br", "list.tmall.com", "google.ru", "google.fr", "yandex.ru", "netflix.com", "google.it", "google.com.hk", "linkedin.com", "pornhub.com", "t.co", "google.es", "twitch.tv", "alipay.com", "xvideos.com", "ebay.com", "yahoo.co.jp", "google.ca", "google.com.mx", "bing.com", "ok.ru", "imgur.com", "microsoft.com", "mail.ru", "imdb.com", "aliexpress.com", "hao123.com", "msn.com", "tumblr.com", "csdn.net", "wikia.com", "wordpress.com", "office.com", "google.com.tr", "livejasmin.com", "amazon.co.jp", "deloton.com", "apple.com", "google.com.au", "paypal.com", "google.com.tw", "bongacams.com", "popads.net", "whatsapp.com", "blogspot.com", "detail.tmall.com", "google.pl", "microsoftonline.com", "xhamster.com", "google.co.id", "github.com", "stackoverflow.com", "pinterest.com", "amazon.de", "diply.com", "amazon.co.uk", "so.com", "google.com.ar", "coccoc.com", "soso.com", "espn.com", "adobe.com", "google.com.ua", "tianya.cn", "xnxx.com", "googleusercontent.com", "savefrom.net", "google.com.pk", "amazon.in", "nicovideo.jp", "google.co.th", "dropbox.com", "thepiratebay.org", "google.com.sa", "google.com.eg", "pixnet.net", "localhost")));

    private static final Map<String, String> DOMAINS_PUNYCODE = new HashMap<String, String>();

    static {
        DnsNameResolverTest.DOMAINS_PUNYCODE.put("b?chner.de", "xn--bchner-3ya.de");
        DnsNameResolverTest.DOMAINS_PUNYCODE.put("m?ller.de", "xn--mller-kva.de");
    }

    private static final Set<String> DOMAINS_ALL;

    static {
        Set<String> all = new HashSet<String>(((DnsNameResolverTest.DOMAINS.size()) + (DnsNameResolverTest.DOMAINS_PUNYCODE.size())));
        all.addAll(DnsNameResolverTest.DOMAINS);
        all.addAll(DnsNameResolverTest.DOMAINS_PUNYCODE.values());
        DOMAINS_ALL = Collections.unmodifiableSet(all);
    }

    /**
     * The list of the domain names to exclude from {@link #testResolveAorAAAA()}.
     */
    private static final Set<String> EXCLUSIONS_RESOLVE_A = new HashSet<String>();

    static {
        Collections.addAll(DnsNameResolverTest.EXCLUSIONS_RESOLVE_A, "akamaihd.net", "googleusercontent.com", EMPTY_STRING);
    }

    /**
     * The list of the domain names to exclude from {@link #testResolveAAAA()}.
     * Unfortunately, there are only handful of domain names with IPv6 addresses.
     */
    private static final Set<String> EXCLUSIONS_RESOLVE_AAAA = new HashSet<String>();

    static {
        DnsNameResolverTest.EXCLUSIONS_RESOLVE_AAAA.addAll(DnsNameResolverTest.EXCLUSIONS_RESOLVE_A);
        DnsNameResolverTest.EXCLUSIONS_RESOLVE_AAAA.addAll(DnsNameResolverTest.DOMAINS);
        DnsNameResolverTest.EXCLUSIONS_RESOLVE_AAAA.removeAll(Arrays.asList("google.com", "facebook.com", "youtube.com", "wikipedia.org", "google.co.in", "blogspot.com", "vk.com", "google.de", "google.co.jp", "google.co.uk", "google.fr", "google.com.br", "google.ru", "google.it", "google.es", "google.com.mx", "xhamster.com", "google.ca", "google.co.id", "blogger.com", "flipkart.com", "google.com.tr", "google.com.au", "google.pl", "google.com.hk", "blogspot.in"));
    }

    /**
     * The list of the domain names to exclude from {@link #testQueryMx()}.
     */
    private static final Set<String> EXCLUSIONS_QUERY_MX = new HashSet<String>();

    static {
        Collections.addAll(DnsNameResolverTest.EXCLUSIONS_QUERY_MX, "hao123.com", "blogspot.com", "t.co", "espn.go.com", "people.com.cn", "googleusercontent.com", "blogspot.in", "localhost", EMPTY_STRING);
    }

    private static final TestDnsServer dnsServer = new TestDnsServer(DnsNameResolverTest.DOMAINS_ALL);

    private static final EventLoopGroup group = new NioEventLoopGroup(1);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testResolveAorAAAA() throws Exception {
        DnsNameResolver resolver = DnsNameResolverTest.newResolver(IPV4_PREFERRED).build();
        try {
            DnsNameResolverTest.testResolve0(resolver, DnsNameResolverTest.EXCLUSIONS_RESOLVE_A, AAAA);
        } finally {
            resolver.close();
        }
    }

    @Test
    public void testResolveAAAAorA() throws Exception {
        DnsNameResolver resolver = DnsNameResolverTest.newResolver(IPV6_PREFERRED).build();
        try {
            DnsNameResolverTest.testResolve0(resolver, DnsNameResolverTest.EXCLUSIONS_RESOLVE_A, A);
        } finally {
            resolver.close();
        }
    }

    /**
     * This test will start an second DNS test server which returns fixed results that can be easily verified as
     * originating from the second DNS test server. The resolver will put {@link DnsServerAddressStreamProvider} under
     * test to ensure that some hostnames can be directed toward both the primary and secondary DNS test servers
     * simultaneously.
     */
    @Test
    public void testNameServerCache() throws IOException, InterruptedException {
        final String overriddenIP = "12.34.12.34";
        final TestDnsServer dnsServer2 = new TestDnsServer(new RecordStore() {
            @Override
            public Set<ResourceRecord> getRecords(QuestionRecord question) {
                switch (question.getRecordType()) {
                    case A :
                        Map<String, Object> attr = new HashMap<String, Object>();
                        attr.put(IP_ADDRESS.toLowerCase(Locale.US), overriddenIP);
                        return Collections.<ResourceRecord>singleton(new TestDnsServer.TestResourceRecord(question.getDomainName(), question.getRecordType(), attr));
                    default :
                        return null;
                }
            }
        });
        dnsServer2.start();
        try {
            final Set<String> overriddenHostnames = new HashSet<String>();
            for (String name : DnsNameResolverTest.DOMAINS) {
                if (DnsNameResolverTest.EXCLUSIONS_RESOLVE_A.contains(name)) {
                    continue;
                }
                if (PlatformDependent.threadLocalRandom().nextBoolean()) {
                    overriddenHostnames.add(name);
                }
            }
            DnsNameResolver resolver = DnsNameResolverTest.newResolver(false, new DnsServerAddressStreamProvider() {
                @Override
                public DnsServerAddressStream nameServerAddressStream(String hostname) {
                    return overriddenHostnames.contains(hostname) ? DnsServerAddresses.sequential(dnsServer2.localAddress()).stream() : null;
                }
            }).build();
            try {
                final Map<String, InetAddress> resultA = DnsNameResolverTest.testResolve0(resolver, DnsNameResolverTest.EXCLUSIONS_RESOLVE_A, AAAA);
                for (Map.Entry<String, InetAddress> resolvedEntry : resultA.entrySet()) {
                    if (resolvedEntry.getValue().isLoopbackAddress()) {
                        continue;
                    }
                    if (overriddenHostnames.contains(resolvedEntry.getKey())) {
                        Assert.assertEquals(("failed to resolve " + (resolvedEntry.getKey())), overriddenIP, resolvedEntry.getValue().getHostAddress());
                    } else {
                        Assert.assertNotEquals(("failed to resolve " + (resolvedEntry.getKey())), overriddenIP, resolvedEntry.getValue().getHostAddress());
                    }
                }
            } finally {
                resolver.close();
            }
        } finally {
            stop();
        }
    }

    @Test
    public void testResolveA() throws Exception {
        DnsNameResolver resolver = // Cache for eternity
        DnsNameResolverTest.newResolver(IPV4_ONLY).ttl(Integer.MAX_VALUE, Integer.MAX_VALUE).build();
        try {
            final Map<String, InetAddress> resultA = DnsNameResolverTest.testResolve0(resolver, DnsNameResolverTest.EXCLUSIONS_RESOLVE_A, null);
            // Now, try to resolve again to see if it's cached.
            // This test works because the DNS servers usually randomizes the order of the records in a response.
            // If cached, the resolved addresses must be always same, because we reuse the same response.
            final Map<String, InetAddress> resultB = DnsNameResolverTest.testResolve0(resolver, DnsNameResolverTest.EXCLUSIONS_RESOLVE_A, null);
            // Ensure the result from the cache is identical from the uncached one.
            Assert.assertThat(resultB.size(), Matchers.is(resultA.size()));
            for (Map.Entry<String, InetAddress> e : resultA.entrySet()) {
                InetAddress expected = e.getValue();
                InetAddress actual = resultB.get(e.getKey());
                if (!(actual.equals(expected))) {
                    // Print the content of the cache when test failure is expected.
                    System.err.println(((("Cache for " + (e.getKey())) + ": ") + (resolver.resolveAll(e.getKey()).getNow())));
                }
                Assert.assertThat(actual, Matchers.is(expected));
            }
        } finally {
            resolver.close();
        }
    }

    @Test
    public void testResolveAAAA() throws Exception {
        DnsNameResolver resolver = DnsNameResolverTest.newResolver(IPV6_ONLY).build();
        try {
            DnsNameResolverTest.testResolve0(resolver, DnsNameResolverTest.EXCLUSIONS_RESOLVE_AAAA, null);
        } finally {
            resolver.close();
        }
    }

    @Test
    public void testNonCachedResolve() throws Exception {
        DnsNameResolver resolver = DnsNameResolverTest.newNonCachedResolver(IPV4_ONLY).build();
        try {
            DnsNameResolverTest.testResolve0(resolver, DnsNameResolverTest.EXCLUSIONS_RESOLVE_A, null);
        } finally {
            resolver.close();
        }
    }

    @Test(timeout = DnsNameResolverTest.DEFAULT_TEST_TIMEOUT_MS)
    public void testNonCachedResolveEmptyHostName() throws Exception {
        DnsNameResolverTest.testNonCachedResolveEmptyHostName("");
    }

    @Test(timeout = DnsNameResolverTest.DEFAULT_TEST_TIMEOUT_MS)
    public void testNonCachedResolveNullHostName() throws Exception {
        DnsNameResolverTest.testNonCachedResolveEmptyHostName(null);
    }

    @Test(timeout = DnsNameResolverTest.DEFAULT_TEST_TIMEOUT_MS)
    public void testNonCachedResolveAllEmptyHostName() throws Exception {
        DnsNameResolverTest.testNonCachedResolveAllEmptyHostName("");
    }

    @Test(timeout = DnsNameResolverTest.DEFAULT_TEST_TIMEOUT_MS)
    public void testNonCachedResolveAllNullHostName() throws Exception {
        DnsNameResolverTest.testNonCachedResolveAllEmptyHostName(null);
    }

    @Test
    public void testQueryMx() {
        DnsNameResolver resolver = DnsNameResolverTest.newResolver().build();
        try {
            Assert.assertThat(resolver.isRecursionDesired(), Matchers.is(true));
            Map<String, Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> futures = new LinkedHashMap<String, Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>>();
            for (String name : DnsNameResolverTest.DOMAINS) {
                if (DnsNameResolverTest.EXCLUSIONS_QUERY_MX.contains(name)) {
                    continue;
                }
                DnsNameResolverTest.queryMx(resolver, futures, name);
            }
            for (Map.Entry<String, Future<AddressedEnvelope<DnsResponse, InetSocketAddress>>> e : futures.entrySet()) {
                String hostname = e.getKey();
                Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> f = e.getValue().awaitUninterruptibly();
                DnsResponse response = f.getNow().content();
                Assert.assertThat(response.code(), Matchers.is(NOERROR));
                final int answerCount = response.count(ANSWER);
                final List<DnsRecord> mxList = new ArrayList<DnsRecord>(answerCount);
                for (int i = 0; i < answerCount; i++) {
                    final DnsRecord r = response.recordAt(ANSWER, i);
                    if ((r.type()) == (DnsRecordType.MX)) {
                        mxList.add(r);
                    }
                }
                Assert.assertThat(mxList.size(), Matchers.is(Matchers.greaterThan(0)));
                StringBuilder buf = new StringBuilder();
                for (DnsRecord r : mxList) {
                    ByteBuf recordContent = content();
                    buf.append(NEWLINE);
                    buf.append('\t');
                    buf.append(r.name());
                    buf.append(' ');
                    buf.append(r.type().name());
                    buf.append(' ');
                    buf.append(recordContent.readUnsignedShort());
                    buf.append(' ');
                    buf.append(DnsResolveContext.decodeDomainName(recordContent));
                }
                DnsNameResolverTest.logger.info("{} has the following MX records:{}", hostname, buf);
                response.release();
                // We only track query lifecycle if it is managed by the DnsNameResolverContext, and not direct calls
                // to query.
                DnsNameResolverTest.assertNoQueriesMade(resolver);
            }
        } finally {
            resolver.close();
        }
    }

    @Test
    public void testNegativeTtl() throws Exception {
        final DnsNameResolver resolver = DnsNameResolverTest.newResolver().negativeTtl(10).build();
        try {
            DnsNameResolverTest.resolveNonExistentDomain(resolver);
            final int size = 10000;
            final List<UnknownHostException> exceptions = new ArrayList<UnknownHostException>();
            // If negative cache works, this thread should be done really quickly.
            final Thread negativeLookupThread = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < size; i++) {
                        exceptions.add(DnsNameResolverTest.resolveNonExistentDomain(resolver));
                        if (isInterrupted()) {
                            break;
                        }
                    }
                }
            };
            negativeLookupThread.start();
            negativeLookupThread.join(DnsNameResolverTest.DEFAULT_TEST_TIMEOUT_MS);
            if (negativeLookupThread.isAlive()) {
                negativeLookupThread.interrupt();
                Assert.fail("Cached negative lookups did not finish quickly.");
            }
            Assert.assertThat(exceptions, Matchers.hasSize(size));
        } finally {
            resolver.close();
        }
    }

    @Test
    public void testResolveIp() {
        DnsNameResolver resolver = DnsNameResolverTest.newResolver().build();
        try {
            InetAddress address = resolver.resolve("10.0.0.1").syncUninterruptibly().getNow();
            Assert.assertEquals("10.0.0.1", address.getHostAddress());
            // This address is already resolved, and so we shouldn't have to query for anything.
            DnsNameResolverTest.assertNoQueriesMade(resolver);
        } finally {
            resolver.close();
        }
    }

    @Test
    public void testResolveEmptyIpv4() {
        DnsNameResolverTest.testResolve0(IPV4_ONLY, LOCALHOST4, EMPTY_STRING);
    }

    @Test
    public void testResolveEmptyIpv6() {
        DnsNameResolverTest.testResolve0(IPV6_ONLY, LOCALHOST6, EMPTY_STRING);
    }

    @Test
    public void testResolveNullIpv4() {
        DnsNameResolverTest.testResolve0(IPV4_ONLY, LOCALHOST4, null);
    }

    @Test
    public void testResolveNullIpv6() {
        DnsNameResolverTest.testResolve0(IPV6_ONLY, LOCALHOST6, null);
    }

    @Test
    public void testResolveAllEmptyIpv4() {
        DnsNameResolverTest.testResolveAll0(IPV4_ONLY, LOCALHOST4, EMPTY_STRING);
    }

    @Test
    public void testResolveAllEmptyIpv6() {
        DnsNameResolverTest.testResolveAll0(IPV6_ONLY, LOCALHOST6, EMPTY_STRING);
    }

    @Test
    public void testCNAMEResolveAllIpv4() throws IOException {
        DnsNameResolverTest.testCNAMERecursiveResolve(true);
    }

    @Test
    public void testCNAMEResolveAllIpv6() throws IOException {
        DnsNameResolverTest.testCNAMERecursiveResolve(false);
    }

    @Test
    public void testCNAMERecursiveResolveMultipleNameServersIPv4() throws IOException {
        DnsNameResolverTest.testCNAMERecursiveResolveMultipleNameServers(true);
    }

    @Test
    public void testCNAMERecursiveResolveMultipleNameServersIPv6() throws IOException {
        DnsNameResolverTest.testCNAMERecursiveResolveMultipleNameServers(false);
    }

    @Test
    public void testResolveAllNullIpv4() {
        DnsNameResolverTest.testResolveAll0(IPV4_ONLY, LOCALHOST4, null);
    }

    @Test
    public void testResolveAllNullIpv6() {
        DnsNameResolverTest.testResolveAll0(IPV6_ONLY, LOCALHOST6, null);
    }

    @Test
    public void testResolveAllMx() {
        final DnsNameResolver resolver = DnsNameResolverTest.newResolver().build();
        try {
            Assert.assertThat(resolver.isRecursionDesired(), Matchers.is(true));
            final Map<String, Future<List<DnsRecord>>> futures = new LinkedHashMap<String, Future<List<DnsRecord>>>();
            for (String name : DnsNameResolverTest.DOMAINS) {
                if (DnsNameResolverTest.EXCLUSIONS_QUERY_MX.contains(name)) {
                    continue;
                }
                futures.put(name, resolver.resolveAll(new io.netty.handler.codec.dns.DefaultDnsQuestion(name, DnsRecordType.MX)));
            }
            for (Map.Entry<String, Future<List<DnsRecord>>> e : futures.entrySet()) {
                String hostname = e.getKey();
                Future<List<DnsRecord>> f = e.getValue().awaitUninterruptibly();
                final List<DnsRecord> mxList = f.getNow();
                Assert.assertThat(mxList.size(), Matchers.is(Matchers.greaterThan(0)));
                StringBuilder buf = new StringBuilder();
                for (DnsRecord r : mxList) {
                    ByteBuf recordContent = content();
                    buf.append(NEWLINE);
                    buf.append('\t');
                    buf.append(r.name());
                    buf.append(' ');
                    buf.append(r.type().name());
                    buf.append(' ');
                    buf.append(recordContent.readUnsignedShort());
                    buf.append(' ');
                    buf.append(DnsResolveContext.decodeDomainName(recordContent));
                    ReferenceCountUtil.release(r);
                }
                DnsNameResolverTest.logger.info("{} has the following MX records:{}", hostname, buf);
            }
        } finally {
            resolver.close();
        }
    }

    @Test
    public void testResolveAllHostsFile() {
        final DnsNameResolver resolver = channelType(NioDatagramChannel.class).hostsFileEntriesResolver(new HostsFileEntriesResolver() {
            @Override
            public InetAddress address(String inetHost, ResolvedAddressTypes resolvedAddressTypes) {
                if ("foo.com.".equals(inetHost)) {
                    try {
                        return InetAddress.getByAddress("foo.com", new byte[]{ 1, 2, 3, 4 });
                    } catch (UnknownHostException e) {
                        throw new Error(e);
                    }
                }
                return null;
            }
        }).build();
        final List<DnsRecord> records = resolver.resolveAll(new io.netty.handler.codec.dns.DefaultDnsQuestion("foo.com.", A)).syncUninterruptibly().getNow();
        Assert.assertThat(records, Matchers.<DnsRecord>hasSize(1));
        Assert.assertThat(records.get(0), Matchers.<DnsRecord>instanceOf(DnsRawRecord.class));
        final DnsRawRecord record = ((DnsRawRecord) (records.get(0)));
        final ByteBuf content = record.content();
        Assert.assertThat(record.name(), Matchers.is("foo.com."));
        Assert.assertThat(record.dnsClass(), Matchers.is(CLASS_IN));
        Assert.assertThat(record.type(), Matchers.is(A));
        Assert.assertThat(content.readableBytes(), Matchers.is(4));
        Assert.assertThat(content.readInt(), Matchers.is(16909060));
        record.release();
    }

    @Test
    public void testResolveDecodeUnicode() {
        DnsNameResolverTest.testResolveUnicode(true);
    }

    @Test
    public void testResolveNotDecodeUnicode() {
        DnsNameResolverTest.testResolveUnicode(false);
    }

    @Test(timeout = DnsNameResolverTest.DEFAULT_TEST_TIMEOUT_MS)
    public void secondDnsServerShouldBeUsedBeforeCNAMEFirstServerNotStarted() throws IOException {
        DnsNameResolverTest.secondDnsServerShouldBeUsedBeforeCNAME(false);
    }

    @Test(timeout = DnsNameResolverTest.DEFAULT_TEST_TIMEOUT_MS)
    public void secondDnsServerShouldBeUsedBeforeCNAMEFirstServerFailResolve() throws IOException {
        DnsNameResolverTest.secondDnsServerShouldBeUsedBeforeCNAME(true);
    }

    @Test(timeout = DnsNameResolverTest.DEFAULT_TEST_TIMEOUT_MS)
    public void aAndAAAAQueryShouldTryFirstDnsServerBeforeSecond() throws IOException {
        final String knownHostName = "netty.io";
        final TestDnsServer dnsServer1 = new TestDnsServer(Collections.singleton("notnetty.com"));
        final TestDnsServer dnsServer2 = new TestDnsServer(Collections.singleton(knownHostName));
        DnsNameResolver resolver = null;
        try {
            dnsServer1.start();
            dnsServer2.start();
            DnsNameResolverTest.TestRecursiveCacheDnsQueryLifecycleObserverFactory lifecycleObserverFactory = new DnsNameResolverTest.TestRecursiveCacheDnsQueryLifecycleObserverFactory();
            DnsNameResolverBuilder builder = channelType(NioDatagramChannel.class).optResourceEnabled(false).ndots(1);
            builder.nameServerProvider(new SequentialDnsServerAddressStreamProvider(dnsServer1.localAddress(), dnsServer2.localAddress()));
            resolver = builder.build();
            Assert.assertNotNull(resolver.resolve(knownHostName).syncUninterruptibly().getNow());
            DnsNameResolverTest.TestDnsQueryLifecycleObserver observer = lifecycleObserverFactory.observers.poll();
            Assert.assertNotNull(observer);
            Assert.assertEquals(1, lifecycleObserverFactory.observers.size());
            Assert.assertEquals(2, observer.events.size());
            DnsNameResolverTest.QueryWrittenEvent writtenEvent = ((DnsNameResolverTest.QueryWrittenEvent) (observer.events.poll()));
            Assert.assertEquals(dnsServer1.localAddress(), writtenEvent.dnsServerAddress);
            DnsNameResolverTest.QueryFailedEvent failedEvent = ((DnsNameResolverTest.QueryFailedEvent) (observer.events.poll()));
            observer = lifecycleObserverFactory.observers.poll();
            Assert.assertEquals(2, observer.events.size());
            writtenEvent = ((DnsNameResolverTest.QueryWrittenEvent) (observer.events.poll()));
            Assert.assertEquals(dnsServer2.localAddress(), writtenEvent.dnsServerAddress);
            DnsNameResolverTest.QuerySucceededEvent succeededEvent = ((DnsNameResolverTest.QuerySucceededEvent) (observer.events.poll()));
        } finally {
            if (resolver != null) {
                resolver.close();
            }
            stop();
            stop();
        }
    }

    @Test
    public void testRecursiveResolveNoCache() throws Exception {
        DnsNameResolverTest.testRecursiveResolveCache(false);
    }

    @Test
    public void testRecursiveResolveCache() throws Exception {
        DnsNameResolverTest.testRecursiveResolveCache(true);
    }

    @Test
    public void testIpv4PreferredWhenIpv6First() throws Exception {
        DnsNameResolverTest.testResolvesPreferredWhenNonPreferredFirst0(IPV4_PREFERRED);
    }

    @Test
    public void testIpv6PreferredWhenIpv4First() throws Exception {
        DnsNameResolverTest.testResolvesPreferredWhenNonPreferredFirst0(IPV6_PREFERRED);
    }

    @Test
    public void testFollowNsRedirectsNoopCaches() throws Exception {
        testFollowNsRedirects(INSTANCE, NoopAuthoritativeDnsServerCache.INSTANCE, false);
    }

    @Test
    public void testFollowNsRedirectsNoopDnsCache() throws Exception {
        testFollowNsRedirects(INSTANCE, new DefaultAuthoritativeDnsServerCache(), false);
    }

    @Test
    public void testFollowNsRedirectsNoopAuthoritativeDnsServerCache() throws Exception {
        testFollowNsRedirects(new DefaultDnsCache(), NoopAuthoritativeDnsServerCache.INSTANCE, false);
    }

    @Test
    public void testFollowNsRedirectsDefaultCaches() throws Exception {
        testFollowNsRedirects(new DefaultDnsCache(), new DefaultAuthoritativeDnsServerCache(), false);
    }

    @Test
    public void testFollowNsRedirectAndTrySecondNsOnTimeout() throws Exception {
        testFollowNsRedirects(INSTANCE, NoopAuthoritativeDnsServerCache.INSTANCE, true);
    }

    @Test
    public void testFollowNsRedirectAndTrySecondNsOnTimeoutDefaultCaches() throws Exception {
        testFollowNsRedirects(new DefaultDnsCache(), new DefaultAuthoritativeDnsServerCache(), true);
    }

    @Test
    public void testMultipleAdditionalRecordsForSameNSRecord() throws Exception {
        DnsNameResolverTest.testMultipleAdditionalRecordsForSameNSRecord(false);
    }

    @Test
    public void testMultipleAdditionalRecordsForSameNSRecordReordered() throws Exception {
        DnsNameResolverTest.testMultipleAdditionalRecordsForSameNSRecord(true);
    }

    @Test
    public void testNSRecordsFromCache() throws Exception {
        final String domain = "netty.io";
        final String hostname = "test.netty.io";
        final String ns0Name = ("ns0." + domain) + '.';
        final String ns1Name = ("ns1." + domain) + '.';
        final String ns2Name = ("ns2." + domain) + '.';
        final InetSocketAddress ns0Address = new InetSocketAddress(InetAddress.getByAddress(ns0Name, new byte[]{ 10, 1, 0, 1 }), DNS_PORT);
        final InetSocketAddress ns1Address = new InetSocketAddress(InetAddress.getByAddress(ns1Name, new byte[]{ 10, 0, 0, 1 }), DNS_PORT);
        final InetSocketAddress ns2Address = new InetSocketAddress(InetAddress.getByAddress(ns1Name, new byte[]{ 10, 0, 0, 2 }), DNS_PORT);
        final InetSocketAddress ns3Address = new InetSocketAddress(InetAddress.getByAddress(ns1Name, new byte[]{ 10, 0, 0, 3 }), DNS_PORT);
        final InetSocketAddress ns4Address = new InetSocketAddress(InetAddress.getByAddress(ns1Name, new byte[]{ 10, 0, 0, 4 }), DNS_PORT);
        final InetSocketAddress ns5Address = new InetSocketAddress(InetAddress.getByAddress(ns2Name, new byte[]{ 10, 0, 0, 5 }), DNS_PORT);
        TestDnsServer redirectServer = new TestDnsServer(new HashSet<String>(Arrays.asList(hostname, ns1Name))) {
            @Override
            protected DnsMessage filterMessage(DnsMessage message) {
                for (QuestionRecord record : message.getQuestionRecords()) {
                    if (record.getDomainName().equals(hostname)) {
                        message.getAdditionalRecords().clear();
                        message.getAnswerRecords().clear();
                        message.getAuthorityRecords().add(TestDnsServer.newNsRecord(domain, ns0Name));
                        message.getAuthorityRecords().add(TestDnsServer.newNsRecord(domain, ns1Name));
                        message.getAuthorityRecords().add(TestDnsServer.newNsRecord(domain, ns2Name));
                        message.getAdditionalRecords().add(newARecord(ns0Address));
                        message.getAdditionalRecords().add(newARecord(ns5Address));
                        return message;
                    }
                }
                return message;
            }

            private ResourceRecord newARecord(InetSocketAddress address) {
                return TestDnsServer.newARecord(address.getHostName(), address.getAddress().getHostAddress());
            }
        };
        redirectServer.start();
        EventLoopGroup group = new NioEventLoopGroup(1);
        final List<InetSocketAddress> cached = new CopyOnWriteArrayList<InetSocketAddress>();
        final AuthoritativeDnsServerCache authoritativeDnsServerCache = new AuthoritativeDnsServerCache() {
            @Override
            public DnsServerAddressStream get(String hostname) {
                return null;
            }

            @Override
            public void cache(String hostname, InetSocketAddress address, long originalTtl, EventLoop loop) {
                cached.add(address);
            }

            @Override
            public void clear() {
                // NOOP
            }

            @Override
            public boolean clear(String hostname) {
                return false;
            }
        };
        EventLoop loop = group.next();
        DefaultDnsCache cache = new DefaultDnsCache();
        cache.cache(ns1Name, null, ns1Address.getAddress(), 10000, loop);
        cache.cache(ns1Name, null, ns2Address.getAddress(), 10000, loop);
        cache.cache(ns1Name, null, ns3Address.getAddress(), 10000, loop);
        cache.cache(ns1Name, null, ns4Address.getAddress(), 10000, loop);
        final AtomicReference<DnsServerAddressStream> redirectedRef = new AtomicReference<DnsServerAddressStream>();
        final DnsNameResolver resolver = new DnsNameResolver(loop, new io.netty.channel.ReflectiveChannelFactory<DatagramChannel>(NioDatagramChannel.class), cache, authoritativeDnsServerCache, INSTANCE, 2000, ResolvedAddressTypes.IPV4_ONLY, true, 10, true, 4096, false, HostsFileEntriesResolver.DEFAULT, new SingletonDnsServerAddressStreamProvider(redirectServer.localAddress()), DEFAULT_SEARCH_DOMAINS, 0, true) {
            @Override
            protected DnsServerAddressStream newRedirectDnsServerStream(String hostname, List<InetSocketAddress> nameservers) {
                DnsServerAddressStream stream = new SequentialDnsServerAddressStream(nameservers, 0);
                redirectedRef.set(stream);
                return stream;
            }
        };
        try {
            Throwable cause = resolver.resolveAll(hostname).await().cause();
            Assert.assertTrue((cause instanceof UnknownHostException));
            DnsServerAddressStream redirected = redirectedRef.get();
            Assert.assertNotNull(redirected);
            Assert.assertEquals(6, redirected.size());
            Assert.assertEquals(3, cached.size());
            // The redirected addresses should have been retrieven from the DnsCache if not resolved, so these are
            // fully resolved.
            Assert.assertEquals(ns0Address, redirected.next());
            Assert.assertEquals(ns1Address, redirected.next());
            Assert.assertEquals(ns2Address, redirected.next());
            Assert.assertEquals(ns3Address, redirected.next());
            Assert.assertEquals(ns4Address, redirected.next());
            Assert.assertEquals(ns5Address, redirected.next());
            // As this address was supplied as ADDITIONAL we should put it resolved into the cache.
            Assert.assertEquals(ns0Address, cached.get(0));
            Assert.assertEquals(ns5Address, cached.get(1));
            // We should have put the unresolved address in the AuthoritativeDnsServerCache (but only 1 time)
            Assert.assertEquals(DnsNameResolverTest.unresolved(ns1Address), cached.get(2));
        } finally {
            resolver.close();
            group.shutdownGracefully(0, 0, TimeUnit.SECONDS);
            stop();
        }
    }

    private static final class TestRecursiveCacheDnsQueryLifecycleObserverFactory implements DnsQueryLifecycleObserverFactory {
        final Queue<DnsNameResolverTest.TestDnsQueryLifecycleObserver> observers = new ConcurrentLinkedQueue<DnsNameResolverTest.TestDnsQueryLifecycleObserver>();

        @Override
        public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion question) {
            DnsNameResolverTest.TestDnsQueryLifecycleObserver observer = new DnsNameResolverTest.TestDnsQueryLifecycleObserver(question);
            observers.add(observer);
            return observer;
        }
    }

    private static final class QueryWrittenEvent {
        final InetSocketAddress dnsServerAddress;

        QueryWrittenEvent(InetSocketAddress dnsServerAddress) {
            this.dnsServerAddress = dnsServerAddress;
        }
    }

    private static final class QueryCancelledEvent {
        final int queriesRemaining;

        QueryCancelledEvent(int queriesRemaining) {
            this.queriesRemaining = queriesRemaining;
        }
    }

    private static final class QueryRedirectedEvent {
        final List<InetSocketAddress> nameServers;

        QueryRedirectedEvent(List<InetSocketAddress> nameServers) {
            this.nameServers = nameServers;
        }
    }

    private static final class QueryCnamedEvent {
        final DnsQuestion question;

        QueryCnamedEvent(DnsQuestion question) {
            this.question = question;
        }
    }

    private static final class QueryNoAnswerEvent {
        final DnsResponseCode code;

        QueryNoAnswerEvent(DnsResponseCode code) {
            this.code = code;
        }
    }

    private static final class QueryFailedEvent {
        final Throwable cause;

        QueryFailedEvent(Throwable cause) {
            this.cause = cause;
        }
    }

    private static final class QuerySucceededEvent {}

    private static final class TestDnsQueryLifecycleObserver implements DnsQueryLifecycleObserver {
        final Queue<Object> events = new ArrayDeque<Object>();

        final DnsQuestion question;

        TestDnsQueryLifecycleObserver(DnsQuestion question) {
            this.question = question;
        }

        @Override
        public void queryWritten(InetSocketAddress dnsServerAddress, ChannelFuture future) {
            events.add(new DnsNameResolverTest.QueryWrittenEvent(dnsServerAddress));
        }

        @Override
        public void queryCancelled(int queriesRemaining) {
            events.add(new DnsNameResolverTest.QueryCancelledEvent(queriesRemaining));
        }

        @Override
        public DnsQueryLifecycleObserver queryRedirected(List<InetSocketAddress> nameServers) {
            events.add(new DnsNameResolverTest.QueryRedirectedEvent(nameServers));
            return this;
        }

        @Override
        public DnsQueryLifecycleObserver queryCNAMEd(DnsQuestion cnameQuestion) {
            events.add(new DnsNameResolverTest.QueryCnamedEvent(cnameQuestion));
            return this;
        }

        @Override
        public DnsQueryLifecycleObserver queryNoAnswer(DnsResponseCode code) {
            events.add(new DnsNameResolverTest.QueryNoAnswerEvent(code));
            return this;
        }

        @Override
        public void queryFailed(Throwable cause) {
            events.add(new DnsNameResolverTest.QueryFailedEvent(cause));
        }

        @Override
        public void querySucceed() {
            events.add(new DnsNameResolverTest.QuerySucceededEvent());
        }
    }

    private static final class TestAuthoritativeDnsServerCache implements AuthoritativeDnsServerCache {
        final AuthoritativeDnsServerCache cache;

        final Map<String, DnsServerAddressStream> cacheHits = new HashMap<String, DnsServerAddressStream>();

        TestAuthoritativeDnsServerCache(AuthoritativeDnsServerCache cache) {
            this.cache = cache;
        }

        @Override
        public void clear() {
            cache.clear();
        }

        @Override
        public boolean clear(String hostname) {
            return cache.clear(hostname);
        }

        @Override
        public DnsServerAddressStream get(String hostname) {
            DnsServerAddressStream cached = cache.get(hostname);
            if (cached != null) {
                cacheHits.put(hostname, cached.duplicate());
            }
            return cached;
        }

        @Override
        public void cache(String hostname, InetSocketAddress address, long originalTtl, EventLoop loop) {
            cache.cache(hostname, address, originalTtl, loop);
        }
    }

    private static final class TestDnsCache implements DnsCache {
        final DnsCache cache;

        final Map<String, List<? extends DnsCacheEntry>> cacheHits = new HashMap<String, List<? extends DnsCacheEntry>>();

        TestDnsCache(DnsCache cache) {
            this.cache = cache;
        }

        @Override
        public void clear() {
            cache.clear();
        }

        @Override
        public boolean clear(String hostname) {
            return cache.clear(hostname);
        }

        @Override
        public List<? extends DnsCacheEntry> get(String hostname, DnsRecord[] additionals) {
            List<? extends DnsCacheEntry> cached = cache.get(hostname, additionals);
            cacheHits.put(hostname, cached);
            return cached;
        }

        @Override
        public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, InetAddress address, long originalTtl, EventLoop loop) {
            return cache.cache(hostname, additionals, address, originalTtl, loop);
        }

        @Override
        public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, Throwable cause, EventLoop loop) {
            return cache.cache(hostname, additionals, cause, loop);
        }
    }

    private static class RedirectingTestDnsServer extends TestDnsServer {
        private final String dnsAddress;

        private final String domain;

        RedirectingTestDnsServer(String domain, String dnsAddress) {
            super(Collections.singleton(domain));
            this.domain = domain;
            this.dnsAddress = dnsAddress;
        }

        @Override
        protected DnsMessage filterMessage(DnsMessage message) {
            // Clear the answers as we want to add our own stuff to test dns redirects.
            message.getAnswerRecords().clear();
            message.getAuthorityRecords().clear();
            message.getAdditionalRecords().clear();
            String name = domain;
            for (int i = 0; ; i++) {
                int idx = name.indexOf('.');
                if (idx <= 0) {
                    break;
                }
                name = name.substring((idx + 1));// skip the '.' as well.

                String dnsName = (("dns" + idx) + '.') + (domain);
                message.getAuthorityRecords().add(TestDnsServer.newNsRecord(name, dnsName));
                message.getAdditionalRecords().add(TestDnsServer.newARecord(dnsName, (i == 0 ? dnsAddress : "1.2.3." + idx)));
                // Add an unresolved NS record (with no additionals as well)
                message.getAuthorityRecords().add(TestDnsServer.newNsRecord(name, ("unresolved." + dnsName)));
            }
            return message;
        }
    }

    @Test(timeout = 3000)
    public void testTimeoutNotCached() {
        DnsCache cache = new DnsCache() {
            @Override
            public void clear() {
                // NOOP
            }

            @Override
            public boolean clear(String hostname) {
                return false;
            }

            @Override
            public List<? extends DnsCacheEntry> get(String hostname, DnsRecord[] additionals) {
                return Collections.emptyList();
            }

            @Override
            public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, InetAddress address, long originalTtl, EventLoop loop) {
                Assert.fail("Should not be cached");
                return null;
            }

            @Override
            public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, Throwable cause, EventLoop loop) {
                Assert.fail("Should not be cached");
                return null;
            }
        };
        DnsNameResolverBuilder builder = DnsNameResolverTest.newResolver();
        builder.queryTimeoutMillis(100).authoritativeDnsServerCache(cache).resolveCache(cache).nameServerProvider(new SingletonDnsServerAddressStreamProvider(new InetSocketAddress(NetUtil.LOCALHOST, 12345)));
        DnsNameResolver resolver = builder.build();
        Future<InetAddress> result = resolver.resolve("doesnotexist.netty.io").awaitUninterruptibly();
        Throwable cause = result.cause();
        Assert.assertTrue((cause instanceof UnknownHostException));
        Assert.assertTrue(((cause.getCause()) instanceof DnsNameResolverTimeoutException));
        Assert.assertTrue(DnsNameResolver.isTimeoutError(cause));
        Assert.assertTrue(DnsNameResolver.isTransportOrTimeoutError(cause));
        resolver.close();
    }

    @Test
    public void testDnsNameResolverBuilderCopy() {
        ChannelFactory<DatagramChannel> channelFactory = new io.netty.channel.ReflectiveChannelFactory<DatagramChannel>(NioDatagramChannel.class);
        DnsNameResolverBuilder builder = new DnsNameResolverBuilder(DnsNameResolverTest.group.next()).channelFactory(channelFactory);
        DnsNameResolverBuilder copiedBuilder = builder.copy();
        // change channel factory does not propagate to previously made copy
        ChannelFactory<DatagramChannel> newChannelFactory = new io.netty.channel.ReflectiveChannelFactory<DatagramChannel>(NioDatagramChannel.class);
        builder.channelFactory(newChannelFactory);
        Assert.assertEquals(channelFactory, copiedBuilder.channelFactory());
        Assert.assertEquals(newChannelFactory, builder.channelFactory());
    }

    @Test
    public void testFollowCNAMEEvenIfARecordIsPresent() throws IOException {
        TestDnsServer dnsServer2 = new TestDnsServer(new RecordStore() {
            @Override
            public Set<ResourceRecord> getRecords(QuestionRecord question) {
                if (question.getDomainName().equals("cname.netty.io")) {
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put(IP_ADDRESS.toLowerCase(), "10.0.0.99");
                    return Collections.<ResourceRecord>singleton(new TestDnsServer.TestResourceRecord(question.getDomainName(), RecordType.A, map1));
                } else {
                    Set<ResourceRecord> records = new LinkedHashSet<ResourceRecord>(2);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(DOMAIN_NAME.toLowerCase(), "cname.netty.io");
                    records.add(new TestDnsServer.TestResourceRecord(question.getDomainName(), RecordType.CNAME, map));
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put(IP_ADDRESS.toLowerCase(), "10.0.0.2");
                    records.add(new TestDnsServer.TestResourceRecord(question.getDomainName(), RecordType.A, map1));
                    return records;
                }
            }
        });
        dnsServer2.start();
        DnsNameResolver resolver = null;
        try {
            DnsNameResolverBuilder builder = DnsNameResolverTest.newResolver().recursionDesired(true).resolvedAddressTypes(IPV4_ONLY).maxQueriesPerResolve(16).nameServerProvider(new SingletonDnsServerAddressStreamProvider(dnsServer2.localAddress()));
            resolver = builder.build();
            List<InetAddress> resolvedAddresses = resolver.resolveAll("somehost.netty.io").syncUninterruptibly().getNow();
            Assert.assertEquals(2, resolvedAddresses.size());
            Assert.assertTrue(resolvedAddresses.contains(InetAddress.getByAddress(new byte[]{ 10, 0, 0, 99 })));
            Assert.assertTrue(resolvedAddresses.contains(InetAddress.getByAddress(new byte[]{ 10, 0, 0, 2 })));
        } finally {
            stop();
            if (resolver != null) {
                resolver.close();
            }
        }
    }

    @Test
    public void testFollowCNAMELoop() throws IOException {
        expectedException.expect(UnknownHostException.class);
        TestDnsServer dnsServer2 = new TestDnsServer(new RecordStore() {
            @Override
            public Set<ResourceRecord> getRecords(QuestionRecord question) {
                Set<ResourceRecord> records = new LinkedHashSet<ResourceRecord>(4);
                records.add(new TestDnsServer.TestResourceRecord(("x." + (question.getDomainName())), RecordType.A, Collections.<String, Object>singletonMap(IP_ADDRESS.toLowerCase(), "10.0.0.99")));
                records.add(new TestDnsServer.TestResourceRecord("cname2.netty.io", RecordType.CNAME, Collections.<String, Object>singletonMap(DOMAIN_NAME.toLowerCase(), "cname.netty.io")));
                records.add(new TestDnsServer.TestResourceRecord("cname.netty.io", RecordType.CNAME, Collections.<String, Object>singletonMap(DOMAIN_NAME.toLowerCase(), "cname2.netty.io")));
                records.add(new TestDnsServer.TestResourceRecord(question.getDomainName(), RecordType.CNAME, Collections.<String, Object>singletonMap(DOMAIN_NAME.toLowerCase(), "cname.netty.io")));
                return records;
            }
        });
        dnsServer2.start();
        DnsNameResolver resolver = null;
        try {
            DnsNameResolverBuilder builder = DnsNameResolverTest.newResolver().recursionDesired(false).resolvedAddressTypes(IPV4_ONLY).maxQueriesPerResolve(16).nameServerProvider(new SingletonDnsServerAddressStreamProvider(dnsServer2.localAddress()));
            resolver = builder.build();
            resolver.resolveAll("somehost.netty.io").syncUninterruptibly().getNow();
        } finally {
            stop();
            if (resolver != null) {
                resolver.close();
            }
        }
    }

    @Test
    public void testSearchDomainQueryFailureForSingleAddressTypeCompletes() {
        expectedException.expect(UnknownHostException.class);
        testSearchDomainQueryFailureCompletes(IPV4_ONLY);
    }

    @Test
    public void testSearchDomainQueryFailureForMultipleAddressTypeCompletes() {
        expectedException.expect(UnknownHostException.class);
        testSearchDomainQueryFailureCompletes(IPV4_PREFERRED);
    }

    @Test(timeout = 2000L)
    public void testCachesClearedOnClose() throws Exception {
        final CountDownLatch resolveLatch = new CountDownLatch(1);
        final CountDownLatch authoritativeLatch = new CountDownLatch(1);
        DnsNameResolver resolver = DnsNameResolverTest.newResolver().resolveCache(new DnsCache() {
            @Override
            public void clear() {
                resolveLatch.countDown();
            }

            @Override
            public boolean clear(String hostname) {
                return false;
            }

            @Override
            public List<? extends DnsCacheEntry> get(String hostname, DnsRecord[] additionals) {
                return null;
            }

            @Override
            public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, InetAddress address, long originalTtl, EventLoop loop) {
                return null;
            }

            @Override
            public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, Throwable cause, EventLoop loop) {
                return null;
            }
        }).authoritativeDnsServerCache(new DnsCache() {
            @Override
            public void clear() {
                authoritativeLatch.countDown();
            }

            @Override
            public boolean clear(String hostname) {
                return false;
            }

            @Override
            public List<? extends DnsCacheEntry> get(String hostname, DnsRecord[] additionals) {
                return null;
            }

            @Override
            public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, InetAddress address, long originalTtl, EventLoop loop) {
                return null;
            }

            @Override
            public DnsCacheEntry cache(String hostname, DnsRecord[] additionals, Throwable cause, EventLoop loop) {
                return null;
            }
        }).build();
        resolver.close();
        resolveLatch.await();
        authoritativeLatch.await();
    }

    @Test
    public void testResolveACachedWithDot() {
        final DnsCache cache = new DefaultDnsCache();
        DnsNameResolver resolver = DnsNameResolverTest.newResolver(IPV4_ONLY).resolveCache(cache).build();
        try {
            String domain = DnsNameResolverTest.DOMAINS.iterator().next();
            String domainWithDot = domain + '.';
            resolver.resolve(domain).syncUninterruptibly();
            List<? extends DnsCacheEntry> cached = cache.get(domain, null);
            List<? extends DnsCacheEntry> cached2 = cache.get(domainWithDot, null);
            Assert.assertEquals(1, cached.size());
            Assert.assertSame(cached, cached2);
        } finally {
            resolver.close();
        }
    }

    @Test
    public void testResolveACachedWithDotSearchDomain() throws Exception {
        final DnsNameResolverTest.TestDnsCache cache = new DnsNameResolverTest.TestDnsCache(new DefaultDnsCache());
        TestDnsServer server = new TestDnsServer(Collections.singleton("test.netty.io"));
        server.start();
        DnsNameResolver resolver = DnsNameResolverTest.newResolver(IPV4_ONLY).searchDomains(Collections.singletonList("netty.io")).nameServerProvider(new SingletonDnsServerAddressStreamProvider(server.localAddress())).resolveCache(cache).build();
        try {
            resolver.resolve("test").syncUninterruptibly();
            Assert.assertNull(cache.cacheHits.get("test.netty.io"));
            List<? extends DnsCacheEntry> cached = cache.cache.get("test.netty.io", null);
            List<? extends DnsCacheEntry> cached2 = cache.cache.get("test.netty.io.", null);
            Assert.assertEquals(1, cached.size());
            Assert.assertSame(cached, cached2);
            resolver.resolve("test").syncUninterruptibly();
            List<? extends DnsCacheEntry> entries = cache.cacheHits.get("test.netty.io");
            Assert.assertFalse(entries.isEmpty());
        } finally {
            resolver.close();
            stop();
        }
    }

    @Test
    public void testChannelFactoryException() {
        final IllegalStateException exception = new IllegalStateException();
        try {
            DnsNameResolverTest.newResolver().channelFactory(new ChannelFactory<DatagramChannel>() {
                @Override
                public DatagramChannel newChannel() {
                    throw exception;
                }
            }).build();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertSame(exception, e);
        }
    }

    @Test
    public void testCNameCached() throws Exception {
        final Map<String, String> cache = new ConcurrentHashMap<String, String>();
        final AtomicInteger cnameQueries = new AtomicInteger();
        final AtomicInteger aQueries = new AtomicInteger();
        TestDnsServer dnsServer2 = new TestDnsServer(new RecordStore() {
            @Override
            public Set<ResourceRecord> getRecords(QuestionRecord question) {
                if ("cname.netty.io".equals(question.getDomainName())) {
                    aQueries.incrementAndGet();
                    return Collections.<ResourceRecord>singleton(new TestDnsServer.TestResourceRecord(question.getDomainName(), RecordType.A, Collections.<String, Object>singletonMap(IP_ADDRESS.toLowerCase(), "10.0.0.99")));
                }
                if ("x.netty.io".equals(question.getDomainName())) {
                    cnameQueries.incrementAndGet();
                    return Collections.<ResourceRecord>singleton(new TestDnsServer.TestResourceRecord(question.getDomainName(), RecordType.CNAME, Collections.<String, Object>singletonMap(DOMAIN_NAME.toLowerCase(), "cname.netty.io")));
                }
                if ("y.netty.io".equals(question.getDomainName())) {
                    cnameQueries.incrementAndGet();
                    return Collections.<ResourceRecord>singleton(new TestDnsServer.TestResourceRecord(question.getDomainName(), RecordType.CNAME, Collections.<String, Object>singletonMap(DOMAIN_NAME.toLowerCase(), "x.netty.io")));
                }
                return Collections.emptySet();
            }
        });
        dnsServer2.start();
        DnsNameResolver resolver = null;
        try {
            DnsNameResolverBuilder builder = DnsNameResolverTest.newResolver().recursionDesired(true).resolvedAddressTypes(IPV4_ONLY).maxQueriesPerResolve(16).nameServerProvider(new SingletonDnsServerAddressStreamProvider(dnsServer2.localAddress())).resolveCache(INSTANCE).cnameCache(new DnsCnameCache() {
                @Override
                public String get(String hostname) {
                    Assert.assertTrue(hostname, hostname.endsWith("."));
                    return cache.get(hostname);
                }

                @Override
                public void cache(String hostname, String cname, long originalTtl, EventLoop loop) {
                    Assert.assertTrue(hostname, hostname.endsWith("."));
                    cache.put(hostname, cname);
                }

                @Override
                public void clear() {
                    // NOOP
                }

                @Override
                public boolean clear(String hostname) {
                    return false;
                }
            });
            resolver = builder.build();
            List<InetAddress> resolvedAddresses = resolver.resolveAll("x.netty.io").syncUninterruptibly().getNow();
            Assert.assertEquals(1, resolvedAddresses.size());
            Assert.assertTrue(resolvedAddresses.contains(InetAddress.getByAddress(new byte[]{ 10, 0, 0, 99 })));
            Assert.assertEquals("cname.netty.io.", cache.get("x.netty.io."));
            Assert.assertEquals(1, cnameQueries.get());
            Assert.assertEquals(1, aQueries.get());
            resolvedAddresses = resolver.resolveAll("x.netty.io").syncUninterruptibly().getNow();
            Assert.assertEquals(1, resolvedAddresses.size());
            Assert.assertTrue(resolvedAddresses.contains(InetAddress.getByAddress(new byte[]{ 10, 0, 0, 99 })));
            // Should not have queried for the CNAME again.
            Assert.assertEquals(1, cnameQueries.get());
            Assert.assertEquals(2, aQueries.get());
            resolvedAddresses = resolver.resolveAll("y.netty.io").syncUninterruptibly().getNow();
            Assert.assertEquals(1, resolvedAddresses.size());
            Assert.assertTrue(resolvedAddresses.contains(InetAddress.getByAddress(new byte[]{ 10, 0, 0, 99 })));
            Assert.assertEquals("x.netty.io.", cache.get("y.netty.io."));
            // Will only query for one CNAME
            Assert.assertEquals(2, cnameQueries.get());
            Assert.assertEquals(3, aQueries.get());
            resolvedAddresses = resolver.resolveAll("y.netty.io").syncUninterruptibly().getNow();
            Assert.assertEquals(1, resolvedAddresses.size());
            Assert.assertTrue(resolvedAddresses.contains(InetAddress.getByAddress(new byte[]{ 10, 0, 0, 99 })));
            // Should not have queried for the CNAME again.
            Assert.assertEquals(2, cnameQueries.get());
            Assert.assertEquals(4, aQueries.get());
        } finally {
            stop();
            if (resolver != null) {
                resolver.close();
            }
        }
    }

    @Test
    public void testInstanceWithNullPreferredAddressType() {
        close();
    }
}

