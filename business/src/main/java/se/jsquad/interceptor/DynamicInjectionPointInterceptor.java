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

package se.jsquad.interceptor;

import se.jsquad.repository.EntityManagerProducer;
import se.jsquad.thread.NumberOfLocks;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.lang.annotation.Annotation;

public class DynamicInjectionPointInterceptor implements Extension {
    public <T> void processAnnotatedType(
            @Observes ProcessAnnotatedType<T> processAnnotatedType) {

        AnnotatedType<T> annotatedType = processAnnotatedType
                .getAnnotatedType();

        if (annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad")
                && !annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad.interceptor")
                && !annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad.producer")
                && !annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad.qualifier")
                && !annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad.entity")
                && !annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad.client")
                && !annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad.batch")
                && !annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad.ejb")
                && !annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad.generator")
                && !annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad.repository")
                && !annotatedType.getJavaClass().getPackage().getName().contains("se.jsquad.jms")
                && !annotatedType.getJavaClass().equals(NumberOfLocks.class)
                && !annotatedType.getJavaClass().equals(EntityManagerProducer.class)) {
            Annotation loggerInterceptorAnnotation = () -> LoggerInterceptor.class;

            AnnotatedTypeWrapper<T> wrapper = new AnnotatedTypeWrapper<>(
                    annotatedType, annotatedType.getAnnotations());
            wrapper.addAnnotation(loggerInterceptorAnnotation);

            processAnnotatedType.setAnnotatedType(wrapper);
        }
    }
}
