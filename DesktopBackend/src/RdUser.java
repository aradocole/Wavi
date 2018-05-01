public class RdUser {
    String id;
    String name;
    String family_name;
    String given_name;
    String picture_URL;
    String email;
    boolean email_verified;

    double balance;

    public RdUser(String ID, String NAME, String FAMILY_NAME, String GIVEN_NAME, String PICTURE_URL, String EMAIL, boolean EMAIL_VERIFIED, double BALANCE) {
        id = ID;
        name = NAME;
        family_name = FAMILY_NAME;
        given_name = GIVEN_NAME;
        picture_URL = PICTURE_URL;
        email = EMAIL;
        email_verified = EMAIL_VERIFIED;
        balance = BALANCE;
    }
}
