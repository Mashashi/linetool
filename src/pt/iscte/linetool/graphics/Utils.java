package pt.iscte.linetool.graphics;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * @author Rafael Campos
 *
 */
public class Utils {
	
	public static final double ZERO_INCLINATION 	= 0.000000000000000000001;
	public static final double INFINITE_INCLINATION	= 1000000;
	private Utils(){}
	
	public static double getMouseDistanceFromVertice(List<MPolygon> drawObjects, int drawObject, int vertice, int x, int y){
		return getMouseDistanceFromVertice(drawObjects.get(drawObject), vertice,x ,y);
	}
	
	public static double getMouseDistanceFromVertice(MPolygon drawObject, int vertice, int x, int y){
		return getDistanceBetweenPoints(drawObject.getX(vertice), drawObject.getY(vertice), x, y);
	}
	
	public static double getDistanceBetweenPoints(double x1, double y1, double x2, double y2){
		double cateto_oposto = x1-x2;
		double cateto_adjacente = y1-y2;
		return Math.sqrt(Math.pow(cateto_oposto, 2)+Math.pow(cateto_adjacente, 2));
	}
	
	public static double getPerpendicularInclination(double inclination){
		return -(1/inclination);
	}
	
	public static double getInclination(Point2D start, Point2D end){
		return (end.getY()-start.getY())/(end.getX()-start.getX());
	}
	
	public static double getB(double inclination, double x, double y){
		return y-inclination*x;
	}
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @param position
	 * @return The closest point of the line near the position
	 */
	public static Point getIntersectionPoint(Line2D line, Point2D position, boolean continousSnap){
		Point returned = null;
		double inclination = getInclination(line.getP1(), line.getP2());
		
		if(inclination==0){
			inclination=ZERO_INCLINATION;
		}else if(Double.isInfinite(inclination)){
			inclination=INFINITE_INCLINATION;
		}
			double b = getB(inclination, line.getX1(), line.getY1());
			
			double x = (position.getY()-b+(position.getX()/inclination))*(inclination/(Math.pow(inclination, 2)+1));
			double y = inclination*x+b;
			
			if( continousSnap || (x>=line.getX1() && x<=line.getX2() && y<=line.getY1() && y>=line.getY2()) 
					|| (x>=line.getX1() && x<=line.getX2() && y >= line.getY1() && y <=line.getY2())
					|| (x<=line.getX1() && x>=line.getX2() && y<=line.getY1() && y>=line.getY2()) 
					|| (x<=line.getX1() && x>=line.getX2() && y >= line.getY1() && y <=line.getY2())){
				returned = new Point((int)Math.round(x), (int)Math.round(y));
			}
			return returned;
		
	}
	
	public static boolean isOnSameAxis(Line2D line1, Line2D line2){

		final double mil =((double)100);
		double mLine1 = (Math.round(getInclination(line1.getP1(), line1.getP2())*mil))/mil;
		double mLine2 =  Math.round(getInclination(line2.getP1(), line2.getP2())*mil)/mil;
		double bLine1 =  (getB(mLine1, line1.getX1(), line1.getY1()));
		double bLine2 =  (getB(mLine2, line2.getX1(), line2.getY1()));
		
		return  (mLine1 == mLine2) && Math.abs(bLine1-bLine2)<2;
	}
	
	public static Line2D getMaximalLine(Line2D line1, Line2D line2){
		
		if(isOnSameAxis(line1, line2)){
			
			Point2D line1PointClosestToTop = closestToTop(line1);
			
			if(line1PointClosestToTop!=null){
				//A recta não é horizontal
				Point2D line2PointClosestToTop = closestToTop(line2);
				
				Point2D resultPoint1 = (line1PointClosestToTop.getY()<line2PointClosestToTop.getY())?line1PointClosestToTop:line2PointClosestToTop;
				
				Point2D resultPoint1line = closestToBottom(line1);;
				Point2D resultPoint2line = closestToBottom(line2);;
				
				Point2D resultPoint2 = (resultPoint1line.getY()>resultPoint2line.getY())?resultPoint1line:resultPoint2line;
				return new Line2D.Double(resultPoint1, resultPoint2);
			}else{
				//A recta é horizontal
				Point2D line1PointClosestToLeft = closestToLeft(line1);
				Point2D line2PointClosestToLeft = closestToLeft(line2);
				
				Point2D resultPoint1 = (line1PointClosestToLeft.getX()<line2PointClosestToLeft.getX())?line1PointClosestToLeft:line2PointClosestToLeft;
				
				Point2D line1PointClosestToRight = closestToRight(line1);
				Point2D line2PointClosestToRight = closestToRight(line2);
				
				Point2D resultPoint2 = (line1PointClosestToRight.getX()>line2PointClosestToRight.getX())?line1PointClosestToRight:line2PointClosestToRight;
				return new Line2D.Double(resultPoint1, resultPoint2);
			}
			
		}
		
		return null;
		
	} 
	
	private static Point2D closestToTop(Line2D line1){
		
		if((line1.getY1()<line1.getY2())){
			return line1.getP1(); 
		}else if((line1.getY1()>line1.getY2())){
			return line1.getP2();
		}
		
		return null;
	}
	
	private static Point2D closestToBottom(Line2D line1){
		
		if((line1.getY1()>line1.getY2())){
			return line1.getP1(); 
		}else if((line1.getY1()<line1.getY2())){
			return line1.getP2();
		}
		
		return null;
	}
	
	
	private static Point2D closestToLeft(Line2D line1){
		
		if((line1.getX1()<line1.getX2())){
			return line1.getP1(); 
		}else if((line1.getX1()>line1.getX2())){
			return line1.getP2();
		}
		
		return null;
	}
	
	private static Point2D closestToRight(Line2D line1){
		
		if((line1.getX1()>line1.getX2())){
			return line1.getP1(); 
		}else if((line1.getX1()<line1.getX2())){
			return line1.getP2();
		}
		
		return null;
	}
	
	public static double radians(double angle){
		return (Math.PI*angle)/180;
	}
	
	public static Color negativeColor(Color color){
		int red 	= 255-color.getRed();
		int green 	= 255-color.getGreen();
		int blue 	= 255-color.getBlue();
		return new Color(red, green, blue);
	}
	
	public static Color halfNegativeColor(Color color){
		int red 	= Math.abs(126-color.getRed());
		int green 	= Math.abs(126-color.getGreen());
		int blue 	= Math.abs(126-color.getBlue());
		return new Color(red, green, blue);
	}
}		