package objects;

public enum SectionStatus {
    ACTIVE,
    INACTIVE,
    COMPLETED;

    static String[] getValues() {
        String[] v = new String[3];
        v[0] = "ACTIVE";
        v[1] = "INACTIVE";
        v[2] = "COMPLETED";

        return v;
    }
}
