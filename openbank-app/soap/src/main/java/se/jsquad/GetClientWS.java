package se.jsquad;

import se.jsquad.getclientservice.GetClientRequest;
import se.jsquad.getclientservice.GetClientResponse;
import se.jsquad.getclientservice.GetClientServicePort;
import se.jsquad.qualifier.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebService;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@WebService(name = "GetClientService")
public class GetClientWS implements GetClientServicePort {
    @Inject @Log
    private Logger logger;

    @Inject
    GetClientWsBusiness getClientWsBusiness;

    @Override
    public GetClientResponse getClient(GetClientRequest request) {
        logger.log(Level.FINE, "getClient: {0}", new Object[] {"hidden person identification number."});

        return getClientWsBusiness.getClientResponse(request);
    }
}
