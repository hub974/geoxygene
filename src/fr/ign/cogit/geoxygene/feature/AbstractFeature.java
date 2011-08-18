/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut G�ographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut G�ographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.feature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationRole;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.toporoot.TP_Object;

/**
 * @author julien Gaffuri 6 juil. 2009
 * 
 */
public abstract class AbstractFeature implements IFeature {
  /**
   * Logger.
   */
  private static final Logger logger = Logger.getLogger(FT_Feature.class
      .getName());

  /**
   * @return the logger
   */
  public static final Logger getLogger() {
    return AbstractFeature.logger;
  }

  protected int id;

  @Id
  @GeneratedValue
  public int getId() {
    return this.id;
  }

  public void setId(int Id) {
    this.id = Id;
  }

  protected IGeometry geom = null;

  public IGeometry getGeom() {
    return this.geom;
  }

  @SuppressWarnings("unchecked")
  public void setGeom(IGeometry g) {
    IGeometry previousGeometry = this.geom;
    boolean hasPreviousGeometry = (previousGeometry != null);
    this.geom = g;
    synchronized (this.featurecollections) {
      IFeatureCollection<IFeature>[] collections = this.featurecollections
          .toArray(new FT_FeatureCollection[0]);
      for (IFeatureCollection<IFeature> fc : collections) {
        if (fc.hasSpatialIndex()) {
          if (fc.getSpatialIndex().hasAutomaticUpdate()) {
            if (hasPreviousGeometry) {
              fc.getSpatialIndex().update(this, (g != null) ? 0 : -1);
            } else {
              fc.getSpatialIndex().update(this, 1);
            }
          }
        }
        if ((previousGeometry != null)
            && ((this.geom == null) || !previousGeometry.equals(this.geom))) {
          fc.fireActionPerformed(new FeatureCollectionEvent(fc, this,
              FeatureCollectionEvent.Type.CHANGED, previousGeometry));
        }
      }
    }
  }

  public boolean hasGeom() {
    return (this.geom != null);
  }

  protected TP_Object topo = null;

  public TP_Object getTopo() {
    return this.topo;
  }

  public void setTopo(TP_Object t) {
    this.topo = t;
  }

  public boolean hasTopo() {
    return (this.topo != null);
  }

  public FT_Feature cloneGeom() throws CloneNotSupportedException {
    FT_Feature result = (FT_Feature) this.clone();
    result.setGeom((IGeometry) this.getGeom().clone());
    return result;
  }

  /** Lien n-m bidirectionnel vers FT_FeatureCollection. */
  private List<IFeatureCollection<IFeature>> featurecollections = new ArrayList<IFeatureCollection<IFeature>>();

  public List<IFeatureCollection<IFeature>> getFeatureCollections() {
    return this.featurecollections;
  }

  public IFeatureCollection<IFeature> getFeatureCollection(int i) {
    return this.featurecollections.get(i);
  }

  /**
   * Population a laquelle appartient this. Renvoie null si this n'appartient a
   * aucune population. NB : normalement, this appartient � une seule
   * collection. Si ce n'est pas le cas, une seule des collections est renvoy�e
   * au hasard (la premi�re de la liste).
   */
  // @SuppressWarnings("unchecked")
  // public Population<? extends FT_Feature> getPopulation() {
  // Iterator<FT_FeatureCollection<? extends FT_Feature>> it =
  // featurecollections
  // .iterator();
  // while (it.hasNext()) {
  // Object o = it.next();
  // if (o instanceof Population)
  // return (Population<FT_Feature>) o;
  // }
  // return null;
  // }
  /**
   * D�finit la population en relation, et met � jour la relation inverse.
   * ATTENTION : persistance du FT_Feature non g�r�e dans cette m�thode.
   */

  // public void setPopulation(Population O) { Population old =
  // this.getPopulation(); if ( old != null ) old.remove(this); if ( O != null
  // )
  // O.add(this); }
  /**
   * Lien bidirectionnel n-m des �l�ments vers eux-m�mes. Les m�thodes get (sans
   * indice) et set sont n�cessaires au mapping. Les autres m�thodes sont l�
   * seulement pour faciliter l'utilisation de la relation. ATTENTION: Pour
   * assurer la bidirection, il faut modifier les listes uniquement avec ces
   * methodes. NB: si il n'y a pas d'objet en relation, la liste est vide mais
   * n'est pas "null". Pour casser toutes les relations, faire setListe(new
   * ArrayList()) ou emptyListe().
   */
  private List<IFeature> correspondants = new ArrayList<IFeature>();

  public List<IFeature> getCorrespondants() {
    return this.correspondants;
  }

  public void setCorrespondants(List<IFeature> L) {
    List<IFeature> old = new ArrayList<IFeature>(this.correspondants);
    for (IFeature O : old) {
      this.correspondants.remove(O);
      O.getCorrespondants().remove(this);
    }
    for (IFeature O : L) {
      this.correspondants.add(O);
      O.getCorrespondants().add(this);
    }
  }

  public IFeature getCorrespondant(int i) {
    if (this.correspondants.size() == 0) {
      return null;
    }
    return this.correspondants.get(i);
  }

  public void addCorrespondant(IFeature O) {
    if (O == null) {
      return;
    }
    this.correspondants.add(O);
    O.getCorrespondants().add(this);
  }

  public void removeCorrespondant(IFeature O) {
    if (O == null) {
      return;
    }
    this.correspondants.remove(O);
    O.getCorrespondants().remove(this);
  }

  public void clearCorrespondants() {
    for (IFeature O : this.correspondants) {
      O.getCorrespondants().remove(this);
    }
    this.correspondants.clear();
  }

  public void addAllCorrespondants(Collection<IFeature> c) {
    for (IFeature feature : c) {
      this.addCorrespondant(feature);
    }
  }

  public List<IFeature> getCorrespondants(
      IFeatureCollection<? extends IFeature> pop) {
    List<? extends IFeature> elementsPop = pop.getElements();
    List<IFeature> resultats = new ArrayList<IFeature>(this.getCorrespondants());
    resultats.retainAll(elementsPop);
    return resultats;
  }

  // //////////////////////////////////////////////////////////////////////////
  // /////
  /**
   * M�thodes issues de MdFeature : Permettent de cr�er un Feature dont les
   * propri�t�s (valeurs d'attributs, op�rations et objets en relation) peuvent
   * �tre acced�s de fa�on g�n�rique en mentionnant le nom de la propri�t�. Pour
   * permettre cela chaque feature est rattach� � son featureType.
   */
  // //////////////////////////////////////////////////////////////////////////
  // /////
  /**
   * Creation d'un feature du type donn� en param�tre, par exemple Route.
   * L'objet cr�e sera alors une instance de bdcarto.TronconRoute (l'element de
   * sch�ma logique correspondant au featureType Route) qui �tend FeatureCommun.
   * Les valeurs d'attributs ne sont pas initialis�es.
   * @param featureType le feature type de l'objet � cr�er
   * @return l'objet cr��
   */
  public static IFeature createTypedFeature(FeatureType featureType) {
    IFeature feature = null;
    try {
      Class<?> theClass = Class.forName(featureType.getNomClasse());
      feature = (FT_Feature) theClass.newInstance();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return feature;
  }

  /**
   * L'unique population � laquelle appartient cet objet.
   */
  protected IPopulation<IFeature> population;

  @SuppressWarnings("unchecked")
  public IPopulation<IFeature> getPopulation() {
    if (this.population != null) {
      return this.population;
    }
    synchronized (this.featurecollections) {
      IFeatureCollection<IFeature>[] collections = this.featurecollections
          .toArray(new IFeatureCollection[0]);
      for (IFeatureCollection<IFeature> f : collections) {
        if (f instanceof Population<?>) {
          return (Population<IFeature>) f;
        }
      }
    }
    return null;
  }

  public void setPopulation(IPopulation<IFeature> population) {
    this.population = population;
    // Refuse d'�crire dans ma population car ne peut pas pas v�rifier si
    // this h�rite bien de FT_Feature...
    // this.population.addUnique(this);
  }

  /**
   * L'unique featureType auquel appartient cet objet.
   */
  protected GF_FeatureType featureType;

  public void setFeatureType(GF_FeatureType featureType) {
    this.featureType = featureType;
  }

  public GF_FeatureType getFeatureType() {
    if ((this.featureType == null) && (this.getPopulation() != null)) {
      return this.getPopulation().getFeatureType();
    }
    return this.featureType;
  }

  public Object getAttribute(AttributeType attribute) {
    if (attribute.getMemberName().equals("geom")) {
      AbstractFeature.logger
          .warn("WARNING : Pour r�cup�rer la primitive g�om�trique par d�faut, veuillez utiliser "
              + "la m�thode FT_Feature.getGeom() et non pas MdFeature.getAttribute(AttributeType attribute)");
      return this.getGeom();
    }
    if (attribute.getMemberName().equals("topo")) {
      AbstractFeature.logger
          .warn("WARNING : Pour r�cup�rer la primitive topologique par d�faut, veuillez utiliser "
              + "la m�thode FT_Feature.getTopo() et non pas MdFeature.getAttribute(AttributeType attribute)");
      return this.getTopo();
    }
    Object valeur = null;
    String nomFieldMaj = null;
    if (attribute.getNomField().length() == 0) {
      nomFieldMaj = attribute.getNomField();
    } else {
      nomFieldMaj = Character.toUpperCase(attribute.getNomField().charAt(0))
          + attribute.getNomField().substring(1);
    }
    String nomGetFieldMethod = "get" + nomFieldMaj;
    Class<?> classe = this.getClass();
    while (!classe.equals(Object.class)) {
      try {
        Method methodGetter = classe.getDeclaredMethod(nomGetFieldMethod,
            (Class[]) null);
        valeur = methodGetter.invoke(this, (Object[]) null);
        return valeur;
      } catch (SecurityException e) {
        if (AbstractFeature.logger.isTraceEnabled()) {
          AbstractFeature.logger
              .trace("SecurityException pendant l'appel de la m�thode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (IllegalArgumentException e) {
        if (AbstractFeature.logger.isTraceEnabled()) {
          AbstractFeature.logger
              .trace("IllegalArgumentException pendant l'appel de la m�thode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (NoSuchMethodException e) {
        // if (logger.isTraceEnabled())
        // logger.trace("La m�thode "+nomGetFieldMethod+" n'existe pas dans la classe "+classe);
      } catch (IllegalAccessException e) {
        if (AbstractFeature.logger.isTraceEnabled()) {
          AbstractFeature.logger
              .trace("IllegalAccessException pendant l'appel de la m�thode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (InvocationTargetException e) {
        if (AbstractFeature.logger.isTraceEnabled()) {
          AbstractFeature.logger
              .trace("InvocationTargetException pendant l'appel de la m�thode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      }
      classe = classe.getSuperclass();
    }
    // r�essayer si le getter est du genre isAttribute, ie pour un bool�en
    nomGetFieldMethod = "is" + nomFieldMaj;
    classe = this.getClass();
    while (!classe.equals(Object.class)) {
      try {
        Method methodGetter = classe.getDeclaredMethod(nomGetFieldMethod,
            (Class[]) null);
        valeur = methodGetter.invoke(this, (Object[]) null);
        return valeur;
      } catch (SecurityException e) {
        if (AbstractFeature.logger.isTraceEnabled()) {
          AbstractFeature.logger
              .trace("SecurityException pendant l'appel de la m�thode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (IllegalArgumentException e) {
        if (AbstractFeature.logger.isTraceEnabled()) {
          AbstractFeature.logger
              .trace("IllegalArgumentException pendant l'appel de la m�thode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (NoSuchMethodException e) {
        // if (logger.isTraceEnabled())
        // logger.trace("La m�thode "+nomGetFieldMethod+" n'existe pas dans la classe "+classe);
      } catch (IllegalAccessException e) {
        if (AbstractFeature.logger.isTraceEnabled()) {
          AbstractFeature.logger
              .trace("IllegalAccessException pendant l'appel de la m�thode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (InvocationTargetException e) {
        if (AbstractFeature.logger.isTraceEnabled()) {
          AbstractFeature.logger
              .trace("InvocationTargetException pendant l'appel de la m�thode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      }
      classe = classe.getSuperclass();
    }
    AbstractFeature.logger.error("Echec de l'appel � la m�thode "
        + nomGetFieldMethod + " sur la classe " + this.getClass());
    return null;
  }

  public void setAttribute(AttributeType attribute, Object valeur) {
    if (attribute.getMemberName().equals("geom")) {
      AbstractFeature.logger
          .warn("WARNING : Pour affecter la primitive g�om�trique par d�faut, veuillez utiliser "
              + "la m�thode FT_Feature.getGeom() et non pas MdFeature.getAttribute(AttributeType attribute)");
      this.setGeom((IGeometry) valeur);
    } else if (attribute.getMemberName().equals("topo")) {
      AbstractFeature.logger
          .warn("WARNING : Pour affecter la primitive topologique par d�faut, veuillez utiliser "
              + "la m�thode FT_Feature.getTopo() et non pas MdFeature.getAttribute(AttributeType attribute)");
      this.setTopo((TP_Object) valeur);
    } else {
      try {
        String nomFieldMaj2;
        if (attribute.getNomField().length() == 0) {
          nomFieldMaj2 = attribute.getNomField();
        } else {
          nomFieldMaj2 = Character.toUpperCase(attribute.getNomField()
              .charAt(0))
              + attribute.getNomField().substring(1);
        }
        String nomSetFieldMethod = "set" + nomFieldMaj2;
        Method methodSetter = this.getClass().getDeclaredMethod(
            nomSetFieldMethod, valeur.getClass());
        // Method methodGetter =
        // this.getClass().getSuperclass().getDeclaredMethod(
        // nomGetFieldMethod,
        // null);
        valeur = methodSetter.invoke(this, valeur);
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  @SuppressWarnings("unchecked")
  public List<? extends FT_Feature> getRelatedFeatures(GF_FeatureType ftt,
      AssociationRole role) {
    List<FT_Feature> listResult = new ArrayList();
    if (AbstractFeature.logger.isDebugEnabled()) {
      AbstractFeature.logger.debug("\n**recherche des features en relation**");
    }
    try {
      // cas 1-1 ou 1-N ou N-1 o� il n'y a pas de classe association
      if (role.getNomFieldAsso() == null) {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger.debug("pas de classe association");
        }
        String nomFieldClasseMaj;
        if (role.getNomFieldClasse().length() == 0) {
          nomFieldClasseMaj = role.getNomFieldClasse();
        } else {
          nomFieldClasseMaj = Character.toUpperCase(role.getNomFieldClasse()
              .charAt(0))
              + role.getNomFieldClasse().substring(1);
        }
        String nomGetMethod = "get" + nomFieldClasseMaj;
        Method methodGetter = this.getClass().getDeclaredMethod(nomGetMethod,
            (Class[]) null);
        // Method methodGetter =
        // this.getClass().getSuperclass().getDeclaredMethod(
        // nomGetFieldMethod,
        // null);
        Object objResult = methodGetter.invoke(this, (Object[]) null);
        if (objResult instanceof FT_Feature) {
          listResult.add((FT_Feature) objResult);
        } else if (objResult instanceof List) {
          listResult.addAll((List<FT_Feature>) objResult);
        }
      }
      // cas ou il y a une classe association
      else {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger.debug("classe association : "
              + role.getAssociationType().getTypeName());
        }
        List<FT_Feature> listInstancesAsso = new ArrayList<FT_Feature>();
        // je vais chercher les instances de classe-association
        String nomFieldClasseMaj;
        if (role.getNomFieldClasse().length() == 0) {
          nomFieldClasseMaj = role.getNomFieldClasse();
        } else {
          nomFieldClasseMaj = Character.toUpperCase(role.getNomFieldClasse()
              .charAt(0))
              + role.getNomFieldClasse().substring(1);
        }
        String nomGetMethod = "get" + nomFieldClasseMaj;
        Method methodGetter = this.getClass().getDeclaredMethod(nomGetMethod,
            (Class[]) null);
        String nomClasseAsso = ((AssociationType) role.getAssociationType())
            .getNomClasseAsso();
        Class classeAsso = Class.forName(nomClasseAsso);
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger.debug("cardMax de " + role.getMemberName()
              + " = " + role.getCardMax());
        }
        if (!role.getCardMax().equals("1")) {
          if (AbstractFeature.logger.isDebugEnabled()) {
            AbstractFeature.logger.debug("invocation de "
                + methodGetter.getName() + " sur le feature " + this.getId());
          }
          listInstancesAsso.addAll((List<FT_Feature>) methodGetter.invoke(this,
              (Object[]) null));
          if (AbstractFeature.logger.isDebugEnabled()) {
            AbstractFeature.logger.debug("nb instances d'association = "
                + listInstancesAsso.size());
          }
        } else {
          listInstancesAsso.add((FT_Feature) methodGetter.invoke(this,
              (Object[]) null));
          if (AbstractFeature.logger.isDebugEnabled()) {
            AbstractFeature.logger.debug("nb instances d'association = "
                + listInstancesAsso.size());
          }
        }
        // je cherche le (ou les) role(s) allant de l'association �
        // l'autre featureType
        List listRoles = role.getAssociationType().getRoles();
        listRoles.remove(role);
        List listRolesAGarder = role.getAssociationType().getRoles();
        listRolesAGarder.remove(role);
        for (int i = 0; i < listRoles.size(); i++) {
          if (!((AssociationRole) listRoles.get(i)).getFeatureType()
              .equals(ftt)) {
            listRolesAGarder.remove(listRoles.get(i));
          }
        }
        /**
         * pour chaque role concern� (il peut y en avoir plus d'un) je vais
         * chercher les instances en relation
         */
        AssociationRole roleExplore;
        for (int i = 0; i < listRolesAGarder.size(); i++) {
          roleExplore = (AssociationRole) listRolesAGarder.get(i);
          if (AbstractFeature.logger.isDebugEnabled()) {
            AbstractFeature.logger.debug("role explor� = "
                + roleExplore.getMemberName());
          }
          String nomFieldAssoMaj;
          if (roleExplore.getNomFieldAsso().length() == 0) {
            nomFieldAssoMaj = roleExplore.getNomFieldAsso();
          } else {
            nomFieldAssoMaj = Character.toUpperCase(roleExplore
                .getNomFieldAsso().charAt(0))
                + roleExplore.getNomFieldAsso().substring(1);
          }
          nomGetMethod = "get" + nomFieldAssoMaj;
          methodGetter = classeAsso.getDeclaredMethod(nomGetMethod,
              (Class[]) null);
          if (AbstractFeature.logger.isDebugEnabled()) {
            AbstractFeature.logger
                .debug("methode de la classe-asso pour recuperer les instances en relation = "
                    + methodGetter.getName());
          }
          /**
           * je vais chercher les objets en relation via chaque instance de
           * classe-association
           */
          for (int j = 0; j < listInstancesAsso.size(); j++) {
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger.debug("j = " + j);
            }
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger.debug("instance = "
                  + listInstancesAsso.get(j).getId());
            }
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger.debug("class  "
                  + listInstancesAsso.get(j).getClass());
            }
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger.debug(methodGetter.invoke(
                  listInstancesAsso.get(j), (Object[]) null));
            }
            Object objResult = methodGetter.invoke(listInstancesAsso.get(j),
                (Object[]) null);
            if (objResult instanceof FT_Feature) {
              listResult.add((FT_Feature) objResult);
            } else if (objResult instanceof List) {
              listResult.addAll((List<FT_Feature>) objResult);
            }
          }
        }
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    if (AbstractFeature.logger.isDebugEnabled()) {
      AbstractFeature.logger
          .debug("\n**fin de la recherche des features en relation**");
    }
    return listResult;
  }

  // //////////////////////////////////////////////////////////////////////////
  // /
  // ///////////////// Ajout Nathalie
  // //////////////////////////////////////////////////////////////////////////
  // /

  public Object getAttribute(String nomAttribut) {
    GF_FeatureType ft = this.getFeatureType();
    if (ft == null) {
      AttributeType type = new AttributeType();
      type.setNomField(nomAttribut);
      type.setMemberName(nomAttribut);
      // FIXME c'est un peu une bidouille
      return this.getAttribute(type);
      // logger.error("Le FeatureType correspondant � ce FT_Feature est introuvable: Impossible de remonter au AttributeType � partir de son nom.");
      // return null;
    }
    AttributeType attribute = ((FeatureType) ft)
        .getFeatureAttributeByName(nomAttribut);
    return (this.getAttribute(attribute));
  }

  public List<? extends FT_Feature> getRelatedFeatures(String nomFeatureType,
      String nomRole) {
    // Initialisation de la liste des r�sultats
    List<? extends FT_Feature> listResultats = null;
    // On r�cup�re le featuretype nomm� nomFeatureType
    GF_FeatureType ftt = ((FeatureType) this.getFeatureType()).getSchema()
        .getFeatureTypeByName(nomFeatureType);
    // On r�cup�re l'AssociationRole nomm� nomRole
    AssociationRole role = null;
    List<GF_AssociationRole> listeRoles = ftt.getRoles();
    for (GF_AssociationRole r : listeRoles) {
      if (r.getMemberName().equalsIgnoreCase(nomRole)) {
        role = (AssociationRole) r;
      } else {
        continue;
      }
    }
    if ((ftt == null) || (role == null)) {
      AbstractFeature.logger
          .error("Le FeatureType "
              + nomFeatureType
              + " ou l'AssociationRole "
              + nomRole
              + " est introuvable. Impossible de calculer les FT_Feature en relation!");
      return null;
    }
    listResultats = this.getRelatedFeatures(ftt, role);
    return listResultats;
  }

  /**
   * La s�miologie de l'objet g�ographique
   */
  private Representation representation = null;

  public Representation getRepresentation() {
    return this.representation;
  }

  public void setRepresentation(Representation rep) {
    this.representation = rep;
  }

  /**
   * marqueur de suppression d'un objet (utilis� par exemple en g�n�ralisation)
   */
  private boolean estSupprime = false;

  public boolean isDeleted() {
    return this.estSupprime;
  }

  public void setDeleted(boolean estSupprime) {
    this.estSupprime = estSupprime;
  }

  public boolean intersecte(IEnvelope env) {
    if (this.getGeom() == null || env == null) {
      return false;
    }
    return this.getGeom().envelope().intersects(env);
  }

  @Override
  public String toString() {
    return this.getClass().getName() + " " + this.getGeom();
  }

  @Override
  public boolean equals(Object obj) {
    IFeature other = (IFeature) obj;
    if (!other.getClass().equals(this.getClass())) {
      return false;
    }
    if (other.getId() != this.getId()) {
      return false;
    }
    if (other.getGeom() == null) {
      return false;
    }
    if (!other.getGeom().equals(this.getGeom())) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return this.getId();
  }

}