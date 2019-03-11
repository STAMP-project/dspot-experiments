package cc.blynk.server.api.http.logic;


import org.junit.Test;


/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 23.01.18.
 */
public class HttpSignatureTest {
    @Test
    public void printSignatures() {
        System.out.println(HttpSignatureTest.getLongVal("GET "));// 1195725856

        System.out.println(HttpSignatureTest.getLongVal("POST"));// 1347375956

        System.out.println(HttpSignatureTest.getLongVal("PUT "));// 1347769376

        System.out.println(HttpSignatureTest.getLongVal("HEAD"));// 1212498244

        System.out.println(HttpSignatureTest.getLongVal("OPTI"));// 1330664521

        System.out.println(HttpSignatureTest.getLongVal("PATC"));// 1346458691

        System.out.println(HttpSignatureTest.getLongVal("DELE"));// 1145392197

        System.out.println(HttpSignatureTest.getLongVal("TRAC"));// 1414676803

        System.out.println(HttpSignatureTest.getLongVal("CONN"));// 1129270862

    }
}

