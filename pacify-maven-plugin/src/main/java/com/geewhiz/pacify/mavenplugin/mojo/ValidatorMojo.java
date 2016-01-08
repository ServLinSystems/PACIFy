package com.geewhiz.pacify.mavenplugin.mojo;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.Validator;

/**
 * 
 * @goal checkAllPropertiesAreReplaced
 * @phase install
 */
public class ValidatorMojo extends BaseResolveMojo {

	public void executePacify() throws MojoExecutionException {
		checkStartPath();

		Validator validator = new Validator(null);
		validator.setPackagePath(getStartPath());
		validator.enableMarkerFileChecks();

		try {
			validator.execute();
		} catch (Exception e) {
			throw new MojoExecutionException("We got errors... Aborting!", e);
		}

	}
}
