package pt.iscte.linetool.graphics;

import java.awt.Point;


/**
 * @author Rafael Campos
 *
 */
public class MVertex{
	
	int x;
	int y;
	private Point point;
	
	public MVertex(int x, int y){
		this.x = x;
		this.y = y;
		point = null;
	}
	
	public Point getPoint(){
		if(point==null || point.x!=x || point.y!=y){
			point = new Point(x,y);
		}
		return point;
	}
	@Override
	public String toString() {
		return "("+x+","+y+")";
	}
	
}
