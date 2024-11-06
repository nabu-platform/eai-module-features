/*
* Copyright (C) 2020 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

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
	
	public void update(@NotNull @WebParam(name = "featureSetId") String featureSetId, @WebParam(name = "enabledFeatures") List<String> enabledFeatures, @WebParam(name = "persist") Boolean persist) throws IOException {
		Artifact resolve = EAIResourceRepository.getInstance().resolve(featureSetId);
		if (resolve instanceof FeatureSet) {
			if (((FeatureSet) resolve).getConfig().getFeatures() == null) {
				((FeatureSet) resolve).getConfig().setFeatures(new ArrayList<String>());
			}
			((FeatureSet) resolve).getConfig().getFeatures().clear();
			((FeatureSet) resolve).getConfig().getFeatures().addAll(enabledFeatures);
			if (persist != null && persist) {
				new FeatureSetManager().save((ResourceEntry) EAIResourceRepository.getInstance().getEntry(resolve.getId()), (FeatureSet) resolve);
			}
		}
	}
	
	public void toggle(@NotNull @WebParam(name = "featureSetId") String featureSetId, @WebParam(name = "feature") String feature, @WebParam(name = "enabled") Boolean enabled, @WebParam(name = "persist") Boolean persist) throws IOException {
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
				if (persist != null && persist) {
					new FeatureSetManager().save((ResourceEntry) EAIResourceRepository.getInstance().getEntry(resolve.getId()), (FeatureSet) resolve);
				}
			}
		}
	}
}
