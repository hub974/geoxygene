package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.citygml.building.BuildingFurnitureImpl;
import org.citygml4j.jaxb.gml._3_1_1.GeometryPropertyType;
import org.citygml4j.model.citygml.building.BuildingFurniture;
import org.citygml4j.model.citygml.core.CityObject;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertToCityGMLGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_BuildingFurniture extends CG_CityObject {

  public CG_BuildingFurniture(BuildingFurniture bF) {
    super(bF);
    this.setClazz(bF.getClazz());
    this.getFunction().addAll(bF.getFunction());
    this.getUsage().addAll(bF.getUsage());
    this.setLod4Geometry(ConvertyCityGMLGeometry.convertGMLGeometry(bF
        .getLod4Geometry()));

  }
  
  @Override
  public CityObject export() {
    
    BuildingFurniture bF = new BuildingFurnitureImpl();
    bF.setClazz(this.getClazz());
    bF.setFunction(this.getFunction());
    bF.setUsage(this.getUsage());
    if(bF.isSetLod4Geometry()){
      bF.setLod4Geometry(ConvertToCityGMLGeometry.convertGeometryProperty(this.getLod4Geometry()));
            
    }
    
    
    return bF;
  }
  
  

  protected String clazz;
  protected List<String> function;
  protected List<String> usage;
  protected IGeometry lod4Geometry;

  /**
   * Gets the value of the clazz property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getClazz() {
    return this.clazz;
  }

  /**
   * Sets the value of the clazz property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setClazz(String value) {
    this.clazz = value;
  }

  public boolean isSetClazz() {
    return (this.clazz != null);
  }

  /**
   * Gets the value of the function property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the function property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getFunction().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link String }
   * 
   * 
   */
  public List<String> getFunction() {
    if (this.function == null) {
      this.function = new ArrayList<String>();
    }
    return this.function;
  }

  public boolean isSetFunction() {
    return ((this.function != null) && (!this.function.isEmpty()));
  }

  public void unsetFunction() {
    this.function = null;
  }

  /**
   * Gets the value of the usage property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
   * for the usage property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getUsage().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list {@link String }
   * 
   * 
   */
  public List<String> getUsage() {
    if (this.usage == null) {
      this.usage = new ArrayList<String>();
    }
    return this.usage;
  }

  public boolean isSetUsage() {
    return ((this.usage != null) && (!this.usage.isEmpty()));
  }

  public void unsetUsage() {
    this.usage = null;
  }

  /**
   * Gets the value of the lod4Geometry property.
   * 
   * @return possible object is {@link GeometryPropertyType }
   * 
   */
  public IGeometry getLod4Geometry() {
    return this.lod4Geometry;
  }

  /**
   * Sets the value of the lod4Geometry property.
   * 
   * @param value allowed object is {@link GeometryPropertyType }
   * 
   */
  public void setLod4Geometry(IGeometry value) {
    this.lod4Geometry = value;
  }

  public boolean isSetLod4Geometry() {
    return (this.lod4Geometry != null);
  }



}
