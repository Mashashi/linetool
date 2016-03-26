/**
 * 
 */
package pt.iscte.linetool;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import pt.iscte.linetool.graphics.Utils;



/**
 * @author Rafael Campos
 *
 */
public class Tests {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Line2D line = Utils.getMaximalLine(new Line2D.Double(new Point2D.Double(210,159),new Point2D.Double(455,69)), new Line2D.Double(new Point2D.Double(362,103), new Point2D.Double(100,200)));
		System.out.println("("+line.getP1()+", "+line.getP2()+")");
	}
	
}
