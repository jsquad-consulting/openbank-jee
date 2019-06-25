/*
 * Copyright 2019 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
