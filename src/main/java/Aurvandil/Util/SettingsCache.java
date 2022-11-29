package Aurvandil.Util;

public class SettingsCache {
    public String ra;
    public String dec;
    public String rad;
    public String dbip;
    public String port;
    public String dbname;
    public String user;
    public String pass;

    public boolean[] toggles;
    public boolean[] useFilters;
    public String[] numberArg1;
    public String[] numberArg2;
    public String[] textArg;
    public boolean[] boolValue;

    public SettingsCache() {}

    /**
     * Create a new cache of settings to be saved
     * @param ra right ascension
     * @param dec declination
     * @param rad radius
     * @param dbip database ip
     * @param port database port
     * @param dbname database name
     * @param user database username
     * @param pass database password
     * @param toggles parameter toggles
     * @param useFilters filter toggles
     * @param numberArg1 first argument of filter
     * @param numberArg2 second argument of filter
     * @param textArg text argument of filter
     * @param boolValue boolean value of filter
     */
    public SettingsCache(String ra, String dec, String rad, String dbip, String port, String dbname, String user,
                         String pass, boolean[] toggles, boolean[] useFilters, String[] numberArg1,
                         String[] numberArg2, String[] textArg, boolean[] boolValue) {
        this.ra = ra;
        this.dec = dec;
        this.rad = rad;
        this.dbip = dbip;
        this.port = port;
        this.dbname = dbname;
        this.user = user;
        this.pass = pass;
        this.toggles = toggles;
        this.useFilters = useFilters;
        this.numberArg1 = numberArg1;
        this.numberArg2 = numberArg2;
        this.textArg = textArg;
        this.boolValue = boolValue;
    }
}
