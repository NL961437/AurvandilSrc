package Aurvandil.BA;

import essentials.HealpixBase;
import essentials.Pointing;

import static essentials.Scheme.NESTED;

public class HealPixBA {
    HealpixBase sphere;
    public HealPixBA(int numPixels) {
        try {
            int nSize = (int) Math.sqrt(numPixels / 12.0);
            sphere = new HealpixBase(nSize, NESTED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public long getPixelLocation(double ra, double dec) {
        //ra = 0 -> 360
        //dec = -90 -> 90
        double rightAscension = Math.toRadians(ra);
        double declination = Math.toRadians((dec - 90) * -1);

        long pixel = -1L;
        try {
            pixel = sphere.ang2pix(new Pointing(declination, rightAscension));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pixel;
    }

    public long[] conePixelSearch(double ra, double dec, double radius) {
        //ra = 0 -> 360
        //dec = -90 -> 90
        double rightAscension = Math.toRadians(ra); // radians
        double declination = Math.toRadians((dec - 90) * -1); // 0 to pi

        long[] pixels = null;
        try {
            pixels = sphere.queryDiscInclusive(new Pointing(declination, rightAscension), Math.toRadians(radius), 2).toArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return pixels;
    }
}
