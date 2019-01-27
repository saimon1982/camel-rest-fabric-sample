package it.poste.rest.hello;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// https://github.com/mmistretta/RHSummit2018Camel3ScaleLab/tree/master/01-create-camel-route

@Component
public class MySimpleCamelRouter extends RouteBuilder {

    @Value("${njia.api.path}")
    String contextPath;

    @Value("${server.port}")
    int serverPort;

    @Value("${server.address}")
    String serverAddress;

    @Override
    public void configure() throws Exception {
        restConfiguration()
            // path to api docs in swagger style
            // swagger http://localhost:8080/api/v1/api-doc
            .port(serverPort).host(serverAddress).enableCORS(true).apiContextPath("/api-doc")
            // api title
            .apiProperty("api.title", "Sample Camel REST services")
            // a description: HTML allowed
            .apiProperty("api.description", "Some sample REST services implemented with <b>Apache Camel</b> routes")
            // api version
            .apiProperty("api.version", "v0.1.0")
            // route id
            .apiContextRouteId("doc-api")
            // expose as servlet
            .component("servlet")
            // bind as JSON
            .bindingMode(RestBindingMode.json);

        /*
         * REST service with external implementation and external metrics, see
         * GreetingService#greetUserByName1(String)
         * 
         * 
         * http://localhost:8080/api/v1/hi/pippo
         */
        rest() //
               // rest route name
            .id("hi.rest")

            // HTTP GET
            .get("/hi/{name}") //
            .bindingMode(RestBindingMode.json) //
            .description("greets user by name") //
            // return JSON object
            .outType(Greeting.class) //
            // define input param
            .param().name("name") //
            .type(RestParamType.query) //
            .description("The name of the user to greet") //
            .dataType("string") //
            .endParam() //
            // implementation by bean: camel inject the param by name into header context
            // see camel bean component
            .to("bean:greetingService?method=greetUserByName1(${header.name})");

        /*
         * REST service with external implementation and external metrics, see
         * GreetingService#greetUserByName2(String)
         * 
         * 
         * http://localhost:8080/api/v1/aloha/pippo
         */
        rest() //
               // rest route name
            .id("aloha.rest")

            // HTTP GET
            .get("/aloha/{name}") //
            .bindingMode(RestBindingMode.json) //
            .description("greets user by name") //
            // return JSON object
            .outType(Greeting.class) //
            // define input param
            .param().name("name") //
            .type(RestParamType.query) //
            .description("The name of the user to greet") //
            .dataType("string") //
            .endParam() //
            // implementation by bean: camel inject the param by name into header context
            // see camel bean component
            .to("bean:greetingService?method=greetUserByName2(${header.name})");

        /*
         * REST service with external implementation and external metrics, see
         * GreetingService#greetUserByName3(String)
         * 
         * 
         * http://localhost:8080/api/v1/bye/pippo
         */
        rest() //
               // rest route name
            .id("bye.rest")

            // HTTP GET
            .get("/bye/{name}") //
            .bindingMode(RestBindingMode.json) //
            .description("greets user by name") //
            // return JSON object
            .outType(Greeting.class) //
            // define input param
            .param().name("name") //
            .type(RestParamType.query) //
            .description("The name of the user to greet") //
            .dataType("string") //
            .endParam() //
            // implementation by bean: camel inject the param by name into header context
            // see camel bean component
            .to("direct:bye.rest");
        
        
        /*
         * REST service with implementation by route direct:hello
         *
         * http://localhost:8080/api/v1/hello
         */
        rest("/hello") //
            // rest route name
            .id("hello.rest")

            // HTTP GET
            .get() //
            .description("greets caller") //
            .outType(String.class) //
            // implementation by route
            .to("direct:hello.get")//

            // HTTP POST
            .post() //
            .description("sample POST call with counter")
            // implementation by route
            .to("direct:hello.post"); //

        /*
         * service implementation by ROUTE
         */
        from("direct:hello.post") //
            // route name
            .routeId("post.hello.direct")
            // increment custom counter METRIC, see camel micrometer component
            .to("micrometer:counter:poste.hello.post.counter?increment=1")
            // write log line
            .log(LoggingLevel.INFO, "route POST implementation invoked") //
            // random delay
            .delay(simple("${random(500,1000)}")) //
            // return empty string
            .transform().simple("") //
            // return HTTP 201
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201));

        /*
         * service implementation by ROUTE
         */
        from("direct:hello.get") //
            // route name
            .routeId("get.hello.direct")
            // increment custom counter METRIC, see camel micrometer component
            .to("micrometer:counter:poste.hello.get.counter?increment=1")
            // write log line
            .log(LoggingLevel.INFO, "route GET implementation invoked") //
            // random delay
            .delay(simple("${random(500,1000)}"))
            // return fixed string
            .transform().simple("Hello World") //
            // return HTTP 200F
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));

        /*
         * service implementation by ROUTE
         */
        from("direct:bye.rest") //
            // route name
            .routeId("get.bye.direct")
            // timer METRIC start, see camel micrometer component
            .to("micrometer:timer:bye.business?action=start")
            // write log line
            .log(LoggingLevel.INFO, "route BYE implementation invoked") 
            // service bean
            .to("bean:greetingService?method=greetUserByName3(${header.name})")
            // timer METRIC stop, see camel micrometer component
            .to("micrometer:timer:bye.business?action=stop");
    }

}
