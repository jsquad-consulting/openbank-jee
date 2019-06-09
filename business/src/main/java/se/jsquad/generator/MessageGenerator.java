package se.jsquad.generator;

import se.jsquad.qualifier.Log;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class MessageGenerator {
    @Inject @Log
    private Logger logger;

    public String generateClientValidationMessage(Set<ConstraintViolation<?>> constraintViolationSet) {
        StringBuilder stringBuilder = new StringBuilder();

        Iterator<ConstraintViolation<?>> constraintViolationIterator = constraintViolationSet.iterator();

        while(constraintViolationIterator.hasNext()) {
            ConstraintViolation<?> clientConstraintViolation = constraintViolationIterator.next();

            stringBuilder.append(clientConstraintViolation.getPropertyPath().toString()).append(": ")
                    .append(clientConstraintViolation.getMessage()).append("\n");
        }

        return stringBuilder.toString();
    }
}
