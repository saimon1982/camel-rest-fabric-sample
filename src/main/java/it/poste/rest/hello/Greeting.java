package it.poste.rest.hello;

public class Greeting {

    private final double id;
    private final String content;

    public Greeting(double id, String content) {
        this.id = id;
        this.content = content;
    }

    public double getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
