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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pt.iscte.linetool.graphics.MPolygon;
import pt.iscte.linetool.graphics.Panel2D;



/**
 * @author Rafael Campos
 *
 */
@SuppressWarnings("serial")
public class ContextMenuMPolygon  extends JPopupMenu{
	
	private JMenuItem deleteMPolygon;
	private JMenuItem changeColorMPolygon;
	private JMenuItem bringPolygonToFront;
	private JMenuItem drawWidth;
	
	private JMenuItem scale;
	private JMenuItem rotation;
	private JMenuItem translation;
	
	public ContextMenuMPolygon(final Panel2D panel, final MPolygon mpolygon) {
		
		deleteMPolygon 		= new JMenuItem("Delete Line");
		bringPolygonToFront	= new JMenuItem("Bring Line to Front");
		changeColorMPolygon = new JMenuItem("Color");
		drawWidth			= new JMenuItem("Width");
		
		JMenu transformations = new JMenu("Transformations");
		translation			= new JMenuItem("Translation");
		scale				= new JMenuItem("Scale");
		rotation			= new JMenuItem("Rotation");
		
		translation.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String desireScalingX = JOptionPane.showInputDialog(null, "Type the desired translation in X axis in px");
				try{
					int desireScalingXParsed = Integer.parseInt(desireScalingX);
					String desireScalingY = JOptionPane.showInputDialog(null, "Type the desired translation in Y axis in px");
					int desireScalingYParsed = Integer.parseInt(desireScalingY);
					mpolygon.translation(desireScalingXParsed, desireScalingYParsed);
					panel.repaint();
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(null,  "The request input must be a integer", "Wrong type of input", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		scale.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String desireScalingX = JOptionPane.showInputDialog(null, "Type the desired scaling in X axis");
				try{
					Double desireScalingXParsed = Double.parseDouble(desireScalingX);
					String desireScalingY = JOptionPane.showInputDialog(null, "Type the desired scaling in Y axis");
					Double desireScalingYParsed = Double.parseDouble(desireScalingY);
					mpolygon.scaling(desireScalingXParsed, desireScalingYParsed);
					panel.repaint();
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(null,  "The request input must be a float greater than 0", "Wrong type of input", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		rotation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String desireRotation = JOptionPane.showInputDialog(null, "Type the desired rotation in angles");
				try{
					Double rotation = Double.parseDouble(desireRotation);
					mpolygon.rotation(rotation);
					panel.repaint();
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(null,  "The request input must be a double", "Wrong type of input", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		transformations.add(scale);
		transformations.add(rotation);
		transformations.add(translation);
		
		JMenuItem editingLine = new JMenuItem("Editing line "+mpolygon.hashCode());
		editingLine.setEnabled(false);
		add(editingLine);
		add(new JSeparator());
		
		deleteMPolygon.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(JOptionPane.showConfirmDialog(panel, "You are about to DELETE LINE "+mpolygon.hashCode()+", do you wish to proceed?", "Delete line "+mpolygon.hashCode(),JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION){
					panel.deletePolygon(mpolygon.hashCode());
				}
			}
		});
		bringPolygonToFront.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg) {
				panel.bringMPolygonToFront(mpolygon);
				panel.repaint();
			}
		});
		add(bringPolygonToFront);
		add(deleteMPolygon);
		
		drawWidth.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String lineWidth = JOptionPane.showInputDialog(null, "Type the desired width (the actual with is "+mpolygon.getWidth()+" px)");
				try{
				Float lineWidthParsed =Float.parseFloat(lineWidth);
				if(lineWidthParsed<=0) throw new NumberFormatException();
				mpolygon.setWidth(lineWidthParsed);
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(null,  "The request input must be a float greater than 0", "Wrong type of input", JOptionPane.ERROR_MESSAGE);
				}
				panel.repaint();
			}
		});
		add(new JSeparator());
		add(drawWidth);
		changeColorMPolygon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame colorChoose = new JFrame();
				final JColorChooser bgColorChooser = new JColorChooser(panel.getBgColor());
				bgColorChooser.getSelectionModel().addChangeListener(new ChangeListener(){
					@Override
					public void stateChanged(ChangeEvent e) {
						mpolygon.setColor(bgColorChooser.getColor());
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
		add(changeColorMPolygon);
		
		add(transformations);
		
		
	}
}
