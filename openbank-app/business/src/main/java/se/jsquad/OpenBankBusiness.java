package se.jsquad;

import javax.ejb.Stateless;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class OpenBankBusiness {
    static Logger logger = Logger.getLogger(OpenBankBusiness.class.getName());

    public String getHelloWorld() {
        logger.log(Level.INFO, "Hello world!");
        return "Hello world!";
    }
}
