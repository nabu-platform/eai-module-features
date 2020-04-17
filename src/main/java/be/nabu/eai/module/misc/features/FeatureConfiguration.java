package be.nabu.eai.module.misc.features;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import be.nabu.eai.api.EnvironmentSpecific;

@XmlRootElement(name = "features")
public class FeatureConfiguration {
	// the features that are enabled
	private List<String> features;
	private String context;

	@EnvironmentSpecific
	public List<String> getFeatures() {
		return features;
	}
	public void setFeatures(List<String> features) {
		this.features = features;
	}
	
	@EnvironmentSpecific
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	
}
