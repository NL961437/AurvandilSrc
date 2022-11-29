package Aurvandil.BA;

import HEALPixUtil.HealpixBase;
import HEALPixUtil.Pointing;

import static HEALPixUtil.Scheme.NESTED;

public class HealPixBA {
    HealpixBase sphere;

    /**
     * Constructor to generate the HEALPix sphere from an existing database
     * @param numPixels number of pixels in the database
     */
    public HealPixBA(int numPixels) {
        try {
            int nSize = (int) Math.sqrt(numPixels / 12.0);
            sphere = new HealpixBase(nSize, NESTED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Get the pixel associated with a specific point on a HEALPix sphere
     * @param ra right ascension in degrees (0 to 360)
     * @param dec declination in degrees (-90 to 90)
     * @return pixel number
     */
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

    /**
     * Get the pixels that lie within a specific cone search
     * @param ra right ascension in degrees (0 to 360)
     * @param dec declination in degrees (-90 to 90)
     * @param radius radius in degrees
     * @return array of pixel numbers
     */
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
