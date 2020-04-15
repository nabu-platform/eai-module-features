package be.nabu.eai.module.misc.features;

import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.resources.api.ResourceContainer;

public class FeatureSet extends JAXBArtifact<FeatureConfiguration> {

	public FeatureSet(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, repository, "feature-set.xml", FeatureConfiguration.class);
	}
	
}
