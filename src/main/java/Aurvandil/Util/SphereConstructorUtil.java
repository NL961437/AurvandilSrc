package Aurvandil.Util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SphereConstructorUtil {
    public static String findAllTables = "SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'";
    public static String createTable = "CREATE TABLE pixel%d (" +
            "solution_id int8," +
            "designation text," +
            "source_id int8," +
            "random_index int8," +
            "ref_epoch float8," +
            "ra float8," +
            "ra_error float4," +
            "dec float8," +
            "dec_error float4," +
            "parallax float8," +
            "parallax_error float4," +
            "parallax_over_error float4," +
            "pm float4," +
            "pmra float8," +
            "pmra_error float4," +
            "pmdec float8," +
            "pmdec_error float4," +
            "ra_dec_corr float4," +
            "ra_parallax_corr float4," +
            "ra_pmra_corr float4," +
			"ra_pmdec_corr float4," +
			"dec_parallax_corr float4," +
			"dec_pmra_corr float4," +
			"dec_pmdec_corr float4," +
			"parallax_pmra_corr float4," +
			"parallax_pmdec_corr float4," +
			"pmra_pmdec_corr float4," +
			"astrometric_n_obs_al int2," +
			"astrometric_n_obs_ac int2," +
			"astrometric_n_good_obs_al int2," +
			"astrometric_n_bad_obs_al int2," +
			"astrometric_gof_al float4," +
			"astrometric_chi2_al float4," +
			"astrometric_excess_noise float4," +
			"astrometric_excess_noise_sig float4," +
			"astrometric_params_solved int2," +
			"astrometric_primary_flag bool," +
			"nu_eff_used_in_astrometry float4," +
			"pseudocolour float4," +
			"pseudocolour_error float4," +
			"ra_pseudocolour_corr float4," +
			"dec_pseudocolour_corr float4," +
			"parallax_pseudocolour_corr float4," +
			"pmra_pseudocolour_corr float4," +
			"pmdec_pseudocolour_corr float4," +
			"astrometric_matched_transits int2," +
			"visibility_periods_used int2," +
			"astrometric_sigma5d_max float4," +
			"matched_transits int2," +
			"new_matched_transits int2," +
			"matched_transits_removed int2," +
			"ipd_gof_harmonic_amplitude float4," +
			"ipd_gof_harmonic_phase float4," +
			"ipd_frac_multi_peak int2," +
			"ipd_frac_odd_win int2," +
			"ruwe float4," +
			"scan_direction_strength_k1 float4," +
			"scan_direction_strength_k2 float4," +
			"scan_direction_strength_k3 float4," +
			"scan_direction_strength_k4 float4," +
			"scan_direction_mean_k1 float4," +
			"scan_direction_mean_k2 float4," +
			"scan_direction_mean_k3 float4," +
			"scan_direction_mean_k4 float4," +
			"duplicated_source bool," +
			"phot_g_n_obs int2," +
			"phot_g_mean_flux float8," +
			"phot_g_mean_flux_error float4," +
			"phot_g_mean_flux_over_error float4," +
			"phot_g_mean_mag float4," +
			"phot_bp_n_obs int2," +
			"phot_bp_mean_flux float8," +
			"phot_bp_mean_flux_error float4," +
			"phot_bp_mean_flux_over_error float4," +
			"phot_bp_mean_mag float4," +
			"phot_rp_n_obs int2," +
			"phot_rp_mean_flux float8," +
			"phot_rp_mean_flux_error float4," +
			"phot_rp_mean_flux_over_error float4," +
			"phot_rp_mean_mag float4," +
			"phot_bp_rp_excess_factor float4," +
			"phot_bp_n_contaminated_transits int2," +
			"phot_bp_n_blended_transits int2," +
			"phot_rp_n_contaminated_transits int2," +
			"phot_rp_n_blended_transits int2," +
			"phot_proc_mode int2," +
			"bp_rp float4," +
			"bp_g float4," +
			"g_rp float4," +
			"radial_velocity float4," +
			"radial_velocity_error float4," +
			"rv_method_used int2," +
			"rv_nb_transits int2," +
			"rv_nb_deblended_transits int2," +
			"rv_visibility_periods_used int2," +
			"rv_expected_sig_to_noise float4," +
			"rv_renormalised_gof float4," +
			"rv_chisq_pvalue float4," +
			"rv_time_duration float4," +
			"rv_amplitude_robust float4," +
			"rv_template_teff float4," +
			"rv_template_logg float4," +
			"rv_template_fe_h float4," +
			"rv_atm_param_origin int2," +
			"vbroad float4," +
			"vbroad_error float4," +
			"vbroad_nb_transits int2," +
			"grvs_mag float4," +
			"grvs_mag_error float4," +
			"grvs_mag_nb_transits int2," +
			"rvs_spec_sig_to_noise float4," +
			"phot_variable_flag text," +
			"l float8," +
			"b float8," +
			"ecl_lon float8," +
			"ecl_lat float8," +
			"in_qso_candidates bool," +
			"in_galaxy_candidates bool," +
			"non_single_star int2," +
			"has_xp_continuous bool," +
			"has_xp_sampled bool," +
			"has_rvs bool," +
			"has_epoch_photometry bool," +
			"has_epoch_rv bool," +
			"has_mcmc_gspphot bool," +
			"has_mcmc_msc bool," +
			"in_andromeda_survey bool," +
			"classprob_dsc_combmod_quasar float4," +
			"classprob_dsc_combmod_galaxy float4," +
			"classprob_dsc_combmod_star float4," +
			"teff_gspphot float4," +
			"teff_gspphot_lower float4," +
			"teff_gspphot_upper float4," +
			"logg_gspphot float4," +
			"logg_gspphot_lower float4," +
			"logg_gspphot_upper float4," +
			"mh_gspphot float4," +
			"mh_gspphot_lower float4," +
			"mh_gspphot_upper float4," +
			"distance_gspphot float4," +
			"distance_gspphot_lower float4," +
			"distance_gspphot_upper float4," +
			"azero_gspphot float4," +
			"azero_gspphot_lower float4," +
			"azero_gspphot_upper float4," +
			"ag_gspphot float4," +
			"ag_gspphot_lower float4," +
			"ag_gspphot_upper float4," +
			"ebpminrp_gspphot float4," +
			"ebpminrp_gspphot_lower float4," +
			"ebpminrp_gspphot_upper float4," +
            "libname_gspphot text" +
            ");";

	public static String[] getDestroySphereCommands(Connection conn) {
		List<String> names = new ArrayList<>();
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(findAllTables);
			while (rs.next()) {
				names.add(rs.getString(1));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		String[] commands = new String[names.size()];
		for (int name = 0; name < names.size(); name++) {
			commands[name] = "DROP TABLE " + names.get(name);
		}
		return commands;
	}

	public static String[] getBuildSphereCommands(int numberOfPixels) {
		String[] commands = new String[numberOfPixels];
		for (int pixel = 0; pixel < numberOfPixels; pixel++) {
			commands[pixel] = String.format(createTable, pixel);
		}
		return commands;
	}
}
