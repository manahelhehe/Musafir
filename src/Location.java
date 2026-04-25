public class Location {

    private String name;
    private double x;   // km east of F-6
    private double y;   // km north of F-6

    public static final Location[] AREAS = {
        new Location("F-6 Markaz",          0.0,   0.0),
        new Location("F-10 Markaz",        -4.5,   0.0),
        new Location("G-9 Markaz",         -2.5,  -3.0),
        new Location("Blue Area",           1.0,   1.5),
        new Location("Bahria Town",        -9.0,  -5.5),
        new Location("DHA Phase 2",         6.0,  -3.5),
        new Location("Islamabad Airport",   8.0,   6.0),
        new Location("Rawalpindi Saddar",  -4.0,  -9.0),
        new Location("PIMS Hospital",      -1.0,  -2.0),
        new Location("Zero Point",          0.5,   2.5)
    };

    public Location(String name, double x, double y) {
        this.name = name;
        this.x    = x;
        this.y    = y;
    }

    // ── Distance between two locations ────────────────────────
    public static double distanceBetween(String nameA, String nameB) {
        Location a = getByName(nameA);
        Location b = getByName(nameB);
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Math.round(Math.sqrt(dx * dx + dy * dy) * 10.0) / 10.0;
    }

    // ── Lookup ────────────────────────────────────────────────
    public static Location getByIndex(int i) {
        if (i < 1 || i > AREAS.length) {
            System.out.println("  Invalid choice, defaulting to F-6.");
            return AREAS[0];
        }
        return AREAS[i - 1];
    }

    public static Location getByName(String name) {
        for (Location loc : AREAS)
            if (loc.name.equalsIgnoreCase(name)) return loc;
        return AREAS[0];
    }

    // ── Display menu ─────────────────────────────────────────
    public static void displayAll() {
        System.out.println("  ┌──────────────────────────────────────┐");
        for (int i = 0; i < AREAS.length; i++)
            System.out.printf("  │  [%2d]  %-30s│%n", i + 1, AREAS[i].name);
        System.out.println("  └──────────────────────────────────────┘");
    }

    public String getName() { return name; }

    @Override
    public String toString() { return name; }
}

