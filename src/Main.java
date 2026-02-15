import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {

    public static List<Movie> allMovies = new ArrayList<>();
    public static double[] weights;
    public static String[] featureNames;

    public static void main(String[] args) {
        loadData("movies.csv");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("ברוכים הבאים למערכת המלצת סרטים!");

        while (running) {
            System.out.println("\n--- תפריט ראשי ---");
            System.out.println("1. קבלת המלצות לפי סרט קיים במערכת");
            System.out.println("2. קבלת המלצות לפי נתונים אישיים");
            System.out.println("3. יציאה");
            System.out.print("בחר אפשרות: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("הכנס שם של סרט (למשל The Matrix): ");
                    String movieName = scanner.nextLine();
                    Movie selectedMovie = findMovieByName(movieName);

                    if (selectedMovie != null) {
                        List<String> recommendations = getRecommendations(allMovies, selectedMovie.getAttributes(), weights, 3);
                        System.out.println("הסרטים הכי דומים ל-" + movieName + " הם:");
                        for (String rec : recommendations) {
                            System.out.println(rec);
                        }
                    } else {
                        System.out.println("שגיאה: הסרט לא נמצא במאגר.");
                    }
                    break;

                case 2:
                    System.out.println("הכנס ציונים לתכונות (בין 0.0 ל-1.0):");
                    double[] userVector = new double[weights.length];

                    for (int i = 0; i < featureNames.length; i++) {
                        System.out.print(featureNames[i] + ": ");
                        userVector[i] = scanner.nextDouble();
                    }

                    List<String> userRecs = getRecommendations(allMovies, userVector, weights, 3);
                    System.out.println("הסרטים שהכי מתאימים להעדפות שלך:");
                    for (String rec : userRecs) {
                        System.out.println(rec);
                    }
                    break;

                case 3:
                    System.out.println("להתראות!");
                    running = false;
                    break;

                default:
                    System.out.println("אפשרות לא חוקית, נסה שוב.");
            }
        }
    }

    public static void loadData(String filename) {
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String headerLine = br.readLine();
            String[] headersParts = headerLine.split(",");
            featureNames = new String[headersParts.length - 1];
            for (int i = 1; i < headersParts.length; i++) {
                featureNames[i-1] = headersParts[i];
            }

            String weightLine = br.readLine();
            String[] weightParts = weightLine.split(",");
            weights = new double[weightParts.length - 1];
            for (int i = 1; i < weightParts.length; i++) {
                weights[i-1] = Double.parseDouble(weightParts[i]);
            }

            while ((line = br.readLine()) != null) {
                String[] movieParts = line.split(",");
                String title = movieParts[0];
                double[] attrs = new double[movieParts.length - 1];

                for (int i = 1; i < movieParts.length; i++) {
                    attrs[i-1] = Double.parseDouble(movieParts[i]);
                }

                Movie m = new Movie(title, attrs);
                allMovies.add(m);
            }
            System.out.println("הנתונים נטענו בהצלחה. סה\"כ סרטים: " + allMovies.size());

        } catch (IOException e) {
            System.out.println("שגיאה בקריאת הקובץ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Movie findMovieByName(String name) {
        for (Movie m : allMovies) {
            if (m.getTitle().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public static List<String> getRecommendations(List<Movie> movies, double[] targetVec, double[] weights, int limit) {
        class MovieScore implements Comparable<MovieScore> {
            String title;
            double score;

            public MovieScore(String title, double score) {
                this.title = title;
                this.score = score;
            }

            @Override
            public int compareTo(MovieScore other) {
                return Double.compare(other.score, this.score);
            }
        }

        List<MovieScore> scores = new ArrayList<>();

        for (Movie m : movies) {
            try {
                double sim = calculateSimilarity(targetVec, m.getAttributes(), weights);
                scores.add(new MovieScore(m.getTitle(), sim));
            } catch (Exception e) {
                System.out.println("שגיאה בחישוב לסרט " + m.getTitle());
            }
        }

        Collections.sort(scores);

        List<String> result = new ArrayList<>();
        int count = 0;

        for (MovieScore ms : scores) {
            boolean isSameVector = Arrays.equals(targetVec, findMovieByName(ms.title).getAttributes());
            if (isSameVector && count == 0) {
                continue;
            }

            result.add(ms.title + " (התאמה: " + String.format("%.2f", ms.score) + ")");
            count++;
            if (count >= limit) break;
        }

        return result;
    }

    public static double calculateSimilarity(double[] vec1, double[] vec2, double[] weights) {
        if (vec1.length != vec2.length || vec1.length == 0) {
            throw new IllegalArgumentException("הווקטורים חייבים להיות באותו אורך ולא ריקים");
        }

        double numerator = 0.0;
        double denom1 = 0.0;
        double denom2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            numerator += vec1[i] * vec2[i] * weights[i];

            denom1 += Math.pow(vec1[i], 2) * weights[i];
            denom2 += Math.pow(vec2[i], 2) * weights[i];
        }

        if (denom1 == 0 || denom2 == 0) {
            return 0.0;
        }

        return numerator / (Math.sqrt(denom1) * Math.sqrt(denom2));
    }
}