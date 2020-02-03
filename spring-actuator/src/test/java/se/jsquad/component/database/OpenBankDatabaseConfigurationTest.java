/*
 * Copyright 2020 JSquad AB
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

package se.jsquad.component.database;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenBankDatabaseConfigurationTest {
    @Test
    public void testDatabaseConfigurationFieldConstraints() {
        // Given
        DatabaseConfiguration openBankDatabaseConfiguration = new OpenBankDatabaseConfiguration();

        // When
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<DatabaseConfiguration>> constraintViolationSet = validator
                .validate(openBankDatabaseConfiguration);

        // Then
        assertEquals(8, constraintViolationSet.size());

        Optional<ConstraintViolation<DatabaseConfiguration>> constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be null"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "url".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString()))
                        .findFirst();

        assertTrue(constraintViolationOptional.isPresent());


        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be empty"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "url".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());

        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be null"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "username".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());


        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be empty"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "username".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());

        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be null".equals(
                                databaseConfigurationConstraintViolation1.getMessage()) &&
                                "password".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                        .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());


        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be empty"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "password".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());

        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be null"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "driverclassname".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString())).findFirst();

        assertTrue(constraintViolationOptional.isPresent());


        constraintViolationOptional =
                constraintViolationSet.stream()
                        .filter(databaseConfigurationConstraintViolation1 -> "must not be empty"
                                .equals(databaseConfigurationConstraintViolation1.getMessage())
                                && "driverclassname".equals(databaseConfigurationConstraintViolation1.getPropertyPath()
                                .toString()))
                        .findFirst();

        assertTrue(constraintViolationOptional.isPresent());

        // Given
        openBankDatabaseConfiguration.setDriverclassname("test");
        openBankDatabaseConfiguration.setPassword("test");
        openBankDatabaseConfiguration.setUsername("test");
        openBankDatabaseConfiguration.setUrl("test");

        // When and then
        assertEquals(0, validator.validate(openBankDatabaseConfiguration).size());
    }
}