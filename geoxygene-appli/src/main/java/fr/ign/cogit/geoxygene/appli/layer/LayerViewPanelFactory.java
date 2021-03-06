/**
 * 
 */
package fr.ign.cogit.geoxygene.appli.layer;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;

/** @author JeT This factory creates LayerViewPanel class instances */
public final class LayerViewPanelFactory {

    private static Logger logger = Logger.getLogger(LayerViewPanelFactory.class
            .getName());

    /** Layer view panel types */
    public enum RenderingType {
        AWT, LWJGL
    }

    private static RenderingType layerViewPanelType = RenderingType.AWT;

    /** private constructor */
    private LayerViewPanelFactory() {
        // private constructor for factories
    }

    /** @return the layerViewPanelType */
    public static RenderingType getLayerViewPanelType() {
        return layerViewPanelType;
    }

    /**
     * Change the factory created layer instance types
     * 
     * @param layerViewPanelType
     *            the layerViewPanelType to set
     */
    public static void setRenderingType(final RenderingType layerViewPanelType) {
        LayerViewPanelFactory.layerViewPanelType = layerViewPanelType;
    }

    /**
     * Create a new layer
     * 
     * @param projectFrame
     *            parent frame containing the newly created layer
     * @return newly created layer view
     */
    public static LayerViewPanel newLayerViewPanel() {
        switch (getLayerViewPanelType()) {
        case AWT:
            return newLayerViewAwtPanel();
        case LWJGL:
            return newLayerViewGLPanel();
        }
        logger.error("Unhandled layer type " + getLayerViewPanelType());
        return null;
    }

    /**
     * Create a new AWT layer
     * 
     * @param projectFrame
     *            parent frame containing the newly created layer
     * @return newly created layer view
     */

    public static LayerViewAwtPanel newLayerViewAwtPanel() {
        return new LayerViewAwtPanel();
    }

    /**
     * Create a new LwJGL layer
     * 
     * @param projectFrame
     *            parent frame containing the newly created layer
     * @return newly created layer view
     */
    public static LayerViewGLPanel newLayerViewGLPanel() {
        return new LayerViewGLPanel();
    }

    public static LayerViewGLCanvas newLayerViewGLCanvas(
            final LayerViewGLPanel glPanel) {
        return newLayerViewGL4Canvas(glPanel);
    }

    private static LayerViewGL4Canvas newLayerViewGL4Canvas(
            final LayerViewGLPanel glPanel) {
        try {
            LayerViewGL4Canvas gl4Canvas = new LayerViewGL4Canvas(glPanel);
            return gl4Canvas;
        } catch (LWJGLException e) {
            logger.error("LWJGL creation error");
            logger.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
