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

package be.nabu.eai.module.misc.features;

import java.util.Date;
import java.util.List;

import be.nabu.eai.repository.api.FeatureConfigurator;
import be.nabu.eai.repository.api.Repository;
import be.nabu.eai.repository.artifacts.jaxb.JAXBArtifact;
import be.nabu.libs.authentication.api.Token;
import be.nabu.libs.resources.api.Resource;
import be.nabu.libs.resources.api.ResourceContainer;
import be.nabu.libs.resources.api.TimestampedResource;

public class FeatureSet extends JAXBArtifact<FeatureConfiguration> implements FeatureConfigurator {

	public FeatureSet(String id, ResourceContainer<?> directory, Repository repository) {
		super(id, directory, repository, "feature-set.xml", FeatureConfiguration.class);
	}

	@Override
	public List<String> getEnabledFeatures(Token token) {
		// TODO: allow differentiation per "role", then use the hasRole to validate whether or not it is applicable
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
