package com.geewhiz.pacify.mavenplugin.mojo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.checks.Check;
import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.checks.impl.CheckPropertyDuplicateInPropertyFile;
import com.geewhiz.pacify.checks.impl.CheckPropertyExists;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PMarker;

/**
 * @goal checkDeliveryProperties
 * @phase install
 */
public class CheckDeliveryPropertyFilesMojo extends BaseResolveMojo {

	/**
	 * which files should be checked? its a comma separated list
	 * 
	 * @parameter
	 * @required
	 */
	private String propertyFiles;

	@Override
	protected void executePacify() throws MojoExecutionException {
		checkStartPath();

		PropertyResolveManager propertyResolveManager = createPropertyResolveManager();

		List<PMarkerCheck> pCheck = new ArrayList<PMarkerCheck>();
		pCheck.add(new CheckPropertyExists(propertyResolveManager));

		List<Check> check = new ArrayList<Check>();
		check.add(new CheckPropertyDuplicateInPropertyFile(propertyResolveManager));

		LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();
		for (String propertyFile : propertyFiles.split(",")) {
			getLog().info("Checking property file [" + propertyFile + "] ...");
			defects.addAll(checkPropertyFile(propertyFile, propertyResolveManager, pCheck, check));
		}

		if (defects.isEmpty()) {
			return;
		}

		getLog().error("==== !!!!!! We got Errors !!!!! ...");
		for (Defect defect : defects) {
			getLog().error(defect.getDefectMessage());
		}
		throw new MojoExecutionException("We got errors... Aborting!");
	}

	private LinkedHashSet<Defect> checkPropertyFile(String propertyFile, PropertyResolveManager propertyResolveManager,
			List<PMarkerCheck> pCheck, List<Check> check) throws MojoExecutionException {

		LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();
		for (Check checker : check) {
			defects.addAll(checker.checkForErrors());
		}

		EntityManager entityManager = new EntityManager(getStartPath());
		for (PMarker pMarker : entityManager.getPMarkers()) {
			for (PMarkerCheck checker : pCheck) {
				defects.addAll(checker.checkForErrors(pMarker));
			}
		}

		return defects;
	}
}
