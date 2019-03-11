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
package org.jf.dexlib2.writer;


import java.io.IOException;
import org.junit.Test;


public class DexWriterUleb128Test {
    private NakedByteArrayOutputStream output = new NakedByteArrayOutputStream();

    private int startPosition;

    private DexDataWriter writer;

    @Test
    public void testUleb128() throws IOException {
        performTest(0, new byte[]{ 0, 17 }, 1);
        performTest(1, new byte[]{ 1, 17 }, 1);
        performTest(63, new byte[]{ 63, 17 }, 1);
        performTest(64, new byte[]{ 64, 17 }, 1);
        performTest(112, new byte[]{ 112, 17 }, 1);
        performTest(127, new byte[]{ 127, 17 }, 1);
        performTest(128, new byte[]{ ((byte) (128)), 1, 17 }, 2);
        performTest(256, new byte[]{ ((byte) (128)), 2, 17 }, 2);
        performTest(2048, new byte[]{ ((byte) (128)), 16, 17 }, 2);
        performTest(8064, new byte[]{ ((byte) (128)), 63, 17 }, 2);
        performTest(8192, new byte[]{ ((byte) (128)), 64, 17 }, 2);
        performTest(8320, new byte[]{ ((byte) (128)), 65, 17 }, 2);
        performTest(14336, new byte[]{ ((byte) (128)), 112, 17 }, 2);
        performTest(16256, new byte[]{ ((byte) (128)), 127, 17 }, 2);
        performTest(255, new byte[]{ ((byte) (255)), 1, 17 }, 2);
        performTest(383, new byte[]{ ((byte) (255)), 2, 17 }, 2);
        performTest(2175, new byte[]{ ((byte) (255)), 16, 17 }, 2);
        performTest(8191, new byte[]{ ((byte) (255)), 63, 17 }, 2);
        performTest(8319, new byte[]{ ((byte) (255)), 64, 17 }, 2);
        performTest(8447, new byte[]{ ((byte) (255)), 65, 17 }, 2);
        performTest(14463, new byte[]{ ((byte) (255)), 112, 17 }, 2);
        performTest(16383, new byte[]{ ((byte) (255)), 127, 17 }, 2);
        performTest(16384, new byte[]{ ((byte) (128)), ((byte) (128)), 1, 17 }, 3);
        performTest(32768, new byte[]{ ((byte) (128)), ((byte) (128)), 2, 17 }, 3);
        performTest(262144, new byte[]{ ((byte) (128)), ((byte) (128)), 16, 17 }, 3);
        performTest(1032192, new byte[]{ ((byte) (128)), ((byte) (128)), 63, 17 }, 3);
        performTest(1048576, new byte[]{ ((byte) (128)), ((byte) (128)), 64, 17 }, 3);
        performTest(1064960, new byte[]{ ((byte) (128)), ((byte) (128)), 65, 17 }, 3);
        performTest(1835008, new byte[]{ ((byte) (128)), ((byte) (128)), 112, 17 }, 3);
        performTest(2080768, new byte[]{ ((byte) (128)), ((byte) (128)), 127, 17 }, 3);
        performTest(32767, new byte[]{ ((byte) (255)), ((byte) (255)), 1, 17 }, 3);
        performTest(49151, new byte[]{ ((byte) (255)), ((byte) (255)), 2, 17 }, 3);
        performTest(278527, new byte[]{ ((byte) (255)), ((byte) (255)), 16, 17 }, 3);
        performTest(1048575, new byte[]{ ((byte) (255)), ((byte) (255)), 63, 17 }, 3);
        performTest(1064959, new byte[]{ ((byte) (255)), ((byte) (255)), 64, 17 }, 3);
        performTest(1081343, new byte[]{ ((byte) (255)), ((byte) (255)), 65, 17 }, 3);
        performTest(1851391, new byte[]{ ((byte) (255)), ((byte) (255)), 112, 17 }, 3);
        performTest(2097151, new byte[]{ ((byte) (255)), ((byte) (255)), 127, 17 }, 3);
        performTest(2097152, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 1, 17 }, 4);
        performTest(4194304, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 2, 17 }, 4);
        performTest(33554432, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 16, 17 }, 4);
        performTest(132120576, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 63, 17 }, 4);
        performTest(134217728, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 64, 17 }, 4);
        performTest(136314880, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 65, 17 }, 4);
        performTest(234881024, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 112, 17 }, 4);
        performTest(266338304, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), 127, 17 }, 4);
        performTest(4194303, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 1, 17 }, 4);
        performTest(6291455, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 2, 17 }, 4);
        performTest(35651583, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 16, 17 }, 4);
        performTest(134217727, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 63, 17 }, 4);
        performTest(136314879, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 64, 17 }, 4);
        performTest(138412031, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 65, 17 }, 4);
        performTest(236978175, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 112, 17 }, 4);
        performTest(268435455, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), 127, 17 }, 4);
        performTest(268435456, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 1, 17 }, 5);
        performTest(536870912, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 2, 17 }, 5);
        performTest(1879048192, new byte[]{ ((byte) (128)), ((byte) (128)), ((byte) (128)), ((byte) (128)), 7, 17 }, 5);
        performTest(536870911, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 1, 17 }, 5);
        performTest(805306367, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 2, 17 }, 5);
        performTest(2147483647, new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 7, 17 }, 5);
        performTest(204, new byte[]{ ((byte) (204)), 1 });
        performTest(15207, new byte[]{ ((byte) (231)), 118 });
        performTest(461731209, new byte[]{ ((byte) (137)), ((byte) (235)), ((byte) (149)), ((byte) (220)), 1 });
        performTest(928875237, new byte[]{ ((byte) (229)), ((byte) (133)), ((byte) (246)), ((byte) (186)), 3 });
        performTest(1428478608, new byte[]{ ((byte) (144)), ((byte) (181)), ((byte) (147)), ((byte) (169)), 5 });
        performTest(53, new byte[]{ 53 });
        performTest(215, new byte[]{ ((byte) (215)), 1 });
        performTest(99, new byte[]{ 99 });
        performTest(2280283, new byte[]{ ((byte) (219)), ((byte) (150)), ((byte) (139)), 1 });
        performTest(22622, new byte[]{ ((byte) (222)), ((byte) (176)), 1 });
        performTest(1566746981, new byte[]{ ((byte) (229)), ((byte) (210)), ((byte) (138)), ((byte) (235)), 5 });
        performTest(1794208475, new byte[]{ ((byte) (219)), ((byte) (229)), ((byte) (197)), ((byte) (215)), 6 });
        performTest(14, new byte[]{ 14 });
        performTest(12017530, new byte[]{ ((byte) (250)), ((byte) (190)), ((byte) (221)), 5 });
        performTest(34308, new byte[]{ ((byte) (132)), ((byte) (140)), 2 });
        performTest(828522534, new byte[]{ ((byte) (166)), ((byte) (128)), ((byte) (137)), ((byte) (139)), 3 });
        performTest(141, new byte[]{ ((byte) (141)), 1 });
        performTest(192, new byte[]{ ((byte) (192)), 1 });
        performTest(225843403, new byte[]{ ((byte) (203)), ((byte) (177)), ((byte) (216)), 107 });
        performTest(255, new byte[]{ ((byte) (255)), 1 });
        performTest(1553088066, new byte[]{ ((byte) (194)), ((byte) (252)), ((byte) (200)), ((byte) (228)), 5 });
        performTest(145, new byte[]{ ((byte) (145)), 1 });
        performTest(12455831, new byte[]{ ((byte) (151)), ((byte) (159)), ((byte) (248)), 5 });
        performTest(143378310, new byte[]{ ((byte) (134)), ((byte) (143)), ((byte) (175)), 68 });
        performTest(9218714, new byte[]{ ((byte) (154)), ((byte) (213)), ((byte) (178)), 4 });
        performTest(19182, new byte[]{ ((byte) (238)), ((byte) (149)), 1 });
        performTest(4426886, new byte[]{ ((byte) (134)), ((byte) (153)), ((byte) (142)), 2 });
        performTest(192, new byte[]{ ((byte) (192)), 1 });
        performTest(46214, new byte[]{ ((byte) (134)), ((byte) (233)), 2 });
        performTest(33789, new byte[]{ ((byte) (253)), ((byte) (135)), 2 });
        performTest(123, new byte[]{ 123 });
        performTest(499666452, new byte[]{ ((byte) (148)), ((byte) (156)), ((byte) (161)), ((byte) (238)), 1 });
        performTest(11772, new byte[]{ ((byte) (252)), 91 });
        performTest(136, new byte[]{ ((byte) (136)), 1 });
        performTest(37278, new byte[]{ ((byte) (158)), ((byte) (163)), 2 });
        performTest(12239, new byte[]{ ((byte) (207)), 95 });
        performTest(15730292, new byte[]{ ((byte) (244)), ((byte) (140)), ((byte) (192)), 7 });
        performTest(15556477, new byte[]{ ((byte) (253)), ((byte) (190)), ((byte) (181)), 7 });
        performTest(56281, new byte[]{ ((byte) (217)), ((byte) (183)), 3 });
        performTest(161, new byte[]{ ((byte) (161)), 1 });
        performTest(16185196, new byte[]{ ((byte) (236)), ((byte) (238)), ((byte) (219)), 7 });
        performTest(2026863, new byte[]{ ((byte) (239)), ((byte) (218)), 123 });
        performTest(2396, new byte[]{ ((byte) (220)), 18 });
        performTest(30, new byte[]{ 30 });
        performTest(229, new byte[]{ ((byte) (229)), 1 });
        performTest(3092243, new byte[]{ ((byte) (147)), ((byte) (222)), ((byte) (188)), 1 });
        performTest(25, new byte[]{ 25 });
        performTest(63, new byte[]{ 63 });
        performTest(30179, new byte[]{ ((byte) (227)), ((byte) (235)), 1 });
        performTest(6792388, new byte[]{ ((byte) (196)), ((byte) (201)), ((byte) (158)), 3 });
        performTest(47432, new byte[]{ ((byte) (200)), ((byte) (242)), 2 });
        performTest(884066782, new byte[]{ ((byte) (222)), ((byte) (147)), ((byte) (199)), ((byte) (165)), 3 });
        performTest(22768, new byte[]{ ((byte) (240)), ((byte) (177)), 1 });
        performTest(0, new byte[]{ 0 });
        performTest(10138597, new byte[]{ ((byte) (229)), ((byte) (231)), ((byte) (234)), 4 });
        performTest(1279953469, new byte[]{ ((byte) (189)), ((byte) (148)), ((byte) (170)), ((byte) (226)), 4 });
        performTest(153, new byte[]{ ((byte) (153)), 1 });
        performTest(1730537, new byte[]{ ((byte) (233)), ((byte) (207)), 105 });
        performTest(6150957, new byte[]{ ((byte) (173)), ((byte) (182)), ((byte) (247)), 2 });
        performTest(248297088, new byte[]{ ((byte) (128)), ((byte) (237)), ((byte) (178)), 118 });
        performTest(1762704368, new byte[]{ ((byte) (240)), ((byte) (247)), ((byte) (194)), ((byte) (200)), 6 });
        performTest(197, new byte[]{ ((byte) (197)), 1 });
        performTest(14512677, new byte[]{ ((byte) (165)), ((byte) (228)), ((byte) (245)), 6 });
        performTest(1164044846, new byte[]{ ((byte) (174)), ((byte) (212)), ((byte) (135)), ((byte) (171)), 4 });
        performTest(8343304, new byte[]{ ((byte) (136)), ((byte) (158)), ((byte) (253)), 3 });
        performTest(6527, new byte[]{ ((byte) (255)), 50 });
        performTest(12102931, new byte[]{ ((byte) (147)), ((byte) (218)), ((byte) (226)), 5 });
        performTest(1015897524, new byte[]{ ((byte) (180)), ((byte) (187)), ((byte) (181)), ((byte) (228)), 3 });
        performTest(2118901629, new byte[]{ ((byte) (253)), ((byte) (190)), ((byte) (175)), ((byte) (242)), 7 });
        performTest(2002467, new byte[]{ ((byte) (163)), ((byte) (156)), 122 });
        performTest(5634, new byte[]{ ((byte) (130)), 44 });
        performTest(226, new byte[]{ ((byte) (226)), 1 });
        performTest(14569, new byte[]{ ((byte) (233)), 113 });
        performTest(12551781, new byte[]{ ((byte) (229)), ((byte) (140)), ((byte) (254)), 5 });
        performTest(67, new byte[]{ 67 });
        performTest(13228396, new byte[]{ ((byte) (236)), ((byte) (178)), ((byte) (167)), 6 });
        performTest(4968816, new byte[]{ ((byte) (240)), ((byte) (162)), ((byte) (175)), 2 });
        performTest(8831259, new byte[]{ ((byte) (155)), ((byte) (130)), ((byte) (155)), 4 });
        performTest(438702567, new byte[]{ ((byte) (231)), ((byte) (163)), ((byte) (152)), ((byte) (209)), 1 });
        performTest(16723818, new byte[]{ ((byte) (234)), ((byte) (222)), ((byte) (252)), 7 });
        performTest(1862604341, new byte[]{ ((byte) (181)), ((byte) (172)), ((byte) (148)), ((byte) (248)), 6 });
        performTest(30143, new byte[]{ ((byte) (191)), ((byte) (235)), 1 });
        performTest(15257157, new byte[]{ ((byte) (197)), ((byte) (156)), ((byte) (163)), 7 });
        performTest(692494808, new byte[]{ ((byte) (216)), ((byte) (195)), ((byte) (154)), ((byte) (202)), 2 });
        performTest(226, new byte[]{ ((byte) (226)), 1 });
        performTest(17646, new byte[]{ ((byte) (238)), ((byte) (137)), 1 });
        performTest(17530, new byte[]{ ((byte) (250)), ((byte) (136)), 1 });
        performTest(2327, new byte[]{ ((byte) (151)), 18 });
        performTest(37, new byte[]{ 37 });
        performTest(1388493035, new byte[]{ ((byte) (235)), ((byte) (241)), ((byte) (138)), ((byte) (150)), 5 });
        performTest(400211684, new byte[]{ ((byte) (228)), ((byte) (253)), ((byte) (234)), ((byte) (190)), 1 });
        performTest(40298, new byte[]{ ((byte) (234)), ((byte) (186)), 2 });
        performTest(12890413, new byte[]{ ((byte) (173)), ((byte) (226)), ((byte) (146)), 6 });
        performTest(13194781, new byte[]{ ((byte) (157)), ((byte) (172)), ((byte) (165)), 6 });
        performTest(34983, new byte[]{ ((byte) (167)), ((byte) (145)), 2 });
        performTest(1383960442, new byte[]{ ((byte) (250)), ((byte) (158)), ((byte) (246)), ((byte) (147)), 5 });
        performTest(11313, new byte[]{ ((byte) (177)), 88 });
        performTest(15244, new byte[]{ ((byte) (140)), 119 });
        performTest(49704, new byte[]{ ((byte) (168)), ((byte) (132)), 3 });
        performTest(14102739, new byte[]{ ((byte) (211)), ((byte) (225)), ((byte) (220)), 6 });
    }
}

