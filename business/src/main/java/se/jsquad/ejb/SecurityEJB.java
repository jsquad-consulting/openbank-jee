package se.jsquad.ejb;

import se.jsquad.RoleConstants;
import se.jsquad.authorization.SecurityOpenBank;
import se.jsquad.interceptor.LoggerInterceptor;
import se.jsquad.qualifier.Log;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

@Stateless
@LoggerInterceptor
public class SecurityEJB {
    @Inject @Log
    private Logger logger;

    @Resource
    SessionContext sessionContext;

    @Inject
    SecurityOpenBank securityOpenBank;

    @RolesAllowed({RoleConstants.ADMIN})
    public void adminCall() {
        securityOpenBank.adminCall();
    }

    @RolesAllowed({RoleConstants.CUSTOMER})
    public void customerCall() {
        securityOpenBank.customerCall();
    }
}
