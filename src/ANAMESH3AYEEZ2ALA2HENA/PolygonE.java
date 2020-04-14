package ANAMESH3AYEEZ2ALA2HENA;

import java.awt.Dimension;
import java.awt.Polygon;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

@SuppressWarnings("all")

/**
 * PolygonE
 */
public class PolygonE extends Polygon implements Serializable {
   
	public boolean equals(Polygon p2) {
        // if the first polygon is bigger it returns 1;
        // if the 2nd polygon is bigger it returns -1
        // if they are equal it returns 0
        Polygon p1 = (Polygon) this;
        boolean n = false;
        Dimension dim1 = p1.getBounds().getSize();
        int p1area = dim1.width * dim1.height;

        Dimension dim2 = p2.getBounds().getSize();
        int p2area = dim2.width * dim2.height;
        // System.out.println(p1area+" "+p2area);
        if (p1area > p2area) {
            n = false;
        } else if (Arrays.equals(p1.xpoints,p2.xpoints)&&Arrays.equals(p1.ypoints,p2.ypoints)) {
            n = true;
        } else if (p1area < p2area) {
            n = false;
        }
        System.out.println("polyeq   " + n);
        return n;

    }

    public String toString() {
        return this.xpoints + " " + this.ypoints;
    }

}