import java.util.ArrayList;
import java.util.List;

public class HarknessMap {
    protected String date;
    protected String topic;

    protected String mapper_id;

    protected List user_ids;

    protected List topUsers;
    protected List bottomUsers;

    protected List leftUsers;
    protected List rightUsers;

    protected int namereferences;
    protected int textreferences;

    protected String grade;
    protected String totaltime;

    protected List topics;

    protected List<List> responses;

    public HarknessMap(String DATE, String TOPIC, String MAPPER_ID, List USER_IDS, List TOPUSERS,
                       List BOTTOMUSERS, List LEFTUSERS, List RIGHTUSERS, int NAMES, int TEXTS, String GRADE, String TOTALTIME, List TOPICS, List RESPONSES) {
        date = DATE;
        topic = TOPIC;
        mapper_id = MAPPER_ID;
        user_ids = new ArrayList<String>(USER_IDS);
        topUsers = new ArrayList<String>(TOPUSERS);
        bottomUsers = new ArrayList<String>(BOTTOMUSERS);
        leftUsers = new ArrayList<String>(LEFTUSERS);
        rightUsers = new ArrayList<String>(RIGHTUSERS);
        namereferences = NAMES;
        textreferences = TEXTS;
        grade = GRADE;
        totaltime = TOTALTIME;
        topics = new ArrayList<String>(TOPICS);
        responses = new ArrayList<List>(RESPONSES);
    }
}
