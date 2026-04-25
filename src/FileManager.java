import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final String DIR          = "data";
    private static final String USERS_FILE   = DIR + "/users.txt";
    private static final String DRIVERS_FILE = DIR + "/drivers.txt";
    private static final String RIDES_FILE   = DIR + "/rides.txt";

    // Create data/ folder if it doesn't exist
    private static void ensureDir() {
        File dir = new File(DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // ── USERS ─────────────────────────────────────────────────

    public static void saveUsers(List<User> users) {
        ensureDir();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(USERS_FILE));
            for (User u : users) {
                bw.write(u.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [ERROR] Could not save users: " + e.getMessage());
        } finally {
            try { if (bw != null) bw.close(); }
            catch (IOException e) { /* ignore */ }
        }
    }

    public static List<User> loadUsers() {
        List<User> list = new ArrayList<>();
        File f = new File(USERS_FILE);
        if (!f.exists()) return list;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try { list.add(User.fromFileString(line)); }
                    catch (Exception e) {
                        System.out.println("  [WARN] Skipped corrupt user record.");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("  [ERROR] Could not load users: " + e.getMessage());
        } finally {
            try { if (br != null) br.close(); }
            catch (IOException e) { /* ignore */ }
        }
        return list;
    }

    // ── DRIVERS ───────────────────────────────────────────────

    public static void saveDrivers(List<Driver> drivers) {
        ensureDir();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(DRIVERS_FILE));
            for (Driver d : drivers) {
                bw.write(d.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [ERROR] Could not save drivers: " + e.getMessage());
        } finally {
            try { if (bw != null) bw.close(); }
            catch (IOException e) { /* ignore */ }
        }
    }

    public static List<Driver> loadDrivers() {
        List<Driver> list = new ArrayList<>();
        File f = new File(DRIVERS_FILE);
        if (!f.exists()) return list;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try { list.add(Driver.fromFileString(line)); }
                    catch (Exception e) {
                        System.out.println("  [WARN] Skipped corrupt driver record.");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("  [ERROR] Could not load drivers: " + e.getMessage());
        } finally {
            try { if (br != null) br.close(); }
            catch (IOException e) { /* ignore */ }
        }
        return list;
    }

    // ── RIDES ─────────────────────────────────────────────────

    public static void saveRides(List<Ride> rides) {
        ensureDir();
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(RIDES_FILE));
            for (Ride r : rides) {
                bw.write(r.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [ERROR] Could not save rides: " + e.getMessage());
        } finally {
            try { if (bw != null) bw.close(); }
            catch (IOException e) { /* ignore */ }
        }
    }

    public static List<Ride> loadRides() {
        List<Ride> list = new ArrayList<>();
        File f = new File(RIDES_FILE);
        if (!f.exists()) return list;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Ride r = Ride.fromFileString(line);
                    if (r != null) list.add(r);
                }
            }
        } catch (IOException e) {
            System.out.println("  [ERROR] Could not load rides: " + e.getMessage());
        } finally {
            try { if (br != null) br.close(); }
            catch (IOException e) { /* ignore */ }
        }
        return list;
    }
}
