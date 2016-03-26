/**
 * 
 */
package pt.iscte.linetool;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

import pt.iscte.linetool.graphics.Panel2D;




/**
 * @author Rafael Campos
 *
 */
public class Launcher {
	
	public final static String 	NAME    = "Editor de Linhas Maximais";
	public final static String 	VERSION = "v1.0";
	public final static String  PROGRAM = NAME+" "+VERSION;
//	public final static boolean DEBUG = true;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		final JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		
		
		
		
		
//		JMenuBar bar = new JMenuBar();
//		JMenu file = new JMenu("File");
//		JMenuItem saveProject = new JMenuItem("Save project");
//		file.add(saveProject);
//		JMenuItem openProject = new JMenuItem("Open project");
//		openProject.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				final JFileChooser chooseProject = new JFileChooser();
//				chooseProject.showOpenDialog(frame);
//			}
//		});
//		file.add(openProject);
//		
//		file.add(new JSeparator());
//		
//		JMenu export = new JMenu("Export");
//		JMenuItem saveGif = new JMenuItem("As an image");
//		export.add(saveGif);
//		JMenuItem saveMovie = new JMenuItem("As a mp4");
//		export.add(saveMovie);
//		file.add(export);
//		
//		file.add(new JSeparator());
//		JMenuItem exit = new JMenuItem("Exit");
//		file.add(exit);
//		bar.add(file);
//		frame.setJMenuBar(bar);
		
		
		
//		JToolBar keyFrames = new JToolBar();
//		keyFrames.setFloatable(false);
//		keyFrames.setRollover(false);
//		keyFrames.add(new JButton("Animate"));
//		frame.add(keyFrames, BorderLayout.PAGE_START);
		
//		JTabbedPane tabbedPane = new JTabbedPane();
//		tabbedPane.addTab("Keyframe 1", null, new Panel2D());
//		tabbedPane.addTab("Keyframe 2", null, new Panel2D());
//		tabbedPane.addTab("Keyframe 3", null, new Panel2D());
//		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
//		frame.add(tabbedPane, BorderLayout.CENTER);
		
		try {
			final Panel2D panel2d = new Panel2D(Color.GRAY.darker(), Color.ORANGE);
			
			frame.addWindowFocusListener(new WindowFocusListener(){
				@Override
				public void windowGainedFocus(WindowEvent arg0) {
					panel2d.updateCaps();
				}
				@Override
				public void windowLostFocus(WindowEvent arg0) {}
			});
			
			frame.addKeyListener(new KeyListener(){
				private boolean pressedCaps= false;
				private boolean pressedCtrl = false;
				@Override
				public void keyPressed(KeyEvent arg) {
					
						if(!pressedCaps && arg.getKeyCode()==KeyEvent.VK_CAPS_LOCK){
							pressedCaps = true;
							panel2d.toogleCaps();
						}
					
						if(!pressedCtrl && arg.getKeyCode()==KeyEvent.VK_CONTROL){
							pressedCtrl = true;
							panel2d.toogleControl();
						}
					
				}

				@Override
				public void keyReleased(KeyEvent arg) {
					
					if(pressedCaps && arg.getKeyCode()==KeyEvent.VK_CAPS_LOCK){
						pressedCaps = false;
					}
					if(pressedCtrl && arg.getKeyCode()==KeyEvent.VK_CONTROL){
						pressedCtrl = false;
						panel2d.toogleControl();
					}
				}

				@Override
				public void keyTyped(KeyEvent arg) {}
				
			});
			frame.add(panel2d);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		frame.setTitle(Launcher.PROGRAM);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
	}

}
