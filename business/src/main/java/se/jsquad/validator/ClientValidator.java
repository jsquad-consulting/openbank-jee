package se.jsquad.validator;

import se.jsquad.Account;
import se.jsquad.AccountTransaction;
import se.jsquad.Client;
import se.jsquad.qualifier.Log;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientValidator {
    @Inject @Log
    private Logger logger;

    public Set<ConstraintViolation<Object>> createConstraintViolationSet(Client client) {
        logger.log(Level.FINE, "clientValidator(client: {0})",
                new Object[] {"hidden"});

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
