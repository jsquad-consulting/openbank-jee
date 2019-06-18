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
