package se.jsquad.generator;

import javax.validation.ConstraintViolation;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageGenerator {
    private static final Logger logger = Logger.getLogger(MessageGenerator.class.getName());


    public String generateClientValidationMessage(Set<ConstraintViolation<?>> constraintViolationSet) {
        logger.log(Level.FINE, "generateClientValidationMessage(constraintViolationSet: {0}",
                new Object[] {"hidden"});

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
