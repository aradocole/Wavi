public class Player {

    public String id;
    public String name;

    public double ppg;
    public double rpg;
    public double apg;
    public double mpg;

   // public int lgp;
   // public int lgr;
   // public int lga;
   // public int lgm;


    public Player(String Name, double Ppg, double Rpg, double Apg, double Mpg) {
        name = Name;

        ppg = Ppg;
        rpg = Rpg;
        apg = Apg;
        mpg = Mpg;
/*
        lgp = getLgp();
        lgr = getRgp();
        lga = getAgp();
        lgm = getMgp();
    }
    public String getName(){
        return name;
    }

    public double getPpg(){
        return ppg;
    }

    public double getRpg(){
        return rpg;
    }

    public double getApg(){
        return apg;
    }

    public double getMpg(){
        return mpg;
    }

    public int getLgp(){
        return lgp;
    }

    public int getRgp(){
        return lgr;
    }

    public int getAgp(){
        return lga;
    }

    public int getMgp(){
        return lgm;
    }
*/
    }
}
