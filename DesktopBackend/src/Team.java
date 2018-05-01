public class Team {

    public String id;
    public String name;
    public String abrev;
    public String record;
    //public Player[] roster;
    //public Game[] schedule;
    public String backgroundLogo;
    public Player[] roster;

    public Team(String Id, String Name, String Abrev, String BackgroundLogo, Player[] r){
        id = Id;
        name = Name;
        abrev = Abrev;
        // record = Record;
        // roster = getRoster();
        // schedule = getSchedule();
        backgroundLogo = BackgroundLogo;
        roster = r;
    }

}
