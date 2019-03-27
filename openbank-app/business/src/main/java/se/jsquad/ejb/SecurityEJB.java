package se.jsquad.ejb;

import se.jsquad.RoleConstants;
import se.jsquad.authorization.SecurityOpenBank;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class SecurityEJB {
    private static final Logger logger = Logger.getLogger(SecurityEJB.class.getName());

    @Resource
    SessionContext sessionContext;

    @Inject
    SecurityOpenBank securityOpenBank;

    @RolesAllowed({RoleConstants.ADMIN})
    public void adminCall() {
        logger.log(Level.FINE, "adminCall() executed by " + sessionContext.getCallerPrincipal().getName());
        securityOpenBank.adminCall();
    }

    @RolesAllowed({RoleConstants.CUSTOMER})
    public void customerCall() {
        logger.log(Level.FINE, "customerCall() executed by " + sessionContext.getCallerPrincipal().getName());
        securityOpenBank.customerCall();
    }
}
