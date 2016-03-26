/**
 * 
 */
package pt.iscte.linetool.interaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pt.iscte.linetool.graphics.Panel2D;




/**
 * @author Rafael Campos
 *
 */
@SuppressWarnings("serial")
public class ContextMenu extends JPopupMenu {
    
	private JMenuItem setBgColor;
	private JMenuItem setDrawColor;
	private JMenuItem setDrawWidth;
	private JMenuItem selectedColor; 
	
	private JMenuItem setToogleLineId;
	private JMenuItem setToogleCursorPosition;
	private JMenuItem setTooglePositionLineEdge;
	private JMenuItem setSnapDiameter;
	private JMenuItem toogleContinuousSnap;
	private JMenuItem clear;
	
    public ContextMenu(final Panel2D panel){
    	
    	setBgColor 		= new JMenuItem("Background Color");
    	setDrawColor 	= new JMenuItem("Draw Color");
    	setDrawWidth	= new JMenuItem("Draw Width");
    	selectedColor	= new JMenuItem("Selected Color");
    	clear			= new JMenuItem("Delete All Lines");
    	
    	JMenu line = new JMenu("Line");
    	JMenu snap = new JMenu("Snap");
    	
    	setToogleLineId			= new JMenuItem("Toogle Line ID");
    	setToogleCursorPosition = new JMenuItem("Toogle Cursor Position");
    	setTooglePositionLineEdge	= new JMenuItem("Toogle Position Line Edge");
    	toogleContinuousSnap	= new JMenuItem("Toogle Continuous Snap");
    	setSnapDiameter 		= new JMenuItem("Set Snap Radius");
    	
    	
    	snap.add(toogleContinuousSnap);
    	snap.add(setSnapDiameter);
    	
    	line.add(setDrawColor);
    	line.add(setDrawWidth);
    	
    	line.add(new JSeparator());
    	line.add(selectedColor);
    	line.add(setToogleLineId);
    	line.add(setTooglePositionLineEdge);
    	line.add(clear);
    	
    	setToogleCursorPosition.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panel.toogleCursorPosition();
				panel.repaint();
			}
    	});
    	add(setToogleCursorPosition);
    	
        add(setBgColor);
        setBgColor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame colorChoose = new JFrame();
				final JColorChooser bgColorChooser = new JColorChooser(panel.getBgColor());
				bgColorChooser.getSelectionModel().addChangeListener(new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e) {
						panel.setBgColor(bgColorChooser.getColor());
						panel.repaint();
					}
				});
				colorChoose.add(bgColorChooser);
				colorChoose.setVisible(true);
				
				colorChoose.setTitle("Choose a backgorund color");
				colorChoose.pack();
				colorChoose.setResizable(false);
				colorChoose.setLocationRelativeTo(null);
				colorChoose.setAlwaysOnTop(true);
			}
        });
        setToogleLineId.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panel.toogleLineIdentifier();
				panel.repaint();
			}
        });
        selectedColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame colorChoose = new JFrame();
				final JColorChooser bgColorChooser = new JColorChooser(panel.getBgColor());
				bgColorChooser.getSelectionModel().addChangeListener(new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e) {
						panel.setSelectedLineColor(bgColorChooser.getColor());
						panel.repaint();
					}
				});
				colorChoose.add(bgColorChooser);
				colorChoose.setVisible(true);
				
				colorChoose.setTitle("Choose selected line color");
				colorChoose.pack();
				colorChoose.setResizable(false);
				colorChoose.setLocationRelativeTo(null);
				colorChoose.setAlwaysOnTop(true);
			}
		});
        
        
        clear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(JOptionPane.showConfirmDialog(panel, "Are you sure you want to DELETE ALL LINES?", "Delete all lines", JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION){
					panel.cleanAllLines();
					panel.repaint();
				}
			}
        });
        
        setDrawColor.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame colorChoose = new JFrame();
				final JColorChooser bgColorChooser = new JColorChooser(panel.getBgColor());
				bgColorChooser.getSelectionModel().addChangeListener(new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e) {
						panel.setDrawColor(bgColorChooser.getColor());
						panel.repaint();
					}
				});
				colorChoose.add(bgColorChooser);
				colorChoose.setVisible(true);
				
				colorChoose.setTitle("Choose new line color");
				colorChoose.pack();
				colorChoose.setResizable(false);
				colorChoose.setLocationRelativeTo(null);
				colorChoose.setAlwaysOnTop(true);
			}
        });
        setDrawWidth.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg) {
				String lineWidth = JOptionPane.showInputDialog(null, "Type the desired width in pixels");
				try{
				Float lineWidthParsed =Float.parseFloat(lineWidth);
				if(lineWidthParsed<=0) throw new NumberFormatException();
				panel.setDefaultMPolygonWidth(lineWidthParsed);
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(null,  "The request input must be a positive float greater than 0", "Wrong type of input", JOptionPane.ERROR_MESSAGE);
				}
			}
        });
    	setTooglePositionLineEdge.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panel.toogleLineEdgesLocation();
				panel.repaint();
			}
    	});
    	toogleContinuousSnap.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				panel.toogleContinousSnap();
				panel.repaint();
			}
    	});
    	add(new JSeparator());
    	add(line);
    	setSnapDiameter.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFrame radiusChoose = new JFrame();
				final JSlider diameterIndication = new JSlider(JSlider.HORIZONTAL, 0, 250,  panel.VERTICE_INDICATOR_CIRCLE_RADIUS);
				diameterIndication.setMajorTickSpacing(50);
				diameterIndication.setPaintTicks(true);
				diameterIndication.setPaintLabels(true);
				diameterIndication.addChangeListener(new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e) {
						panel.VERTICE_INDICATOR_CIRCLE_RADIUS = diameterIndication.getValue();
						radiusChoose.setTitle("Choose a snap radius int pixels (corrent radius is "+panel.VERTICE_INDICATOR_CIRCLE_RADIUS+" px)");
					}
				});
				radiusChoose.add(diameterIndication);
				radiusChoose.setVisible(true);
				
				radiusChoose.setTitle("Choose a snap radius int pixels (corrent radius is "+panel.VERTICE_INDICATOR_CIRCLE_RADIUS+" px)");
				radiusChoose.pack();
				radiusChoose.setSize(600, radiusChoose.getHeight());
				radiusChoose.setLocationRelativeTo(null);
				radiusChoose.setAlwaysOnTop(true);
			}    		
    	});
    	add(snap);
    	
    }
    
}
