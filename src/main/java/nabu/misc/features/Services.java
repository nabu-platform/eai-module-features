package nabu.misc.features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.validation.constraints.NotNull;

import be.nabu.eai.module.misc.features.FeatureSet;
import be.nabu.eai.module.misc.features.FeatureSetManager;
import be.nabu.eai.repository.EAIResourceRepository;
import be.nabu.eai.repository.api.ResourceEntry;
import be.nabu.libs.artifacts.api.Artifact;

@WebService
public class Services {
	
	public void update(@NotNull @WebParam(name = "featureSetId") String featureSetId, @WebParam(name = "enabledFeatures") List<String> enabledFeatures) throws IOException {
		Artifact resolve = EAIResourceRepository.getInstance().resolve(featureSetId);
		if (resolve instanceof FeatureSet) {
			((FeatureSet) resolve).getConfig().setFeatures(enabledFeatures);
			new FeatureSetManager().save((ResourceEntry) EAIResourceRepository.getInstance().getEntry(resolve.getId()), (FeatureSet) resolve);
		}
	}
	
	public void toggle(@NotNull @WebParam(name = "featureSetId") String featureSetId, @WebParam(name = "feature") String feature, @WebParam(name = "enabled") Boolean enabled) throws IOException {
		if (feature != null) {
			Artifact resolve = EAIResourceRepository.getInstance().resolve(featureSetId);
			if (resolve instanceof FeatureSet) {
				List<String> features = ((FeatureSet) resolve).getConfig().getFeatures();
				if (features == null) {
					features = new ArrayList<String>();
					((FeatureSet) resolve).getConfig().setFeatures(features);
				}
				if (enabled == null || !enabled) {
					features.remove(feature);
				}
				else if (!features.contains(feature)) {
					features.add(feature);
				}
				new FeatureSetManager().save((ResourceEntry) EAIResourceRepository.getInstance().getEntry(resolve.getId()), (FeatureSet) resolve);
			}
		}
	}
}