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

package se.jsquad.ejb;

import se.jsquad.RoleConstants;
import se.jsquad.authorization.SecurityOpenBank;
import se.jsquad.qualifier.Log;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Logger;

@Stateless
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
