package fr.ign.cogit.calculation.parcelDecomposition;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.convert.FromGeomToSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition.OBBBlockDecomposition;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class TestOBBBlockDecomposition {

	@Test
	public void test1() {

		IFeatureCollection<IFeature> featColl = ShapefileReader
				.read(getClass().getClassLoader().getResource("parcelDecomposition/parcelle.shp").toString());

		IPolygon pol = (IPolygon) FromGeomToSurface.convertGeom(featColl.get(0).getGeom()).get(0);

		double maximalArea = 100;
		double maximalWidth = 20;
		RandomGenerator rng = new MersenneTwister(42);
		double epsilon = 0;
		double noise = 10;

		OBBBlockDecomposition obb = new OBBBlockDecomposition(pol, maximalArea, maximalWidth, rng, epsilon, noise);
		try {
			IFeatureCollection<IFeature> featCOut = obb.decompParcel();
			assertNotNull(featCOut);
			assertTrue(! featCOut.isEmpty());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}

	}

}
