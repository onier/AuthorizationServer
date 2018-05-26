
import java.io.UnsupportedEncodingException;
import org.springframework.security.crypto.codec.Base64;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class Test {

    public static void main(String[] args) {
        System.out.println(generate("oauth_client", "oauth_client_secret"));
    }

    private static String generate(String clientId, String clientSecret) {
        String creds = String.format("%s:%s", new Object[]{clientId, clientSecret});
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

}
