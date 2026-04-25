public //Fare calculation 
// ============================================================
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ride {

    // ── Ride states ───────────────────────────────────────────
    public enum RideStatus { REQUESTED, MATCHED, IN_PROGRESS, COMPLETED, CANCELLED }

    // ── Admin-controlled surge flags (static = global) ────────
    private static boolean fuelSurchargeOn = false;
    private static boolean highDemandOn    = false;

    // ── Base fares per vehicle type (PKR) ────────────────────
    private static final double ECONOMY_BASE    = 100;
    private static final double COMFORT_BASE    = 175;
    private static final double PREMIUM_BASE    = 300;
    private static final double ECONOMY_PER_KM  = 30;
    private static final double COMFORT_PER_KM  = 55;
    private static final double PREMIUM_PER_KM  = 85;

    private static int  rideCount = 0;

    // ── Fields ───────────────────────────────────────────────
    private String     rideId;
    private String     userId;
    private String     driverId;
    private String     pickupArea;
    private String     dropArea;
    private String     vehicleType;
    private RideStatus status;
    private String     requestTime;
    private String     completionTime;

    // Fare fields — calculated at booking time
    private double distance;
    private double baseFare;
    private double distanceCost;
    private double surgeMultiplier;
    private double totalFare;
    private String surgeReason;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    // ── Constructor ──────────────────────────────────────────
    public Ride(String userId, String pickupArea, String dropArea, String vehicleType) {
        this.rideId      = "RD" + String.format("%04d", ++rideCount);
        this.userId      = userId;
        this.pickupArea  = pickupArea;
        this.dropArea    = dropArea;
        this.vehicleType = vehicleType.toUpperCase();
        this.status      = RideStatus.REQUESTED;
        this.requestTime = LocalDateTime.now().format(FMT);
        this.driverId    = null;
        this.completionTime = null;

        calculateFare();
    }

    // Private no-arg constructor for file loading
    private Ride() {}

    // ── Fare Calculation ─────────────────────────────────────
    private void calculateFare() {
        this.distance = Location.distanceBetween(pickupArea, dropArea);

        switch (vehicleType) {
            case "COMFORT":
                baseFare = COMFORT_BASE;
                distanceCost = COMFORT_PER_KM * distance;
                break;
            case "PREMIUM":
                baseFare = PREMIUM_BASE;
                distanceCost = PREMIUM_PER_KM * distance;
                break;
            default:
                baseFare = ECONOMY_BASE;
                distanceCost = ECONOMY_PER_KM * distance;
        }

        // Surge: check current hour
        int hour = LocalDateTime.now().getHour();
        boolean isPeak  = (hour >= 8 && hour <= 10) || (hour >= 17 && hour <= 20);
        boolean isNight = (hour >= 23 || hour <= 4);

        if (highDemandOn) {
            surgeMultiplier = 2.0;
            surgeReason     = "High Demand x2.0";
        } else if (isPeak) {
            surgeMultiplier = 1.5;
            surgeReason     = "Peak Hours x1.5";
        } else if (isNight) {
            surgeMultiplier = 1.3;
            surgeReason     = "Night Ride x1.3";
        } else {
            surgeMultiplier = 1.0;
            surgeReason     = "Normal";
        }

        if (fuelSurchargeOn) {
            surgeMultiplier = Math.round(surgeMultiplier * 1.2 * 100.0) / 100.0;
            surgeReason    += " + Fuel x1.2";
        }

        totalFare = Math.round((baseFare + distanceCost) * surgeMultiplier * 100.0) / 100.0;
    }

    // ── Display fare breakdown ────────────────────────────────
    public void printFareBreakdown() {
        System.out.println("  ┌──────────────────────────────────────┐");
        System.out.println("  │           FARE BREAKDOWN             │");
        System.out.println("  ├──────────────────────────────────────┤");
        System.out.printf( "  │  Vehicle   : %-22s│%n", vehicleType);
        System.out.printf( "  │  Distance  : %-18.1f km  │%n", distance);
        System.out.printf( "  │  Base Fare : PKR %-18.2f│%n", baseFare);
        System.out.printf( "  │  Dist Cost : PKR %-18.2f│%n", distanceCost);
        System.out.printf( "  │  Surge     : x%-5.2f %-16s│%n", surgeMultiplier, surgeReason);
        System.out.println("  ├──────────────────────────────────────┤");
        System.out.printf( "  │  TOTAL     : PKR %-18.2f│%n", totalFare);
        System.out.println("  └──────────────────────────────────────┘");
    }

    // ── Display ride summary ──────────────────────────────────
    public void printSummary() {
        System.out.println("  ┌──────────────────────────────────────┐");
        System.out.printf( "  │  Ride ID  : %-24s│%n", rideId);
        System.out.printf( "  │  From     : %-24s│%n", pickupArea);
        System.out.printf( "  │  To       : %-24s│%n", dropArea);
        System.out.printf( "  │  Vehicle  : %-24s│%n", vehicleType);
        System.out.printf( "  │  Status   : %-24s│%n", status);
        System.out.printf( "  │  Date     : %-24s│%n", requestTime);
        if (completionTime != null)
            System.out.printf("  │  Done     : %-24s│%n", completionTime);
        System.out.printf( "  │  Fare     : PKR %-20.2f│%n", totalFare);
        System.out.println("  └──────────────────────────────────────┘");
    }

    // ── State transitions ─────────────────────────────────────
    public void assignDriver(String driverId) {
        this.driverId = driverId;
        this.status   = RideStatus.MATCHED;
    }

    public void startRide() {
        this.status = RideStatus.IN_PROGRESS;
    }

    public void completeRide() {
        this.status         = RideStatus.COMPLETED;
        this.completionTime = LocalDateTime.now().format(FMT);
    }

    public void cancelRide() {
        this.status = RideStatus.CANCELLED;
    }

    // ── File serialization ────────────────────────────────────
    public String toFileString() {
        String drv  = driverId      != null ? driverId      : "NONE";
        String done = completionTime != null ? completionTime : "NONE";
        return rideId + "|" + userId + "|" + drv + "|" + pickupArea + "|" +
               dropArea + "|" + vehicleType + "|" + status.name() + "|" +
               requestTime + "|" + done + "|" + distance + "|" +
               baseFare + "|" + distanceCost + "|" + surgeMultiplier + "|" +
               totalFare + "|" + surgeReason;
    }

    public static Ride fromFileString(String line) {
        try {
            String[] p = line.split("\\|", 15);
            Ride r = new Ride();
            r.rideId          = p[0];
            r.userId          = p[1];
            r.driverId        = p[2].equals("NONE") ? null : p[2];
            r.pickupArea      = p[3];
            r.dropArea        = p[4];
            r.vehicleType     = p[5];
            r.status          = RideStatus.valueOf(p[6]);
            r.requestTime     = p[7];
            r.completionTime  = p[8].equals("NONE") ? null : p[8];
            r.distance        = Double.parseDouble(p[9]);
            r.baseFare        = Double.parseDouble(p[10]);
            r.distanceCost    = Double.parseDouble(p[11]);
            r.surgeMultiplier = Double.parseDouble(p[12]);
            r.totalFare       = Double.parseDouble(p[13]);
            r.surgeReason     = p.length > 14 ? p[14] : "Normal";
            return r;
        } catch (Exception e) {
            System.out.println("  [WARN] Skipped corrupt ride record.");
            return null;
        }
    }

    // ── Static pricing info display ───────────────────────────
    public static void printPricingInfo() {
        System.out.println("\n  ┌──────────────────────────────────────────┐");
        System.out.println("  │            PRICING INFO                  │");
        System.out.println("  ├──────────────────────────────────────────┤");
        System.out.printf( "  │  Economy : PKR 100 base + 30/km          │%n");
        System.out.printf( "  │  Comfort : PKR 175 base + 55/km          │%n");
        System.out.printf( "  │  Premium : PKR 300 base + 85/km          │%n");
        System.out.println("  ├──────────────────────────────────────────┤");
        System.out.println("  │  Normal hours    : x1.0                  │");
        System.out.println("  │  Peak (8-10, 5-8): x1.5                  │");
        System.out.println("  │  Night (11pm-5am): x1.3                  │");
        System.out.println("  │  High Demand     : x2.0                  │");
        System.out.println("  │  Fuel Surcharge  : x1.2 (extra)          │");
        System.out.println("  ├──────────────────────────────────────────┤");
        System.out.printf( "  │  Fuel Surcharge : %-22s│%n", fuelSurchargeOn ? "ACTIVE" : "Off");
        System.out.printf( "  │  High Demand    : %-22s│%n", highDemandOn    ? "ACTIVE" : "Off");
        System.out.println("  └──────────────────────────────────────────┘");
    }

    // ── Admin toggles ─────────────────────────────────────────
    public static void setFuelSurcharge(boolean b) { fuelSurchargeOn = b; }
    public static void setHighDemand(boolean b)    { highDemandOn    = b; }
    public static boolean isFuelSurchargeOn()      { return fuelSurchargeOn; }
    public static boolean isHighDemandOn()         { return highDemandOn; }

    // ── Getters ───────────────────────────────────────────────
    public String     getRideId()         { return rideId; }
    public void       setRideId(String s) { this.rideId = s; }
    public String     getUserId()         { return userId; }
    public String     getDriverId()       { return driverId; }
    public String     getPickupArea()     { return pickupArea; }
    public String     getDropArea()       { return dropArea; }
    public String     getVehicleType()    { return vehicleType; }
    public RideStatus getStatus()         { return status; }
    public void       setStatus(RideStatus s) { this.status = s; }
    public String     getRequestTime()    { return requestTime; }
    public double     getTotalFare()      { return totalFare; }
    public double     getDistance()       { return distance; }

    public static int  getRideCount()     { return rideCount; }
    public static void setRideCount(int c){ rideCount = c; }
}
 {
    
}
