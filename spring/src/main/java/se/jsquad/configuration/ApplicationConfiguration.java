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

package se.jsquad.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import se.jsquad.ejb.ClientInformationEjbLocal;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableWebMvc
@EnableSwagger2
@ComponentScan(basePackages = {"se.jsquad"})
public class ApplicationConfiguration {
    @Bean("logger")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    Logger getLogger(final InjectionPoint injectionPoint) {
        return LogManager.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }

    @Bean
    public LocalStatelessSessionProxyFactoryBean getClientInformationEjbLocal(){
        LocalStatelessSessionProxyFactoryBean factory = new LocalStatelessSessionProxyFactoryBean();
        factory.setBusinessInterface(ClientInformationEjbLocal.class);
        factory.setJndiName("java:app/spring-1.0-SNAPSHOT/ClientInformationEJB!se.jsquad.ejb.ClientInformationEjbLocal");
        return factory;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .host("localhost:8080/restful-spring");
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "OpenBank API",
                "OpenBank API.",
                "1",
                "http://jsquad.se/terms/",
                new Contact("John Doe", "http://jsquad.se", "info@jsquad.se"),
                "Apache 2.0", "https://www.apache.org/licenses/LICENSE-2.0.html", Collections.emptyList());

    }
}