import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SmartRideApp {

    // In-memory data (Aggregation)
    private List<User>   users;
    private List<Driver> drivers;
    private List<Ride>   rides;

    // Session state
    private User   currentUser   = null;
    private Driver currentDriver = null;

    // Services (Association)
    private final RideService rideService = new RideService();
    private final Scanner sc = new Scanner(System.in);

    // ── Entry point ───────────────────────────────────────────
    public static void main(String[] args) {
        SmartRideApp app = new SmartRideApp();
        app.start();
    }

    private void start() {
        printBanner();
        loadAll();
        mainMenu();
        saveAll();
        System.out.println("\n  Goodbye!\n");
    }

    // ══════════════════════════════════════════════════════════
    //  LOAD / SAVE
    // ══════════════════════════════════════════════════════════
    private void loadAll() {
        users   = FileManager.loadUsers();
        drivers = FileManager.loadDrivers();
        rides   = FileManager.loadRides();

        // Restore ride ID counter so new IDs don't repeat
        if (!rides.isEmpty()) {
            try {
                String last = rides.get(rides.size() - 1).getRideId();
                Ride.setRideCount(Integer.parseInt(last.substring(2)));
            } catch (Exception e) { /* ignore */ }
        }

        System.out.println("  Loaded: " + users.size() + " users, "
            + drivers.size() + " drivers, " + rides.size() + " rides.\n");
    }

    private void saveAll() {
        FileManager.saveUsers(users);
        FileManager.saveDrivers(drivers);
        FileManager.saveRides(rides);
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN MENU
    // ══════════════════════════════════════════════════════════
    private void mainMenu() {
        while (true) {
            System.out.println("\n  ╔══════════════════════════════════════╗");
            System.out.println("  ║        SMART RIDE SHARING            ║");
            System.out.println("  ╠══════════════════════════════════════╣");
            System.out.println("  ║  [1]  Register as Rider              ║");
            System.out.println("  ║  [2]  Register as Driver             ║");
            System.out.println("  ║  [3]  Login as Rider                 ║");
            System.out.println("  ║  [4]  Login as Driver                ║");
            System.out.println("  ║  [5]  Admin Panel                    ║");
            System.out.println("  ║  [0]  Exit                           ║");
            System.out.println("  ╚══════════════════════════════════════╝");
            System.out.print("  Choice: ");
            int ch = readInt();

            if      (ch == 1) registerUser();
            else if (ch == 2) registerDriver();
            else if (ch == 3) { if (loginUser())   userMenu(); }
            else if (ch == 4) { if (loginDriver()) driverMenu(); }
            else if (ch == 5) adminMenu();
            else if (ch == 0) return;
            else System.out.println("  Invalid choice.");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  USER MENU
    // ══════════════════════════════════════════════════════════
    private void userMenu() {
        while (true) {
            System.out.println("\n  ╔══════════════════════════════════════╗");
            System.out.printf( "  ║  Hi, %-31s║%n", currentUser.getName() + "!");
            System.out.println("  ╠══════════════════════════════════════╣");
            System.out.println("  ║  [1]  Book a Ride                    ║");
            System.out.println("  ║  [2]  My Ride History                ║");
            System.out.println("  ║  [3]  My Stats                       ║");
            System.out.println("  ║  [4]  Rate a Driver                  ║");
            System.out.println("  ║  [5]  Report a Driver                ║");
            System.out.println("  ║  [6]  My Profile                     ║");
            System.out.println("  ║  [7]  Update Profile                 ║");
            System.out.println("  ║  [8]  Fare & Pricing Info            ║");
            System.out.println("  ║  [0]  Logout                         ║");
            System.out.println("  ╚══════════════════════════════════════╝");
            System.out.print("  Choice: ");
            int ch = readInt();

            if      (ch == 1) bookRide();
            else if (ch == 2) viewUserHistory();
            else if (ch == 3) showUserStats(currentUser);
            else if (ch == 4) rateDriver();
            else if (ch == 5) reportDriver();
            else if (ch == 6) currentUser.printProfile();
            else if (ch == 7) updateProfile();
            else if (ch == 8) Ride.printPricingInfo();
            else if (ch == 0) {
                saveAll();
                currentUser = null;
                System.out.println("  Logged out.");
                return;
            } else System.out.println("  Invalid choice.");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  DRIVER MENU
    // ══════════════════════════════════════════════════════════
    private void driverMenu() {
        while (true) {
            String status = currentDriver.isOnline() ? "ONLINE" : "OFFLINE";
            System.out.println("\n  ╔══════════════════════════════════════╗");
            System.out.printf( "  ║  %-23s [%s]  ║%n",
                "Hi, " + currentDriver.getName() + "!", status);
            System.out.println("  ╠══════════════════════════════════════╣");
            System.out.println("  ║  [1]  Go Online / Offline            ║");
            System.out.println("  ║  [2]  View Pending Ride              ║");
            System.out.println("  ║  [3]  Accept Ride (Start)            ║");
            System.out.println("  ║  [4]  Complete Ride                  ║");
            System.out.println("  ║  [5]  Rate a Rider                   ║");
            System.out.println("  ║  [6]  My Stats                       ║");
            System.out.println("  ║  [7]  My Profile                     ║");
            System.out.println("  ║  [8]  Update My Location             ║");
            System.out.println("  ║  [0]  Logout                         ║");
            System.out.println("  ╚══════════════════════════════════════╝");
            System.out.print("  Choice: ");
            int ch = readInt();

            if      (ch == 1) toggleOnline();
            else if (ch == 2) viewPendingRide();
            else if (ch == 3) acceptRide();
            else if (ch == 4) completeRide();
            else if (ch == 5) rateRider();
            else if (ch == 6) showDriverStats(currentDriver);
            else if (ch == 7) currentDriver.printProfile();
            else if (ch == 8) updateDriverLocation();
            else if (ch == 0) {
                saveAll();
                currentDriver = null;
                System.out.println("  Logged out.");
                return;
            } else System.out.println("  Invalid choice.");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  ADMIN MENU
    // ══════════════════════════════════════════════════════════
    private void adminMenu() {
        System.out.print("  Admin password: ");
        String pass = sc.nextLine().trim();
        if (!pass.equals("admin123")) {
            System.out.println("  Access denied.");
            return;
        }

        while (true) {
            System.out.println("\n  ╔══════════════════════════════════════╗");
            System.out.println("  ║             ADMIN PANEL              ║");
            System.out.println("  ╠══════════════════════════════════════╣");
            System.out.printf( "  ║  [1]  Fuel Surcharge  [%-12s]║%n",
                Ride.isFuelSurchargeOn() ? "ON" : "OFF");
            System.out.printf( "  ║  [2]  High Demand     [%-12s]║%n",
                Ride.isHighDemandOn() ? "ON" : "OFF");
            System.out.println("  ║  [3]  Verify a Driver                ║");
            System.out.println("  ║  [4]  View Reported Drivers          ║");
            System.out.println("  ║  [5]  System Stats                   ║");
            System.out.println("  ║  [0]  Exit Admin                     ║");
            System.out.println("  ╚══════════════════════════════════════╝");
            System.out.print("  Choice: ");
            int ch = readInt();

            if (ch == 1) {
                Ride.setFuelSurcharge(!Ride.isFuelSurchargeOn());
                System.out.println("  Fuel surcharge: " + (Ride.isFuelSurchargeOn() ? "ON" : "OFF"));
            } else if (ch == 2) {
                Ride.setHighDemand(!Ride.isHighDemandOn());
                System.out.println("  High demand: " + (Ride.isHighDemandOn() ? "ON" : "OFF"));
            } else if (ch == 3) {
                verifyDriver();
            } else if (ch == 4) {
                viewReportedDrivers();
            } else if (ch == 5) {
                showSystemStats();
            } else if (ch == 0) {
                return;
            } else System.out.println("  Invalid choice.");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  REGISTRATION
    // ══════════════════════════════════════════════════════════
    private void registerUser() {
        System.out.println("\n  ── Register as Rider ──────────────────");
        try {
            System.out.print("  Name     : "); String name  = sc.nextLine().trim();
            System.out.print("  Email    : "); String email = sc.nextLine().trim();
            System.out.print("  Phone    : "); String phone = sc.nextLine().trim();
            System.out.print("  Password : "); String pass  = sc.nextLine().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                System.out.println("  All fields are required.");
                return;
            }
            if (findUserByEmail(email) != null) {
                System.out.println("  Email already registered.");
                return;
            }

            User u = new User(name, email, phone, pass);
            users.add(u);
            saveAll();
            System.out.println("  Registered successfully! Your ID: " + u.getId());

        } catch (Exception e) {
            System.out.println("  Registration failed: " + e.getMessage());
        }
    }

    private void registerDriver() {
        System.out.println("\n  ── Register as Driver ─────────────────");
        try {
            System.out.print("  Name           : "); String name    = sc.nextLine().trim();
            System.out.print("  Email          : "); String email   = sc.nextLine().trim();
            System.out.print("  Phone          : "); String phone   = sc.nextLine().trim();
            System.out.print("  Password       : "); String pass    = sc.nextLine().trim();
            System.out.print("  License Number : "); String license = sc.nextLine().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || license.isEmpty()) {
                System.out.println("  All fields are required.");
                return;
            }
            if (findDriverByEmail(email) != null) {
                System.out.println("  Email already registered.");
                return;
            }

            System.out.println("\n  ── Vehicle Details ─────────────────────");
            System.out.print("  Make   (e.g. Toyota)  : "); String make  = sc.nextLine().trim();
            System.out.print("  Model  (e.g. Corolla) : "); String model = sc.nextLine().trim();
            System.out.print("  Plate  (e.g. ABC-123) : "); String plate = sc.nextLine().trim();

            System.out.println("  Type: [1] Economy  [2] Comfort  [3] Premium");
            System.out.print("  Choice: ");
            Vehicle.VehicleType vtype;
            switch (readInt()) {
                case 2:  vtype = Vehicle.VehicleType.COMFORT; break;
                case 3:  vtype = Vehicle.VehicleType.PREMIUM; break;
                default: vtype = Vehicle.VehicleType.ECONOMY;
            }

            System.out.print("  Seats (4 or 7): ");
            int seats = readInt();
            if (seats != 7) seats = 4;

            Vehicle v = new Vehicle(make, model, plate, vtype, seats);
            Driver  d = new Driver(name, email, phone, pass, license, v);
            drivers.add(d);
            saveAll();
            System.out.println("  Registered! Your ID: " + d.getId());
            System.out.println("  Note: Admin must verify your account before you can go online.");

        } catch (Exception e) {
            System.out.println("  Registration failed: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  LOGIN
    // ══════════════════════════════════════════════════════════
    private boolean loginUser() {
        System.out.println("\n  ── Rider Login ────────────────────────");
        try {
            System.out.print("  Email    : "); String email = sc.nextLine().trim();
            System.out.print("  Password : "); String pass  = sc.nextLine().trim();

            User u = findUserByEmail(email);
            if (u == null || !u.authenticate(pass)) {
                System.out.println("  Invalid email or password.");
                return false;
            }
            currentUser = u;
            System.out.println("  Welcome back, " + u.getName() + "!");
            return true;

        } catch (Exception e) {
            System.out.println("  Login error: " + e.getMessage());
            return false;
        }
    }

    private boolean loginDriver() {
        System.out.println("\n  ── Driver Login ───────────────────────");
        try {
            System.out.print("  Email    : "); String email = sc.nextLine().trim();
            System.out.print("  Password : "); String pass  = sc.nextLine().trim();

            Driver d = findDriverByEmail(email);
            if (d == null || !d.authenticate(pass)) {
                System.out.println("  Invalid email or password.");
                return false;
            }
            currentDriver = d;
            System.out.println("  Welcome back, " + d.getName() + "!");
            if (!d.isVerified())
                System.out.println("  Note: Your account is pending admin verification.");
            return true;

        } catch (Exception e) {
            System.out.println("  Login error: " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  BOOKING
    // ══════════════════════════════════════════════════════════
    private void bookRide() {
        System.out.println("\n  ── Book a Ride ────────────────────────");
        try {
            System.out.println("  PICKUP location:");
            Location.displayAll();
            System.out.print("  Choice: ");
            Location pickup = Location.getByIndex(readInt());

            System.out.println("  DROP location:");
            Location.displayAll();
            System.out.print("  Choice: ");
            Location drop = Location.getByIndex(readInt());

            if (pickup.getName().equals(drop.getName())) {
                System.out.println("  Pickup and drop cannot be the same.");
                return;
            }

            System.out.println("  Vehicle: [1] Economy  [2] Comfort  [3] Premium");
            System.out.print("  Choice: ");
            String vtype;
            switch (readInt()) {
                case 2:  vtype = "COMFORT"; break;
                case 3:  vtype = "PREMIUM"; break;
                default: vtype = "ECONOMY";
            }

            // Find and display matching drivers
            List<Driver> matches = rideService.findMatches(drivers, pickup.getName(), vtype);
            rideService.displayMatches(matches, pickup.getName());

            if (matches.isEmpty()) return;

            // Show fare estimate
            Ride preview = new Ride(currentUser.getId(),
                                    pickup.getName(), drop.getName(), vtype);
            System.out.println("\n  Estimated fare:");
            preview.printFareBreakdown();

            System.out.print("\n  Confirm booking? (y/n): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("  Booking cancelled.");
                return;
            }

            // Assign best driver and save
            Driver best = matches.get(0);
            preview.assignDriver(best.getId());
            best.setPendingRideId(preview.getRideId());
            rides.add(preview);

            String otp = rideService.generateOTP();

            System.out.println("\n  ╔══════════════════════════════════════╗");
            System.out.println("  ║      RIDE BOOKED SUCCESSFULLY!       ║");
            System.out.println("  ╠══════════════════════════════════════╣");
            System.out.printf( "  ║  Ride ID  : %-24s║%n", preview.getRideId());
            System.out.printf( "  ║  Driver   : %-24s║%n", best.getName());
            System.out.printf( "  ║  Vehicle  : %-24s║%n", best.getVehicle().getPlate());
            System.out.printf( "  ║  OTP      : %-24s║%n", otp);
            System.out.println("  ║  Share OTP only with your driver!    ║");
            System.out.println("  ╚══════════════════════════════════════╝");

            currentUser.recordRide(preview.getRideId(), preview.getTotalFare());
            saveAll();

        } catch (Exception e) {
            System.out.println("  Booking error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  ANALYTICS — simple, exactly as requested
    // ══════════════════════════════════════════════════════════
    private void showUserStats(User u) {
        System.out.println("\n  ── My Stats ───────────────────────────");
        System.out.println("  Total rides : " + u.getTotalRides());
        System.out.println("  Total spent : PKR " + String.format("%.2f", u.getTotalSpent()));
        System.out.println("  My rating   : " + u.getRatingStars());
    }

    private void showDriverStats(Driver d) {
        System.out.println("\n  ── My Stats ───────────────────────────");
        System.out.println("  Total trips    : " + d.getTotalTrips());
        System.out.println("  Total earned   : PKR " + String.format("%.2f", d.getTotalEarned()));
        System.out.println("  My rating      : " + d.getRatingStars());
    }

    // ══════════════════════════════════════════════════════════
    //  RIDE HISTORY
    // ══════════════════════════════════════════════════════════
    private void viewUserHistory() {
        System.out.println("\n  ── My Ride History ────────────────────");
        boolean found = false;
        for (Ride r : rides) {
            if (currentUser.getId().equals(r.getUserId())) {
                r.printSummary();
                found = true;
            }
        }
        if (!found) System.out.println("  No rides yet.");
    }

    private void viewPendingRide() {
        String pid = currentDriver.getPendingRideId();
        if (pid == null) { System.out.println("  No pending ride."); return; }
        try {
            Ride r = findRideById(pid);
            System.out.println("\n  ── Pending Ride ───────────────────────");
            r.printSummary();
            User u = findUserById(r.getUserId());
            if (u != null)
                System.out.println("  Rider: " + u.getName() + " | " + u.getPhone());
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  DRIVER ACTIONS
    // ══════════════════════════════════════════════════════════
    private void toggleOnline() {
        if (!currentDriver.isVerified()) {
            System.out.println("  Account not verified. Contact admin.");
            return;
        }
        if (currentDriver.isOnline()) {
            currentDriver.goOffline();
            System.out.println("  You are now OFFLINE.");
        } else {
            currentDriver.goOnline();
            System.out.println("  You are now ONLINE.");
        }
        saveAll();
    }

    private void acceptRide() {
        String pid = currentDriver.getPendingRideId();
        if (pid == null) { System.out.println("  No pending ride."); return; }
        try {
            Ride r = findRideById(pid);
            if (r.getStatus() != Ride.RideStatus.MATCHED) {
                System.out.println("  Ride is not in MATCHED state.");
                return;
            }
            r.startRide();
            System.out.println("  Ride started! Navigate to: " + r.getPickupArea());
            System.out.println("  Ask rider for their OTP to verify identity.");
            saveAll();
        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private void completeRide() {
        String pid = currentDriver.getPendingRideId();
        if (pid == null) { System.out.println("  No active ride."); return; }
        try {
            Ride r = findRideById(pid);
            if (r.getStatus() != Ride.RideStatus.IN_PROGRESS) {
                System.out.println("  Ride must be accepted first.");
                return;
            }
            r.completeRide();
            double earning = r.getTotalFare();
            currentDriver.recordTrip(r.getRideId(), earning);
            currentDriver.setPendingRideId(null);

            System.out.println("  ╔══════════════════════════════════════╗");
            System.out.println("  ║           RIDE COMPLETED!            ║");
            System.out.printf( "  ║  Earned  : PKR %-22.2f║%n", earning);
            System.out.printf( "  ║  Total   : PKR %-22.2f║%n", currentDriver.getTotalEarned());
            System.out.println("  ╚══════════════════════════════════════╝");
            saveAll();

        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private void updateDriverLocation() {
        System.out.println("  Select your current location:");
        Location.displayAll();
        System.out.print("  Choice: ");
        Location loc = Location.getByIndex(readInt());
        currentDriver.setCurrentArea(loc.getName());
        System.out.println("  Location updated to: " + loc.getName());
        saveAll();
    }

    // ══════════════════════════════════════════════════════════
    //  RATING
    // ══════════════════════════════════════════════════════════
    private void rateDriver() {
        System.out.println("\n  ── Rate a Driver ──────────────────────");
        System.out.print("  Ride ID (e.g. RD0001): ");
        String rideId = sc.nextLine().trim().toUpperCase();
        try {
            Ride r = findRideById(rideId);
            if (!currentUser.getId().equals(r.getUserId())) {
                System.out.println("  This is not your ride."); return;
            }
            if (r.getStatus() != Ride.RideStatus.COMPLETED) {
                System.out.println("  Can only rate completed rides."); return;
            }
            Driver d = findDriverById(r.getDriverId());
            if (d == null) { System.out.println("  Driver not found."); return; }

            System.out.print("  Rate " + d.getName() + " (1-5): ");
            double rating = readDouble();
            d.addRating(rating);
            System.out.println("  Rated " + rating + "/5. Thanks!");
            saveAll();

        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    private void rateRider() {
        System.out.println("\n  ── Rate a Rider ───────────────────────");
        System.out.print("  Ride ID: ");
        String rideId = sc.nextLine().trim().toUpperCase();
        try {
            Ride r = findRideById(rideId);
            if (!currentDriver.getId().equals(r.getDriverId())) {
                System.out.println("  This is not your ride."); return;
            }
            if (r.getStatus() != Ride.RideStatus.COMPLETED) {
                System.out.println("  Can only rate completed rides."); return;
            }
            User u = findUserById(r.getUserId());
            if (u == null) { System.out.println("  Rider not found."); return; }

            System.out.print("  Rate " + u.getName() + " (1-5): ");
            double rating = readDouble();
            u.addRating(rating);
            System.out.println("  Rated " + rating + "/5. Thanks!");
            saveAll();

        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  DRIVER REPORTING
    // ══════════════════════════════════════════════════════════
    private void reportDriver() {
        System.out.println("\n  ── Report a Driver ────────────────────");
        System.out.print("  Enter Driver ID (e.g. P001): ");
        String id = sc.nextLine().trim();
        Driver d = findDriverById(id);
        if (d == null) {
            System.out.println("  Driver not found.");
            return;
        }
        d.setReported(true);
        System.out.println("  Driver " + d.getName() + " has been reported.");
        saveAll();
    }

    // ══════════════════════════════════════════════════════════
    //  PROFILE UPDATE — shows method overloading in action
    // ══════════════════════════════════════════════════════════
    private void updateProfile() {
        System.out.println("\n  ── Update Profile ─────────────────────");
        System.out.println("  [1] Name only");
        System.out.println("  [2] Name + Phone");
        System.out.println("  [3] Name + Phone + Email");
        System.out.print("  Choice: ");
        int ch = readInt();

        try {
            System.out.print("  New Name : "); String name = sc.nextLine().trim();
            if (ch == 1) {
                currentUser.updateProfile(name);                // OVERLOAD 1
            } else if (ch == 2) {
                System.out.print("  New Phone: "); String phone = sc.nextLine().trim();
                currentUser.updateProfile(name, phone);         // OVERLOAD 2
            } else {
                System.out.print("  New Phone: "); String phone = sc.nextLine().trim();
                System.out.print("  New Email: "); String email = sc.nextLine().trim();
                currentUser.updateProfile(name, phone, email);  // OVERLOAD 3
            }
            saveAll();
        } catch (Exception e) {
            System.out.println("  Update failed: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  ADMIN ACTIONS
    // ══════════════════════════════════════════════════════════
    private void verifyDriver() {
        System.out.println("\n  ── All Drivers ────────────────────────");
        System.out.println("  ┌───────────────────────────────────────────┐");
        for (Driver d : drivers)
            System.out.printf("  │  %-6s  %-20s  [%-8s]  │%n",
                d.getId(), d.getName(), d.isVerified() ? "Verified" : "Pending");
        System.out.println("  └───────────────────────────────────────────┘");
        System.out.print("  Enter Driver ID to verify (0 to cancel): ");
        String id = sc.nextLine().trim();
        if (id.equals("0")) return;
        Driver d = findDriverById(id);
        if (d == null) { System.out.println("  Driver not found."); return; }
        d.setVerified(true);
        System.out.println("  " + d.getName() + " is now verified.");
        saveAll();
    }

    private void viewReportedDrivers() {
        System.out.println("\n  ── Reported Drivers ───────────────────");
        boolean found = false;
        for (Driver d : drivers) {
            if (d.isReported()) {
                System.out.println("  " + d.getId() + " | " + d.getName()
                    + " | " + d.getPhone());
                found = true;
            }
        }
        if (!found) System.out.println("  No drivers have been reported.");
    }

    private void showSystemStats() {
        int completed = 0;
        int cancelled = 0;
        double revenue = 0;
        long online = 0;
        for (Ride r : rides) {
            if (r.getStatus() == Ride.RideStatus.COMPLETED) { completed++; revenue += r.getTotalFare(); }
            if (r.getStatus() == Ride.RideStatus.CANCELLED)   cancelled++;
        }
        for (Driver d : drivers) if (d.isOnline()) online++;

        System.out.println("\n  ── System Stats ───────────────────────");
        System.out.println("  Users          : " + users.size());
        System.out.println("  Drivers        : " + drivers.size());
        System.out.println("  Online now     : " + online);
        System.out.println("  Total rides    : " + rides.size());
        System.out.println("  Completed      : " + completed);
        System.out.println("  Cancelled      : " + cancelled);
        System.out.printf( "  Total revenue  : PKR %.2f%n", revenue);
    }

    // ══════════════════════════════════════════════════════════
    //  LOOKUP HELPERS
    // ══════════════════════════════════════════════════════════
    private User findUserByEmail(String email) {
        for (User u : users)
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        return null;
    }

    private User findUserById(String id) {
        for (User u : users)
            if (u.getId().equals(id)) return u;
        return null;
    }

    private Driver findDriverByEmail(String email) {
        for (Driver d : drivers)
            if (d.getEmail().equalsIgnoreCase(email)) return d;
        return null;
    }

    private Driver findDriverById(String id) {
        for (Driver d : drivers)
            if (d.getId().equals(id)) return d;
        return null;
    }

    private Ride findRideById(String id) {
        for (Ride r : rides)
            if (r.getRideId().equalsIgnoreCase(id)) return r;
        throw new RuntimeException("Ride not found: " + id);
    }

    // ══════════════════════════════════════════════════════════
    //  INPUT HELPERS
    // ══════════════════════════════════════════════════════════
    private int readInt() {
        while (true) {
            try   { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("  Enter a number: "); }
        }
    }

    private double readDouble() {
        while (true) {
            try   { return Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("  Enter a number: "); }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  BANNER
    // ══════════════════════════════════════════════════════════
    private void printBanner() {
        System.out.println();
        System.out.println("  ╔════════════════════════════════════════════╗");
        System.out.println("  ║      SMART RIDE SHARING SYSTEM  v3.0      ║");
        System.out.println("  ║  Smart Matching | Dynamic Pricing | OTP   ║");
        System.out.println("  ╚════════════════════════════════════════════╝");
        System.out.println();
    }
}
