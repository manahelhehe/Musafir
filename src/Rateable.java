public interface Rateable {

    void   addRating(double rating);
    double getAverageRating();
    int    getTotalRatings();

    default String getRatingStars() {
        double avg  = getAverageRating();
        int    full = (int) Math.round(avg);
        String stars = "";
        for (int i = 0; i < 5; i++)
            stars += (i < full) ? "*" : "-";
        return stars + " (" + String.format("%.1f", avg) + "/5.0)";
    }
}
