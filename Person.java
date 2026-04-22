public abstract class Person {

    private String name;
    private String email;
    private String phone;
    protected String password;            

    private        String id;
    private static int    personCount = 0; 
    // ── Constructors ─────────────────────────────────────────
    public Person(String name, String email, String phone, String password) {
        this.id       = "P" + String.format("%03d", ++personCount);
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.password = password;
    }

    public abstract String getRole();
    public abstract void   printProfile();

    public void updateProfile(String name) {
        this.name = name;
        System.out.println("  Name updated.");
    }

    public void updateProfile(String name, String phone) {
        this.name  = name;
        this.phone = phone;
        System.out.println("  Name and phone updated.");
    }

    public void updateProfile(String name, String phone, String email) {
        this.name  = name;
        this.phone = phone;
        this.email = email;
        System.out.println("  Name, phone and email updated.");
    }

    // ── Authentication ────────────────────────────────────────
    public boolean authenticate(String input) {
        return this.password.equals(input);
    }

    // ── Getters & Setters ────────────────────────────────────
    public String getId()                        { return id; }
    public void   setId(String id)               { this.id = id; }
    public String getName()                      { return name; }
    public void   setName(String n)              { this.name = n; }
    public String getEmail()                     { return email; }
    public void   setEmail(String e)             { this.email = e; }
    public String getPhone()                     { return phone; }
    public void   setPhone(String p)             { this.phone = p; }
    public static int  getPersonCount()          { return personCount; }
    public static void setPersonCount(int count) { personCount = count; }
}
