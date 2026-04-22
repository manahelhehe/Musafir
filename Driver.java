public class Driver extends Person implements Rateable {
    private boolean isOnline;
    private boolean isVerified;
    private String  licenseNumber;
    private String  currentArea;
    private int     totalTrips;
    private double  totalEarned;
    private double  ratingSum;
    private int     ratingCount;
    private String  pendingRideId;  //null when free
    private boolean isReported;
    private List<String> rideHistory;

    // ── Constructor ──────────────────────────────────────────
    public Driver(String name, String email, String phone,
                  String password, String licenseNumber, Vehicle vehicle) {
        super(name, email, phone, password);
        this.licenseNumber = licenseNumber;
        this.isOnline      = false;
        this.isVerified    = false;
        this.currentArea   = "F-6 Markaz";
        this.totalTrips    = 0;
        this.totalEarned   = 0;
        this.ratingSum     = 0;
        this.ratingCount   = 0;
        this.pendingRideId = null;
        this.isReported    = false;
    }

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
        System.out.printf( "  ║  Status   : %-24s║%n", isOnline? "Online":"Offline");
        System.out.printf( "  ║  Verified : %-24s║%n", isVerified? "Yes": "Pending");
        System.out.printf( "  ║  Location : %-24s║%n", currentArea);
        System.out.printf( "  ║  Trips    : %-24d║%n", totalTrips);
        System.out.printf( "  ║  Earned   : PKR %-20.2f║%n",totalEarned);
        System.out.printf( "  ║  Rating   : %-24s║%n", getRatingStars());
        System.out.println("  ╚══════════════════════════════════════╝");
    }