package se.jsquad;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Authentication {
    private static final Logger logger = Logger.getLogger(Authentication.class.getName());

    private Authentication() {
    }

    public static List<String> getUserNameAndPassword(String authorization) {
        logger.log(Level.FINE, "getUserName(authorization: {0})", new Object[]{authorization});

        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            return Arrays.asList(credentials.split(":", 2));
        } else {
            return new ArrayList<>();
        }
    }
}
