package it.poste.rest.hello;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Service response by Bean
 *
 *
 *
 */
@Component("greetingService")
public class GreetingService {

    private static final String template1 = "Hi, %s!";
    private static final String template2 = "Aloha, %s!";
    private static final String template3 = "Bye, %s!";
    final Counter counter;
    final Timer timer;

    public GreetingService(MeterRegistry registry) {
        this.counter = Counter.builder("poste.greetUserByName.counter").register(registry);
        this.timer = Timer.builder("poste.greetUserByName.timer2").register(registry);
    }

    // This annotation produces duration metrics for the call
    @Timed(value = "poste.greetUserByName.timer1", description = "This is a custom timer METRIC", extraTags = {
        "custom_tag_1", "custom_tag_2"
    })
    public Greeting greetUserByName1(String name) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(250, 750));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        counter.increment();
        return new Greeting(counter.count(), String.format(template1, name));
    }

    // on the other hand... explicit record
    public Greeting greetUserByName2(String name) {
        return this.timer.record(() -> {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(250, 750));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter.increment();
            return new Greeting(counter.count(), String.format(template2, name));
        });
    }

    // on the other hand... explicit record
    @Timed(value = "poste.greetUserByName3.timer1", description = "This is a custom timer METRIC", extraTags = {
        "custom_tag_greetUserByName3_1", "custom_tag_greetUserByName3_2"
    })
    public Greeting greetUserByName3(String name) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(250, 750));
            String prefix = System.getenv().getOrDefault("GREETING_PREFIX", "Hi");
            System.out.println("contenuto della env var greeting prefix: " + prefix);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        counter.increment();
        return new Greeting(counter.count(), String.format(template3, name));
    }

}
