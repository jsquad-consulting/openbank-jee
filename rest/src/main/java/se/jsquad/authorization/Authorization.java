package se.jsquad.authorization;

import se.jsquad.qualifier.Log;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("")
public class Authorization {
    @Inject @Log
    private Logger logger;

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    public boolean isAuthorized() throws IOException, ServletException {
        logger.log(Level.FINE, "authenticateUser(), request: {0}, response: {1}",
                new Object[] {request, response});

        return request.authenticate(response);
    }

    public boolean isUserInRole(String role) {
        logger.log(Level.FINE, "isUserInRole(role: {0})", new Object[] {role});

        return request.isUserInRole(role);
    }

    public void logout() throws ServletException {
        request.logout();
    }
}
