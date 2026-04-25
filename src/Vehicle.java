public class Vehicle {

    public enum VehicleType { ECONOMY, COMFORT, PREMIUM }

    private String      make;
    private String      model;
    private String      plate;
    private VehicleType type;
    private int         seats;

    // ── Constructors ─────────────────────────────────────────
    public Vehicle(String make, String model, String plate,
                   VehicleType type, int seats) {
        this.make  = make;
        this.model = model;
        this.plate = plate;
        this.type  = type;
        this.seats = seats;
    }

    // ── Copy Constructor ─────────────────────────────────────
    public Vehicle(Vehicle other) {
        this.make  = other.make;
        this.model = other.model;
        this.plate = other.plate;
        this.type  = other.type;
        this.seats = other.seats;
    }

    // ── File serialization ────────────────────────────────────
    public String toFileString() {
        return make + "," + model + "," + plate + "," + type.name() + "," + seats;
    }

    public static Vehicle fromFileString(String s) {
        String[] p = s.split(",");
        return new Vehicle(p[0], p[1], p[2], VehicleType.valueOf(p[3]),
                           Integer.parseInt(p[4]));
    }

    // ── Getters & Setters ────────────────────────────────────
    public String      getMake()             { return make; }
    public String      getModel()            { return model; }
    public String      getPlate()            { return plate; }
    public VehicleType getType()             { return type; }
    public void        setType(VehicleType t){ this.type = t; }
    public int         getSeats()            { return seats; }

    @Override
    public String toString() {
        return make + " " + model + " [" + plate + "] - " + type + " (" + seats + " seats)";
    }
}
