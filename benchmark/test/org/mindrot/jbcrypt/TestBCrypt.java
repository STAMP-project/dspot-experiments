/**
 * Copyright (c) 2006 Damien Miller <djm@mindrot.org>
 */
/**
 *
 */
/**
 * Permission to use, copy, modify, and distribute this software for any
 */
/**
 * purpose with or without fee is hereby granted, provided that the above
 */
/**
 * copyright notice and this permission notice appear in all copies.
 */
/**
 *
 */
/**
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 */
/**
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 */
/**
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 */
/**
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 */
/**
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 */
/**
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 */
/**
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package org.mindrot.jbcrypt;


import java.util.Arrays;
import junit.framework.TestCase;


/**
 * JUnit unit tests for BCrypt routines
 *
 * @author Damien Miller
 * @version 0.2
 */
public class TestBCrypt extends TestCase {
    String[][] test_vectors = new String[][]{ new String[]{ "", "$2a$06$DCq7YPn5Rq63x1Lad4cll.", "$2a$06$DCq7YPn5Rq63x1Lad4cll.TV4S6ytwfsfvkgY8jIucDrjc8deX1s." }, new String[]{ "", "$2a$08$HqWuK6/Ng6sg9gQzbLrgb.", "$2a$08$HqWuK6/Ng6sg9gQzbLrgb.Tl.ZHfXLhvt/SgVyWhQqgqcZ7ZuUtye" }, new String[]{ "", "$2a$10$k1wbIrmNyFAPwPVPSVa/ze", "$2a$10$k1wbIrmNyFAPwPVPSVa/zecw2BCEnBwVS2GbrmgzxFUOqW9dk4TCW" }, new String[]{ "", "$2a$12$k42ZFHFWqBp3vWli.nIn8u", "$2a$12$k42ZFHFWqBp3vWli.nIn8uYyIkbvYRvodzbfbK18SSsY.CsIQPlxO" }, new String[]{ "a", "$2a$06$m0CrhHm10qJ3lXRY.5zDGO", "$2a$06$m0CrhHm10qJ3lXRY.5zDGO3rS2KdeeWLuGmsfGlMfOxih58VYVfxe" }, new String[]{ "a", "$2a$08$cfcvVd2aQ8CMvoMpP2EBfe", "$2a$08$cfcvVd2aQ8CMvoMpP2EBfeodLEkkFJ9umNEfPD18.hUF62qqlC/V." }, new String[]{ "a", "$2a$10$k87L/MF28Q673VKh8/cPi.", "$2a$10$k87L/MF28Q673VKh8/cPi.SUl7MU/rWuSiIDDFayrKk/1tBsSQu4u" }, new String[]{ "a", "$2a$12$8NJH3LsPrANStV6XtBakCe", "$2a$12$8NJH3LsPrANStV6XtBakCez0cKHXVxmvxIlcz785vxAIZrihHZpeS" }, new String[]{ "abc", "$2a$06$If6bvum7DFjUnE9p2uDeDu", "$2a$06$If6bvum7DFjUnE9p2uDeDu0YHzrHM6tf.iqN8.yx.jNN1ILEf7h0i" }, new String[]{ "abc", "$2a$08$Ro0CUfOqk6cXEKf3dyaM7O", "$2a$08$Ro0CUfOqk6cXEKf3dyaM7OhSCvnwM9s4wIX9JeLapehKK5YdLxKcm" }, new String[]{ "abc", "$2a$10$WvvTPHKwdBJ3uk0Z37EMR.", "$2a$10$WvvTPHKwdBJ3uk0Z37EMR.hLA2W6N9AEBhEgrAOljy2Ae5MtaSIUi" }, new String[]{ "abc", "$2a$12$EXRkfkdmXn2gzds2SSitu.", "$2a$12$EXRkfkdmXn2gzds2SSitu.MW9.gAVqa9eLS1//RYtYCmB1eLHg.9q" }, new String[]{ "abcdefghijklmnopqrstuvwxyz", "$2a$06$.rCVZVOThsIa97pEDOxvGu", "$2a$06$.rCVZVOThsIa97pEDOxvGuRRgzG64bvtJ0938xuqzv18d3ZpQhstC" }, new String[]{ "abcdefghijklmnopqrstuvwxyz", "$2a$08$aTsUwsyowQuzRrDqFflhge", "$2a$08$aTsUwsyowQuzRrDqFflhgekJ8d9/7Z3GV3UcgvzQW3J5zMyrTvlz." }, new String[]{ "abcdefghijklmnopqrstuvwxyz", "$2a$10$fVH8e28OQRj9tqiDXs1e1u", "$2a$10$fVH8e28OQRj9tqiDXs1e1uxpsjN0c7II7YPKXua2NAKYvM6iQk7dq" }, new String[]{ "abcdefghijklmnopqrstuvwxyz", "$2a$12$D4G5f18o7aMMfwasBL7Gpu", "$2a$12$D4G5f18o7aMMfwasBL7GpuQWuP3pkrZrOAnqP.bmezbMng.QwJ/pG" }, new String[]{ "~!@#$%^&*()      ~!@#$%^&*()PNBFRD", "$2a$06$fPIsBO8qRqkjj273rfaOI.", "$2a$06$fPIsBO8qRqkjj273rfaOI.HtSV9jLDpTbZn782DC6/t7qT67P6FfO" }, new String[]{ "~!@#$%^&*()      ~!@#$%^&*()PNBFRD", "$2a$08$Eq2r4G/76Wv39MzSX262hu", "$2a$08$Eq2r4G/76Wv39MzSX262huzPz612MZiYHVUJe/OcOql2jo4.9UxTW" }, new String[]{ "~!@#$%^&*()      ~!@#$%^&*()PNBFRD", "$2a$10$LgfYWkbzEvQ4JakH7rOvHe", "$2a$10$LgfYWkbzEvQ4JakH7rOvHe0y8pHKF9OaFgwUZ2q7W2FFZmZzJYlfS" }, new String[]{ "~!@#$%^&*()      ~!@#$%^&*()PNBFRD", "$2a$12$WApznUOJfkEGSmYRfnkrPO", "$2a$12$WApznUOJfkEGSmYRfnkrPOr466oFDCaj4b6HY3EXGvfxm43seyhgC" } };

    /**
     * Test method for 'BCrypt.hashpw(String, String)'
     */
    public void testHashpw() {
        System.out.print("BCrypt.hashpw(): ");
        for (int i = 0; i < (test_vectors.length); i++) {
            String plain = test_vectors[i][0];
            String salt = test_vectors[i][1];
            String expected = test_vectors[i][2];
            String hashed = BCrypt.hashpw(plain, salt);
            TestCase.assertEquals(hashed, expected);
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.gensalt(int)'
     */
    public void testGensaltInt() {
        System.out.print("BCrypt.gensalt(log_rounds):");
        for (int i = 4; i <= 12; i++) {
            System.out.print(((" " + (Integer.toString(i))) + ":"));
            for (int j = 0; j < (test_vectors.length); j += 4) {
                String plain = test_vectors[j][0];
                String salt = BCrypt.gensalt(i);
                String hashed1 = BCrypt.hashpw(plain, salt);
                String hashed2 = BCrypt.hashpw(plain, hashed1);
                TestCase.assertEquals(hashed1, hashed2);
                System.out.print(".");
            }
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.gensalt()'
     */
    public void testGensalt() {
        System.out.print("BCrypt.gensalt(): ");
        for (int i = 0; i < (test_vectors.length); i += 4) {
            String plain = test_vectors[i][0];
            String salt = BCrypt.gensalt();
            String hashed1 = BCrypt.hashpw(plain, salt);
            String hashed2 = BCrypt.hashpw(plain, hashed1);
            TestCase.assertEquals(hashed1, hashed2);
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.checkpw(String, String)'
     * expecting success
     */
    public void testCheckpw_success() {
        System.out.print("BCrypt.checkpw w/ good passwords: ");
        for (int i = 0; i < (test_vectors.length); i++) {
            String plain = test_vectors[i][0];
            String expected = test_vectors[i][2];
            TestCase.assertTrue(BCrypt.checkpw(plain, expected));
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.checkpw(String, String)'
     * expecting failure
     */
    public void testCheckpw_failure() {
        System.out.print("BCrypt.checkpw w/ bad passwords: ");
        for (int i = 0; i < (test_vectors.length); i++) {
            int broken_index = (i + 4) % (test_vectors.length);
            String plain = test_vectors[i][0];
            String expected = test_vectors[broken_index][2];
            TestCase.assertFalse(BCrypt.checkpw(plain, expected));
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test for correct hashing of non-US-ASCII passwords
     */
    public void testInternationalChars() {
        System.out.print("BCrypt.hashpw w/ international chars: ");
        String pw1 = "\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605";
        String pw2 = "????????";
        String h1 = BCrypt.hashpw(pw1, BCrypt.gensalt());
        TestCase.assertFalse(BCrypt.checkpw(pw2, h1));
        System.out.print(".");
        String h2 = BCrypt.hashpw(pw2, BCrypt.gensalt());
        TestCase.assertFalse(BCrypt.checkpw(pw1, h2));
        System.out.print(".");
        System.out.println("");
    }

    private static class BCryptHashTV {
        private final byte[] pass;

        private final byte[] salt;

        private final byte[] out;

        public BCryptHashTV(byte[] pass, byte[] salt, byte[] out) {
            this.pass = pass;
            this.salt = salt;
            this.out = out;
        }
    }

    TestBCrypt.BCryptHashTV[] bcrypt_hash_test_vectors = new TestBCrypt.BCryptHashTV[]{ new TestBCrypt.BCryptHashTV(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, new byte[]{ ((byte) (70)), ((byte) (2)), ((byte) (134)), ((byte) (233)), ((byte) (114)), ((byte) (250)), ((byte) (131)), ((byte) (63)), ((byte) (139)), ((byte) (18)), ((byte) (131)), ((byte) (173)), ((byte) (143)), ((byte) (169)), ((byte) (25)), ((byte) (250)), ((byte) (41)), ((byte) (189)), ((byte) (226)), ((byte) (14)), ((byte) (35)), ((byte) (50)), ((byte) (158)), ((byte) (119)), ((byte) (77)), ((byte) (132)), ((byte) (34)), ((byte) (186)), ((byte) (192)), ((byte) (167)), ((byte) (146)), ((byte) (108)) }), new TestBCrypt.BCryptHashTV(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63 }, new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63 }, new byte[]{ ((byte) (198)), ((byte) (169)), ((byte) (95)), ((byte) (230)), ((byte) (65)), ((byte) (49)), ((byte) (21)), ((byte) (251)), ((byte) (87)), ((byte) (233)), ((byte) (159)), ((byte) (117)), ((byte) (116)), ((byte) (152)), ((byte) (232)), ((byte) (93)), ((byte) (163)), ((byte) (198)), ((byte) (225)), ((byte) (223)), ((byte) (12)), ((byte) (60)), ((byte) (147)), ((byte) (170)), ((byte) (151)), ((byte) (92)), ((byte) (84)), ((byte) (138)), ((byte) (52)), ((byte) (67)), ((byte) (38)), ((byte) (248)) }) };

    public void testBCryptHashTestVectors() throws Exception {
        System.out.print("BCrypt.hash w/ known vectors: ");
        for (TestBCrypt.BCryptHashTV tv : bcrypt_hash_test_vectors) {
            byte[] output = new byte[tv.out.length];
            new BCrypt().hash(tv.pass, tv.salt, output);
            TestCase.assertEquals(Arrays.toString(tv.out), Arrays.toString(output));
            System.out.print(".");
        }
        System.out.println("");
    }

    private static class BCryptPbkdfTV {
        private final byte[] pass;

        private final byte[] salt;

        private final int rounds;

        private final byte[] out;

        public BCryptPbkdfTV(byte[] pass, byte[] salt, int rounds, byte[] out) {
            this.pass = pass;
            this.salt = salt;
            this.rounds = rounds;
            this.out = out;
        }
    }

    TestBCrypt.BCryptPbkdfTV[] bcrypt_pbkdf_test_vectors = new TestBCrypt.BCryptPbkdfTV[]{ new TestBCrypt.BCryptPbkdfTV("password".getBytes(), "salt".getBytes(), 4, new byte[]{ ((byte) (91)), ((byte) (191)), ((byte) (12)), ((byte) (194)), ((byte) (147)), ((byte) (88)), ((byte) (127)), ((byte) (28)), ((byte) (54)), ((byte) (53)), ((byte) (85)), ((byte) (92)), ((byte) (39)), ((byte) (121)), ((byte) (101)), ((byte) (152)), ((byte) (212)), ((byte) (126)), ((byte) (87)), ((byte) (144)), ((byte) (113)), ((byte) (191)), ((byte) (66)), ((byte) (126)), ((byte) (157)), ((byte) (143)), ((byte) (190)), ((byte) (132)), ((byte) (42)), ((byte) (186)), ((byte) (52)), ((byte) (217)) }), new TestBCrypt.BCryptPbkdfTV("password".getBytes(), "salt".getBytes(), 8, new byte[]{ ((byte) (225)), ((byte) (54)), ((byte) (126)), ((byte) (197)), ((byte) (21)), ((byte) (26)), ((byte) (51)), ((byte) (250)), ((byte) (172)), ((byte) (76)), ((byte) (193)), ((byte) (193)), ((byte) (68)), ((byte) (205)), ((byte) (35)), ((byte) (250)), ((byte) (21)), ((byte) (213)), ((byte) (84)), ((byte) (132)), ((byte) (147)), ((byte) (236)), ((byte) (201)), ((byte) (155)), ((byte) (155)), ((byte) (93)), ((byte) (156)), ((byte) (13)), ((byte) (59)), ((byte) (39)), ((byte) (190)), ((byte) (199)), ((byte) (98)), ((byte) (39)), ((byte) (234)), ((byte) (102)), ((byte) (8)), ((byte) (139)), ((byte) (132)), ((byte) (155)), ((byte) (32)), ((byte) (171)), ((byte) (122)), ((byte) (164)), ((byte) (120)), ((byte) (1)), ((byte) (2)), ((byte) (70)), ((byte) (231)), ((byte) (75)), ((byte) (186)), ((byte) (81)), ((byte) (114)), ((byte) (63)), ((byte) (239)), ((byte) (169)), ((byte) (249)), ((byte) (71)), ((byte) (77)), ((byte) (101)), ((byte) (8)), ((byte) (132)), ((byte) (94)), ((byte) (141)) }), new TestBCrypt.BCryptPbkdfTV("password".getBytes(), "salt".getBytes(), 42, new byte[]{ ((byte) (131)), ((byte) (60)), ((byte) (240)), ((byte) (220)), ((byte) (245)), ((byte) (109)), ((byte) (182)), ((byte) (86)), ((byte) (8)), ((byte) (232)), ((byte) (240)), ((byte) (220)), ((byte) (12)), ((byte) (232)), ((byte) (130)), ((byte) (189)) }) };

    public void testBCryptPbkdfTestVectors() throws Exception {
        System.out.print("BCrypt.pbkdf w/ known vectors: ");
        for (TestBCrypt.BCryptPbkdfTV tv : bcrypt_pbkdf_test_vectors) {
            byte[] output = new byte[tv.out.length];
            new BCrypt().pbkdf(tv.pass, tv.salt, tv.rounds, output);
            TestCase.assertEquals(Arrays.toString(tv.out), Arrays.toString(output));
            System.out.print(".");
        }
        System.out.println("");
    }
}

