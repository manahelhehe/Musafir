//  Purpose : Smart driver matching + OTP generation
// ============================================================
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RideService {

    // Matching weights (must sum to 1.0)
    private static final double WEIGHT_DISTANCE = 0.60;
    private static final double WEIGHT_RATING   = 0.40;

    private final Random random = new Random();

    // ── Find matching drivers, sorted best first ──────────────
    public List<Driver> findMatches(List<Driver> allDrivers,
                                    String pickupArea,
                                    String vehicleType) {
        List<Driver> candidates = new ArrayList<>();

        for (Driver d : allDrivers) {
            if (!d.isAvailable()) continue;
            if (d.getVehicle() == null) continue;
            if (!d.getVehicle().getType().name().equalsIgnoreCase(vehicleType)) continue;
            candidates.add(d);
        }

        // Sort highest score first
        candidates.sort((a, b) -> 
            Double.compare(score(b, pickupArea), score(a, pickupArea)));
// for descending order we do compare (b,a) and not (a,b)
        return candidates;
    }

    // ── Scoring: distance + rating only ──────────────────────
    private double score(Driver d, String pickupArea) {
        double dist        = Location.distanceBetween(d.getCurrentArea(), pickupArea);
        double distScore   = 1.0 / (dist + 1.0);   // closer = higher
        double ratingScore = d.getAverageRating() / 5.0;
        return (distScore * WEIGHT_DISTANCE) + (ratingScore * WEIGHT_RATING);
    }

    // ── Display top 3 matches ─────────────────────────────────
    public void displayMatches(List<Driver> matches, String pickupArea) {
        if (matches.isEmpty()) {
            System.out.println("  No available drivers found.");
            return;
        }
        int count = Math.min(3, matches.size());
        System.out.println("\n  ┌───────────────────────────────────────────────┐");
        System.out.println("  │              AVAILABLE DRIVERS                │");
        System.out.println("  ├───────────────────────────────────────────────┤");
        for (int i = 0; i < count; i++) {
            Driver d    = matches.get(i);
            double dist = Location.distanceBetween(d.getCurrentArea(), pickupArea);
            long   eta  = Math.max(1, Math.round(dist * 3));
            System.out.printf("  │  [%d] %-20s  %s  │%n",
                i + 1, d.getName(), d.getRatingStars());
            System.out.printf("  │      ETA ~%d min  |  %s%n", eta, d.getVehicle());
            if (i < count - 1)
                System.out.println("  ├───────────────────────────────────────────────┤");
        }
        System.out.println("  └───────────────────────────────────────────────┘");
    }

    // ── OTP: random 6-digit code, shown to rider at booking ──
    public String generateOTP() {
        return String.format("%06d", random.nextInt(1000000));
    }
}
