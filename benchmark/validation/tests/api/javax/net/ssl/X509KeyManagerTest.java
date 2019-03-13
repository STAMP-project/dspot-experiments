package tests.api.javax.net.ssl;


import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;
import junit.framework.TestCase;


/**
 * Tests for <code>X509KeyManager</code> class constructors and methods.
 */
public class X509KeyManagerTest extends TestCase {
    private X509KeyManager manager;

    private KeyManagerFactory factory;

    private static final String CLIENT = "CLIENT";

    private static final String SERVER = "SERVER";

    private static final String TYPE_RSA = "RSA";

    private static final char[] PASSWORD = "1234".toCharArray();

    private String keyType;

    private KeyStore keyTest;

    private X509Certificate[] cert;

    private PrivateKey[] keys;

    /* Certificate:
    Data:
    Version: 3 (0x2)
    Serial Number: 0 (0x0)
    Signature Algorithm: sha1WithRSAEncryption
    Issuer: C=AN, ST=Android, O=Android, OU=Android, CN=Android/emailAddress=android@android.com
    Validity
    Not Before: Mar 20 17:00:06 2009 GMT
    Not After : Mar 19 17:00:06 2012 GMT
    Subject: C=AN, ST=Android, O=Android, OU=Android, CN=Android/emailAddress=android@android.com
    Subject Public Key Info:
    Public Key Algorithm: rsaEncryption
    RSA Public Key: (1024 bit)
    Modulus (1024 bit):
    00:aa:42:40:ed:92:21:17:99:5f:0e:e4:42:b8:cb:
    66:3d:63:2a:16:34:3c:7b:d3:3e:1f:a8:3f:bd:9a:
    eb:b3:24:6b:8c:e4:da:2f:31:bc:61:07:27:2e:28:
    71:77:58:ae:b4:89:7c:eb:b0:06:24:07:57:3c:54:
    71:db:71:41:05:ab:3d:9f:05:d2:ca:cb:1c:bf:9d:
    8a:21:96:8f:13:61:25:69:12:3b:77:bd:f7:34:b2:
    09:a9:e0:52:94:44:31:ce:db:3d:eb:64:f1:d6:ca:
    c5:7d:2f:d6:6f:8d:e4:29:8b:06:98:8a:95:3d:7a:
    97:41:9a:f1:66:c5:09:82:0d
    Exponent: 65537 (0x10001)
    X509v3 extensions:
    X509v3 Subject Key Identifier:
    E7:9B:7D:90:29:EA:90:0B:7F:08:41:76:4E:41:23:E8:43:2C:A9:03
    X509v3 Authority Key Identifier:
    keyid:E7:9B:7D:90:29:EA:90:0B:7F:08:41:76:4E:41:23:E8:43:2C:A9:03
    DirName:/C=AN/ST=Android/O=Android/OU=Android/CN=Android/emailAddress=android@android.com
    serial:00

    X509v3 Basic Constraints:
    CA:TRUE
    Signature Algorithm: sha1WithRSAEncryption
    14:98:30:29:42:ef:ab:e6:b8:25:4b:55:85:04:a5:c4:dd:1d:
    8b:6a:c1:6f:6c:1c:1d:c3:61:34:30:07:34:4d:6a:8b:55:6f:
    75:55:6e:15:58:c5:f8:af:e0:be:73:ba:d9:a5:85:d7:b5:1a:
    85:44:2b:88:fd:cc:cb:d1:ed:46:69:43:ff:59:ae:9b:5c:17:
    26:da:ee:c8:bf:67:55:01:a0:0e:10:b9:85:49:54:d9:79:1e:
    7b:2e:6f:65:4f:d9:10:2e:9d:b8:92:63:67:74:8b:22:0d:6d:
    d3:5d:9e:29:63:f9:36:93:1b:a7:80:e2:b1:f1:bf:29:19:81:
    3d:07
     */
    String certificate = "-----BEGIN CERTIFICATE-----\n" + (((((((((((((((((("MIIDPzCCAqigAwIBAgIBADANBgkqhkiG9w0BAQUFADB5MQswCQYDVQQGEwJBTjEQ\n" + "MA4GA1UECBMHQW5kcm9pZDEQMA4GA1UEChMHQW5kcm9pZDEQMA4GA1UECxMHQW5k\n") + "cm9pZDEQMA4GA1UEAxMHQW5kcm9pZDEiMCAGCSqGSIb3DQEJARYTYW5kcm9pZEBh\n") + "bmRyb2lkLmNvbTAeFw0wOTAzMjAxNzAwMDZaFw0xMjAzMTkxNzAwMDZaMHkxCzAJ\n") + "BgNVBAYTAkFOMRAwDgYDVQQIEwdBbmRyb2lkMRAwDgYDVQQKEwdBbmRyb2lkMRAw\n") + "DgYDVQQLEwdBbmRyb2lkMRAwDgYDVQQDEwdBbmRyb2lkMSIwIAYJKoZIhvcNAQkB\n") + "FhNhbmRyb2lkQGFuZHJvaWQuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKB\n") + "gQCqQkDtkiEXmV8O5EK4y2Y9YyoWNDx70z4fqD+9muuzJGuM5NovMbxhBycuKHF3\n") + "WK60iXzrsAYkB1c8VHHbcUEFqz2fBdLKyxy/nYohlo8TYSVpEjt3vfc0sgmp4FKU\n") + "RDHO2z3rZPHWysV9L9ZvjeQpiwaYipU9epdBmvFmxQmCDQIDAQABo4HWMIHTMB0G\n") + "A1UdDgQWBBTnm32QKeqQC38IQXZOQSPoQyypAzCBowYDVR0jBIGbMIGYgBTnm32Q\n") + "KeqQC38IQXZOQSPoQyypA6F9pHsweTELMAkGA1UEBhMCQU4xEDAOBgNVBAgTB0Fu\n") + "ZHJvaWQxEDAOBgNVBAoTB0FuZHJvaWQxEDAOBgNVBAsTB0FuZHJvaWQxEDAOBgNV\n") + "BAMTB0FuZHJvaWQxIjAgBgkqhkiG9w0BCQEWE2FuZHJvaWRAYW5kcm9pZC5jb22C\n") + "AQAwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQAUmDApQu+r5rglS1WF\n") + "BKXE3R2LasFvbBwdw2E0MAc0TWqLVW91VW4VWMX4r+C+c7rZpYXXtRqFRCuI/czL\n") + "0e1GaUP/Wa6bXBcm2u7Iv2dVAaAOELmFSVTZeR57Lm9lT9kQLp24kmNndIsiDW3T\n") + "XZ4pY/k2kxungOKx8b8pGYE9Bw==\n") + "-----END CERTIFICATE-----");

    ByteArrayInputStream certArray = new ByteArrayInputStream(certificate.getBytes());

    /* The key in DER format.
    Below is the same key in PEM format as reference
     */
    byte[] keyBytes = new byte[]{ ((byte) (48)), ((byte) (130)), ((byte) (2)), ((byte) (119)), ((byte) (2)), ((byte) (1)), ((byte) (0)), ((byte) (48)), ((byte) (13)), ((byte) (6)), ((byte) (9)), ((byte) (42)), ((byte) (134)), ((byte) (72)), ((byte) (134)), ((byte) (247)), ((byte) (13)), ((byte) (1)), ((byte) (1)), ((byte) (1)), ((byte) (5)), ((byte) (0)), ((byte) (4)), ((byte) (130)), ((byte) (2)), ((byte) (97)), ((byte) (48)), ((byte) (130)), ((byte) (2)), ((byte) (93)), ((byte) (2)), ((byte) (1)), ((byte) (0)), ((byte) (2)), ((byte) (129)), ((byte) (129)), ((byte) (0)), ((byte) (170)), ((byte) (66)), ((byte) (64)), ((byte) (237)), ((byte) (146)), ((byte) (33)), ((byte) (23)), ((byte) (153)), ((byte) (95)), ((byte) (14)), ((byte) (228)), ((byte) (66)), ((byte) (184)), ((byte) (203)), ((byte) (102)), ((byte) (61)), ((byte) (99)), ((byte) (42)), ((byte) (22)), ((byte) (52)), ((byte) (60)), ((byte) (123)), ((byte) (211)), ((byte) (62)), ((byte) (31)), ((byte) (168)), ((byte) (63)), ((byte) (189)), ((byte) (154)), ((byte) (235)), ((byte) (179)), ((byte) (36)), ((byte) (107)), ((byte) (140)), ((byte) (228)), ((byte) (218)), ((byte) (47)), ((byte) (49)), ((byte) (188)), ((byte) (97)), ((byte) (7)), ((byte) (39)), ((byte) (46)), ((byte) (40)), ((byte) (113)), ((byte) (119)), ((byte) (88)), ((byte) (174)), ((byte) (180)), ((byte) (137)), ((byte) (124)), ((byte) (235)), ((byte) (176)), ((byte) (6)), ((byte) (36)), ((byte) (7)), ((byte) (87)), ((byte) (60)), ((byte) (84)), ((byte) (113)), ((byte) (219)), ((byte) (113)), ((byte) (65)), ((byte) (5)), ((byte) (171)), ((byte) (61)), ((byte) (159)), ((byte) (5)), ((byte) (210)), ((byte) (202)), ((byte) (203)), ((byte) (28)), ((byte) (191)), ((byte) (157)), ((byte) (138)), ((byte) (33)), ((byte) (150)), ((byte) (143)), ((byte) (19)), ((byte) (97)), ((byte) (37)), ((byte) (105)), ((byte) (18)), ((byte) (59)), ((byte) (119)), ((byte) (189)), ((byte) (247)), ((byte) (52)), ((byte) (178)), ((byte) (9)), ((byte) (169)), ((byte) (224)), ((byte) (82)), ((byte) (148)), ((byte) (68)), ((byte) (49)), ((byte) (206)), ((byte) (219)), ((byte) (61)), ((byte) (235)), ((byte) (100)), ((byte) (241)), ((byte) (214)), ((byte) (202)), ((byte) (197)), ((byte) (125)), ((byte) (47)), ((byte) (214)), ((byte) (111)), ((byte) (141)), ((byte) (228)), ((byte) (41)), ((byte) (139)), ((byte) (6)), ((byte) (152)), ((byte) (138)), ((byte) (149)), ((byte) (61)), ((byte) (122)), ((byte) (151)), ((byte) (65)), ((byte) (154)), ((byte) (241)), ((byte) (102)), ((byte) (197)), ((byte) (9)), ((byte) (130)), ((byte) (13)), ((byte) (2)), ((byte) (3)), ((byte) (1)), ((byte) (0)), ((byte) (1)), ((byte) (2)), ((byte) (129)), ((byte) (128)), ((byte) (52)), ((byte) (145)), ((byte) (142)), ((byte) (80)), ((byte) (139)), ((byte) (252)), ((byte) (241)), ((byte) (183)), ((byte) (102)), ((byte) (53)), ((byte) (71)), ((byte) (223)), ((byte) (30)), ((byte) (5)), ((byte) (151)), ((byte) (68)), ((byte) (190)), ((byte) (248)), ((byte) (128)), ((byte) (176)), ((byte) (146)), ((byte) (56)), ((byte) (61)), ((byte) (74)), ((byte) (2)), ((byte) (38)), ((byte) (69)), ((byte) (191)), ((byte) (250)), ((byte) (52)), ((byte) (106)), ((byte) (52)), ((byte) (133)), ((byte) (140)), ((byte) (148)), ((byte) (32)), ((byte) (149)), ((byte) (207)), ((byte) (202)), ((byte) (117)), ((byte) (62)), ((byte) (235)), ((byte) (39)), ((byte) (2)), ((byte) (79)), ((byte) (190)), ((byte) (100)), ((byte) (192)), ((byte) (84)), ((byte) (119)), ((byte) (218)), ((byte) (253)), ((byte) (62)), ((byte) (117)), ((byte) (54)), ((byte) (236)), ((byte) (153)), ((byte) (79)), ((byte) (196)), ((byte) (86)), ((byte) (255)), ((byte) (69)), ((byte) (97)), ((byte) (168)), ((byte) (168)), ((byte) (65)), ((byte) (228)), ((byte) (66)), ((byte) (113)), ((byte) (122)), ((byte) (140)), ((byte) (132)), ((byte) (194)), ((byte) (2)), ((byte) (64)), ((byte) (11)), ((byte) (61)), ((byte) (66)), ((byte) (224)), ((byte) (139)), ((byte) (34)), ((byte) (247)), ((byte) (76)), ((byte) (163)), ((byte) (187)), ((byte) (216)), ((byte) (143)), ((byte) (69)), ((byte) (162)), ((byte) (85)), ((byte) (199)), ((byte) (208)), ((byte) (106)), ((byte) (37)), ((byte) (191)), ((byte) (218)), ((byte) (84)), ((byte) (87)), ((byte) (20)), ((byte) (145)), ((byte) (12)), ((byte) (9)), ((byte) (11)), ((byte) (154)), ((byte) (80)), ((byte) (202)), ((byte) (230)), ((byte) (158)), ((byte) (40)), ((byte) (195)), ((byte) (120)), ((byte) (57)), ((byte) (16)), ((byte) (6)), ((byte) (2)), ((byte) (150)), ((byte) (16)), ((byte) (26)), ((byte) (210)), ((byte) (75)), ((byte) (123)), ((byte) (108)), ((byte) (114)), ((byte) (158)), ((byte) (30)), ((byte) (172)), ((byte) (210)), ((byte) (193)), ((byte) (2)), ((byte) (65)), ((byte) (0)), ((byte) (222)), ((byte) (39)), ((byte) (189)), ((byte) (67)), ((byte) (164)), ((byte) (189)), ((byte) (149)), ((byte) (20)), ((byte) (46)), ((byte) (28)), ((byte) (160)), ((byte) (116)), ((byte) (165)), ((byte) (62)), ((byte) (250)), ((byte) (249)), ((byte) (21)), ((byte) (178)), ((byte) (41)), ((byte) (106)), ((byte) (42)), ((byte) (66)), ((byte) (148)), ((byte) (90)), ((byte) (242)), ((byte) (129)), ((byte) (243)), ((byte) (225)), ((byte) (118)), ((byte) (73)), ((byte) (17)), ((byte) (157)), ((byte) (24)), ((byte) (197)), ((byte) (235)), ((byte) (182)), ((byte) (188)), ((byte) (129)), ((byte) (58)), ((byte) (20)), ((byte) (156)), ((byte) (65)), ((byte) (1)), ((byte) (88)), ((byte) (86)), ((byte) (169)), ((byte) (155)), ((byte) (115)), ((byte) (47)), ((byte) (217)), ((byte) (168)), ((byte) (142)), ((byte) (196)), ((byte) (72)), ((byte) (105)), ((byte) (53)), ((byte) (230)), ((byte) (244)), ((byte) (115)), ((byte) (47)), ((byte) (249)), ((byte) (18)), ((byte) (18)), ((byte) (113)), ((byte) (2)), ((byte) (65)), ((byte) (0)), ((byte) (196)), ((byte) (50)), ((byte) (129)), ((byte) (93)), ((byte) (25)), ((byte) (84)), ((byte) (44)), ((byte) (41)), ((byte) (90)), ((byte) (159)), ((byte) (54)), ((byte) (76)), ((byte) (111)), ((byte) (45)), ((byte) (253)), ((byte) (98)), ((byte) (14)), ((byte) (230)), ((byte) (55)), ((byte) (194)), ((byte) (246)), ((byte) (105)), ((byte) (100)), ((byte) (249)), ((byte) (58)), ((byte) (204)), ((byte) (178)), ((byte) (99)), ((byte) (47)), ((byte) (169)), ((byte) (254)), ((byte) (126)), ((byte) (139)), ((byte) (45)), ((byte) (105)), ((byte) (19)), ((byte) (229)), ((byte) (97)), ((byte) (88)), ((byte) (183)), ((byte) (250)), ((byte) (85)), ((byte) (116)), ((byte) (44)), ((byte) (232)), ((byte) (161)), ((byte) (172)), ((byte) (195)), ((byte) (221)), ((byte) (91)), ((byte) (98)), ((byte) (174)), ((byte) (10)), ((byte) (39)), ((byte) (206)), ((byte) (176)), ((byte) (242)), ((byte) (129)), ((byte) (95)), ((byte) (154)), ((byte) (111)), ((byte) (95)), ((byte) (63)), ((byte) (93)), ((byte) (2)), ((byte) (65)), ((byte) (0)), ((byte) (146)), ((byte) (66)), ((byte) (255)), ((byte) (172)), ((byte) (229)), ((byte) (109)), ((byte) (156)), ((byte) (21)), ((byte) (41)), ((byte) (54)), ((byte) (215)), ((byte) (189)), ((byte) (116)), ((byte) (126)), ((byte) (62)), ((byte) (166)), ((byte) (119)), ((byte) (206)), ((byte) (80)), ((byte) (206)), ((byte) (0)), ((byte) (252)), ((byte) (204)), ((byte) (200)), ((byte) (4)), ((byte) (25)), ((byte) (227)), ((byte) (3)), ((byte) (113)), ((byte) (233)), ((byte) (49)), ((byte) (155)), ((byte) (136)), ((byte) (143)), ((byte) (230)), ((byte) (92)), ((byte) (237)), ((byte) (70)), ((byte) (247)), ((byte) (130)), ((byte) (82)), ((byte) (77)), ((byte) (202)), ((byte) (32)), ((byte) (235)), ((byte) (13)), ((byte) (199)), ((byte) (182)), ((byte) (210)), ((byte) (174)), ((byte) (46)), ((byte) (247)), ((byte) (175)), ((byte) (235)), ((byte) (44)), ((byte) (185)), ((byte) (188)), ((byte) (80)), ((byte) (252)), ((byte) (245)), ((byte) (124)), ((byte) (186)), ((byte) (149)), ((byte) (65)), ((byte) (2)), ((byte) (64)), ((byte) (84)), ((byte) (248)), ((byte) (70)), ((byte) (156)), ((byte) (106)), ((byte) (94)), ((byte) (208)), ((byte) (237)), ((byte) (108)), ((byte) (8)), ((byte) (237)), ((byte) (252)), ((byte) (54)), ((byte) (94)), ((byte) (101)), ((byte) (145)), ((byte) (117)), ((byte) (64)), ((byte) (113)), ((byte) (63)), ((byte) (231)), ((byte) (118)), ((byte) (7)), ((byte) (188)), ((byte) (4)), ((byte) (162)), ((byte) (40)), ((byte) (83)), ((byte) (218)), ((byte) (141)), ((byte) (181)), ((byte) (225)), ((byte) (90)), ((byte) (39)), ((byte) (101)), ((byte) (141)), ((byte) (175)), ((byte) (86)), ((byte) (244)), ((byte) (148)), ((byte) (97)), ((byte) (63)), ((byte) (103)), ((byte) (28)), ((byte) (23)), ((byte) (248)), ((byte) (5)), ((byte) (25)), ((byte) (162)), ((byte) (161)), ((byte) (116)), ((byte) (96)), ((byte) (73)), ((byte) (151)), ((byte) (169)), ((byte) (229)), ((byte) (106)), ((byte) (113)), ((byte) (107)), ((byte) (85)), ((byte) (56)), ((byte) (12)), ((byte) (185)), ((byte) (37)), ((byte) (2)), ((byte) (65)), ((byte) (0)), ((byte) (174)), ((byte) (242)), ((byte) (168)), ((byte) (109)), ((byte) (29)), ((byte) (53)), ((byte) (56)), ((byte) (115)), ((byte) (152)), ((byte) (21)), ((byte) (199)), ((byte) (21)), ((byte) (2)), ((byte) (47)), ((byte) (41)), ((byte) (93)), ((byte) (24)), ((byte) (75)), ((byte) (125)), ((byte) (178)), ((byte) (89)), ((byte) (190)), ((byte) (90)), ((byte) (199)), ((byte) (114)), ((byte) (208)), ((byte) (128)), ((byte) (216)), ((byte) (119)), ((byte) (161)), ((byte) (127)), ((byte) (178)), ((byte) (53)), ((byte) (13)), ((byte) (120)), ((byte) (146)), ((byte) (145)), ((byte) (53)), ((byte) (71)), ((byte) (235)), ((byte) (75)), ((byte) (0)), ((byte) (89)), ((byte) (180)), ((byte) (196)), ((byte) (44)), ((byte) (41)), ((byte) (231)), ((byte) (57)), ((byte) (157)), ((byte) (72)), ((byte) (139)), ((byte) (79)), ((byte) (70)), ((byte) (230)), ((byte) (206)), ((byte) (211)), ((byte) (108)), ((byte) (132)), ((byte) (155)), ((byte) (210)), ((byte) (16)), ((byte) (176)), ((byte) (225)) };

    /* The same key in PEM format.
    The DER version of this key was created using

    openssl pkcs8 -topk8 -nocrypt -in key1.pem
            -inform PEM -out key1.der -outform DER

    -----BEGIN RSA PRIVATE KEY-----
    Proc-Type: 4,ENCRYPTED
    DEK-Info: DES-EDE3-CBC,69E26FCC3A7F136E

    YKiLXOwf2teog4IoOvbbROy9vqp0EMt1KF9eNKeKFCWGCS4RFATaAGjKrdA26bOV
    MBdyB4V7qaxLC8/UwLlzFLpprouIfGqrEoR/NT0eKQ+4Pl25GlMvlPaR0pATBLZ2
    OEaB3zcNygOQ02Jdrmw2+CS9qVtGGXjn6Qp6TVFm6edNCoOVZODLP9kkzPLn8Mkm
    /isgsprwMELuth8Y5BC0brI5XYdMqZFI5dLz4wzVH81wBYbRmJqR7yOE1pzAJS9I
    gJ5YvcP7pSmoA2SHVN4v4qolM+GAM9YIp2bwEyWFRjbriNlF1yM+HflGMEZ1HNpZ
    FSFFA3G8EIH9ogbZ3j+7EujrndJC7GIibwiu5rd3eIHtcwrWprp+wEoPc/vM8OpR
    so9ms7iQYV6faYCWK4yeCfErYw7t+AhGqfLiqHO6bO2XAYJcD28RYV9gXmugZOhT
    9471MOw94HWF5tBVjgIkyNBcbRyMF9iyQKafbkHYpmxaB4s2EqQr1SNZl3SLEwhX
    MEGy3/tyveuMLAvdTlSDZbt6memWoXXEX4Ep/q6r0ErCTY31awdP/XaJcJBGb9ni
    Iai8DICaG1v4bUuBVgaiacZlgw1O4Hhj8D2DWfVZsgpx5y8tBRM2lGWvyzEi5n2F
    PiR2UlT0DjCD1ObjCpWJ5insX/w8dXSHGZLLb9ccGRUrw/+5Bptn+AoEfdP+8S3j
    UdMdxl6qt2gneCYu1Lr3cQ+qKPqikQty2UQ6Yp8dJkheLJ2Tr+rnaytOCp2dAT9K
    KXTimIcXV+ftvUMbDPXYu4LJBldr2VokD+k3QbHDgFnfHIiNkwiPzA==
    -----END RSA PRIVATE KEY-----
     */
    /* Certificate:
    Data:
    Version: 3 (0x2)
    Serial Number: 1 (0x1)
    Signature Algorithm: sha1WithRSAEncryption
    Issuer: C=AN, ST=Android, O=Android, OU=Android, CN=Android/emailAddress=android@android.com
    Validity
    Not Before: Mar 20 17:00:40 2009 GMT
    Not After : Mar 20 17:00:40 2010 GMT
    Subject: C=AN, ST=Android, L=Android, O=Android, OU=Android, CN=Android/emailAddress=android@android.com
    Subject Public Key Info:
    Public Key Algorithm: rsaEncryption
    RSA Public Key: (1024 bit)
    Modulus (1024 bit):
    00:d0:44:5a:c4:76:ef:ae:ff:99:5b:c3:37:c1:09:
    33:c1:97:e5:64:7a:a9:7e:98:4b:3a:a3:33:d0:5c:
    c7:56:ac:d8:42:e8:4a:ac:9c:d9:8f:89:84:c8:46:
    95:ce:22:f7:6a:09:de:91:47:9c:38:23:a5:4a:fc:
    08:af:5a:b4:6e:39:8e:e9:f5:0e:46:00:69:e1:e5:
    cc:4c:81:b6:82:7b:56:fb:f4:dc:04:ff:61:e2:7e:
    5f:e2:f9:97:53:93:d4:69:9b:ba:79:20:cd:1e:3e:
    d5:9a:44:95:7c:cf:c1:51:f2:22:fc:ec:cc:66:18:
    74:60:2a:a2:be:06:c2:9e:8d
    Exponent: 65537 (0x10001)
    X509v3 extensions:
    X509v3 Basic Constraints:
    CA:FALSE
    Netscape Comment:
    OpenSSL Generated Certificate
    X509v3 Subject Key Identifier:
    95:3E:C3:46:69:52:78:08:05:46:B9:00:69:E5:E7:A7:99:E3:C4:67
    X509v3 Authority Key Identifier:
    keyid:E7:9B:7D:90:29:EA:90:0B:7F:08:41:76:4E:41:23:E8:43:2C:A9:03

    Signature Algorithm: sha1WithRSAEncryption
    a3:5b:30:f5:28:3f:87:f6:1b:36:6a:22:6d:66:48:fa:cb:ee:
    4c:04:cf:11:14:e2:1f:b5:68:0c:e7:61:0e:bc:d3:69:19:02:
    8b:d5:d3:05:4a:c8:29:e8:e3:d0:e9:32:ad:6c:7d:9c:c4:46:
    6c:f9:66:e6:64:60:47:6b:ef:8e:c8:1c:67:5a:5a:cf:73:a3:
    7e:9d:6e:89:0c:67:99:17:3d:b2:b8:8e:41:95:9c:84:95:bf:
    57:95:24:22:8f:19:12:c1:fd:23:45:75:7f:4f:61:06:e3:9f:
    05:dc:e7:29:9a:6b:17:e1:e1:37:d5:8b:ba:b4:d0:8a:3c:dd:
    3f:6a
     */
    String certificate2 = "-----BEGIN CERTIFICATE-----\n" + (((((((((((((((("MIIC9jCCAl+gAwIBAgIBATANBgkqhkiG9w0BAQUFADB5MQswCQYDVQQGEwJBTjEQ\n" + "MA4GA1UECBMHQW5kcm9pZDEQMA4GA1UEChMHQW5kcm9pZDEQMA4GA1UECxMHQW5k\n") + "cm9pZDEQMA4GA1UEAxMHQW5kcm9pZDEiMCAGCSqGSIb3DQEJARYTYW5kcm9pZEBh\n") + "bmRyb2lkLmNvbTAeFw0wOTAzMjAxNzAwNDBaFw0xMDAzMjAxNzAwNDBaMIGLMQsw\n") + "CQYDVQQGEwJBTjEQMA4GA1UECBMHQW5kcm9pZDEQMA4GA1UEBxMHQW5kcm9pZDEQ\n") + "MA4GA1UEChMHQW5kcm9pZDEQMA4GA1UECxMHQW5kcm9pZDEQMA4GA1UEAxMHQW5k\n") + "cm9pZDEiMCAGCSqGSIb3DQEJARYTYW5kcm9pZEBhbmRyb2lkLmNvbTCBnzANBgkq\n") + "hkiG9w0BAQEFAAOBjQAwgYkCgYEA0ERaxHbvrv+ZW8M3wQkzwZflZHqpfphLOqMz\n") + "0FzHVqzYQuhKrJzZj4mEyEaVziL3agnekUecOCOlSvwIr1q0bjmO6fUORgBp4eXM\n") + "TIG2gntW+/TcBP9h4n5f4vmXU5PUaZu6eSDNHj7VmkSVfM/BUfIi/OzMZhh0YCqi\n") + "vgbCno0CAwEAAaN7MHkwCQYDVR0TBAIwADAsBglghkgBhvhCAQ0EHxYdT3BlblNT\n") + "TCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYEFJU+w0ZpUngIBUa5AGnl\n") + "56eZ48RnMB8GA1UdIwQYMBaAFOebfZAp6pALfwhBdk5BI+hDLKkDMA0GCSqGSIb3\n") + "DQEBBQUAA4GBAKNbMPUoP4f2GzZqIm1mSPrL7kwEzxEU4h+1aAznYQ6802kZAovV\n") + "0wVKyCno49DpMq1sfZzERmz5ZuZkYEdr747IHGdaWs9zo36dbokMZ5kXPbK4jkGV\n") + "nISVv1eVJCKPGRLB/SNFdX9PYQbjnwXc5ymaaxfh4TfVi7q00Io83T9q\n\n") + "-----END CERTIFICATE-----");

    ByteArrayInputStream certArray2 = new ByteArrayInputStream(certificate2.getBytes());

    /* The key in DER format.
    Below is the same key in PEM format as reference
     */
    byte[] key2Bytes = new byte[]{ ((byte) (48)), ((byte) (130)), ((byte) (2)), ((byte) (117)), ((byte) (2)), ((byte) (1)), ((byte) (0)), ((byte) (48)), ((byte) (13)), ((byte) (6)), ((byte) (9)), ((byte) (42)), ((byte) (134)), ((byte) (72)), ((byte) (134)), ((byte) (247)), ((byte) (13)), ((byte) (1)), ((byte) (1)), ((byte) (1)), ((byte) (5)), ((byte) (0)), ((byte) (4)), ((byte) (130)), ((byte) (2)), ((byte) (95)), ((byte) (48)), ((byte) (130)), ((byte) (2)), ((byte) (91)), ((byte) (2)), ((byte) (1)), ((byte) (0)), ((byte) (2)), ((byte) (129)), ((byte) (129)), ((byte) (0)), ((byte) (208)), ((byte) (68)), ((byte) (90)), ((byte) (196)), ((byte) (118)), ((byte) (239)), ((byte) (174)), ((byte) (255)), ((byte) (153)), ((byte) (91)), ((byte) (195)), ((byte) (55)), ((byte) (193)), ((byte) (9)), ((byte) (51)), ((byte) (193)), ((byte) (151)), ((byte) (229)), ((byte) (100)), ((byte) (122)), ((byte) (169)), ((byte) (126)), ((byte) (152)), ((byte) (75)), ((byte) (58)), ((byte) (163)), ((byte) (51)), ((byte) (208)), ((byte) (92)), ((byte) (199)), ((byte) (86)), ((byte) (172)), ((byte) (216)), ((byte) (66)), ((byte) (232)), ((byte) (74)), ((byte) (172)), ((byte) (156)), ((byte) (217)), ((byte) (143)), ((byte) (137)), ((byte) (132)), ((byte) (200)), ((byte) (70)), ((byte) (149)), ((byte) (206)), ((byte) (34)), ((byte) (247)), ((byte) (106)), ((byte) (9)), ((byte) (222)), ((byte) (145)), ((byte) (71)), ((byte) (156)), ((byte) (56)), ((byte) (35)), ((byte) (165)), ((byte) (74)), ((byte) (252)), ((byte) (8)), ((byte) (175)), ((byte) (90)), ((byte) (180)), ((byte) (110)), ((byte) (57)), ((byte) (142)), ((byte) (233)), ((byte) (245)), ((byte) (14)), ((byte) (70)), ((byte) (0)), ((byte) (105)), ((byte) (225)), ((byte) (229)), ((byte) (204)), ((byte) (76)), ((byte) (129)), ((byte) (182)), ((byte) (130)), ((byte) (123)), ((byte) (86)), ((byte) (251)), ((byte) (244)), ((byte) (220)), ((byte) (4)), ((byte) (255)), ((byte) (97)), ((byte) (226)), ((byte) (126)), ((byte) (95)), ((byte) (226)), ((byte) (249)), ((byte) (151)), ((byte) (83)), ((byte) (147)), ((byte) (212)), ((byte) (105)), ((byte) (155)), ((byte) (186)), ((byte) (121)), ((byte) (32)), ((byte) (205)), ((byte) (30)), ((byte) (62)), ((byte) (213)), ((byte) (154)), ((byte) (68)), ((byte) (149)), ((byte) (124)), ((byte) (207)), ((byte) (193)), ((byte) (81)), ((byte) (242)), ((byte) (34)), ((byte) (252)), ((byte) (236)), ((byte) (204)), ((byte) (102)), ((byte) (24)), ((byte) (116)), ((byte) (96)), ((byte) (42)), ((byte) (162)), ((byte) (190)), ((byte) (6)), ((byte) (194)), ((byte) (158)), ((byte) (141)), ((byte) (2)), ((byte) (3)), ((byte) (1)), ((byte) (0)), ((byte) (1)), ((byte) (2)), ((byte) (129)), ((byte) (128)), ((byte) (6)), ((byte) (65)), ((byte) (215)), ((byte) (124)), ((byte) (73)), ((byte) (154)), ((byte) (127)), ((byte) (230)), ((byte) (124)), ((byte) (4)), ((byte) (14)), ((byte) (196)), ((byte) (113)), ((byte) (15)), ((byte) (70)), ((byte) (183)), ((byte) (205)), ((byte) (73)), ((byte) (126)), ((byte) (16)), ((byte) (85)), ((byte) (97)), ((byte) (81)), ((byte) (80)), ((byte) (9)), ((byte) (77)), ((byte) (247)), ((byte) (243)), ((byte) (141)), ((byte) (166)), ((byte) (11)), ((byte) (139)), ((byte) (155)), ((byte) (223)), ((byte) (190)), ((byte) (188)), ((byte) (231)), ((byte) (156)), ((byte) (186)), ((byte) (200)), ((byte) (158)), ((byte) (56)), ((byte) (24)), ((byte) (16)), ((byte) (78)), ((byte) (213)), ((byte) (231)), ((byte) (165)), ((byte) (9)), ((byte) (81)), ((byte) (140)), ((byte) (151)), ((byte) (78)), ((byte) (208)), ((byte) (121)), ((byte) (187)), ((byte) (80)), ((byte) (111)), ((byte) (5)), ((byte) (77)), ((byte) (121)), ((byte) (127)), ((byte) (63)), ((byte) (38)), ((byte) (118)), ((byte) (193)), ((byte) (204)), ((byte) (64)), ((byte) (15)), ((byte) (222)), ((byte) (66)), ((byte) (93)), ((byte) (193)), ((byte) (95)), ((byte) (112)), ((byte) (70)), ((byte) (112)), ((byte) (141)), ((byte) (255)), ((byte) (38)), ((byte) (53)), ((byte) (117)), ((byte) (154)), ((byte) (151)), ((byte) (210)), ((byte) (116)), ((byte) (83)), ((byte) (17)), ((byte) (43)), ((byte) (193)), ((byte) (118)), ((byte) (156)), ((byte) (159)), ((byte) (147)), ((byte) (170)), ((byte) (168)), ((byte) (65)), ((byte) (35)), ((byte) (154)), ((byte) (4)), ((byte) (17)), ((byte) (110)), ((byte) (86)), ((byte) (234)), ((byte) (245)), ((byte) (214)), ((byte) (29)), ((byte) (73)), ((byte) (42)), ((byte) (131)), ((byte) (73)), ((byte) (125)), ((byte) (183)), ((byte) (209)), ((byte) (230)), ((byte) (141)), ((byte) (147)), ((byte) (26)), ((byte) (129)), ((byte) (142)), ((byte) (194)), ((byte) (185)), ((byte) (191)), ((byte) (253)), ((byte) (0)), ((byte) (226)), ((byte) (181)), ((byte) (1)), ((byte) (2)), ((byte) (65)), ((byte) (0)), ((byte) (234)), ((byte) (206)), ((byte) (198)), ((byte) (17)), ((byte) (30)), ((byte) (246)), ((byte) (207)), ((byte) (58)), ((byte) (140)), ((byte) (231)), ((byte) (128)), ((byte) (22)), ((byte) (143)), ((byte) (29)), ((byte) (235)), ((byte) (162)), ((byte) (210)), ((byte) (35)), ((byte) (158)), ((byte) (249)), ((byte) (241)), ((byte) (20)), ((byte) (22)), ((byte) (200)), ((byte) (135)), ((byte) (242)), ((byte) (23)), ((byte) (223)), ((byte) (198)), ((byte) (228)), ((byte) (28)), ((byte) (116)), ((byte) (116)), ((byte) (176)), ((byte) (187)), ((byte) (64)), ((byte) (235)), ((byte) (166)), ((byte) (178)), ((byte) (91)), ((byte) (109)), ((byte) (245)), ((byte) (154)), ((byte) (133)), ((byte) (241)), ((byte) (115)), ((byte) (132)), ((byte) (236)), ((byte) (219)), ((byte) (155)), ((byte) (249)), ((byte) (248)), ((byte) (61)), ((byte) (186)), ((byte) (235)), ((byte) (215)), ((byte) (108)), ((byte) (69)), ((byte) (123)), ((byte) (202)), ((byte) (18)), ((byte) (103)), ((byte) (95)), ((byte) (205)), ((byte) (2)), ((byte) (65)), ((byte) (0)), ((byte) (227)), ((byte) (16)), ((byte) (91)), ((byte) (208)), ((byte) (173)), ((byte) (89)), ((byte) (144)), ((byte) (24)), ((byte) (23)), ((byte) (220)), ((byte) (104)), ((byte) (212)), ((byte) (117)), ((byte) (85)), ((byte) (171)), ((byte) (125)), ((byte) (209)), ((byte) (181)), ((byte) (90)), ((byte) (196)), ((byte) (176)), ((byte) (45)), ((byte) (169)), ((byte) (209)), ((byte) (111)), ((byte) (233)), ((byte) (33)), ((byte) (74)), ((byte) (39)), ((byte) (196)), ((byte) (152)), ((byte) (137)), ((byte) (250)), ((byte) (101)), ((byte) (182)), ((byte) (16)), ((byte) (93)), ((byte) (102)), ((byte) (221)), ((byte) (23)), ((byte) (179)), ((byte) (243)), ((byte) (211)), ((byte) (227)), ((byte) (160)), ((byte) (26)), ((byte) (147)), ((byte) (228)), ((byte) (251)), ((byte) (136)), ((byte) (167)), ((byte) (59)), ((byte) (151)), ((byte) (27)), ((byte) (241)), ((byte) (8)), ((byte) (12)), ((byte) (102)), ((byte) (208)), ((byte) (134)), ((byte) (94)), ((byte) (57)), ((byte) (249)), ((byte) (193)), ((byte) (2)), ((byte) (64)), ((byte) (36)), ((byte) (124)), ((byte) (205)), ((byte) (58)), ((byte) (139)), ((byte) (221)), ((byte) (62)), ((byte) (134)), ((byte) (146)), ((byte) (174)), ((byte) (198)), ((byte) (176)), ((byte) (186)), ((byte) (188)), ((byte) (163)), ((byte) (137)), ((byte) (65)), ((byte) (174)), ((byte) (87)), ((byte) (93)), ((byte) (239)), ((byte) (160)), ((byte) (119)), ((byte) (137)), ((byte) (225)), ((byte) (214)), ((byte) (52)), ((byte) (239)), ((byte) (137)), ((byte) (48)), ((byte) (153)), ((byte) (91)), ((byte) (95)), ((byte) (102)), ((byte) (183)), ((byte) (50)), ((byte) (119)), ((byte) (108)), ((byte) (7)), ((byte) (251)), ((byte) (61)), ((byte) (51)), ((byte) (21)), ((byte) (56)), ((byte) (11)), ((byte) (53)), ((byte) (48)), ((byte) (74)), ((byte) (190)), ((byte) (53)), ((byte) (150)), ((byte) (186)), ((byte) (132)), ((byte) (157)), ((byte) (47)), ((byte) (88)), ((byte) (226)), ((byte) (114)), ((byte) (73)), ((byte) (178)), ((byte) (52)), ((byte) (249)), ((byte) (235)), ((byte) (97)), ((byte) (2)), ((byte) (64)), ((byte) (42)), ((byte) (212)), ((byte) (137)), ((byte) (29)), ((byte) (33)), ((byte) (181)), ((byte) (197)), ((byte) (50)), ((byte) (102)), ((byte) (61)), ((byte) (211)), ((byte) (32)), ((byte) (80)), ((byte) (73)), ((byte) (170)), ((byte) (161)), ((byte) (127)), ((byte) (15)), ((byte) (32)), ((byte) (97)), ((byte) (253)), ((byte) (129)), ((byte) (127)), ((byte) (136)), ((byte) (219)), ((byte) (253)), ((byte) (51)), ((byte) (164)), ((byte) (83)), ((byte) (64)), ((byte) (8)), ((byte) (45)), ((byte) (238)), ((byte) (167)), ((byte) (132)), ((byte) (226)), ((byte) (45)), ((byte) (92)), ((byte) (27)), ((byte) (212)), ((byte) (62)), ((byte) (195)), ((byte) (125)), ((byte) (114)), ((byte) (112)), ((byte) (94)), ((byte) (211)), ((byte) (10)), ((byte) (220)), ((byte) (79)), ((byte) (120)), ((byte) (140)), ((byte) (11)), ((byte) (2)), ((byte) (224)), ((byte) (66)), ((byte) (78)), ((byte) (100)), ((byte) (142)), ((byte) (108)), ((byte) (234)), ((byte) (21)), ((byte) (49)), ((byte) (129)), ((byte) (2)), ((byte) (64)), ((byte) (87)), ((byte) (114)), ((byte) (185)), ((byte) (120)), ((byte) (192)), ((byte) (31)), ((byte) (91)), ((byte) (29)), ((byte) (178)), ((byte) (207)), ((byte) (148)), ((byte) (66)), ((byte) (237)), ((byte) (189)), ((byte) (231)), ((byte) (170)), ((byte) (20)), ((byte) (86)), ((byte) (208)), ((byte) (148)), ((byte) (37)), ((byte) (48)), ((byte) (135)), ((byte) (53)), ((byte) (130)), ((byte) (160)), ((byte) (66)), ((byte) (181)), ((byte) (127)), ((byte) (102)), ((byte) (119)), ((byte) (176)), ((byte) (19)), ((byte) (190)), ((byte) (87)), ((byte) (6)), ((byte) (126)), ((byte) (80)), ((byte) (103)), ((byte) (19)), ((byte) (167)), ((byte) (9)), ((byte) (172)), ((byte) (214)), ((byte) (191)), ((byte) (34)), ((byte) (116)), ((byte) (107)), ((byte) (55)), ((byte) (146)), ((byte) (43)), ((byte) (145)), ((byte) (189)), ((byte) (10)), ((byte) (216)), ((byte) (15)), ((byte) (141)), ((byte) (134)), ((byte) (75)), ((byte) (32)), ((byte) (94)), ((byte) (80)), ((byte) (96)), ((byte) (128)) };

    /* The same key in PEM format.
    The DER version of this key was created using

    openssl pkcs8 -topk8 -nocrypt -in key1.pem
            -inform PEM -out key1.der -outform DER

    -----BEGIN RSA PRIVATE KEY-----
    Proc-Type: 4,ENCRYPTED
    DEK-Info: DES-EDE3-CBC,370723FFDC1B1CFA

    KJ20ODBEQujoOpnzNfHNoo5DF/qENhw9IaApChGMj+WhqYuFfKfPQKuRli8sJSEk
    uoPmEqjJndHz5M5bI7wVxiafv/Up4+SaNKhn/vu6xjx/senJMX8HMUchqfvn0eCd
    31NHQeNbQ67O73xGIdltLzwTRsavTu/hwhnnJxiXzXnYtI5HTZUaRbVJQNpdlkNW
    H91u70lwlT8W2MATBhl3R3wIbRHQG1I0RQX12O04gMfK1PBl9d/tnFOi4ESfth1W
    e06XV0U12g06V5/UUuicJANvgyf0Pix0xxPr2tqibWeGpFwCvJpNHl4L3tUocydF
    HYoUKx/r3VSmesnZ1zUMsuO2zXOuLLcwCSFN+73GBLWocCxBvag6HFvCemy5Tuhs
    9MhfF+5lKER/9Ama/e7C61usaoUhR1OvpGWMfjewrFLCsyWlInscoZ1ad5YtcWGx
    MM7+BsTnK00fcXZuPHTPsiwQ0fMVeNM2a/e65aIivfzzHmb6gqUigNpfNYcqQsJJ
    Wwoc5hXVO92vugdHOHOiAUpfZZgNDZwgCTluMuI+KJ0QCb0dhF5w/TDA8z+vRwmW
    sz5WrA4F+T3LfwwLQfxJyHTnbAu38VlMMZP98iIobOX3AAkBw4+kTOCEedvmKt0f
    s7iSKrnnV6AyzRPEJUWknMF8xNFH7HDqkZf4Mv8cMM6e45K4kBGd17d3tcEFi2An
    5l6S9hHtoyMhHjnAcyuHJbD9rGRgyOlbhSYTcbX/gKiECZj0kf8xHi20qntO3c+p
    jdpp97fIMnQTl5IDNxOy5h9MDLs/SYAR7iyF19RkIGc=
    -----END RSA PRIVATE KEY-----
     */
    /* Certificate:
    Data:
    Version: 3 (0x2)
    Serial Number: 2 (0x2)
    Signature Algorithm: sha1WithRSAEncryption
    Issuer: C=AN, ST=Android, O=Android, OU=Android, CN=Android/emailAddress=android@android.com
    Validity
    Not Before: Mar 20 17:02:32 2009 GMT
    Not After : Mar 20 17:02:32 2010 GMT
    Subject: C=AN, ST=Android, L=Android, O=Android, OU=Android, CN=Android/emailAddress=android@android.com
    Subject Public Key Info:
    Public Key Algorithm: rsaEncryption
    RSA Public Key: (1024 bit)
    Modulus (1024 bit):
    00:b4:c5:ed:df:30:42:6d:8b:af:4b:e4:9c:13:5e:
    83:23:cd:2f:ce:34:e2:43:d7:6c:72:bb:03:b3:b9:
    24:02:e0:cc:b5:8d:d6:92:41:04:2b:5c:94:b2:c3:
    9c:9d:56:f0:99:bc:0f:81:af:eb:54:ed:80:a6:a0:
    c7:c2:43:05:04:7c:9c:7e:07:03:10:b9:bd:c5:16:
    cf:19:dd:e3:4f:73:83:72:c5:66:e4:5b:14:c4:96:
    d1:e3:24:0b:b6:d4:f7:84:2e:b1:e7:93:02:9d:f5:
    da:aa:c1:d9:cc:5e:36:e9:8f:bf:8b:da:a7:45:82:
    f2:b0:f5:a7:e4:e1:80:a3:17
    Exponent: 65537 (0x10001)
    X509v3 extensions:
    X509v3 Basic Constraints:
    CA:FALSE
    Netscape Comment:
    OpenSSL Generated Certificate
    X509v3 Subject Key Identifier:
    3B:5B:3D:DB:45:F5:8F:58:70:0B:FC:70:3E:31:2B:43:63:A9:FE:2B
    X509v3 Authority Key Identifier:
    keyid:E7:9B:7D:90:29:EA:90:0B:7F:08:41:76:4E:41:23:E8:43:2C:A9:03

    Signature Algorithm: sha1WithRSAEncryption
    1c:7f:93:1c:59:21:88:15:45:4b:e0:9c:78:3a:88:3e:55:19:
    86:31:e8:53:3d:74:e2:4a:34:9f:92:17:4e:13:46:92:54:f8:
    43:eb:5e:03:4f:14:51:61:d2:04:b8:04:5a:31:eb:14:6a:18:
    b0:20:03:92:0c:7f:07:c4:1b:f9:9e:7f:5f:ec:03:7a:c8:e3:
    df:d3:94:6e:68:8a:3a:3d:e4:61:f3:e0:87:5d:40:d8:cb:99:
    4d:9a:7b:bc:95:7c:d2:9d:b7:04:9a:9a:63:89:cd:39:ec:32:
    60:0a:97:da:e9:50:a5:73:4a:a2:aa:9c:9b:a8:7f:5a:20:d6:
    48:bd
     */
    String certificate3 = "-----BEGIN CERTIFICATE-----\n" + (((((((((((((((("MIIC9jCCAl+gAwIBAgIBAjANBgkqhkiG9w0BAQUFADB5MQswCQYDVQQGEwJBTjEQ\n" + "MA4GA1UECBMHQW5kcm9pZDEQMA4GA1UEChMHQW5kcm9pZDEQMA4GA1UECxMHQW5k\n") + "cm9pZDEQMA4GA1UEAxMHQW5kcm9pZDEiMCAGCSqGSIb3DQEJARYTYW5kcm9pZEBh\n") + "bmRyb2lkLmNvbTAeFw0wOTAzMjAxNzAyMzJaFw0xMDAzMjAxNzAyMzJaMIGLMQsw\n") + "CQYDVQQGEwJBTjEQMA4GA1UECBMHQW5kcm9pZDEQMA4GA1UEBxMHQW5kcm9pZDEQ\n") + "MA4GA1UEChMHQW5kcm9pZDEQMA4GA1UECxMHQW5kcm9pZDEQMA4GA1UEAxMHQW5k\n") + "cm9pZDEiMCAGCSqGSIb3DQEJARYTYW5kcm9pZEBhbmRyb2lkLmNvbTCBnzANBgkq\n") + "hkiG9w0BAQEFAAOBjQAwgYkCgYEAtMXt3zBCbYuvS+ScE16DI80vzjTiQ9dscrsD\n") + "s7kkAuDMtY3WkkEEK1yUssOcnVbwmbwPga/rVO2ApqDHwkMFBHycfgcDELm9xRbP\n") + "Gd3jT3ODcsVm5FsUxJbR4yQLttT3hC6x55MCnfXaqsHZzF426Y+/i9qnRYLysPWn\n") + "5OGAoxcCAwEAAaN7MHkwCQYDVR0TBAIwADAsBglghkgBhvhCAQ0EHxYdT3BlblNT\n") + "TCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYEFDtbPdtF9Y9YcAv8cD4x\n") + "K0Njqf4rMB8GA1UdIwQYMBaAFOebfZAp6pALfwhBdk5BI+hDLKkDMA0GCSqGSIb3\n") + "DQEBBQUAA4GBABx/kxxZIYgVRUvgnHg6iD5VGYYx6FM9dOJKNJ+SF04TRpJU+EPr\n") + "XgNPFFFh0gS4BFox6xRqGLAgA5IMfwfEG/mef1/sA3rI49/TlG5oijo95GHz4Idd\n") + "QNjLmU2ae7yVfNKdtwSammOJzTnsMmAKl9rpUKVzSqKqnJuof1og1ki9\n") + "-----END CERTIFICATE-----");

    ByteArrayInputStream certArray3 = new ByteArrayInputStream(certificate3.getBytes());

    /* The key in DER format.
    Below is the same key in PEM format as reference
     */
    byte[] key3Bytes = new byte[]{ ((byte) (48)), ((byte) (130)), ((byte) (2)), ((byte) (118)), ((byte) (2)), ((byte) (1)), ((byte) (0)), ((byte) (48)), ((byte) (13)), ((byte) (6)), ((byte) (9)), ((byte) (42)), ((byte) (134)), ((byte) (72)), ((byte) (134)), ((byte) (247)), ((byte) (13)), ((byte) (1)), ((byte) (1)), ((byte) (1)), ((byte) (5)), ((byte) (0)), ((byte) (4)), ((byte) (130)), ((byte) (2)), ((byte) (96)), ((byte) (48)), ((byte) (130)), ((byte) (2)), ((byte) (92)), ((byte) (2)), ((byte) (1)), ((byte) (0)), ((byte) (2)), ((byte) (129)), ((byte) (129)), ((byte) (0)), ((byte) (180)), ((byte) (197)), ((byte) (237)), ((byte) (223)), ((byte) (48)), ((byte) (66)), ((byte) (109)), ((byte) (139)), ((byte) (175)), ((byte) (75)), ((byte) (228)), ((byte) (156)), ((byte) (19)), ((byte) (94)), ((byte) (131)), ((byte) (35)), ((byte) (205)), ((byte) (47)), ((byte) (206)), ((byte) (52)), ((byte) (226)), ((byte) (67)), ((byte) (215)), ((byte) (108)), ((byte) (114)), ((byte) (187)), ((byte) (3)), ((byte) (179)), ((byte) (185)), ((byte) (36)), ((byte) (2)), ((byte) (224)), ((byte) (204)), ((byte) (181)), ((byte) (141)), ((byte) (214)), ((byte) (146)), ((byte) (65)), ((byte) (4)), ((byte) (43)), ((byte) (92)), ((byte) (148)), ((byte) (178)), ((byte) (195)), ((byte) (156)), ((byte) (157)), ((byte) (86)), ((byte) (240)), ((byte) (153)), ((byte) (188)), ((byte) (15)), ((byte) (129)), ((byte) (175)), ((byte) (235)), ((byte) (84)), ((byte) (237)), ((byte) (128)), ((byte) (166)), ((byte) (160)), ((byte) (199)), ((byte) (194)), ((byte) (67)), ((byte) (5)), ((byte) (4)), ((byte) (124)), ((byte) (156)), ((byte) (126)), ((byte) (7)), ((byte) (3)), ((byte) (16)), ((byte) (185)), ((byte) (189)), ((byte) (197)), ((byte) (22)), ((byte) (207)), ((byte) (25)), ((byte) (221)), ((byte) (227)), ((byte) (79)), ((byte) (115)), ((byte) (131)), ((byte) (114)), ((byte) (197)), ((byte) (102)), ((byte) (228)), ((byte) (91)), ((byte) (20)), ((byte) (196)), ((byte) (150)), ((byte) (209)), ((byte) (227)), ((byte) (36)), ((byte) (11)), ((byte) (182)), ((byte) (212)), ((byte) (247)), ((byte) (132)), ((byte) (46)), ((byte) (177)), ((byte) (231)), ((byte) (147)), ((byte) (2)), ((byte) (157)), ((byte) (245)), ((byte) (218)), ((byte) (170)), ((byte) (193)), ((byte) (217)), ((byte) (204)), ((byte) (94)), ((byte) (54)), ((byte) (233)), ((byte) (143)), ((byte) (191)), ((byte) (139)), ((byte) (218)), ((byte) (167)), ((byte) (69)), ((byte) (130)), ((byte) (242)), ((byte) (176)), ((byte) (245)), ((byte) (167)), ((byte) (228)), ((byte) (225)), ((byte) (128)), ((byte) (163)), ((byte) (23)), ((byte) (2)), ((byte) (3)), ((byte) (1)), ((byte) (0)), ((byte) (1)), ((byte) (2)), ((byte) (129)), ((byte) (128)), ((byte) (83)), ((byte) (188)), ((byte) (31)), ((byte) (28)), ((byte) (52)), ((byte) (9)), ((byte) (129)), ((byte) (30)), ((byte) (163)), ((byte) (251)), ((byte) (94)), ((byte) (144)), ((byte) (161)), ((byte) (52)), ((byte) (53)), ((byte) (64)), ((byte) (159)), ((byte) (41)), ((byte) (214)), ((byte) (181)), ((byte) (142)), ((byte) (93)), ((byte) (104)), ((byte) (106)), ((byte) (246)), ((byte) (150)), ((byte) (3)), ((byte) (247)), ((byte) (250)), ((byte) (249)), ((byte) (96)), ((byte) (79)), ((byte) (234)), ((byte) (226)), ((byte) (234)), ((byte) (41)), ((byte) (139)), ((byte) (35)), ((byte) (140)), ((byte) (159)), ((byte) (221)), ((byte) (73)), ((byte) (143)), ((byte) (168)), ((byte) (166)), ((byte) (98)), ((byte) (7)), ((byte) (68)), ((byte) (121)), ((byte) (161)), ((byte) (175)), ((byte) (249)), ((byte) (29)), ((byte) (152)), ((byte) (191)), ((byte) (133)), ((byte) (40)), ((byte) (3)), ((byte) (135)), ((byte) (20)), ((byte) (32)), ((byte) (186)), ((byte) (212)), ((byte) (150)), ((byte) (97)), ((byte) (42)), ((byte) (208)), ((byte) (170)), ((byte) (48)), ((byte) (25)), ((byte) (75)), ((byte) (64)), ((byte) (53)), ((byte) (176)), ((byte) (121)), ((byte) (11)), ((byte) (127)), ((byte) (215)), ((byte) (205)), ((byte) (100)), ((byte) (217)), ((byte) (147)), ((byte) (56)), ((byte) (226)), ((byte) (89)), ((byte) (224)), ((byte) (158)), ((byte) (58)), ((byte) (37)), ((byte) (39)), ((byte) (162)), ((byte) (217)), ((byte) (32)), ((byte) (176)), ((byte) (69)), ((byte) (95)), ((byte) (108)), ((byte) (21)), ((byte) (111)), ((byte) (16)), ((byte) (85)), ((byte) (167)), ((byte) (249)), ((byte) (61)), ((byte) (146)), ((byte) (60)), ((byte) (124)), ((byte) (35)), ((byte) (27)), ((byte) (192)), ((byte) (181)), ((byte) (23)), ((byte) (65)), ((byte) (94)), ((byte) (140)), ((byte) (220)), ((byte) (37)), ((byte) (29)), ((byte) (53)), ((byte) (43)), ((byte) (211)), ((byte) (151)), ((byte) (26)), ((byte) (111)), ((byte) (174)), ((byte) (235)), ((byte) (245)), ((byte) (249)), ((byte) (2)), ((byte) (65)), ((byte) (0)), ((byte) (215)), ((byte) (62)), ((byte) (237)), ((byte) (112)), ((byte) (254)), ((byte) (238)), ((byte) (14)), ((byte) (48)), ((byte) (41)), ((byte) (250)), ((byte) (215)), ((byte) (56)), ((byte) (207)), ((byte) (142)), ((byte) (193)), ((byte) (156)), ((byte) (120)), ((byte) (6)), ((byte) (45)), ((byte) (218)), ((byte) (51)), ((byte) (88)), ((byte) (161)), ((byte) (123)), ((byte) (191)), ((byte) (0)), ((byte) (185)), ((byte) (223)), ((byte) (234)), ((byte) (101)), ((byte) (134)), ((byte) (187)), ((byte) (204)), ((byte) (131)), ((byte) (206)), ((byte) (222)), ((byte) (195)), ((byte) (248)), ((byte) (137)), ((byte) (245)), ((byte) (159)), ((byte) (166)), ((byte) (29)), ((byte) (201)), ((byte) (251)), ((byte) (152)), ((byte) (161)), ((byte) (46)), ((byte) (224)), ((byte) (87)), ((byte) (110)), ((byte) (189)), ((byte) (87)), ((byte) (32)), ((byte) (249)), ((byte) (107)), ((byte) (19)), ((byte) (66)), ((byte) (157)), ((byte) (141)), ((byte) (102)), ((byte) (77)), ((byte) (122)), ((byte) (45)), ((byte) (2)), ((byte) (65)), ((byte) (0)), ((byte) (215)), ((byte) (0)), ((byte) (24)), ((byte) (84)), ((byte) (232)), ((byte) (55)), ((byte) (219)), ((byte) (248)), ((byte) (152)), ((byte) (123)), ((byte) (24)), ((byte) (51)), ((byte) (246)), ((byte) (40)), ((byte) (168)), ((byte) (140)), ((byte) (217)), ((byte) (253)), ((byte) (76)), ((byte) (78)), ((byte) (65)), ((byte) (115)), ((byte) (46)), ((byte) (121)), ((byte) (49)), ((byte) (204)), ((byte) (125)), ((byte) (66)), ((byte) (183)), ((byte) (161)), ((byte) (210)), ((byte) (188)), ((byte) (31)), ((byte) (98)), ((byte) (207)), ((byte) (21)), ((byte) (124)), ((byte) (98)), ((byte) (151)), ((byte) (112)), ((byte) (241)), ((byte) (21)), ((byte) (241)), ((byte) (51)), ((byte) (161)), ((byte) (157)), ((byte) (187)), ((byte) (95)), ((byte) (215)), ((byte) (90)), ((byte) (249)), ((byte) (36)), ((byte) (88)), ((byte) (172)), ((byte) (134)), ((byte) (106)), ((byte) (237)), ((byte) (212)), ((byte) (132)), ((byte) (228)), ((byte) (63)), ((byte) (254)), ((byte) (176)), ((byte) (211)), ((byte) (2)), ((byte) (65)), ((byte) (0)), ((byte) (212)), ((byte) (183)), ((byte) (132)), ((byte) (178)), ((byte) (57)), ((byte) (206)), ((byte) (11)), ((byte) (73)), ((byte) (128)), ((byte) (3)), ((byte) (60)), ((byte) (181)), ((byte) (17)), ((byte) (50)), ((byte) (52)), ((byte) (150)), ((byte) (172)), ((byte) (106)), ((byte) (246)), ((byte) (223)), ((byte) (128)), ((byte) (4)), ((byte) (228)), ((byte) (57)), ((byte) (198)), ((byte) (14)), ((byte) (50)), ((byte) (163)), ((byte) (94)), ((byte) (35)), ((byte) (13)), ((byte) (159)), ((byte) (4)), ((byte) (195)), ((byte) (114)), ((byte) (42)), ((byte) (230)), ((byte) (162)), ((byte) (245)), ((byte) (188)), ((byte) (63)), ((byte) (21)), ((byte) (76)), ((byte) (181)), ((byte) (51)), ((byte) (38)), ((byte) (168)), ((byte) (140)), ((byte) (9)), ((byte) (251)), ((byte) (126)), ((byte) (30)), ((byte) (50)), ((byte) (64)), ((byte) (13)), ((byte) (29)), ((byte) (203)), ((byte) (127)), ((byte) (246)), ((byte) (242)), ((byte) (41)), ((byte) (155)), ((byte) (1)), ((byte) (213)), ((byte) (2)), ((byte) (64)), ((byte) (36)), ((byte) (38)), ((byte) (28)), ((byte) (241)), ((byte) (49)), ((byte) (182)), ((byte) (42)), ((byte) (163)), ((byte) (10)), ((byte) (168)), ((byte) (47)), ((byte) (178)), ((byte) (148)), ((byte) (225)), ((byte) (211)), ((byte) (45)), ((byte) (19)), ((byte) (125)), ((byte) (214)), ((byte) (53)), ((byte) (150)), ((byte) (37)), ((byte) (146)), ((byte) (155)), ((byte) (199)), ((byte) (246)), ((byte) (180)), ((byte) (220)), ((byte) (225)), ((byte) (217)), ((byte) (48)), ((byte) (128)), ((byte) (118)), ((byte) (218)), ((byte) (123)), ((byte) (45)), ((byte) (6)), ((byte) (163)), ((byte) (225)), ((byte) (8)), ((byte) (153)), ((byte) (80)), ((byte) (114)), ((byte) (36)), ((byte) (151)), ((byte) (56)), ((byte) (217)), ((byte) (7)), ((byte) (77)), ((byte) (67)), ((byte) (59)), ((byte) (126)), ((byte) (147)), ((byte) (246)), ((byte) (54)), ((byte) (7)), ((byte) (134)), ((byte) (131)), ((byte) (99)), ((byte) (240)), ((byte) (168)), ((byte) (157)), ((byte) (223)), ((byte) (7)), ((byte) (2)), ((byte) (64)), ((byte) (62)), ((byte) (88)), ((byte) (3)), ((byte) (191)), ((byte) (234)), ((byte) (62)), ((byte) (52)), ((byte) (44)), ((byte) (183)), ((byte) (195)), ((byte) (9)), ((byte) (233)), ((byte) (244)), ((byte) (67)), ((byte) (65)), ((byte) (196)), ((byte) (124)), ((byte) (110)), ((byte) (117)), ((byte) (114)), ((byte) (93)), ((byte) (252)), ((byte) (163)), ((byte) (117)), ((byte) (29)), ((byte) (160)), ((byte) (238)), ((byte) (194)), ((byte) (31)), ((byte) (113)), ((byte) (176)), ((byte) (243)), ((byte) (29)), ((byte) (236)), ((byte) (129)), ((byte) (219)), ((byte) (69)), ((byte) (229)), ((byte) (106)), ((byte) (232)), ((byte) (224)), ((byte) (100)), ((byte) (144)), ((byte) (255)), ((byte) (185)), ((byte) (248)), ((byte) (18)), ((byte) (237)), ((byte) (85)), ((byte) (92)), ((byte) (155)), ((byte) (129)), ((byte) (205)), ((byte) (187)), ((byte) (6)), ((byte) (145)), ((byte) (254)), ((byte) (39)), ((byte) (44)), ((byte) (58)), ((byte) (237)), ((byte) (150)), ((byte) (59)), ((byte) (254)) };

    /**
     * X509KeyManager#getClientAliases(String keyType, Principal[] issuers)
     */
    public void test_getClientAliases() {
        init(X509KeyManagerTest.CLIENT);
        TestCase.assertNull(manager.getClientAliases(null, null));
        TestCase.assertNull(manager.getClientAliases("", null));
        String[] resArray = manager.getClientAliases(X509KeyManagerTest.TYPE_RSA, null);
        TestCase.assertNotNull(resArray);
        TestCase.assertEquals(3, resArray.length);
        assertKnownAliases(resArray);
    }

    /**
     * X509KeyManager#chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket)
     */
    public void test_chooseClientAlias() {
        init(X509KeyManagerTest.CLIENT);
        TestCase.assertNull(manager.chooseClientAlias(null, null, new Socket()));
        TestCase.assertNull(manager.chooseClientAlias(new String[0], null, new Socket()));
        TestCase.assertNull(manager.chooseClientAlias(new String[]{ "BOGUS" }, null, new Socket()));
        String res = manager.chooseClientAlias(new String[]{ X509KeyManagerTest.TYPE_RSA }, null, null);
        TestCase.assertNotNull(res);
        assertKnownAlias(res);
    }

    /**
     * X509KeyManager#getServerAliases(String keyType, Principal[] issuers)
     */
    public void test_getServerAliases() {
        init(X509KeyManagerTest.SERVER);
        TestCase.assertNull(manager.getServerAliases(null, null));
        TestCase.assertNull(manager.getServerAliases("", null));
        String[] resArray = manager.getServerAliases(X509KeyManagerTest.TYPE_RSA, null);
        TestCase.assertNotNull(resArray);
        TestCase.assertEquals("Incorrect length", 1, resArray.length);
        TestCase.assertEquals("Incorrect aliase", "serverkey_00", resArray[0].toLowerCase());
    }

    /**
     * X509KeyManager#chooseServerAlias(String keyType, Principal[] issuers, Socket socket)
     */
    public void test_chooseServerAlias() {
        init(X509KeyManagerTest.SERVER);
        TestCase.assertNull(manager.chooseServerAlias(null, null, new Socket()));
        TestCase.assertNull(manager.chooseServerAlias("", null, new Socket()));
        String res = manager.chooseServerAlias(X509KeyManagerTest.TYPE_RSA, null, null);
        TestCase.assertNotNull(res);
        TestCase.assertEquals("serverkey_00", res.toLowerCase());
        res = manager.chooseServerAlias(X509KeyManagerTest.TYPE_RSA, null, new Socket());
        TestCase.assertNotNull(res);
        TestCase.assertEquals("serverkey_00", res.toLowerCase());
    }

    /**
     * X509KeyManager#getCertificateChain(String alias)
     */
    public void test_getCertificateChain() {
        init(X509KeyManagerTest.SERVER);
        TestCase.assertNull("Not NULL for NULL parameter", manager.getCertificateChain(null));
        TestCase.assertNull("Not NULL for empty parameter", manager.getCertificateChain(""));
        TestCase.assertNull("Not NULL for clientAlias_01 parameter", manager.getCertificateChain("clientAlias_01"));
        TestCase.assertNull("Not NULL for serverAlias_00 parameter", manager.getCertificateChain("serverAlias_00"));
    }

    /**
     * X509KeyManager#getPrivateKey(String alias)
     */
    public void test_getPrivateKey() {
        init(X509KeyManagerTest.CLIENT);
        TestCase.assertNull("Not NULL for NULL parameter", manager.getPrivateKey(null));
        TestCase.assertNull("Not NULL for serverAlias_00 parameter", manager.getPrivateKey("serverAlias_00"));
        TestCase.assertNull("Not NULL for clientAlias_02 parameter", manager.getPrivateKey("clientAlias_02"));
    }
}

