package Aurvandil.Util;

public class HaversineDistanceUtil {
    public static boolean checkDistance(double centerRA, double centerDEC, double radius, double pointRA, double pointDEC) {
        //1 DEG = 100
        //RA = 0 -> 360 // LONG
        //DEC = -90 -> 90 //LAT
        double R = 18000 / Math.PI;  // radius of Earth in meters
        double phi_1 = Math.toRadians(centerDEC);
        double phi_2 = Math.toRadians(pointDEC);

        double delta_phi = Math.toRadians(pointDEC - centerDEC);
        double delta_lambda = Math.toRadians(pointRA - centerRA);

        double a = Math.pow(Math.sin(delta_phi / 2.0), 2) + Math.cos(phi_1) * Math.cos(phi_2) * Math.pow(Math.sin(delta_lambda / 2.0), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;  // output distance in meters
        return !(distance > radius*100);
    }
}
