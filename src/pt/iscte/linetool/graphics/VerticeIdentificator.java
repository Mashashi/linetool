package pt.iscte.linetool.graphics;

/**
 * @author Rafael Campos
 *
 */
public class VerticeIdentificator implements Comparable<VerticeIdentificator>{
	
	private MPolygon drawObject;
	private MVertex vertex;
	private double mouseDistance;
	
	public VerticeIdentificator(MPolygon drawObject, MVertex vertex, double mouseDistance) {
		this.drawObject = drawObject;
		this.vertex = vertex;
		this.mouseDistance = mouseDistance;
	}

	public int getX() {
		return vertex.x;
	}

	public int getY() {
		return vertex.y;
	}

	public MPolygon getDrawObject() {
		return drawObject;
	}
	
	public MVertex getVertex(){
		return vertex;
	}
	
	public double getMouseDistance() {
		return mouseDistance;
	}
	
	
	
	public void setX(int x) {
		vertex.x = x;
	}

	public void setY(int y) {
		vertex.y = y;
	}
	
	public void setMouseDistance(double mouseDistance) {
		this.mouseDistance = mouseDistance;
	}
	
	@Override
	public String toString() {
		return vertex.toString() +" from polygon "+drawObject.hashCode();
	}
	
	@Override
	public int compareTo(VerticeIdentificator vi) {
		double result = this.mouseDistance-vi.mouseDistance;
		if(result<0){ 
			return -1;
		}else if(result>0){
			return 1;
		}
		return 0;
	}
	
	
	@Override
	public boolean equals(Object arg) {
		
		if(!(arg instanceof VerticeIdentificator))
			throw new IllegalArgumentException();
		
		VerticeIdentificator vi = (VerticeIdentificator) arg;
		
		return this.drawObject.equals(vi.drawObject) && vertex.x==vi.getX() && vertex.y==vi.getY();
	}
	
}
