/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.partition;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.schemageo.support.partition.Partition;
import fr.ign.cogit.geoxygene.api.schemageo.support.partition.Zone;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class PartitionImpl implements Partition {

  /**
   * l'id de l'objet
   */
  private int id = 0;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /**
   * le nom de l'objet
   */
  private String nom = "";

  public String getNom() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  /**
   * les zones de la partition
   */
  private Collection<Zone> zones = new FT_FeatureCollection<Zone>();

  public Collection<Zone> getZones() {
    return this.zones;
  }

}
