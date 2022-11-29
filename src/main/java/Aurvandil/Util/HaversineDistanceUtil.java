package Aurvandil.Util;

public class HaversineDistanceUtil {
    /**
     * Check if distance a point from a center point lies in a given radius
     * @param centerRA right ascension of center point
     * @param centerDEC declination of center point
     * @param radius radius around center point
     * @param pointRA right ascension of point
     * @param pointDEC declination of point
     * @return boolean whether point lies within radius around center
     */
    public static boolean checkDistance(double centerRA, double centerDEC, double radius, double pointRA, double pointDEC) {
        //1 DEG = 100
        //RA = 0 -> 360 // LONG
        //DEC = -90 -> 90 //LAT
        double R = 18000 / Math.PI;
        double phi_1 = Math.toRadians(centerDEC);
        double phi_2 = Math.toRadians(pointDEC);

        double delta_phi = Math.toRadians(pointDEC - centerDEC);
        double delta_lambda = Math.toRadians(pointRA - centerRA);

        double a = Math.pow(Math.sin(delta_phi / 2.0), 2) + Math.cos(phi_1) * Math.cos(phi_2) * Math.pow(Math.sin(delta_lambda / 2.0), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;
        return !(distance > radius*100);
    }
}
