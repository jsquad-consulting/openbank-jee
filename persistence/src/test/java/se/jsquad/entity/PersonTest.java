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

package se.jsquad.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonTest {
    private Validator validator;

    @BeforeEach
    void setUpBeforeEachUnitTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testPersonBeanValidation() {
        // Given
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setPersonIdentification("191212121212");
        person.setMail("test@test.se");
        person.setClient(null);

        // When
        Set<ConstraintViolation<Person>> constraintViolationSet = validator.validate(person);

        // Then
        assertEquals(0, constraintViolationSet.size());

        // Given
        person.setFirstName("Kulan1");
        person.setLastName("1234");
        person.setPersonIdentification("19121212-1212");
        person.setMail("@test.se");

        // When
        constraintViolationSet = validator.validate(person);

        // Then
        assertEquals(4, constraintViolationSet.size());

        ConstraintViolation<Person> constraintViolation = constraintViolationSet.stream().filter(cv ->
                "personIdentification".equals(cv.getPropertyPath().toString())).findFirst().get();

        assertEquals("must match \"\\d{12}\"", constraintViolation.getMessage());
        assertEquals("personIdentification", constraintViolation.getPropertyPath().toString());

        constraintViolation = constraintViolationSet.stream().filter(cv ->
                "lastName".equals(cv.getPropertyPath().toString())).findFirst().get();

        assertEquals("must match \"^\\D*$\"", constraintViolation.getMessage());
        assertEquals("lastName", constraintViolation.getPropertyPath().toString());

        constraintViolation = constraintViolationSet.stream().filter(cv ->
                "firstName".equals(cv.getPropertyPath().toString())).findFirst().get();

        assertEquals("must match \"^\\D*$\"", constraintViolation.getMessage());
        assertEquals("firstName", constraintViolation.getPropertyPath().toString());

        constraintViolation = constraintViolationSet.stream().filter(cv ->
                "mail".equals(cv.getPropertyPath().toString())).findFirst().get();

        assertEquals("must match \"" + Person.MAIL_REGEXP + "\"", constraintViolation.getMessage());
        assertEquals("mail", constraintViolation.getPropertyPath().toString());
    }

    @Test
    public void testMailComvinations() {
        // Given
        Person person = new Person();
        person.setMail("a@..se");

        // When
        Set<ConstraintViolation<Person>> constraintViolationSet = validator.validate(person);

        // Then
        assertEquals(1, constraintViolationSet.size());

        ConstraintViolation<Person> constraintViolation = constraintViolationSet.iterator().next();

        assertEquals("must match \"" + Person.MAIL_REGEXP + "\"", constraintViolation.getMessage());
        assertEquals("mail", constraintViolation.getPropertyPath().toString());

        // Given
        person.setMail("@test.se");

        // When
        constraintViolationSet = validator.validate(person);

        // Then
        assertEquals(1, constraintViolationSet.size());

        constraintViolation = constraintViolationSet.iterator().next();

        assertEquals("must match \"" + Person.MAIL_REGEXP + "\"", constraintViolation.getMessage());
        assertEquals("mail", constraintViolation.getPropertyPath().toString());

        // Given
        person.setMail("a@test.");

        // When
        constraintViolationSet = validator.validate(person);

        // Then
        assertEquals(1, constraintViolationSet.size());

        constraintViolation = constraintViolationSet.iterator().next();

        assertEquals("must match \"" + Person.MAIL_REGEXP + "\"", constraintViolation.getMessage());
        assertEquals("mail", constraintViolation.getPropertyPath().toString());

        // Given
        person.setMail("@test.com");

        // When
        constraintViolationSet = validator.validate(person);

        // Then
        assertEquals(1, constraintViolationSet.size());

        constraintViolation = constraintViolationSet.iterator().next();

        assertEquals("must match \"" + Person.MAIL_REGEXP + "\"", constraintViolation.getMessage());
        assertEquals("mail", constraintViolation.getPropertyPath().toString());
    }
}
