package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.Set;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.event.CoordPaintListener;
import fr.ign.cogit.geoxygene.appli.gui.FileChooser;
import fr.ign.cogit.geoxygene.appli.mode.ModeSelector;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.style.Layer;

/**
 * File, view, configuration and help menu in GeOxygene main frame.
 * 
 */
public class GeOxgeneMenuBar extends JMenuBar {
    
    private MainFrame mainFrame;
    
    public static FileChooser fc = new FileChooser();
    
    /** Logger of the application. */
    private static Logger LOGGER = Logger.getLogger(GeOxgeneMenuBar.class.getName());
    
    /** serial version uid. */
    private static final long serialVersionUID = -6860364246334166387L;
    
    /**
     * Constructor.
     * @param frame
     */
    public GeOxgeneMenuBar(MainFrame frame) {
        
        super();
        
        mainFrame = frame;
        this.setFont(mainFrame.getApplication().getFont());
        
        // Init all menus
        initFileMenu();
        initViewMenu();
        initConfigurationMenu();
        initHelpMenu();
    }
    
    /**
     * 
     */
    private void initViewMenu() {
        JMenu viewMenu = new JMenu(I18N.getString("MainFrame.View")); //$NON-NLS-1$
        
        JMenuItem mScale6250 = new JMenuItem("1:6250"); //$NON-NLS-1$
        mScale6250.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            LayerViewPanel layerViewPanel = mainFrame.getSelectedProjectFrame().getLayerViewPanel();
            layerViewPanel.getViewport().setScale(
                1 / (6250 * LayerViewPanel.getMETERS_PER_PIXEL()));
            layerViewPanel.repaint();
          }
        });
        JMenuItem mScale12500 = new JMenuItem("1:12500"); //$NON-NLS-1$
        mScale12500.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            LayerViewPanel layerViewPanel = mainFrame.getSelectedProjectFrame().getLayerViewPanel();
            layerViewPanel.getViewport().setScale(
                1 / (12500 * LayerViewPanel.getMETERS_PER_PIXEL()));
            layerViewPanel.repaint();
          }
        });
        JMenuItem mScale25k = new JMenuItem("1:25k"); //$NON-NLS-1$
        mScale25k.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            LayerViewPanel layerViewPanel = mainFrame.getSelectedProjectFrame().getLayerViewPanel();
            layerViewPanel.getViewport().setScale(
                1 / (25000 * LayerViewPanel.getMETERS_PER_PIXEL()));
            layerViewPanel.repaint();
          }
        });
        JMenuItem mScale50k = new JMenuItem("1:50k"); //$NON-NLS-1$
        mScale50k.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            LayerViewPanel layerViewPanel = mainFrame.getSelectedProjectFrame().getLayerViewPanel();
            layerViewPanel.getViewport().setScale(
                1 / (50000 * LayerViewPanel.getMETERS_PER_PIXEL()));
            layerViewPanel.repaint();
          }
        });
        JMenuItem mScale100k = new JMenuItem("1:100k"); //$NON-NLS-1$
        mScale100k.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            LayerViewPanel layerViewPanel = mainFrame.getSelectedProjectFrame().getLayerViewPanel();
            layerViewPanel.getViewport().setScale(
                1 / (100000 * LayerViewPanel.getMETERS_PER_PIXEL()));
            layerViewPanel.repaint();
          }
        });
        JMenuItem mScaleCustom = new JMenuItem(
            I18N.getString("MainFrame.CustomScale")); //$NON-NLS-1$
        mScaleCustom.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            int scale = Integer.parseInt(JOptionPane.showInputDialog(I18N
                .getString("MainFrame.NewScale"))); //$NON-NLS-1$
            LayerViewPanel layerViewPanel = mainFrame.getSelectedProjectFrame().getLayerViewPanel();
            layerViewPanel.getViewport().setScale(
                1 / (scale * LayerViewPanel.getMETERS_PER_PIXEL()));
            layerViewPanel.repaint();
          }
        });
        viewMenu.add(mScale6250);
        viewMenu.add(mScale12500);
        viewMenu.add(mScale25k);
        viewMenu.add(mScale50k);
        viewMenu.add(mScale100k);
        viewMenu.add(mScaleCustom);
        viewMenu.addSeparator();
        
        JMenuItem mGoTo = new JMenuItem(I18N.getString("MainFrame.GoTo")); //$NON-NLS-1$
        mGoTo.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            LayerViewPanel layerViewPanel = mainFrame.getSelectedProjectFrame().getLayerViewPanel();

            String lat = JOptionPane.showInputDialog("Latitude"); //$NON-NLS-1$
            if (lat == null) {
              return;
            }
            double latitude = Double.parseDouble(lat);
            String lon = JOptionPane.showInputDialog("Longitude"); //$NON-NLS-1$
            if (lon == null) {
              return;
            }
            double longitude = Double.parseDouble(lon);
            try {
              layerViewPanel.getViewport().center(
                  new DirectPosition(latitude, longitude));
            } catch (NoninvertibleTransformException e1) {
              e1.printStackTrace();
            }
            layerViewPanel.repaint();
          }
        });
        viewMenu.add(mGoTo);

        JMenuItem mCoord = new JCheckBoxMenuItem(
            I18N.getString("MainFrame.Coordinate")); //$NON-NLS-1$
        mCoord.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LayerViewPanel layerViewPanel = mainFrame.getSelectedProjectFrame().getLayerViewPanel();
            if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {
              layerViewPanel.addMouseMotionListener(new CoordPaintListener());
            } else {
              for (MouseMotionListener m : layerViewPanel.getMouseMotionListeners()) {
                if (m.getClass().equals(CoordPaintListener.class)) {
                  layerViewPanel.removeMouseMotionListener(m);
                  layerViewPanel.repaint();
                }
              }
            }
          }
        });
        viewMenu.add(mCoord);
        
        add(viewMenu);
    }
    
    private void initHelpMenu() {
        JMenu helpMenu = new JMenu(I18N.getString("MainFrame.Help")); //$NON-NLS-1$
        
        // ...
        
        add(helpMenu);
    }
     
    
    private void initConfigurationMenu() {
        
        JMenu configurationMenu = new JMenu(
                I18N.getString("MainFrame.Configuration")); //$NON-NLS-1$
        
        JMenuItem organizeMenuItem = new JMenuItem("Organize"); //$NON-NLS-1$
        organizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            ProjectFrame[] projectFrames = mainFrame.getAllProjectFrames();
            
            mainFrame.getDesktopPane().removeAll();
            GridLayout layout = new GridLayout(0, 2);
            mainFrame.getDesktopPane().setLayout(layout);
            for (ProjectFrame frame : projectFrames) {
              mainFrame.getDesktopPane().add(frame);
            }
            mainFrame.getDesktopPane().doLayout();
          }
        });
        configurationMenu.add(organizeMenuItem);
        
        add(configurationMenu);
        
    }
    
    /**
     * File menu : .
     */
    private void initFileMenu() {
        
        JMenu fileMenu = new JMenu(I18N.getString("MainFrame.File")); //$NON-NLS-1$
        
        // Open File
        JMenuItem openFileMenuItem = new JMenuItem(
                I18N.getString("MainFrame.OpenFile")); //$NON-NLS-1$
        
        openFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
              @Override
              public void actionPerformed(final ActionEvent e) {
                ProjectFrame projectFrame = (ProjectFrame) mainFrame.getDesktopPane().getSelectedFrame();
                if (projectFrame == null) {
                  if (mainFrame.getDesktopPane().getAllFrames().length != 0) {
                    // TODO ask the user in which frame (s)he
                    // wants to load into?
                    projectFrame = (ProjectFrame) mainFrame.getDesktopPane()
                        .getAllFrames()[0];
                  } else {
                    // TODO create a new project frame?
                    MainFrame.getLogger().info(
                        I18N.getString("MainFrame.NoFrameToLoadInto")); //$NON-NLS-1$
                    return;
                  }
                }
                File file = fc.getFile(mainFrame);
                if (file != null) {
                  projectFrame.addLayer(file);
                }
              }
            });
        fileMenu.add(openFileMenuItem);
        
        // New Project
        JMenuItem newProjectFrameMenuItem = new JMenuItem(
                I18N.getString("MainFrame.NewProject")); //$NON-NLS-1$
        newProjectFrameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                    mainFrame.newProjectFrame();
            }
        });
        fileMenu.add(newProjectFrameMenuItem);
        
        // separator
        fileMenu.addSeparator();
        
        // Save as Shp
        JMenuItem saveAsShpMenuItem = new JMenuItem(
                I18N.getString("MainFrame.SaveAsShp")); //$NON-NLS-1$
        saveAsShpMenuItem.addActionListener(new java.awt.event.ActionListener() {
        
            @Override
            public void actionPerformed(final ActionEvent e) {
                ProjectFrame project = mainFrame.getSelectedProjectFrame();
                Set<Layer> selectedLayers = project.getLayerLegendPanel()
                    .getSelectedLayers();
                if (selectedLayers.size() != 1) {
                    LOGGER.error("You must select one (and only one) layer."); //$NON-NLS-1$
                  return;
                }
                Layer layer = selectedLayers.iterator().next();

                IFeatureCollection<? extends IFeature> layerfeatures = layer
                    .getFeatureCollection();
                if (layerfeatures == null) {
                    LOGGER.error("The layer selected does not contain any feature."); //$NON-NLS-1$
                  return;
                }
                JFileChooser chooser = new JFileChooser(fc.getPreviousDirectory());
                int result = chooser.showSaveDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                  File file = chooser.getSelectedFile();
                  if (file != null) {
                    String fileName = file.getAbsolutePath();
                    project.saveAsShp(fileName, layer);
                  }
                }
            }
        });
        fileMenu.add(saveAsShpMenuItem);
        
        // 
        JMenuItem saveAsImageMenuItem = new JMenuItem(
                I18N.getString("MainFrame.SaveAsImage")); //$NON-NLS-1$
            saveAsImageMenuItem.addActionListener(new java.awt.event.ActionListener() {
              @Override
              public void actionPerformed(final ActionEvent e) {
                ProjectFrame projectFrame = (ProjectFrame) mainFrame.getDesktopPane().getSelectedFrame();
                if (projectFrame == null) {
                  if (mainFrame.getDesktopPane().getAllFrames().length != 0) {
                    projectFrame = (ProjectFrame) mainFrame.getDesktopPane()
                        .getAllFrames()[0];
                  } else {
                    return;
                  }
                }
                JFileChooser chooser = new JFileChooser(fc.getPreviousDirectory());
                int result = chooser.showSaveDialog(mainFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                  File file = chooser.getSelectedFile();
                  if (file != null) {
                    String fileName = file.getAbsolutePath();
                    projectFrame.saveAsImage(fileName);
                  }
                }
              }
            });
        fileMenu.add(saveAsImageMenuItem);
        
        JMenuItem printMenu = new JMenuItem(I18N.getString("MainFrame.Print")); //$NON-NLS-1$
        printMenu.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            Thread th = new Thread(new Runnable() {
              @Override
              public void run() {
                try {
                  PrinterJob printJob = PrinterJob.getPrinterJob();
                  printJob.setPrintable(mainFrame.getSelectedProjectFrame()
                      .getLayerViewPanel());
                  PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                  if (printJob.printDialog(aset)) {
                    printJob.print(aset);
                  }
                } catch (java.security.AccessControlException ace) {
                  JOptionPane.showMessageDialog(mainFrame.getSelectedProjectFrame().getLayerViewPanel(),
                      I18N.getString("MainFrame.ImpossibleToPrint") //$NON-NLS-1$
                          + ";" //$NON-NLS-1$
                          + I18N.getString("MainFrame.AccessControlProblem") //$NON-NLS-1$
                          + ace.getMessage(), I18N
                          .getString("MainFrame.ImpossibleToPrint"), //$NON-NLS-1$
                      JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
            });
            th.start();
          }
        });
        fileMenu.add(printMenu);
        
        // Separator
        fileMenu.addSeparator();
        
        // Exit
        JMenuItem exitMenuItem = new JMenuItem(I18N.getString("MainFrame.Exit")); //$NON-NLS-1$
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
          @Override
          public void actionPerformed(final ActionEvent e) {
            mainFrame.dispose();
            mainFrame.getApplication().exit();
          }
        });
        fileMenu.add(exitMenuItem);
        
        add(fileMenu);
    }

}
