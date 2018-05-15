import java.util.ArrayList;
import java.util.List;

public class WaviUser {
    protected String id;
    protected String name;
    protected String family_name;
    protected String given_name;
    protected String picture_URL;
    protected String email;
    protected boolean email_verified;
    protected String locale;

    protected List<String> songNames;
    protected List<String> songURLS;

    public WaviUser(String ID, String NAME, String FAMILY_NAME, String GIVEN_NAME, String PICTURE_URL, String EMAIL, boolean EMAIL_VERIFIED, String LOCALE, List SONGS,
                    List URLS) {
        id = ID;
        name = NAME;
        family_name = FAMILY_NAME;
        given_name = GIVEN_NAME;
        picture_URL = PICTURE_URL;
        email = EMAIL;
        email_verified = EMAIL_VERIFIED;
        locale = LOCALE;

        songNames = new ArrayList<String>(SONGS);
        songURLS = new ArrayList<String>(URLS);
    }

    public String getString() {
        String JSON = "{ \"id\":\"" + id + "\", \"name\":\"" + name + "\", " +
                "\"family_name\":\"" + family_name + "\", \"given_name\":\"" + given_name + "\", " +
                "\"picture_URL\":\"" + picture_URL + "\", \"email\":\"" + email + "\", " +
                "\"email_verified\":\"" + email_verified + "\", \"locale\":\"" + locale + "\"}";
        return JSON;
    }
}
