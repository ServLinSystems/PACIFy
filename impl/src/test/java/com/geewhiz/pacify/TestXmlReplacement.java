package com.geewhiz.pacify;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.property.resolver.HashMapPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.test.TestUtil;

public class TestXmlReplacement {
	@Test
	public void testAll() {
		File testResourceFolder = new File("src/test/resources/testXmlReplacement/correct/complete");
		File targetResourceFolder = new File("target/test-classes/testXmlReplacement/correct/complete");

		TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

		EntityManager entityManager = new EntityManager(new File(targetResourceFolder, "package"));
		LinkedHashSet<Defect> defects = entityManager.initialize();

		TestUtil.checkForNoDefects(defects);

		PMarker pMarker = entityManager.getPMarkers().get(0);

		Assert.assertEquals(2, pMarker.getPXmls().size());
		Assert.assertEquals("correctConf.xml", pMarker.getPXmls().get(0).getRelativePath());
		Assert.assertEquals(1, pMarker.getPXmls().get(0).getPProperties().size());

		defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);
		
		TestUtil.checkForNoDefects(defects);

		// Assert.assertEquals("someConf.conf",
		// pMarker.getPFiles().get(0).getRelativePath());
		// Assert.assertEquals("subfolder/someOtherConf.conf",
		// pMarker.getPFiles().get(1).getRelativePath());
		// Assert.assertEquals("someParentConf.conf",
		// pMarker.getPFiles().get(2).getRelativePath());
		//
		// Assert.assertEquals(1,
		// pMarker.getPFiles().get(0).getPProperties().size());
		// Assert.assertEquals(1,
		// pMarker.getPFiles().get(1).getPProperties().size());
		// Assert.assertEquals(1,
		// pMarker.getPFiles().get(2).getPProperties().size());
		//
		// Assert.assertEquals("foobar1",
		// pMarker.getPFiles().get(0).getPProperties().get(0).getName());
		// Assert.assertEquals("foobar1",
		// pMarker.getPFiles().get(1).getPProperties().get(0).getName());
		// Assert.assertEquals("foobar2",
		// pMarker.getPFiles().get(2).getPProperties().get(0).getName());
	}

	private LinkedHashSet<Defect> createPrepareAndExecutePacify(File testResourceFolder, File targetResourceFolder) {
		TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

		File packagePath = new File(targetResourceFolder, "package");

		HashMapPropertyResolver hpr = new HashMapPropertyResolver();
		PropertyResolveManager prm = getPropertyResolveManager(hpr);

		EntityManager entityManager = new EntityManager(packagePath);
		LinkedHashSet<Defect> defects = entityManager.initialize();

		Replacer replacer = new Replacer(prm);
		replacer.setPackagePath(packagePath);

		defects.addAll(replacer.doReplacement(entityManager));
		return defects;
	}

	private PropertyResolveManager getPropertyResolveManager(HashMapPropertyResolver hpr) {
		hpr.addProperty("barfoo", "barfooValue");

		Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
		propertyResolverList.add(hpr);
		PropertyResolveManager prm = new PropertyResolveManager(propertyResolverList);
		return prm;
	}
}
