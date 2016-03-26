/**
 * 
 */
package pt.iscte.linetool.graphics;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import pt.iscte.linetool.interaction.ContextMenu;
import pt.iscte.linetool.interaction.ContextMenuMPolygon;




/**
 * @author Rafael Campos
 *
 */
@SuppressWarnings("serial")
public class Panel2D extends JPanel implements ComponentListener, MouseListener, MouseMotionListener{
	
	
	public int VERTICE_INDICATOR_CIRCLE_RADIUS = 10;
	public static final int VERTICE_INDICATOR_MAX_DISTANCE_TO_HIGH_LIGHT = 200;
	
	private Color bgColor;
	
	private List<MPolygon> drawObjects;
	private ArrayList<VerticeIdentificator> nearVertices;
	private ArrayList<VerticeIdentificator> snapVertices;
	
	//Coordenadas do rato
	private int x;
	private int y;
	
	private boolean isCapsOn;
	private boolean controlPressed;
	
	private boolean newLineCreated;
	
	private boolean moveDragOn;
	
	private Color defaultPolygonColor;
	private boolean lineIdentifierOn;
	private boolean lineEdgesLocation;
	private boolean cursorPosition;
	private MPolygon polygonMoved;
	private Color selectedColor;
	private float defaultLineWidth;
	
	//Para lidar com o flicker
	private Graphics buffer;
	private Image offScreen;
	
	private boolean continuousSnap;
	
	public Panel2D(Color defaultBgColor, Color defaultPolygonColor) throws AWTException {
		
		this.addComponentListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
	
		x = 0;
		y = 0;
		bgColor = defaultBgColor;
		nearVertices = new ArrayList<VerticeIdentificator>();
		snapVertices = new ArrayList<VerticeIdentificator>();
		moveDragOn =true;
		
		newLineCreated = false;
		
		lineIdentifierOn  = false;
		lineEdgesLocation = false;
		cursorPosition	  = false;
		selectedColor = Color.RED;
		defaultLineWidth = 1;
		
		this.defaultPolygonColor = defaultPolygonColor;
		isCapsOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		continuousSnap = true;
		
		drawObjects = Collections.synchronizedList(new LinkedList<MPolygon>());
		drawObjects.add(new MPolygon(new int[][]{{2,100},{100,200}},Color.ORANGE, defaultLineWidth));
	}
	
	public void updateCaps(){
		isCapsOn = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
		//System.out.println("hello "+isCapsOn);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param maximum Indica quantos resutados devem ser tirados
	 * @param distance_max 
	 * @param max_after_order Indica se o tirar dos resultados deve ser efectuado depois do cálculo de todos os pontos
	 * @param discard_closer 
	 * @return
	 */
	public ArrayList<VerticeIdentificator> nearVertices(int x, int y, final int maximum, final int distance_max, boolean max_after_order, boolean discard_closer){
		ArrayList<VerticeIdentificator> vertices = new ArrayList<VerticeIdentificator>();
		for(int object=0;object<drawObjects.size() && (vertices.size() <maximum || max_after_order);object++){
			for(int vertice=0;vertice<drawObjects.get(object).getNumberOfPoints() && (vertices.size()<maximum|| max_after_order);vertice++){
				double distance = Utils.getMouseDistanceFromVertice(drawObjects,object, vertice, x, y);
				if(distance != Double.NaN && distance<distance_max){
					
					//Refracting
					vertices.add(new VerticeIdentificator(drawObjects.get(object), 
							drawObjects.get(object).getVertex(vertice),
							distance));
					
					
				}
			}
		}
		Collections.sort(vertices);
		if(discard_closer && vertices.size()>0){
			vertices.remove(0);
		}
		if(vertices.size()>maximum && max_after_order){
			vertices = new ArrayList<VerticeIdentificator>(vertices.subList(0, maximum));
		}
		return vertices;
	}
	
	
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return Retorna o poligno do vértice que está ser movimentado e o índice do vértice que foi modificado
	 */
	private Object[] highlightCloseVertices(int x, int y){
		snapVertices = nearVertices(x, y, 10, VERTICE_INDICATOR_MAX_DISTANCE_TO_HIGH_LIGHT, true, false);
		MPolygon polygon = null;
		int i = -1;
		if(nearVertices.size()!=0){
			polygon = nearVertices.get(0).getDrawObject();
			
			for(i=0;i<polygon.getNumberOfPoints(); i++){
				if(polygon.getX(i) == nearVertices.get(0).getX() && polygon.getY(i) == nearVertices.get(0).getY()){
					
					if(moveDragOn){
						nearVertices.get(0).setX(x);
						nearVertices.get(0).setY(y);
						nearVertices.get(0).setMouseDistance(Utils.getMouseDistanceFromVertice(nearVertices.get(0).getDrawObject(), i, x, y));
					}
					
					break;
					
				}
			}
			
		}else if(polygonMoved==null){
			MPolygon newLine = new MPolygon(new int[][]{{x,y},{x,y}}, defaultPolygonColor, defaultLineWidth);
			drawObjects.add(newLine);
			nearVertices.add(new VerticeIdentificator(newLine, newLine.getVertex(0), 0));
		}
		
		return new Object[]{polygon, new Integer(i)};
		
	}
	
	public void toogleCaps(){
		isCapsOn = !isCapsOn;
	}
	
	public void toogleControl(){
		controlPressed = !controlPressed; 
		if(controlPressed){
			newLineCreated = false;
		}
	}
	
	//Swing
	
	private void showPop(MouseEvent e){
        ContextMenu menu = new ContextMenu(this);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
	
	private void showPopMPolygon(MouseEvent e, MPolygon mpolygon){
        ContextMenuMPolygon menu = new ContextMenuMPolygon(this, mpolygon);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }
	
	private void indicateNearVertices(Graphics2D buffer2d){
		if(nearVertices.size()>0){
			buffer2d.setColor(Color.RED);
			buffer2d.drawOval(nearVertices.get(0).getX()-VERTICE_INDICATOR_CIRCLE_RADIUS, nearVertices.get(0).getY()-VERTICE_INDICATOR_CIRCLE_RADIUS, VERTICE_INDICATOR_CIRCLE_RADIUS*2, VERTICE_INDICATOR_CIRCLE_RADIUS*2);
		}
	}
	
	private void indicateSnapVertices(Graphics2D buffer2d){
		
		Iterator<VerticeIdentificator> iterator = snapVertices.iterator();
		if(iterator.hasNext()){
			//Não queremos pintar o vértice selecionado
			iterator.next();
		}
		
		while(iterator.hasNext()){
			VerticeIdentificator elem = iterator.next();
			Color actualColor = Color.RED;
			int newAlpha = (int) (actualColor.getAlpha()*(1-(elem.getMouseDistance()/VERTICE_INDICATOR_MAX_DISTANCE_TO_HIGH_LIGHT)));
			Color color = new Color(actualColor.getRed(),actualColor.getGreen(),actualColor.getBlue(), newAlpha<0?0:(newAlpha>255?255:newAlpha));
			
			buffer2d.setColor(color);
			buffer2d.drawOval(elem.getX()-VERTICE_INDICATOR_CIRCLE_RADIUS, elem.getY()-VERTICE_INDICATOR_CIRCLE_RADIUS, VERTICE_INDICATOR_CIRCLE_RADIUS*2, VERTICE_INDICATOR_CIRCLE_RADIUS*2);
		}
	}
	
	private void getNewImageBuffer(){
		if(this.getWidth()>0 && this.getHeight()>0){
			offScreen = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			offScreen.setAccelerationPriority(1);
			buffer = offScreen.getGraphics();
		}
	}
	
	public Color getBgColor() {
		return bgColor;
	}
	
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}
	
	public void setSelectedLineColor(Color selLineColor) {
		selectedColor = selLineColor;
	}
	
	public void setDrawColor(Color defaultPolygonColor) {
		this.defaultPolygonColor = defaultPolygonColor;
	}
	
	public void toogleLineIdentifier(){
		lineIdentifierOn = !lineIdentifierOn;
	}
	
	public void toogleLineEdgesLocation(){
		lineEdgesLocation = !lineEdgesLocation;
	}
	
	public void toogleCursorPosition(){
		cursorPosition = !cursorPosition;
	}
	
	public void toogleContinousSnap(){
		continuousSnap = !continuousSnap;
	}
	
	public void bringMPolygonToFront(MPolygon mpolygon){
		drawObjects.remove(mpolygon);
		drawObjects.add(0, mpolygon);
	}
	
	public void setDefaultMPolygonWidth(float defaultLineWidth){
		this.defaultLineWidth = defaultLineWidth;
	}
	
	private void drawAxis(Graphics2D g){
		g.setColor(Utils.negativeColor(bgColor));
		Stroke strokeOld = g.getStroke();
		
		g.setStroke(new BasicStroke(
			      6, 
			      BasicStroke.CAP_ROUND, 
			      BasicStroke.JOIN_ROUND, 
			      1f, 
			      new float[] {1f}, 
			      1f));
		
		g.drawLine(3, 3, this.getWidth()-10, 3);
		g.drawString("X", this.getWidth()-10, 20);
		g.drawLine(3, 3, 3, this.getHeight()-10);
		g.drawString("Y", 10, this.getHeight());
		
		g.drawString("O", 10, 20);
		g.setColor(Utils.halfNegativeColor(bgColor));
		g.setStroke(strokeOld);
		g.drawLine(0, 0, 10, 20);
		
	}
	
	@Override
	public void paint(Graphics g) {	
		
		if(buffer!=null && offScreen!=null){
			super.paint(g);
			
			Graphics2D buffer2d = ((Graphics2D)buffer);
			
			
			buffer2d.clearRect(0,0,this.getWidth(),this.getHeight()); 
			
			buffer2d.setBackground(bgColor);
			buffer2d.setColor(bgColor);
			buffer2d.fillRect(0, 0, this.getWidth(), this.getHeight());
			drawAxis(buffer2d);
			for(int i=drawObjects.size()-1;i>=0;i--){
				drawObjects.get(i).drawMPolygon(buffer2d,selectedColor, lineIdentifierOn, lineEdgesLocation, continuousSnap);
			}
			
			indicateNearVertices(buffer2d);
			indicateSnapVertices(buffer2d);
			
			
			if(cursorPosition){
				buffer2d.setColor(Utils.negativeColor(bgColor));
				buffer2d.drawString("("+x+","+y+")", x, y);
			}
			
			g.drawImage(offScreen,0,0,this);
			
			
		}
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	
	//Listeners
	
	@Override
	public void componentHidden(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentResized(ComponentEvent arg0) {
		getNewImageBuffer();
		repaint();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {}
	
	
	public void cleanAllLines(){
		drawObjects.clear();
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isPopupTrigger()){
            showPop(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	
	
	@Override
	public void mousePressed(MouseEvent e) {
		if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0){ //Left Click
			polygonMoved = getClosestLineEdgesNearPoint(e.getPoint());
			if(!isSnappingPoint(e.getPoint())){
				
				if(polygonMoved!=null){
					polygonMoved.editing(new Point(e.getX(), e.getY()));
				}
				
			}else{
				polygonMoved = null;
				highlightCloseVertices(e.getX(), e.getY());
			}
			repaint();
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()){
            MPolygon editMPolygon = getClosestLineEdgesNearPoint(new Point(e.getX(),e.getY()));
            
            if(editMPolygon!=null){
            	showPopMPolygon( e, editMPolygon);
            }else{
            	showPop(e);
            }
		}
		if((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0){ //Left Click
			snapVertices.removeAll(snapVertices);
			moveDragOn = true;
		}
		if(polygonMoved!=null){
			polygonMoved.editing(null);
			polygonMoved = null;
		}
		repaint();
	}
	
	public void deletePolygon(int hash){
		Iterator<MPolygon> ite = drawObjects.iterator();
		while(ite.hasNext()){
			MPolygon polygon = ite.next();
			if(polygon.hashCode()==hash){
				ite.remove();
			}
		}
	}
	
	public MPolygon getClosestLineEdgesNearPoint(Point point){
		
		Double minDistance = null;
		MPolygon returned = null;
		//Arestas
		for(MPolygon polygon:drawObjects){
			
			Iterator<Line2D> lines = polygon.getLines().iterator();
			while(lines.hasNext()){
				
				Line2D line = lines.next();
				Point2D intersection = Utils.getIntersectionPoint(line, new Point(x,y), continuousSnap);
				if(intersection!=null){
					double distance = Utils.getDistanceBetweenPoints(x, y, intersection.getX(), intersection.getY());
					
					if((minDistance==null||minDistance>distance) && distance<(VERTICE_INDICATOR_CIRCLE_RADIUS*2)){
							returned = polygon;
							minDistance = distance;
					}
				}
			}
			
		}	
		return returned;
	}
	
	
	
	public boolean isSnappingPoint(Point point){
		boolean returned = (highlightCloseVertices(point.x, point.y)[0]!=null);
		snapVertices.removeAll(snapVertices);
		return returned;
	}
	
	
	
	
	@Override
	public void mouseDragged(MouseEvent arg) {
		x = arg.getX();
		y = arg.getY();
		
		if((arg.getModifiers() & MouseEvent.BUTTON1_MASK) != 0){ //Left Click
			
			if(polygonMoved!=null){
				polygonMoved.moveTo(arg.getX(), arg.getY());
			}else{
				Object[] moved = highlightCloseVertices(arg.getX(), arg.getY());
				
				MPolygon polygonMoved = (MPolygon)	moved[0];
				Integer verticeIndex = (Integer)	moved[1];
				
				MPolygon polygonAffected = null;
				
				boolean snapped = false;
				
				Point moveToVertice = null;
				Double minDistance = null; 
				
				if(isCapsOn && moved[0]!=null && verticeIndex<polygonMoved.getNumberOfPoints()){
					//Vértices
					for(VerticeIdentificator sv: snapVertices){
							if( !(sv.getDrawObject().equals(polygonMoved) && sv.getVertex().equals(polygonMoved.getVertex(verticeIndex))) ){ 
								double distance = Utils.getDistanceBetweenPoints(sv.getX(), sv.getY(), polygonMoved.getX(verticeIndex), polygonMoved.getY(verticeIndex));
								if((minDistance==null||minDistance>distance) && distance<(VERTICE_INDICATOR_CIRCLE_RADIUS*2)){
									if(polygonMoved.getX(verticeIndex)!=sv.getX() && polygonMoved.getY(verticeIndex)!=sv.getY()){
										
										moveToVertice = new Point(sv.getX(),sv.getY());
										polygonAffected = sv.getDrawObject();
										if(moveDragOn){
											moveDragOn = false;
										}
										snapped = true;
										minDistance = distance;
									}
								}
							}
					}
					if(snapped==false){
						//Arestas
						for(MPolygon polygon:drawObjects){
							
							if(polygon!=polygonMoved){
								Iterator<Line2D> lines = polygon.getLines().iterator();
								while(lines.hasNext()){
									
									Line2D line = lines.next();
									Point2D intersection = Utils.getIntersectionPoint(line, new Point(x,y),continuousSnap);
									if(intersection!=null){
										double distance = Utils.getDistanceBetweenPoints(x, y, intersection.getX(), intersection.getY());
										
										if((minDistance==null||minDistance>distance) && distance<(VERTICE_INDICATOR_CIRCLE_RADIUS)){
												moveToVertice = new Point((int)Math.round(intersection.getX()), (int)Math.round(intersection.getY()));
												if(moveDragOn){
													moveDragOn = false;
												}
												polygonAffected = polygon;
												snapped = true;
												minDistance=distance;
										}
									}
								}
							}
							
						}
					}
					if(!snapped){
						moveDragOn = true;
					}else{
						if(moveToVertice!=null){
							polygonMoved.setX(verticeIndex, (int)Math.round(moveToVertice.getX()));
							polygonMoved.setY(verticeIndex, (int)Math.round(moveToVertice.getY()));
							Toolkit.getDefaultToolkit().beep();
							
							if(controlPressed  && !newLineCreated){
								Line2D newMaximalLine = Utils.getMaximalLine(polygonMoved.getLines().get(0), polygonAffected.getLines().get(0));
								
								if(newMaximalLine!=null){
								final MPolygon newMaximalPolygon = new MPolygon(new int[][]{{(int)newMaximalLine.getX1(),(int)newMaximalLine.getY1()},{(int)newMaximalLine.getX2(),(int)newMaximalLine.getY2()}}, defaultPolygonColor, defaultLineWidth);
								
								class Indicator implements Runnable{
									
									public Color indicationColor;
									
									public Indicator(Color color) {
										indicationColor = color;
									}
									
									@Override
									public void run() {
										int blinkntimes = 4;
										final long intervalTime = 500;
										
										Color color = newMaximalPolygon.getColor();
										
										while(blinkntimes--!=0){
											newMaximalPolygon.setColor(indicationColor);
											repaint();
											try {
												Thread.sleep(intervalTime);
											} catch (InterruptedException e) {}
											newMaximalPolygon.setColor(color);
											repaint();
											try {
												Thread.sleep(intervalTime);
											} catch (InterruptedException e) {}
										}
									}
								}
								
								
								new Thread(new Indicator(selectedColor)).start();
								drawObjects.add(0, newMaximalPolygon);
								newLineCreated = true;
								
								}
							}
						}
					}
				}
				
			}
			repaint();
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent arg) {
		x = arg.getX();
		y = arg.getY();
		
		ArrayList<VerticeIdentificator> nearVerticesBuffer = nearVertices(x, y, 1, VERTICE_INDICATOR_CIRCLE_RADIUS, false, false);
		
		if(!this.nearVertices.containsAll(nearVerticesBuffer) || nearVerticesBuffer.size()==0){
			nearVertices = nearVerticesBuffer;
			repaint();
		}
		
	}
	
}
