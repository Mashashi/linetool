/**
 * 
 */
package pt.iscte.linetool.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

import Jama.Matrix;

/**
 * @author Rafael Campos
 *
 */
public class MPolygon {
	
	private LinkedList<MVertex> vertexList;
	private Color color;
	private Point startEditingPoint; 
	private BasicStroke extendedLineStroke;
	private BasicStroke normalStroke;
	
	public MPolygon(LinkedList<MVertex> vertexList, Color color, float lineWidth) {
		if(vertexList.size()<2)
			throw new IllegalArgumentException();
		this.vertexList = vertexList;
		this.color = color;
		startEditingPoint = null;
		normalStroke = new BasicStroke(
					lineWidth, 
			      BasicStroke.CAP_ROUND, 
			      BasicStroke.JOIN_ROUND
			      );
		extendedLineStroke = new BasicStroke(
			      1f, 
			      BasicStroke.CAP_SQUARE, 
			      BasicStroke.JOIN_ROUND, 
			      1f, 
			      new float[] {10f}, 
			      10f);
	}
	
	public MPolygon(int[][] vertexList, Color color, float lineWidth){
		this(getLinkedListFormatMPolygon(vertexList), color, lineWidth);
	}
	
	private static LinkedList<MVertex> getLinkedListFormatMPolygon(int[][] vertexList){
		LinkedList<MVertex> vertexListBuffer = new LinkedList<>();
		for(int[] coordinates : vertexList){
			if(coordinates.length!=2){
				throw new IllegalArgumentException();
			}
			vertexListBuffer.add(new MVertex(coordinates[0],coordinates[1]));
		}
		return vertexListBuffer;
	}
	
	public void drawMPolygon(Graphics g, Color selectedColor, boolean lineIdentifierOn, boolean lineEdgesLocation, boolean drawExtension){
		
		g.setColor(startEditingPoint==null?getColor():selectedColor);
		
		drawPolygon(g, drawExtension);
		g.setColor(Utils.negativeColor(color));
		
		int sumX = 0;
		int sumY = 0;
		
		for(int i=0;i<vertexList.size();i++){
			
			if(lineEdgesLocation){
				g.drawString(vertexList.get(i).toString(), vertexList.get(i).x,  vertexList.get(i).y);
			}
			if(lineIdentifierOn){
				sumX += vertexList.get(i).x;
				sumY += vertexList.get(i).y;
			}
		}
		
		if(lineIdentifierOn){
			g.drawString(this.hashCode()+"", sumX/vertexList.size(), sumY/vertexList.size());
		}
		
	}

	public void setWidth(float width){
		normalStroke= new BasicStroke(
			      width, 
			      BasicStroke.CAP_ROUND, 
			      BasicStroke.JOIN_ROUND, 
			      1f, 
			      new float[] {1f}, 
			      1f);
	}
	

	private void drawPolygon(Graphics g, boolean drawExtension){
		
		Stroke oldStroke = ((Graphics2D)g).getStroke();
		
		Iterator<MVertex> iterator = vertexList.iterator();
		MVertex first = iterator.next();
		MVertex previous = first;
		MVertex last = null;
					
			while(iterator.hasNext()){
				last = iterator.next();
				
				if(drawExtension){
						((Graphics2D)g).setStroke(extendedLineStroke);
						
						double m = Utils.getInclination(previous.getPoint(), last.getPoint());
						double b = Utils.getB(m, last.getPoint().x, last.getPoint().y);
						if(Double.isInfinite(m)){
							g.drawLine(last.x, last.y, last.x, 0);
							g.drawLine(last.x, last.y, last.x, Toolkit.getDefaultToolkit().getScreenSize().height);
						}else if(m<0){
							g.drawLine(last.x, last.y,  (int)Math.round(-b/m),0);
							g.drawLine(previous.x, previous.y,  0,(int)Math.round(b));
						}else if(m>0){
							g.drawLine(last.x, last.y,  (int)Math.round((Toolkit.getDefaultToolkit().getScreenSize().height-b)/m), Toolkit.getDefaultToolkit().getScreenSize().height);
							g.drawLine(previous.x, previous.y,  (int)Math.round(-b/m),0);
						}else if(m==0){
							g.drawLine(last.x, last.y, 0, last.y);
							g.drawLine(last.x, last.y, Toolkit.getDefaultToolkit().getScreenSize().width, last.y);
						}
				}
					((Graphics2D)g).setStroke(normalStroke);
						g.drawLine(previous.x, previous.y, last.x, last.y);
				previous = last;
			
		}
		
		if(!first.equals(previous)){
			g.drawLine(first.x, first.y, last.x, last.y);
		}
		((Graphics2D)g).setStroke(oldStroke);
	}
	
	public LinkedList<Line2D> getLines(){
		LinkedList<Line2D> lines = new LinkedList<>();
		Iterator<MVertex> iterator = vertexList.iterator();
		MVertex first = iterator.next();
		MVertex previous = first;
		MVertex last = null;
		
		while(iterator.hasNext()){
			last = iterator.next();
			lines.add(new Line2D.Double(new Point2D.Double(first.x, first.y),new Point2D.Double(last.x, last.y)));
			previous = last;
		}
		if(!first.equals(previous)){
			lines.add(new Line2D.Double(new Point2D.Double(first.x, first.y),new Point2D.Double(last.x, last.y)));
		}
		return lines;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getNumberOfPoints(){
		return vertexList.size();
	}
	
	public MVertex getVertex(int vertexIndex){
		return vertexList.get(vertexIndex);
	}
	
	public int getX(int vertixeIndex){
		return vertexList.get(vertixeIndex).x;
	}
	
	public int getY(int vertixeIndex){
		return vertexList.get(vertixeIndex).y;
	}
	
	public int setX(int vertixeIndex, int value){
		return vertexList.get(vertixeIndex).x = value;
	}
	
	public int setY(int vertixeIndex, int value){
		return vertexList.get(vertixeIndex).y = value;
	}
	
	@Override
	public String toString() {
		return this.hashCode()+"";
	}
	
	public void editing(Point startEditingPoint){
		this.startEditingPoint = startEditingPoint;
	}
	
	public void moveTo(int x, int y) {
		for(MVertex coordinates : vertexList){
			coordinates.x = coordinates.x-(startEditingPoint.x-x);
			coordinates.y = coordinates.y-(startEditingPoint.y-y);
		}
		startEditingPoint.x =x;
		startEditingPoint.y =y;
	}

	public double getWidth() {
		return normalStroke.getLineWidth();
	}
	
	
	
	public void rotation(double rotation){
		rotation = Utils.radians(rotation);
		Matrix matrix1 = new Matrix(new double[][]{{(double)vertexList.get(0).x},{(double)vertexList.get(0).y},{1}});
		Matrix matrix2 = new Matrix(new double[][]{{(double)vertexList.get(1).x},{(double)vertexList.get(1).y},{1}});
		
		Matrix matrixRotation = new Matrix(new double[][]{{Math.cos(rotation),-Math.sin(rotation), 0},{Math.sin(rotation), Math.cos(rotation), 0},{0,0,1}});
		matrix1 = matrixRotation.times(matrix1);
		matrix2 = matrixRotation.times(matrix2);
		
		vertexList.get(0).x = (int) matrix1.get(0, 0);
		vertexList.get(0).y = (int) matrix1.get(1, 0);
		
		vertexList.get(1).x = (int) matrix2.get(0, 0);
		vertexList.get(1).y = (int) matrix2.get(1, 0);
		
		
	}
	
	public void scaling(double scalingX, double scalingY){
		
		int xO = (vertexList.get(0).x+vertexList.get(1).x)/2;
		int yO = (vertexList.get(0).y+vertexList.get(1).y)/2;
		
		translation(-xO, -yO);
		
		Matrix matrix1 = new Matrix(new double[][]{{(double)vertexList.get(0).x},{(double)vertexList.get(0).y},{1}});
		Matrix matrix2 = new Matrix(new double[][]{{(double)vertexList.get(1).x},{(double)vertexList.get(1).y},{1}});
		
		Matrix matrixRotation = new Matrix(new double[][]{{scalingX,0,1},{0,scalingY,0},{0,0,1}});
		matrix1 = matrixRotation.times(matrix1);
		matrix2 = matrixRotation.times(matrix2);
		
		vertexList.get(0).x = (int) matrix1.get(0, 0);
		vertexList.get(0).y = (int) matrix1.get(1, 0);
		
		vertexList.get(1).x = (int) matrix2.get(0, 0);
		vertexList.get(1).y = (int) matrix2.get(1, 0);
		
		translation(xO, yO);
		
	}
	
	public void translation(int x, int y){
		vertexList.get(0).x += x;
		vertexList.get(0).y += y;
		
		vertexList.get(1).x += x;
		vertexList.get(1).y += y;
	}
	
}
