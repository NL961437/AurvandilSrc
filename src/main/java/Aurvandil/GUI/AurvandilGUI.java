package Aurvandil.GUI;

import Aurvandil.BA.PostgreSQLBA;
import Aurvandil.GUI.ActionListeners.CloseWindowListener;
import Aurvandil.GUI.ActionListeners.ConeSearcherListener;
import Aurvandil.GUI.ActionListeners.MenuActionListener;
import Aurvandil.GUI.ActionListeners.SetFilterListener;
import Aurvandil.Util.SettingsCache;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class AurvandilGUI {
    private final JFrame frame;
    public final JCheckBox[] toggleBoxes = new JCheckBox[variables.length];
    public final JComboBox[] useFilter = new JComboBox[variables.length];
    public final JTextField[] valueGreaterThan = new JTextField[variables.length];
    public final JTextField[] valueLessThan = new JTextField[variables.length];
    public final JComboBox[] booleanFilter = new JComboBox[variables.length];
    public final JTextField[] textEqualsTo = new JTextField[variables.length];
    public final JLabel[] lt1 = new JLabel[variables.length];
    public final JLabel[] variableName = new JLabel[variables.length];
    public final JLabel[] lt2 = new JLabel[variables.length];
    public final JLabel[] equalsLabel = new JLabel[variables.length];
    public final JLabel[] arrowLabel = new JLabel[variables.length];

    public JTextField raField;
    public JTextField decField;
    public JTextField radField;
    private JTextField dbField;
    public JTextField portField;
    private JTextField dbNameField;
    private JTextField userField;
    private JPasswordField passField;

    /**
     * Create a new Aurvandil GUI window
     * Step 1: Create frame
     * Step 2: Create cascade menus
     * Step 3: Create the Upper panel with the query and database fields
     * Step 4: Create the parameters panel
     * Step 5: Show window
     */
    public AurvandilGUI() {
        //FRAME CREATION
        frame = new JFrame("Aurvandil");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(950, 900);
        frame.addWindowListener(new CloseWindowListener(this));

        createCascadeMenus();

        JPanel commonPanel = new JPanel(new GridBagLayout());
        createQueryPanel(commonPanel);
        createDatabasePanel(commonPanel);
        frame.add(commonPanel, BorderLayout.PAGE_START);

        createParameterPanel();

        loadSettingsFromCache();
        frame.setVisible(true);
    }

    /**
     * Add each of the different "Advanced Commands"
     */
    public void createCascadeMenus() {
        JMenu menu;
        JMenuBar menuBar = new JMenuBar();
        menu = new JMenu("Advanced Commands");
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        ActionListener menuListener = new MenuActionListener(this);

        JMenuItem addFiles = new JMenuItem("Add Files");
        addFiles.addActionListener(menuListener);
        addFiles.setActionCommand("AddFiles");
        menu.add(addFiles);

        JMenuItem countStars = new JMenuItem("Count Stars");
        countStars.addActionListener(menuListener);
        countStars.setActionCommand("CountStars");
        menu.add(countStars);

        JMenuItem buildSphere = new JMenuItem("Build HEALPix Sphere");
        buildSphere.addActionListener(menuListener);
        buildSphere.setActionCommand("BuildSphere");
        menu.add(buildSphere);

        JMenuItem destroySphere = new JMenuItem("Destroy Current HEALPix Sphere");
        destroySphere.addActionListener(menuListener);
        destroySphere.setActionCommand("DestroySphere");
        menu.add(destroySphere);
    }

    /**
     * Create a new query panel with parameters and a "Run Query" button
     * @param commonPanel panel to place it in
     */
    public void createQueryPanel(JPanel commonPanel) {
        JPanel queryPanel = new JPanel(new GridLayout(0,4));
        queryPanel.setBorder(new EmptyBorder(0,0,7,0));
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        commonPanel.add(queryPanel, c);

        //RUN QUERY BUTTON
        JButton queryButton = new JButton("RUN QUERY");
        queryButton.addActionListener(new ConeSearcherListener(this));
        queryPanel.add(queryButton);

        //RIGHT ASCENSION
        JPanel raPanel = new JPanel();
        raPanel.add(new JLabel("Right Ascension:"));
        raField = new JTextField();
        raField.setColumns(5);
        raPanel.add(raField);
        queryPanel.add(raPanel);

        //DECLINATION
        JPanel decPanel = new JPanel();
        decPanel.add(new JLabel("Declination:"));
        decField = new JTextField();
        decField.setColumns(5);
        decPanel.add(decField);
        queryPanel.add(decPanel);

        //RADIUS
        JPanel radPanel = new JPanel();
        radPanel.add(new JLabel("Cone Radius:"));
        radField = new JTextField();
        radField.setColumns(5);
        radPanel.add(radField);
        queryPanel.add(radPanel);
    }

    /**
     * Create new panel with database information
     * @param commonPanel panel to place it in
     */
    public void createDatabasePanel(JPanel commonPanel) {
        JPanel dbInfoPanel = new JPanel(new GridLayout(0,3));
        dbInfoPanel.setBorder(new MatteBorder(1,0, 0,0, Color.BLACK));
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        commonPanel.add(dbInfoPanel, c);

        //DATABASE IP
        JPanel dbPanel = new JPanel();
        dbPanel.add(new JLabel("Database IP:"));
        dbField = new JTextField("localhost");
        dbField.setColumns(10);
        dbPanel.add(dbField);
        dbInfoPanel.add(dbPanel);

        //PORT
        JPanel portPanel = new JPanel();
        portPanel.add(new JLabel("Port:"));
        portField = new JTextField("8080");
        portField.setColumns(4);
        portPanel.add(portField);
        dbInfoPanel.add(portPanel);

        //DATABASE NAME
        JPanel dbNamePanel = new JPanel();
        dbNamePanel.add(new JLabel("Database Name:"));
        dbNameField = new JTextField("gaia");
        dbNameField.setColumns(10);
        dbNamePanel.add(dbNameField);
        dbInfoPanel.add(dbNamePanel);

        //USER
        JPanel userPanel = new JPanel();
        userPanel.add(new JLabel("Username:"));
        userField = new JTextField("admin");
        userField.setColumns(10);
        userPanel.add(userField);
        dbInfoPanel.add(userPanel);

        //PASSWORD
        JPanel passPanel = new JPanel();
        passPanel.add(new JLabel("Password:"));
        passField = new JPasswordField("admin");
        passField.setColumns(10);
        passPanel.add(passField);
        dbInfoPanel.add(passPanel);
    }

    /**
     * Create a panel to house parameters and filter fields
     */
    public void createParameterPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        // GENERIC FIELDS
        JLabel text;
        JTextField field;
        JCheckBox checkBox;
        JComboBox comboBox;

        // PARAMETERS
        JPanel parameterPanel = new JPanel();
        parameterPanel.setLayout(new GridBagLayout());

        //LABELS
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;

        labelConstraints.gridx = 0;
        labelConstraints.weightx = 1;
        parameterPanel.add(new JLabel("Toggle"), labelConstraints);

        labelConstraints.gridx = 1;
        labelConstraints.weightx = 1;
        parameterPanel.add(new JLabel("Name"), labelConstraints);

        labelConstraints.gridx = 2;
        labelConstraints.weightx = 1;
        parameterPanel.add(new JLabel("Type"), labelConstraints);

        labelConstraints.gridx = 3;
        labelConstraints.weightx = 1;
        parameterPanel.add(new JLabel("Use Filter"), labelConstraints);

        labelConstraints.gridx = 4;
        labelConstraints.weightx = 1;
        parameterPanel.add(new JLabel("Filter          "), labelConstraints);

        labelConstraints.gridx = 5;
        labelConstraints.weightx = 1;
        parameterPanel.add(new JLabel(""), labelConstraints);

        labelConstraints.gridx = 6;
        labelConstraints.weightx = 1;
        parameterPanel.add(new JLabel("                                        "), labelConstraints);

        labelConstraints.gridx = 7;
        labelConstraints.weightx = 1;
        parameterPanel.add(new JLabel(""), labelConstraints);

        labelConstraints.gridx = 8;
        labelConstraints.weightx = 1;
        parameterPanel.add(new JLabel("                    "), labelConstraints);


        for (int param = 0; param < variables.length; param++) {
            c.gridy = param + 1;

            // TOGGLE BOX
            checkBox = new JCheckBox("", true);
            c.gridx = 0;
            parameterPanel.add(checkBox, c);
            toggleBoxes[param] = checkBox;

            // NAME
            c.gridx = 1;
            text = new JLabel(variables[param]);
            parameterPanel.add(text, c);

            // Type
            c.gridx = 2;
            parameterPanel.add(new JLabel(variableTypes[param]), c);

            // FILTER
            String[] filters = {"No Filter", "Filter"};
            c.gridx = 3;
            comboBox = new JComboBox<>(filters);
            comboBox.addActionListener(new SetFilterListener(this, param));
            parameterPanel.add(comboBox, c);
            useFilter[param] = comboBox;

            // BOOLEAN FILTERS
            if (variableTypes[param].contains("bool")) {
                Boolean[] boolFilters = {true, false};
                c.gridx = 4;
                comboBox = new JComboBox<>(boolFilters);
                parameterPanel.add(comboBox, c);
                booleanFilter[param] = comboBox;
                comboBox.setVisible(false);
            } else

            // INT/FLOAT FILTERS
            if (variableTypes[param].contains("int") || variableTypes[param].contains("float")) {
                // VALUE GREATER THAN
                c.gridx = 4;
                field = new JTextField();
                parameterPanel.add(field, c);
                valueGreaterThan[param] = field;
                field.setVisible(false);

                // LESS THAN LABEL
                c.gridx = 5;
                text = new JLabel("<");
                text.setHorizontalAlignment(JLabel.CENTER);
                parameterPanel.add(text, c);
                lt1[param] = text;
                text.setVisible(false);

                // VARIABLE LABEL
                c.gridx = 6;
                text = new JLabel(variables[param]);
                text.setHorizontalAlignment(JLabel.CENTER);
                parameterPanel.add(text, c);
                variableName[param] = text;
                text.setVisible(false);

                // LESS THAN LABEL
                c.gridx = 7;
                text = new JLabel("<");
                text.setHorizontalAlignment(JLabel.CENTER);
                parameterPanel.add(text, c);
                lt2[param] = text;
                text.setVisible(false);

                // VALUE LESS THAN
                c.gridx = 8;
                field = new JTextField();
                parameterPanel.add(field, c);
                valueLessThan[param] = field;
                field.setVisible(false);
            } else

            // TEXT FILTERS
            if (variableTypes[param].contains("text")) {
                c.gridx = 4;
                text = new JLabel("Equals");
                text.setHorizontalAlignment(JLabel.CENTER);
                parameterPanel.add(text, c);
                equalsLabel[param] = text;
                text.setVisible(false);

                c.gridx = 5;
                text = new JLabel("=>");
                text.setHorizontalAlignment(JLabel.CENTER);
                parameterPanel.add(text, c);
                arrowLabel[param] = text;
                text.setVisible(false);

                c.gridx = 6;
                field = new JTextField();
                parameterPanel.add(field, c);
                textEqualsTo[param] = field;
                field.setVisible(false);
            }
        }

        frame.add(new JScrollPane(parameterPanel));
    }

    /**
     * Send database information to the PostgreSQLBA object
     */
    public void setDatabaseInformation() {
        String ip = "jbdc:postgresql://" + dbField.getText() + ":" + portField.getText() + "/" + dbNameField.getText();
        String port = portField.getText();
        String username = userField.getText();
        String password = String.valueOf(passField.getPassword());
        PostgreSQLBA.setDatabaseInformation(ip, port, username, password);
        System.out.println(ip);
    }

    /**
     * Save fields to a cahce file to be recalled on program open
     * IN FUTURE: SAVE JSWING FIELDS TO JSON INSTEAD OF JUST THE VALUES. SIMPLER AND FASTER.
     */
    public void saveSettingsToCache() {
        String ra = raField.getText();
        String dec = decField.getText();
        String rad = radField.getText();
        String dbip = dbField.getText();
        String port = portField.getText();
        String dbname = dbNameField.getText();
        String user = userField.getText();
        String pass = String.valueOf(passField.getPassword());

        boolean[] toggles = new boolean[variables.length];
        boolean[] useFilters = new boolean[variables.length];
        String[] numberArg1 = new String[variables.length];
        String[] numberArg2 = new String[variables.length];
        String[] textArg = new String[variables.length];
        boolean[] boolValue = new boolean[variables.length];

        for (int i = 0; i < variables.length; i++) {
            toggles[i] = toggleBoxes[i].isSelected();
            useFilters[i] = useFilter[i].getSelectedItem().equals("Filter");
            numberArg1[i] = valueGreaterThan[i] != null ? valueGreaterThan[i].getText() : "";
            numberArg2[i] = valueLessThan[i] != null ? valueLessThan[i].getText() : "";
            textArg[i] = textEqualsTo[i] != null ? textEqualsTo[i].getText() : "";
            boolValue[i] = booleanFilter[i] != null ? (Boolean) booleanFilter[i].getSelectedItem() : false;
        }

        SettingsCache cacheString = new SettingsCache(ra, dec, rad, dbip, port, dbname, user, pass, toggles, useFilters, numberArg1, numberArg2, textArg, boolValue);
        try {
            FileWriter writer = new FileWriter("AurvandilCache.json");
            writer.write(new ObjectMapper().writeValueAsString(cacheString));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the cached parameters into respective fields on program open
     */
    public void loadSettingsFromCache() {
        if (!new File("AurvandilCache.json").isFile()) {
            return;
        }

        SettingsCache cachedSettings;
        try {
            File myObj = new File("AurvandilCache.json");
            Scanner myReader = new Scanner(myObj);
            cachedSettings = new ObjectMapper().readValue(myReader.nextLine(), SettingsCache.class);
            myReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        raField.setText(cachedSettings.ra);
        decField.setText(cachedSettings.dec);
        radField.setText(cachedSettings.rad);
        dbField.setText(cachedSettings.dbip);
        portField.setText(cachedSettings.port);
        dbNameField.setText(cachedSettings.dbname);
        userField.setText(cachedSettings.user);
        passField.setText(cachedSettings.pass);

        for (int i = 0; i < variables.length; i++) {
            toggleBoxes[i].setSelected(cachedSettings.toggles[i]);
            boolean filterOn = cachedSettings.useFilters[i];
            useFilter[i].setSelectedItem(filterOn ? "Filter" : "No Filter");

            // BOOLEAN FILTERS
            if (variableTypes[i].contains("bool") && filterOn) {
                booleanFilter[i].setSelectedItem(cachedSettings.boolValue[i]);
            } else

            // INT/FLOAT FILTERS
            if ((variableTypes[i].contains("int") || variableTypes[i].contains("float")) && filterOn) {
                valueGreaterThan[i].setText(cachedSettings.numberArg1[i]);
                valueLessThan[i].setText(cachedSettings.numberArg2[i]);
            } else

            // TEXT FILTERS
            if (variableTypes[i].contains("text") && filterOn) {
                textEqualsTo[i].setText(cachedSettings.textArg[i]);
            }
        }
    }

    public static final String[] variables = {"solution_id",
            "designation",
            "source_id",
            "random_index",
            "ref_epoch",
            "ra",
            "ra_error",
            "dec",
            "dec_error",
            "parallax",
            "parallax_error",
            "parallax_over_error",
            "pm",
            "pmra",
            "pmra_error",
            "pmdec",
            "pmdec_error",
            "ra_dec_corr",
            "ra_parallax_corr",
            "ra_pmra_corr",
            "ra_pmdec_corr",
            "dec_parallax_corr",
            "dec_pmra_corr",
            "dec_pmdec_corr",
            "parallax_pmra_corr",
            "parallax_pmdec_corr",
            "pmra_pmdec_corr",
            "astrometric_n_obs_al",
            "astrometric_n_obs_ac",
            "astrometric_n_good_obs_al",
            "astrometric_n_bad_obs_al",
            "astrometric_gof_al",
            "astrometric_chi2_al",
            "astrometric_excess_noise",
            "astrometric_excess_noise_sig",
            "astrometric_params_solved",
            "astrometric_primary_flag",
            "nu_eff_used_in_astrometry",
            "pseudocolour",
            "pseudocolour_error",
            "ra_pseudocolour_corr",
            "dec_pseudocolour_corr",
            "parallax_pseudocolour_corr",
            "pmra_pseudocolour_corr",
            "pmdec_pseudocolour_corr",
            "astrometric_matched_transits",
            "visibility_periods_used",
            "astrometric_sigma5d_max",
            "matched_transits",
            "new_matched_transits",
            "matched_transits_removed",
            "ipd_gof_harmonic_amplitude",
            "ipd_gof_harmonic_phase",
            "ipd_frac_multi_peak",
            "ipd_frac_odd_win",
            "ruwe",
            "scan_direction_strength_k1",
            "scan_direction_strength_k2",
            "scan_direction_strength_k3",
            "scan_direction_strength_k4",
            "scan_direction_mean_k1",
            "scan_direction_mean_k2",
            "scan_direction_mean_k3",
            "scan_direction_mean_k4",
            "duplicated_source",
            "phot_g_n_obs",
            "phot_g_mean_flux",
            "phot_g_mean_flux_error",
            "phot_g_mean_flux_over_error",
            "phot_g_mean_mag",
            "phot_bp_n_obs",
            "phot_bp_mean_flux",
            "phot_bp_mean_flux_error",
            "phot_bp_mean_flux_over_error",
            "phot_bp_mean_mag",
            "phot_rp_n_obs",
            "phot_rp_mean_flux",
            "phot_rp_mean_flux_error",
            "phot_rp_mean_flux_over_error",
            "phot_rp_mean_mag",
            "phot_bp_rp_excess_factor",
            "phot_bp_n_contaminated_transits",
            "phot_bp_n_blended_transits",
            "phot_rp_n_contaminated_transits",
            "phot_rp_n_blended_transits",
            "phot_proc_mode",
            "bp_rp",
            "bp_g",
            "g_rp",
            "radial_velocity",
            "radial_velocity_error",
            "rv_method_used",
            "rv_nb_transits",
            "rv_nb_deblended_transits",
            "rv_visibility_periods_used",
            "rv_expected_sig_to_noise",
            "rv_renormalised_gof",
            "rv_chisq_pvalue",
            "rv_time_duration",
            "rv_amplitude_robust",
            "rv_template_teff",
            "rv_template_logg",
            "rv_template_fe_h",
            "rv_atm_param_origin",
            "vbroad",
            "vbroad_error",
            "vbroad_nb_transits",
            "grvs_mag",
            "grvs_mag_error",
            "grvs_mag_nb_transits",
            "rvs_spec_sig_to_noise",
            "phot_variable_flag",
            "l",
            "b",
            "ecl_lon",
            "ecl_lat",
            "in_qso_candidates",
            "in_galaxy_candidates",
            "non_single_star",
            "has_xp_continuous",
            "has_xp_sampled",
            "has_rvs",
            "has_epoch_photometry",
            "has_epoch_rv",
            "has_mcmc_gspphot",
            "has_mcmc_msc",
            "in_andromeda_survey",
            "classprob_dsc_combmod_quasar",
            "classprob_dsc_combmod_galaxy",
            "classprob_dsc_combmod_star",
            "teff_gspphot",
            "teff_gspphot_lower",
            "teff_gspphot_upper",
            "logg_gspphot",
            "logg_gspphot_lower",
            "logg_gspphot_upper",
            "mh_gspphot",
            "mh_gspphot_lower",
            "mh_gspphot_upper",
            "distance_gspphot",
            "distance_gspphot_lower",
            "distance_gspphot_upper",
            "azero_gspphot",
            "azero_gspphot_lower",
            "azero_gspphot_upper",
            "ag_gspphot",
            "ag_gspphot_lower",
            "ag_gspphot_upper",
            "ebpminrp_gspphot",
            "ebpminrp_gspphot_lower",
            "ebpminrp_gspphot_upper",
            "libname_gspphot"};

    public static final String[] variableTypes = {"int8",
            "text",
            "int8",
            "int8",
            "float8",
            "float8",
            "float4",
            "float8",
            "float4",
            "float8",
            "float4",
            "float4",
            "float4",
            "float8",
            "float4",
            "float8",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "int2",
            "int2",
            "int2",
            "int2",
            "float4",
            "float4",
            "float4",
            "float4",
            "int2",
            "bool",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "int2",
            "int2",
            "float4",
            "int2",
            "int2",
            "int2",
            "float4",
            "float4",
            "int2",
            "int2",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "bool",
            "int2",
            "float8",
            "float4",
            "float4",
            "float4",
            "int2",
            "float8",
            "float4",
            "float4",
            "float4",
            "int2",
            "float8",
            "float4",
            "float4",
            "float4",
            "float4",
            "int2",
            "int2",
            "int2",
            "int2",
            "int2",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "int2",
            "int2",
            "int2",
            "int2",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "int2",
            "float4",
            "float4",
            "int2",
            "float4",
            "float4",
            "int2",
            "float4",
            "text",
            "float8",
            "float8",
            "float8",
            "float8",
            "bool",
            "bool",
            "int2",
            "bool",
            "bool",
            "bool",
            "bool",
            "bool",
            "bool",
            "bool",
            "bool",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "float4",
            "text"};
}
