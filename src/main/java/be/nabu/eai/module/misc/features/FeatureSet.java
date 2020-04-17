package be.nabu.eai.module.misc.features;

import java.util.Date;
import java.util.List;

import be.nabu.eai.repository.api.FeatureConfigurator;
import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.resources.api.TimestampedResource;

public class FeatureSet extends JAXBArtifact<FeatureConfiguration> implements FeatureConfigurator {

	public FeatureSet(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, repository, "feature-set.xml", FeatureConfiguration.class);
	}

	@Override
	public List<String> getEnabledFeatures() {
		return getConfig().getFeatures();
	}

	@Override
	public String getContext() {
		return getConfig().getContext();
	}

	@Override
	public Date getLastModified() {
		Resource child = getDirectory().getChild("feature-set.xml");
		if (child instanceof TimestampedResource) {
			return ((TimestampedResource) child).getLastModified();
		}
		return null;
	}
	
}
