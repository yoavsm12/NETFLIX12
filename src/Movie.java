import java.util.Arrays;

public class Movie {
    private String title;
    private double[] attributes;

    public Movie(String title, double[] attributes) {
        this.title = title;
        this.attributes = attributes;
    }

    public String getTitle() {
        return title;
    }

    public double[] getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "סרט: " + title + " " + Arrays.toString(attributes);
    }
}