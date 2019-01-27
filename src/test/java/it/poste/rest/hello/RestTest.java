package it.poste.rest.hello;

import it.poste.njia.Application;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RestTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void helloGetTest() {
        // Call the REST API
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/hello", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String s = response.getBody();
        assertThat(s.equals("Hello World"));
    }

    @Test
    public void helloPostTest() {
        // Call the REST API
        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/hello", null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String s = response.getBody();
        assertEquals(s, null);
    }

    @Test
    public void hiTest() {
        // Call the REST API
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/hi/Pippo", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String s = response.getBody();
        assertThat(s.equals("Hello World"));
    }

    @Test
    public void byeTest() {
        // Call the REST API
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/bye/Pluto", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String s = response.getBody();
        assertThat(s.equals("Bye, Pluto!"));
    }

}