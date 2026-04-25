
import java.util.ArrayList;
import java.util.List;

public class User extends Person implements Rateable {

    private int          totalRides;
    private double       totalSpent;
    private List<String> rideHistory;  // stores ride IDs
    private double       ratingSum;
    private int          ratingCount;

    // ── Constructor ──────────────────────────────────────────
    public User(String name, String email, String phone, String password) {
        super(name, email, phone, password); // call Person constructor
        this.totalRides  = 0;
        this.totalSpent  = 0;
        this.rideHistory = new ArrayList<>();
        this.ratingSum   = 0;
        this.ratingCount = 0;
    }

    // ── Abstract method implementations ──────────────────────
    @Override
    public String getRole() { return "Rider"; }

    @Override
    public void printProfile() {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║           MY PROFILE (RIDER)         ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf( "  ║  ID       : %-24s║%n", getId());
        System.out.printf( "  ║  Name     : %-24s║%n", getName());
        System.out.printf( "  ║  Email    : %-24s║%n", getEmail());
        System.out.printf( "  ║  Phone    : %-24s║%n", getPhone());
        System.out.printf( "  ║  Rides    : %-24d║%n", totalRides);
        System.out.printf( "  ║  Spent    : PKR %-20.2f║%n", totalSpent);
        System.out.printf( "  ║  Rating   : %-24s║%n", getRatingStars());
        System.out.println("  ╚══════════════════════════════════════╝");
    }

    // ── Rateable implementation ───────────────────────────────
    @Override
    public void addRating(double rating) {
        if (rating < 1 || rating > 5) {
            System.out.println("  Rating must be between 1 and 5.");
            return;
        }
        ratingSum += rating;
        ratingCount++;
    }

    @Override
    public double getAverageRating() {
        return ratingCount == 0 ? 5.0 : ratingSum / ratingCount;
    }

    @Override
    public int getTotalRatings() { return ratingCount; }

    // ── Domain method ─────────────────────────────────────────
    public void recordRide(String rideId, double fare) {
        rideHistory.add(rideId);
        totalRides++;
        totalSpent += fare;
    }

    // ── File serialization ────────────────────────────────────
    public String toFileString() {
        String rides = String.join(";", rideHistory);
        return getId() + "|" + getName() + "|" + getEmail() + "|" +
               getPhone() + "|" + password + "|" + totalRides + "|" +
               totalSpent + "|" + ratingSum + "|" + ratingCount + "|" + rides;
    }

    public static User fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        User u = new User(p[1], p[2], p[3], p[4]);
        u.setId(p[0]);
        u.totalRides  = Integer.parseInt(p[5]);
        u.totalSpent  = Double.parseDouble(p[6]);
        u.ratingSum   = Double.parseDouble(p[7]);
        u.ratingCount = Integer.parseInt(p[8]);
        if (p.length > 9 && !p[9].isEmpty())
            for (String r : p[9].split(";"))
                if (!r.isEmpty()) u.rideHistory.add(r);
        return u;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int          getTotalRides()              { return totalRides; }
    public void         setTotalRides(int r)         { this.totalRides = r; }
    public double       getTotalSpent()              { return totalSpent; }
    public void         setTotalSpent(double s)      { this.totalSpent = s; }
    public List<String> getRideHistory()             { return rideHistory; }
    public double       getRatingSum()               { return ratingSum; }
    public void         setRatingSum(double r)       { this.ratingSum = r; }
    public int          getRatingCount()             { return ratingCount; }
    public void         setRatingCount(int c)        { this.ratingCount = c; }
}