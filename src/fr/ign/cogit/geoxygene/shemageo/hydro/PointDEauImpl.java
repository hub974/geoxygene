/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.hydro;

import fr.ign.cogit.geoxygene.api.schemageo.hydro.PointDEau;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.ElementPonctuelReseauImpl;

/**
 * point d'eau, reservoir (?), point d'eau isole, toponyme de surface hydro (?)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class PointDEauImpl extends ElementPonctuelReseauImpl implements
    PointDEau {

  /**
   * le nom
   */
  private String nom = "";

  public String getNom() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  /**
   * le type
   */
  private FeatureType type;

  public FeatureType getType() {
    return this.type;
  }

  public void setType(FeatureType type) {
    this.type = type;
  }

  /**
   * l'altitude maximale de l'objet
   */
  private double zMax;

  public double getZMax() {
    return this.zMax;
  }

  public void setZMax(double zMax) {
    this.zMax = zMax;
  }

  /**
   * l'altitude minimale de l'objet
   */
  private double zMin;

  public double getZMin() {
    return this.zMin;
  }

  public void setZMin(double zMin) {
    this.zMin = zMin;
  }

  /**
   * la hauteur de l'objet
   */
  private double hauteur;

  public double getHauteur() {
    return this.hauteur;
  }

  public void setHauteur(double hauteur) {
    this.hauteur = hauteur;
  }
}
