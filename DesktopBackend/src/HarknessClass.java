import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HarknessClass {
    protected String class_code;
    protected String instructor_id;

    protected String class_name;

    protected String[] student_ids;

    protected String[] map_ids;

    public HarknessClass(String CLASSCODE, String INSTRUCTORID, String CLASS_NAME, String[] STUDENTIDS, String[] MAPIDS) {
        class_code = CLASSCODE;
        instructor_id = INSTRUCTORID;
        class_name = CLASS_NAME;
        student_ids = STUDENTIDS;
        map_ids = MAPIDS;
    }
}
