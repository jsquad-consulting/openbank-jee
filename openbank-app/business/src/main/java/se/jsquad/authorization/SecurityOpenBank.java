package se.jsquad.authorization;

import se.jsquad.RoleConstants;
import se.jsquad.qualifier.Log;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecurityOpenBank {
    @Inject @Log
    private Logger logger;

    @Resource
    SessionContext sessionContext;

    @RolesAllowed({RoleConstants.ADMIN})
    public void adminCall() {
        logger.log(Level.FINE, "adminCall() executed by " + sessionContext.getCallerPrincipal().getName());
    }

    @RolesAllowed({RoleConstants.CUSTOMER})
    public void customerCall() {
        logger.log(Level.FINE, "customerCall() executed< by " + sessionContext.getCallerPrincipal().getName());
    }
}
