package it.poste.njia;

import static org.apache.camel.component.micrometer.MicrometerConstants.DISTRIBUTION_SUMMARIES;
import static org.apache.camel.component.micrometer.messagehistory.MicrometerMessageHistoryNamingStrategy.MESSAGE_HISTORIES;
import static org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyNamingStrategy.ROUTE_POLICIES;

import java.time.Duration;

import javax.servlet.Servlet;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.NamedNode;
import org.apache.camel.Route;
import org.apache.camel.component.micrometer.DistributionStatisticConfigFilter;
import org.apache.camel.component.micrometer.eventnotifier.MicrometerExchangeEventNotifier;
import org.apache.camel.component.micrometer.eventnotifier.MicrometerExchangeEventNotifierNamingStrategy;
import org.apache.camel.component.micrometer.messagehistory.MicrometerMessageHistoryFactory;
import org.apache.camel.component.micrometer.messagehistory.MicrometerMessageHistoryNamingStrategy;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyNamingStrategy;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.opentracing.contrib.java.spring.jaeger.starter.TracerBuilderCustomizer;

/**
 * Class NjiaConfiguration. Configures the following items:
 * <ul>
 *  <li>CamelHttpTransportServlet</li>
 *  <li>Micrometer registry for Prometheus metrics</li>
 *  <li>Tracer for Jaeger</li>
 * </ul>
 */
@Configuration
public class NjiaConfiguration{


    private TracerTags tracerTags;
    @Autowired
    public void setTracerTags(TracerTags tags){
      this.tracerTags = tags;
    }

    private MetricTags tags;
    @Autowired
    public void setMetricTags(MetricTags tags){
      this.tags = tags;
    }

    @Value("${njia.api.path}")
    String apiPath;

    @Value("${njia.service.id}")
    private String serviceId;


    // Enable aspect for @Timed annotated methods
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public ServletRegistrationBean<Servlet> camelServletRegistrationBean() {
        ServletRegistrationBean<Servlet> registration = new ServletRegistrationBean<Servlet>(
                new CamelHttpTransportServlet(), apiPath + "/*");
        registration.setName("CamelServlet");
        return registration;
    }

    /* Customizing CamelContext to add Micrometer route metrics */
    @Bean
    public CamelContextConfiguration contextConfiguration() {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext context) {

            	MicrometerExchangeEventNotifier meen = new MicrometerExchangeEventNotifier();
				meen.setNamingStrategy(new MicrometerExchangeEventNotifierNamingStrategy() {
					@Override
					public String getName(Exchange exchange, Endpoint endpoint) {
						return "njia_exchange_events";
					}
				});
                context.getManagementStrategy().addEventNotifier(meen);
                
                MicrometerRoutePolicyFactory mrpf = new MicrometerRoutePolicyFactory();
                mrpf.setNamingStrategy(new MicrometerRoutePolicyNamingStrategy() {
					@Override
					public String getName(Route route) {
						return "njia_route_policy";
					}
				});
                context.addRoutePolicyFactory(mrpf);

                MicrometerMessageHistoryFactory mmhf = new MicrometerMessageHistoryFactory();
                mmhf.setNamingStrategy(new MicrometerMessageHistoryNamingStrategy() {
					@Override
					public String getName(Route route, NamedNode node) {
						return "njia_message_history";
					}
				});
                context.setMessageHistoryFactory(mmhf);
            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {
                // noop
            }
        };
    }


    @Value("${metrics.percentiles:0.5,0.75,0.95}")
    private double[] percentiles;


    /**
     * Customize MeterRegistry
     * @return MeterRegistryCustomizer&lt;MeterRegistry&gt;
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> getMeterRegistry() { 

        DistributionStatisticConfigFilter timerMeterFilter = new DistributionStatisticConfigFilter()
                .andAppliesTo(ROUTE_POLICIES)
                .orAppliesTo(MESSAGE_HISTORIES)
                .setPublishPercentileHistogram(true)
                .setPercentiles(percentiles)
                .setMinimumExpectedDuration(Duration.ofMillis(1L))
                .setMaximumExpectedDuration(Duration.ofMillis(150L));

        DistributionStatisticConfigFilter summaryMeterFilter = new DistributionStatisticConfigFilter()
                .andAppliesTo(DISTRIBUTION_SUMMARIES)
                .setPublishPercentileHistogram(true)
                .setPercentiles(percentiles)
                .setMinimumExpectedValue(1L)
                .setMaximumExpectedValue(100L);


        return registry -> registry.config()
                .commonTags("service", serviceId) 
                .commonTags(tags.getMicrometerTags())
                .meterFilter(timerMeterFilter)
                .meterFilter(summaryMeterFilter)
                .meterFilter(
                  MeterFilter.deny(id -> { 
                    String uri = id.getTag("uri");
                    return uri != null && uri.startsWith("/actuator");
                  })
                );

    }




    /**
     * Customization of the Tracer
     * @return TracerBuilderCustomizer
     */
    @Bean
    public TracerBuilderCustomizer getJaegerCustomizer(){
      return builder -> builder.withTags(tracerTags.getTracerTags());
    }



}

