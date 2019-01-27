package it.poste.njia;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.apache.camel.opentracing.starter.CamelOpenTracing;
import org.springframework.context.annotation.ComponentScan;

@CamelOpenTracing
@SpringBootApplication
//@ComponentScan({"it.poste.njia","it.poste.rest","it.poste.grpc"})
@ComponentScan({"it.poste.*"})
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
