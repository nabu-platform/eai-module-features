package be.nabu.eai.module.misc.features;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import be.nabu.eai.api.EnvironmentSpecific;

@XmlRootElement(name = "features")
public class FeatureConfiguration {
	// the features that are enabled
	private List<String> features;

	@EnvironmentSpecific
	public List<String> getFeatures() {
		return features;
	}
	public void setFeatures(List<String> features) {
		this.features = features;
	}
}
