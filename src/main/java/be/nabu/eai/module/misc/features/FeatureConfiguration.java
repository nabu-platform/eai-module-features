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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import be.nabu.eai.api.EnvironmentSpecific;

@XmlRootElement(name = "features")
public class FeatureConfiguration {
	// the features that are enabled
	private List<String> features, disabled;
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
	
	public List<String> getDisabled() {
		return disabled;
	}
	public void setDisabled(List<String> disabled) {
		this.disabled = disabled;
	}
	
}
