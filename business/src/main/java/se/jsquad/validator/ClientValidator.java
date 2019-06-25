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

package se.jsquad.validator;

import se.jsquad.entity.Account;
import se.jsquad.entity.AccountTransaction;
import se.jsquad.entity.Client;
import se.jsquad.qualifier.Log;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class ClientValidator {
    @Inject @Log
    private Logger logger;

    public Set<ConstraintViolation<Object>> createConstraintViolationSet(Client client) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Object>> constraintViolations = new HashSet<>();

        constraintViolations.addAll(validator.validate(client));
        constraintViolations.addAll(validator.validate(client.getPerson()));
        constraintViolations.addAll(validator.validate(client.getClientType()));

        Iterator<Account> accountIterator = client.getAccountSet().iterator();

        while(accountIterator.hasNext()) {
            Account account = accountIterator.next();
            constraintViolations.addAll(validator.validate(account));
            Iterator<AccountTransaction> accountTransactionIterator = account.getAccountTransactionSet().iterator();

            while(accountTransactionIterator.hasNext()) {
                AccountTransaction accountTransaction = accountTransactionIterator.next();
                constraintViolations.addAll(validator.validate(accountTransaction));
            }
        }

        return constraintViolations;
    }
}
