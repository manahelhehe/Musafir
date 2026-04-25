import java.util.ArrayList;
import java.util.List;

public class Driver extends Person implements Rateable {

    private Vehicle vehicle;        // COMPOSITION — Driver HAS-A Vehicle
    private boolean isOnline;
    private boolean isVerified;
    private String  licenseNumber;
    private String  currentArea;
    private int     totalTrips;
    private double  totalEarned;
    private double  ratingSum;
    private int     ratingCount;
    private String  pendingRideId;  // null when free
    private boolean isReported;
    private List<String> rideHistory;

    // ── Constructor ──────────────────────────────────────────
    public Driver(String name, String email, String phone,
                  String password, String licenseNumber, Vehicle vehicle) {
        super(name, email, phone, password);
        this.licenseNumber = licenseNumber;
        this.vehicle       = vehicle;
        this.isOnline      = false;
        this.isVerified    = false;
        this.currentArea   = "F-6 Markaz";
        this.totalTrips    = 0;
        this.totalEarned   = 0;
        this.ratingSum     = 0;
        this.ratingCount   = 0;
        this.pendingRideId = null;
        this.isReported    = false;
        this.rideHistory   = new ArrayList<>();
    }

    // ── Abstract method implementations ──────────────────────
    @Override
    public String getRole() { return "Driver"; }

    @Override
    public void printProfile() {
        System.out.println("\n  ╔══════════════════════════════════════╗");
        System.out.println("  ║          MY PROFILE (DRIVER)         ║");
        System.out.println("  ╠══════════════════════════════════════╣");
        System.out.printf( "  ║  ID       : %-24s║%n", getId());
        System.out.printf( "  ║  Name     : %-24s║%n", getName());
        System.out.printf( "  ║  Phone    : %-24s║%n", getPhone());
        System.out.printf( "  ║  License  : %-24s║%n", licenseNumber);
        System.out.printf( "  ║  Status   : %-24s║%n", isOnline ? "Online" : "Offline");
        System.out.printf( "  ║  Verified : %-24s║%n", isVerified ? "Yes" : "Pending");
        System.out.printf( "  ║  Location : %-24s║%n", currentArea);
        System.out.printf( "  ║  Trips    : %-24d║%n", totalTrips);
        System.out.printf( "  ║  Earned   : PKR %-20.2f║%n", totalEarned);
        System.out.printf( "  ║  Rating   : %-24s║%n", getRatingStars());
        System.out.printf( "  ║  Vehicle  : %-24s║%n", vehicle != null ? vehicle.toString() : "None");
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

    // ── Domain methods ────────────────────────────────────────
    public boolean isAvailable() {
        return isOnline && isVerified && pendingRideId == null;
    }

    public void recordTrip(String rideId, double earning) {
        rideHistory.add(rideId);
        totalTrips++;
        totalEarned += earning;
    }

    public void goOnline()  { isOnline = true; }
    public void goOffline() { isOnline = false; }

    // ── File serialization ────────────────────────────────────
    public String toFileString() {
        String rides   = String.join(";", rideHistory);
        String pending = pendingRideId != null ? pendingRideId : "NONE";
        String veh     = vehicle != null ? vehicle.toFileString() : "NONE";
        return getId() + "|" + getName() + "|" + getEmail() + "|" +
               getPhone() + "|" + password + "|" + licenseNumber + "|" +
               isOnline + "|" + isVerified + "|" + currentArea + "|" +
               totalTrips + "|" + totalEarned + "|" + ratingSum + "|" +
               ratingCount + "|" + pending + "|" + isReported + "|" + rides + "||VEH||" + veh;
    }

    public static Driver fromFileString(String line) {
        String[] halves = line.split("\\|\\|VEH\\|\\|", 2);
        String[] p      = halves[0].split("\\|", -1);

        Vehicle veh = null;
        if (halves.length > 1 && !halves[1].equals("NONE"))
            veh = Vehicle.fromFileString(halves[1]);

        Driver d = new Driver(p[1], p[2], p[3], p[4], p[5], veh);
        d.setId(p[0]);
        d.isOnline      = Boolean.parseBoolean(p[6]);
        d.isVerified    = Boolean.parseBoolean(p[7]);
        d.currentArea   = p[8];
        d.totalTrips    = Integer.parseInt(p[9]);
        d.totalEarned   = Double.parseDouble(p[10]);
        d.ratingSum     = Double.parseDouble(p[11]);
        d.ratingCount   = Integer.parseInt(p[12]);
        d.pendingRideId = p[13].equals("NONE") ? null : p[13];
        d.isReported    = Boolean.parseBoolean(p[14]);
        if (p.length > 15 && !p[15].isEmpty())
            for (String r : p[15].split(";"))
                if (!r.isEmpty()) d.rideHistory.add(r);
        return d;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public Vehicle getVehicle()                    { return vehicle; }
    public void    setVehicle(Vehicle v)           { this.vehicle = v; }
    public boolean isOnline()                      { return isOnline; }
    public boolean isVerified()                    { return isVerified; }
    public void    setVerified(boolean v)          { this.isVerified = v; }
    public String  getLicenseNumber()              { return licenseNumber; }
    public String  getCurrentArea()                { return currentArea; }
    public void    setCurrentArea(String a)        { this.currentArea = a; }
    public int     getTotalTrips()                 { return totalTrips; }
    public void    setTotalTrips(int t)            { this.totalTrips = t; }
    public double  getTotalEarned()                { return totalEarned; }
    public void    setTotalEarned(double e)        { this.totalEarned = e; }
    public double  getRatingSum()                  { return ratingSum; }
    public void    setRatingSum(double r)          { this.ratingSum = r; }
    public int     getRatingCount()                { return ratingCount; }
    public void    setRatingCount(int c)           { this.ratingCount = c; }
    public String  getPendingRideId()              { return pendingRideId; }
    public void    setPendingRideId(String id)     { this.pendingRideId = id; }
    public boolean isReported()                    { return isReported; }
    public void    setReported(boolean r)          { this.isReported = r; }
    public List<String> getRideHistory()           { return rideHistory; }
}