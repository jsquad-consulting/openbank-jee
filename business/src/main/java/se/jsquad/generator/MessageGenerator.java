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
