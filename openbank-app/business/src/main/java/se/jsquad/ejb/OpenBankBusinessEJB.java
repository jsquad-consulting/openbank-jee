package se.jsquad.ejb;

import javax.ejb.Stateless;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class OpenBankBusinessEJB {
    private static Logger logger = Logger.getLogger(OpenBankBusinessEJB.class.getName());

    public String getHelloWorld() {
        logger.log(Level.INFO, "Hello world!");
        return "Hello world!";
    }
}
