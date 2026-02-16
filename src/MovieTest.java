import org.junit.Test;
import static org.junit.Assert.*;

public class MovieTest {

    @Test
    public void testIdentity() {
        double[] vec1 = {1.0, 2.0, 3.0};
        double[] vec2 = {1.0, 2.0, 3.0};
        double[] weights = {1.0, 1.0, 1.0};

        double result = Main.calculateSimilarity(vec1, vec2, weights);

        assertEquals(1.0, result, 0.0001);
    }

    @Test
    public void testOrthogonalVectors() {
        double[] vec1 = {1.0, 0.0};
        double[] vec2 = {0.0, 1.0};
        double[] weights = {1.0, 1.0};

        double result = Main.calculateSimilarity(vec1, vec2, weights);

        assertEquals(0.0, result, 0.0001);
    }

    @Test
    public void testWeightedSimilarity() {
        double[] vec1 = {1.0, 1.0};
        double[] vec2 = {1.0, 0.0};

        double[] weightsEqual = {0.5, 0.5};
        double res1 = Main.calculateSimilarity(vec1, vec2, weightsEqual);

        double[] weightsBiased = {0.9, 0.1};
        double res2 = Main.calculateSimilarity(vec1, vec2, weightsBiased);

        assertTrue(res2 > res1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentLengths() {
        double[] vec1 = {1.0, 0.5};
        double[] vec2 = {1.0, 0.5, 0.3};
        double[] weights = {1.0, 1.0};

        Main.calculateSimilarity(vec1, vec2, weights);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyVector() {
        double[] vec1 = {};
        double[] vec2 = {};
        double[] weights = {};

        Main.calculateSimilarity(vec1, vec2, weights);
    }
}