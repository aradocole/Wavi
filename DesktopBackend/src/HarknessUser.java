public class HarknessUser {
    String id;
    String name;
    String family_name;
    String given_name;
    String picture_URL;
    String email;
    boolean email_verified;
    String locale;

    String class_id;
    boolean elevated;

    boolean first_time;

    public HarknessUser(String ID, String NAME, String FAMILY_NAME, String GIVEN_NAME, String PICTURE_URL, String EMAIL, boolean EMAIL_VERIFIED, String LOCALE, boolean FIRSTTIME, String CLASSID, boolean ELEVATED) {
        id = ID;
        name = NAME;
        family_name = FAMILY_NAME;
        given_name = GIVEN_NAME;
        picture_URL = PICTURE_URL;
        email = EMAIL;
        email_verified = EMAIL_VERIFIED;
        locale = LOCALE;
        first_time = FIRSTTIME;
        class_id = CLASSID;
        elevated = ELEVATED;
    }

    public String getString() {
        String JSON = "{ \"id\":\"" + id + "\", \"name\":\"" + name + "\", " +
                "\"family_name\":\"" + family_name + "\", \"given_name\":\"" + given_name + "\", " +
                "\"picture_URL\":\"" + picture_URL + "\", \"email\":\"" + email + "\", " +
                "\"email_verified\":\"" + email_verified + "\", \"locale\":\"" + locale + "\", " +
                "\"class_id\":\"" + class_id + "\", \"elevated\":\"" + elevated + "\"," +
                "\"first_time\":\"" + first_time + "\"}";
        return JSON;
    }
}
