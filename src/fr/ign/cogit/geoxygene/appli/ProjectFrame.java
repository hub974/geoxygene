/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import fr.ign.cogit.geoxygene.appli.plugin.GeometryToolBar;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.feature.event.FeatureCollectionListener;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * Project Frame.
 * @author Julien Perret
 */
public class ProjectFrame extends JInternalFrame implements
        FeatureCollectionListener {
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The layer view panel.
     */
    private LayerViewPanel layerViewPanel = null;

    /**
     * @return The {@link LayerViewPanel}
     */
    public final LayerViewPanel getLayerViewPanel() {
        return this.layerViewPanel;
    }

    /**
     * The layer legend panel.
     */
    private JPanel layerLegendPanel = null;
    /**
     * The split pane.
     */
    private JSplitPane splitPane = new JSplitPane();
    /**
     * @return The split pane
     */
    public JSplitPane getSplitPane() { return this.splitPane; }
    /**
     * The project styled layer descriptor.
     */
    private StyledLayerDescriptor sld = new StyledLayerDescriptor();

    /**
     * @return The project styled layer descriptor
     */
    public StyledLayerDescriptor getSld() {
        return this.sld;
    }

    /**
     * The default frame width.
     */
    private static final int DEFAULT_WIDTH = 600;
    /**
     * The default frame height.
     */
    private static final int DEFAULT_HEIGHT = 400;
    /**
     * The default frame divider location.
     */
    private static final int DEFAULT_DIVIDER_LOCATION = 200;

    private MainFrame mainFrame = null;
    public MainFrame getMainFrame() { return this.mainFrame; }

    /**
     * Constructor.
     * @param iconImage the project icon image
     */
    public ProjectFrame(final MainFrame frame, final ImageIcon iconImage) {
        this.mainFrame = frame;
        this.layerViewPanel = new LayerViewPanel(this);
        this.layerLegendPanel = new LayerLegendPanel(this.sld,
                this.layerViewPanel);
        this.setSize(ProjectFrame.DEFAULT_WIDTH, ProjectFrame.DEFAULT_HEIGHT);
        this.setResizable(true);
        this.setClosable(true);
        this.setMaximizable(true);
        this.setIconifiable(true);
        this.setFrameIcon(iconImage);
        this.getContentPane().setLayout(new BorderLayout());
        this.splitPane.setBorder(null);
        this.layerLegendPanel.setLayout(new BoxLayout(this.layerLegendPanel,
                BoxLayout.PAGE_AXIS));
        this.getContentPane().add(this.splitPane, BorderLayout.CENTER);
        this.splitPane.add(this.layerLegendPanel, JSplitPane.LEFT);
        this.splitPane.add(this.layerViewPanel, JSplitPane.RIGHT);
        this.splitPane
                .setDividerLocation(ProjectFrame.DEFAULT_DIVIDER_LOCATION);
        this.splitPane.setOneTouchExpandable(true);
    }

    /**
     * Add a feature collection to the project.
     * @param population the population to add
     * @param name the name of the population
     */
    public final void addFeatureCollection(
            final Population<DefaultFeature> population, final String name) {
        Layer layer = this.sld.createLayer(name, population.getFeatureType()
                .getGeometryType());
        this.addLayer(layer);
    }

    /**
     * Add a layer to the project.
     * @param layer the layer to add
     */
    public final void addLayer(final Layer layer) {
        if (layer == null) { return; }
        layer.getFeatureCollection().addFeatureCollectionListener(this);
        this.sld.add(layer);
        this.layerViewPanel.getRenderingManager().addLayer(layer);
        this.featureCollectionToLayerMap.put(layer.getFeatureCollection(),
                layer);
        this.layerViewPanel.repaint();
    }

    /**
     * The map from feature collection to layer.
     */
    private Map<FT_FeatureCollection<? extends FT_Feature>, Layer> featureCollectionToLayerMap = new HashMap<FT_FeatureCollection<? extends FT_Feature>, Layer>();

    /**
     * Dispose of the frame an its {@link LayerViewPanel}.
     */
    @Override
    public final void dispose() {
        this.layerViewPanel.dispose();
        super.dispose();
    }

    /**
     * @return The list of the project's layers
     */
    public final List<Layer> getLayers() {
        return this.sld.getLayers();
    }

    /**
     * @param name name of the layer
     * @return The layer with the given name
     */
    public final Layer getLayer(String name) {
        return this.sld.getLayer(name);
    }

    @Override
    public final void changed(final FeatureCollectionEvent event) {
        //System.out.println("changed received "+event.getType());
        Layer layer = this.featureCollectionToLayerMap.get(event.getSource());
        if (layer == null) { return; }
        // this.layerViewPanel.superRepaint();
        if (event.getType() == FeatureCollectionEvent.Type.ADDED) {
            this.layerViewPanel.repaint(layer, event.getFeature());
        } else {
            GM_Object repaintGeometry = event.getGeometry();
            if (event.getFeature() != null
                        && event.getFeature().getGeom() != null) {
                repaintGeometry = event.getFeature().getGeom().union(
                        event.getGeometry());
            }
            this.layerViewPanel.repaint(layer, repaintGeometry);
        }

        /*
         * GM_Envelope envelope =
         * event.getFeature().getGeom().envelope();
         * try {
         * Point2D upperCorner =
         * layerViewPanel.getViewport().toViewPoint
         * (envelope.getUpperCorner());
         * Point2D lowerCorner =
         * layerViewPanel.getViewport().toViewPoint
         * (envelope.getLowerCorner());
         * this.layerViewPanel.repaint(layer,
         * (int)lowerCorner.getX(),(int
         * )upperCorner.getY(),(int)(upperCorner
         * .getX()-lowerCorner.getX(
         * )),(int)(lowerCorner.getY()-upperCorner.getY()));
         * } catch (NoninvertibleTransformException e)
         * {e.printStackTrace();}
         */
    }

    /**
     * Clear the selection.
     */
    public void clearSelection() {
        this.getLayerViewPanel().getSelectedFeatures().clear();
        this.getLayerViewPanel().repaint();
    }

    /**
     * Créer une nouvelle couche avec le type de géométrie envoyé en paramètre
     * et l'ajoute au panneau de gauche.
     * Retourne le nom de la couche (null si on a cliqué sur annuler).
     * @param newLayerName the new layer name
     * @param geomType geometry type of the new layer
     */
    public Layer createLayer(String newLayerName, Class<? extends GM_Object> geomType) {
        /*
        String newLayerName = (String) JOptionPane.showInputDialog(this,
                "Saisissez le nom de la couche",
                "Créer une nouvelle couche",
                JOptionPane.INFORMATION_MESSAGE, null, null,
                "Nouvelle couche");
                */
        if (newLayerName == null) {
            // TODO
            //getPanelVisu().stopEditing();
            return null;
        }

        if (newLayerName.isEmpty()) {
            newLayerName = generateNewLayerName();
        } else {
            newLayerName = checkLayerName(newLayerName);
        }

        // Initialisation de la nouvelle population
        Population<FT_Feature> newPop = generateNewPopulation(newLayerName,
                geomType);

        // Initialisation du nouveau layer
        Layer layer = this.getSld().createLayerRandomColor(
                newLayerName, geomType);

        FeatureType featureType = new FeatureType();
        featureType.setGeometryType(geomType);
        DataSet.getInstance().addPopulation(newPop);
        layer.getFeatureCollection().setFeatureType(featureType);

        this.addLayer(layer);

        return layer;
    }
    /**
     * Cette méthode génère un nom de couche du type: "Nouvelle couche ('n') où n indique le nombre de couche portant déjà ce nom
     * @return Le nom de la nouvelle couche
     * TODO create I18N text
     */
    public String generateNewLayerName() {
        return checkLayerName("Nouvelle couche"); //$NON-NLS-1$
    }

    public String checkLayerName(String layerName) {
        if (this.getLayer(layerName) != null) {
            /** Il existe déjà une population avec ce nom */
            int n = 2;
            while (this.getLayer(layerName + " (" + n //$NON-NLS-1$
                    + ")") != null) { n++; } //$NON-NLS-1$
            layerName = layerName + " (" + n + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return layerName;
    }
    /**
     * Créer une nouvelle population en fonction du nom et du type de géométrie envoyés en paramètre.
     * Initialise l'index spatial, ajoute le PanelVisu comme ChangeListener et met à jour le FeatureType de la population
     * @param popName Le nom de la population à créer
     * @param geomType Le type de géométrie de la population
     * @return Une nouvelle population à partir du nom et du type de géométrie envoyés en paramètre.
     */
    public Population<FT_Feature> generateNewPopulation(String popName, Class<? extends GM_Object>geomType) {
        Population<FT_Feature> newPop = new Population<FT_Feature>(popName);
        newPop.addFeatureCollectionListener(this);
        FeatureType type = new FeatureType();
        type.setGeometryType(geomType);
        newPop.setFeatureType(type);

        return newPop;
    }

    /**
     * Créer une nouvelle population en fonction du layer et d'une collection de features envoyés en paramètre.
     * Initialise l'index spatial, ajoute le PanelVisu comme ChangeListener et met à jour le FeatureType de la population
     * @param layer Le layer à partir duquel on souhaite créer la population
     * @param features Les features que l'on souhaite ajouter à la population
     * @return Une nouvelle population à partir du layer et des features envoyés en paramètre.
     */
    public Population<FT_Feature> generateNewPopulation(FT_FeatureCollection<? extends FT_Feature> features, Layer layer) {
        Population<FT_Feature> newPop = new Population<FT_Feature>(layer.getName());
        if (features == null || features.isEmpty()) return null;
        newPop.addAll(features);
        newPop.addFeatureCollectionListener(this);
        if (features.getFeatureType() == null) {
            FeatureType type = new FeatureType();
            type.setGeometryType(features.get(0).getGeom().getClass());
            newPop.setFeatureType(type);
        }
        else {
            newPop.setFeatureType(features.getFeatureType());
        }
        return newPop;
    }

    public Layer getLayerFromFeature(FT_Feature ft) {
        for (Layer layer : this.getSld().getLayers()) {
            if (layer.getFeatureCollection().contains(ft)) {
                return layer;
            }
        }
        return null;
    }

    private GeometryToolBar geometryToolBar = null;
    public void setGeometryToolsVisible(boolean b) {
        if (b) {
            if (this.geometryToolBar == null) {
                this.geometryToolBar = new GeometryToolBar(this);
                this.getContentPane().add(this.geometryToolBar,
                        BorderLayout.EAST);
            }
            this.geometryToolBar.setVisible(true);
            this.validate();
        } else {
            this.geometryToolBar.setVisible(false);
            this.validate();
        }
    }
}
