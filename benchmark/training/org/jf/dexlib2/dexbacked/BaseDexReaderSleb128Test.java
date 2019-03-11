/**
 * Copyright 2012, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jf.dexlib2.dexbacked;


import org.junit.Test;


public class BaseDexReaderSleb128Test {
    @Test
    public void testSleb128() {
        performTest(0, new byte[]{ 0, 17 }, 1);
        performTest(1, new byte[]{ 1, 17 }, 1);
        performTest(63, new byte[]{ 63, 17 }, 1);
        performTest(-64, new byte[]{ 64, 17 }, 1);
        performTest(-16, new byte[]{ 112, 17 }, 1);
        performTest(-1, new byte[]{ 127, 17 }, 1);
        performTest(128, new byte[]{ ((byte) (128)), 1, 17 }, 2);
        performTest(256, new byte[]{ ((byte) (128)), 2, 17 }, 2);
        performTest(2048, new byte[]{ ((byte) (128)), 16, 17 }, 2);
        performTest(8064, new byte[]{ ((byte) (128)), 63, 17 }, 2);
        performTest(-8192, new byte[]{ ((byte) (128)), 64, 17 }, 2);
        performTest(-8064, new byte[]{ ((byte) (128)), 65, 17 }, 2);
        performTest(-2048, new byte[]{ ((byte) (128)), 112, 17 }, 2);
        performTest(-128, new byte[]{ ((byte) (128)), 127, 17 }, 2);
        performTest(255, new byte[]{ ((byte) (255)), 1, 17 }, 2);
        performTest(383, new byte[]{ ((byte) (255)), 2, 17 }, 2);
        performTest(2175, new byte[]{ ((byte) (255)), 16, 17 }, 2);
        performTest(8191, new byte[]{ ((byte) (255)), 63, 17 }, 2);
        performTest(-8065, new byte[]{ ((byte) (255)), 64, 17 }, 2);
        performTest(-7937, new byte[]{ ((byte) (255)), 65, 17 }, 2);
        performTest(-1921, new byte[]{ ((byte) (255)), 112, 17 }, 2);
        performTest(-1, new byte[]{ ((byte) (255)), 127, 17 }, 2);
        performTest(16384, new byte[]{ ((byte) (128)), ((byte) (128)), 1, 17 }, 3);
        performTest(32768, new byte[]{ ((byte) (128)), ((byte) (128)), 2, 17 }, 3);
        performTest(262144, new byte[]{ ((byte) (128)), ((byte) (128)), 16, 17 }, 3);
        performTest(1032192, new byte[]{ ((byte) (128)), ((byte) (128)), 63, 17 }, 3);
        performTest(-1048576, new byte[]{ ((byte) (128)), ((byte) (128)), 64, 17 }, 3);
        performTest(-1032192, new byte[]{ ((byte) (128)), ((byte) (128)), 65, 17 }, 3);
        performTest(-262144, new byte[]{ ((byte) (128)), ((byte) (128)), 112, 17 }, 3);
        performTest(-16384, new byte[]{ ((byte) (128)), ((byte) (128)), 127, 17 }, 3);
        performTest(32767, new byte[]{ ((byte) (255)), ((byte) (255)), 1, 17 }, 3);
        performTest(49151, new byte[]{ ((byte) (255)), ((byte) (255)), 2, 17 }, 3);
        performTest(278527, new byte[]{ ((byte) (255)), ((byte) (255)), 16, 17 }, 3);
        performTest(1048575, new byte[]{ ((byte) (255)), ((byte) (255)), 63, 17 }, 3);
        performTest(-1032193, new byte[]{ ((byte) (255)), ((byte) (255)), 64, 17 }, 3);
        performTest(-1015809, new byte[]{ ((byte) (255)), ((byte) (255)), 65, 17 }, 3);
        performTest(-245761, new byte[]{ ((byte) (255)), ((byte) (255)), 112, 17 }, 3);
        performTest(-1, new byte[]{ ((byte) (255)), ((byte) (255)), 127, 17 }, 3);
        performTest(2097152, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 1, 17 }, 4);
        performTest(4194304, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 2, 17 }, 4);
        performTest(33554432, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 16, 17 }, 4);
        performTest(132120576, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 63, 17 }, 4);
        performTest(-134217728, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 64, 17 }, 4);
        performTest(-132120576, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 65, 17 }, 4);
        performTest(-33554432, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 112, 17 }, 4);
        performTest(-2097152, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 127, 17 }, 4);
        performTest(4194303, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 1, 17 }, 4);
        performTest(6291455, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 2, 17 }, 4);
        performTest(35651583, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 16, 17 }, 4);
        performTest(134217727, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 63, 17 }, 4);
        performTest(-132120577, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 64, 17 }, 4);
        performTest(-130023425, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 65, 17 }, 4);
        performTest(-31457281, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 112, 17 }, 4);
        performTest(-1, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 127, 17 }, 4);
        performTest(268435456, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 1, 17 }, 5);
        performTest(536870912, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 2, 17 }, 5);
        performTest(1879048192, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 7, 17 }, 5);
        performTest(1879048192, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 23, 17 }, 5);
        performTest(1879048192, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 71, 17 }, 5);
        performTest(1879048192, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 119, 17 }, 5);
        performTest(-2147483648, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 8, 17 }, 5);
        performTest(-536870912, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 14, 17 }, 5);
        performTest(-268435456, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 15, 17 }, 5);
        performTest(536870911, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 1, 17 }, 5);
        performTest(805306367, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 2, 17 }, 5);
        performTest(2147483647, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 7, 17 }, 5);
        performTest(2147483647, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 23, 17 }, 5);
        performTest(2147483647, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 71, 17 }, 5);
        performTest(2147483647, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 119, 17 }, 5);
        performTest(-1879048193, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 8, 17 }, 5);
        performTest(-268435457, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 14, 17 }, 5);
        performTest(-1, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 15, 17 }, 5);
        performTest(8493010, new byte[]{ ((byte) (210)), ((byte) (175)), ((byte) (134)), 4 });
        performTest(1019800440, new byte[]{ ((byte) (248)), ((byte) (214)), ((byte) (163)), ((byte) (230)), 3 });
        performTest(1362132786, new byte[]{ ((byte) (178)), ((byte) (254)), ((byte) (193)), ((byte) (137)), 5 });
        performTest(34963, new byte[]{ ((byte) (147)), ((byte) (145)), 2 });
        performTest(33019, new byte[]{ ((byte) (251)), ((byte) (129)), 2 });
        performTest(61, new byte[]{ 61 });
        performTest(39036, new byte[]{ ((byte) (252)), ((byte) (176)), 2 });
        performTest(5973112, new byte[]{ ((byte) (248)), ((byte) (200)), ((byte) (236)), 2 });
        performTest(1697976025, new byte[]{ ((byte) (217)), ((byte) (157)), ((byte) (212)), ((byte) (169)), 6 });
        performTest(62, new byte[]{ 62 });
        performTest(31518, new byte[]{ ((byte) (158)), ((byte) (246)), 1 });
        performTest(181, new byte[]{ ((byte) (181)), 1 });
        performTest(150, new byte[]{ ((byte) (150)), 1 });
        performTest(161, new byte[]{ ((byte) (161)), 1 });
        performTest(1297131613, new byte[]{ ((byte) (221)), ((byte) (208)), ((byte) (194)), ((byte) (234)), 4 });
        performTest(50201, new byte[]{ ((byte) (153)), ((byte) (136)), 3 });
        performTest(53044, new byte[]{ ((byte) (180)), ((byte) (158)), 3 });
        performTest(21117, new byte[]{ ((byte) (253)), ((byte) (164)), 1 });
        performTest(5908628, new byte[]{ ((byte) (148)), ((byte) (209)), ((byte) (232)), 2 });
        performTest(166, new byte[]{ ((byte) (166)), 1 });
        performTest(15877, new byte[]{ ((byte) (133)), ((byte) (252)), 0 });
        performTest(95, new byte[]{ ((byte) (223)), 0 });
        performTest(14866863, new byte[]{ ((byte) (175)), ((byte) (179)), ((byte) (139)), 7 });
        performTest(-1470890476, new byte[]{ ((byte) (148)), ((byte) (252)), ((byte) (207)), ((byte) (194)), 10 });
        performTest(-1470890476, new byte[]{ ((byte) (148)), ((byte) (252)), ((byte) (207)), ((byte) (194)), 122 });
        performTest(293463857, new byte[]{ ((byte) (177)), ((byte) (206)), ((byte) (247)), ((byte) (139)), 1 });
        performTest(47049, new byte[]{ ((byte) (201)), ((byte) (239)), 2 });
        performTest(177, new byte[]{ ((byte) (177)), 1 });
        performTest(5183821, new byte[]{ ((byte) (205)), ((byte) (178)), ((byte) (188)), 2 });
        performTest(9262899, new byte[]{ ((byte) (179)), ((byte) (174)), ((byte) (181)), 4 });
        performTest(673507758, new byte[]{ ((byte) (174)), ((byte) (211)), ((byte) (147)), ((byte) (193)), 2 });
        performTest(31022, new byte[]{ ((byte) (174)), ((byte) (242)), 1 });
        performTest(44527, new byte[]{ ((byte) (239)), ((byte) (219)), 2 });
        performTest(92, new byte[]{ ((byte) (220)), 0 });
        performTest(351915256, new byte[]{ ((byte) (248)), ((byte) (153)), ((byte) (231)), ((byte) (167)), 1 });
        performTest(209, new byte[]{ ((byte) (209)), 1 });
        performTest(-1166508339, new byte[]{ ((byte) (205)), ((byte) (253)), ((byte) (225)), ((byte) (211)), 123 });
        performTest(79, new byte[]{ ((byte) (207)), 0 });
        performTest(64259, new byte[]{ ((byte) (131)), ((byte) (246)), 3 });
        performTest(-297829160, new byte[]{ ((byte) (216)), ((byte) (249)), ((byte) (253)), ((byte) (241)), 126 });
        performTest(39534, new byte[]{ ((byte) (238)), ((byte) (180)), 2 });
        performTest(9374083, new byte[]{ ((byte) (131)), ((byte) (147)), ((byte) (188)), 4 });
        performTest(973135903, new byte[]{ ((byte) (159)), ((byte) (192)), ((byte) (131)), ((byte) (208)), 3 });
        performTest(2136157587, new byte[]{ ((byte) (147)), ((byte) (219)), ((byte) (204)), ((byte) (250)), 7 });
        performTest(1547661, new byte[]{ ((byte) (141)), ((byte) (187)), ((byte) (222)), 0 });
        performTest(4037, new byte[]{ ((byte) (197)), 31 });
        performTest(17, new byte[]{ 17 });
        performTest(211506152, new byte[]{ ((byte) (232)), ((byte) (167)), ((byte) (237)), ((byte) (228)), 0 });
        performTest(151, new byte[]{ ((byte) (151)), 1 });
        performTest(21171, new byte[]{ ((byte) (179)), ((byte) (165)), 1 });
        performTest(146, new byte[]{ ((byte) (146)), 1 });
        performTest(210, new byte[]{ ((byte) (210)), 1 });
        performTest(1299248, new byte[]{ ((byte) (176)), ((byte) (166)), ((byte) (207)), 0 });
        performTest(6762305, new byte[]{ ((byte) (193)), ((byte) (222)), ((byte) (156)), 3 });
        performTest(207, new byte[]{ ((byte) (207)), 1 });
        performTest(1423816413, new byte[]{ ((byte) (221)), ((byte) (237)), ((byte) (246)), ((byte) (166)), 5 });
        performTest(8305838, new byte[]{ ((byte) (174)), ((byte) (249)), ((byte) (250)), 3 });
        performTest(56, new byte[]{ 56 });
        performTest(-2129070873, new byte[]{ ((byte) (231)), ((byte) (233)), ((byte) (227)), ((byte) (136)), 120 });
        performTest(172, new byte[]{ ((byte) (172)), 1 });
        performTest(11219100, new byte[]{ ((byte) (156)), ((byte) (225)), ((byte) (172)), 5 });
        performTest(1833394, new byte[]{ ((byte) (178)), ((byte) (243)), ((byte) (239)), 0 });
        performTest(9124976, new byte[]{ ((byte) (240)), ((byte) (248)), ((byte) (172)), 4 });
        performTest(30580, new byte[]{ ((byte) (244)), ((byte) (238)), 1 });
        performTest(3401785, new byte[]{ ((byte) (185)), ((byte) (208)), ((byte) (207)), 1 });
        performTest(-2066328160, new byte[]{ ((byte) (160)), ((byte) (171)), ((byte) (217)), ((byte) (166)), 120 });
        performTest(-212582669, new byte[]{ ((byte) (243)), ((byte) (253)), ((byte) (208)), ((byte) (154)), 127 });
        performTest(1931134, new byte[]{ ((byte) (254)), ((byte) (238)), ((byte) (245)), 0 });
        performTest(247, new byte[]{ ((byte) (247)), 1 });
        performTest(9284, new byte[]{ ((byte) (196)), ((byte) (200)), 0 });
        performTest(21355, new byte[]{ ((byte) (235)), ((byte) (166)), 1 });
        performTest(168, new byte[]{ ((byte) (168)), 1 });
        performTest(56316, new byte[]{ ((byte) (252)), ((byte) (183)), 3 });
        performTest(15101367, new byte[]{ ((byte) (183)), ((byte) (219)), ((byte) (153)), 7 });
        performTest(47050, new byte[]{ ((byte) (202)), ((byte) (239)), 2 });
        performTest(-402140955, new byte[]{ ((byte) (229)), ((byte) (161)), ((byte) (159)), ((byte) (192)), 126 });
        performTest(1700, new byte[]{ ((byte) (164)), 13 });
        performTest(100, new byte[]{ ((byte) (228)), 0 });
        performTest(15989621, new byte[]{ ((byte) (245)), ((byte) (246)), ((byte) (207)), 7 });
        performTest(-1221806407, new byte[]{ ((byte) (185)), ((byte) (237)), ((byte) (178)), ((byte) (185)), 123 });
        performTest(253, new byte[]{ ((byte) (253)), 1 });
        performTest(46219, new byte[]{ ((byte) (139)), ((byte) (233)), 2 });
        performTest(14787, new byte[]{ ((byte) (195)), ((byte) (243)), 0 });
        performTest(314093501, new byte[]{ ((byte) (189)), ((byte) (223)), ((byte) (226)), ((byte) (149)), 1 });
        performTest(5697865, new byte[]{ ((byte) (201)), ((byte) (226)), ((byte) (219)), 2 });
        performTest(191, new byte[]{ ((byte) (191)), 1 });
        performTest(986129537, new byte[]{ ((byte) (129)), ((byte) (201)), ((byte) (156)), ((byte) (214)), 3 });
        performTest(-1231247583, new byte[]{ ((byte) (161)), ((byte) (206)), ((byte) (242)), ((byte) (180)), 123 });
        performTest(9088, new byte[]{ ((byte) (128)), ((byte) (199)), 0 });
        performTest(6644328, new byte[]{ ((byte) (232)), ((byte) (196)), ((byte) (149)), 3 });
        performTest(113, new byte[]{ ((byte) (241)), 0 });
        performTest(15754277, new byte[]{ ((byte) (165)), ((byte) (200)), ((byte) (193)), 7 });
        performTest(11896779, new byte[]{ ((byte) (203)), ((byte) (143)), ((byte) (214)), 5 });
        performTest(34626, new byte[]{ ((byte) (194)), ((byte) (142)), 2 });
        performTest(198, new byte[]{ ((byte) (198)), 1 });
        performTest(-295536481, new byte[]{ ((byte) (159)), ((byte) (241)), ((byte) (137)), ((byte) (243)), 126 });
        performTest(18186, new byte[]{ ((byte) (138)), ((byte) (142)), 1 });
        performTest(300899548, new byte[]{ ((byte) (220)), ((byte) (185)), ((byte) (189)), ((byte) (143)), 1 });
        performTest(12865193, new byte[]{ ((byte) (169)), ((byte) (157)), ((byte) (145)), 6 });
        performTest(-1807253640, new byte[]{ ((byte) (248)), ((byte) (254)), ((byte) (157)), ((byte) (162)), 121 });
        performTest(-461763761, new byte[]{ ((byte) (207)), ((byte) (150)), ((byte) (232)), ((byte) (163)), 126 });
    }

    @Test
    public void testSleb128Failure() {
        // test the case when the MSB of the last byte is set
        performFailureTest(new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (129)), 17 });
        performFailureTest(new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (130)), 17 });
        performFailureTest(new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (135)), 17 });
        performFailureTest(new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (151)), 17 });
        performFailureTest(new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (199)), 17 });
        performFailureTest(new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (247)), 17 });
        performFailureTest(new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (136)), 17 });
        performFailureTest(new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (142)), 17 });
        performFailureTest(new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (143)), 17 });
        performFailureTest(new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (255)), 17 });
        performFailureTest(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (129)), 17 });
        performFailureTest(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (130)), 17 });
        performFailureTest(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (135)), 17 });
        performFailureTest(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (151)), 17 });
        performFailureTest(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (199)), 17 });
        performFailureTest(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (247)), 17 });
        performFailureTest(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (136)), 17 });
        performFailureTest(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (142)), 17 });
        performFailureTest(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 17 });
    }
}

