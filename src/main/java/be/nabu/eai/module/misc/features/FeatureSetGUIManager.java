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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import be.nabu.eai.developer.MainController;
import be.nabu.eai.developer.managers.base.BaseJAXBGUIManager;
import be.nabu.eai.developer.util.EAIDeveloperUtils;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.artifacts.api.Feature;
import be.nabu.libs.artifacts.api.FeaturedArtifact;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class FeatureSetGUIManager extends BaseJAXBGUIManager<FeatureConfiguration, FeatureSet> {

	public FeatureSetGUIManager() {
		super("Feature Set", FeatureSet.class, new FeatureSetManager(), FeatureConfiguration.class);
	}

	@Override
	protected List<Property<?>> getCreateProperties() {
		return null;
	}

	@Override
	protected FeatureSet newInstance(MainController controller, RepositoryEntry entry, Value<?>... values) throws IOException {
		return new FeatureSet(entry.getId(), entry.getContainer(), entry.getRepository());
	}

	@Override
	protected void display(FeatureSet instance, Pane pane) {
		Map<String, Feature> features = new HashMap<String, Feature>();
		// first we build an overview of all features
		for (FeaturedArtifact artifact : instance.getRepository().getArtifacts(FeaturedArtifact.class)) {
			List<Feature> artifactFeatures = artifact.getAvailableFeatures();
			if (artifactFeatures != null) {
				for (Feature feature : artifactFeatures) {
					if (!features.containsKey(feature.getName()) || features.get(feature.getName()).getDescription() == null) {
						features.put(feature.getName(), feature);
					}
				}
			}
		}
		display(instance, pane, features);
	}

	// we don't want to recalculate all the features every time you change something
	private void display(FeatureSet instance, Pane pane, Map<String, Feature> features) {
		List<String> list = new ArrayList<String>();
		list.addAll(features.keySet());
		
		if (instance.getConfig().getFeatures() == null) {
			instance.getConfig().setFeatures(new ArrayList<String>());
		}
		if (instance.getConfig().getDisabled() == null) {
			instance.getConfig().setDisabled(new ArrayList<String>());
		}
		
		VBox box = new VBox();
		box.setPadding(new Insets(10));
		
		
		TextField context = new TextField(instance.getConfig().getContext());
		context.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				instance.getConfig().setContext(arg2 != null && !arg2.trim().isEmpty() ? arg2.trim() : null);
				MainController.getInstance().setChanged();
			}
		});
		HBox newHBox = EAIDeveloperUtils.newHBox("Context", context);
		newHBox.setPadding(new Insets(10, 0, 10, 0));
		box.getChildren().add(newHBox);
		
		if (!instance.getConfig().getFeatures().isEmpty()) {
			Label labelEnabled = new Label("Enabled features");
			labelEnabled.getStyleClass().add("h1");
			VBox.setMargin(labelEnabled, new Insets(10, 0, 10, 0));
			box.getChildren().add(labelEnabled);
		}
		
		Iterator<String> iterator = instance.getConfig().getFeatures().iterator();
		// first we draw the already enabled features
		while (iterator.hasNext()) {
			String enabled = iterator.next();
			if (features.containsKey(enabled)) {
				drawFeature(instance, pane, box, features.get(enabled), instance.getConfig().getFeatures(), instance.getConfig().getDisabled(), null, features);
				// don't list it below
				list.remove(enabled);
			}
			// remove the feature if it no longer exists
			else {
				iterator.remove();
				MainController.getInstance().setChanged();
			}
		}
		
		// update the disabled list
		instance.getConfig().getDisabled().clear();
		instance.getConfig().getDisabled().addAll(list);
		
		if (!list.isEmpty()) {
			Label labelAvailable = new Label("Available features");
			labelAvailable.getStyleClass().add("h1");
			VBox.setMargin(labelAvailable, new Insets(10, 0, 10, 0));
			box.getChildren().add(labelAvailable);
			
			TextField search = new TextField();
			search.setPromptText("Search");
			VBox.setMargin(search, new Insets(10, 0, 10, 0));
			box.getChildren().add(search);
			
			// then we draw the currently disabled features
			for (String disabled : list) {
				drawFeature(instance, pane, box, features.get(disabled), instance.getConfig().getFeatures(), instance.getConfig().getDisabled(), search, features);
			}
		}
		else {
			Label noData = new Label("No features are currently available");
			noData.setPadding(new Insets(10, 0, 10, 0));
			box.getChildren().add(noData);
		}
		
		pane.getChildren().clear();
		pane.getChildren().add(box);
		
		AnchorPane.setBottomAnchor(box, 0d);
		AnchorPane.setLeftAnchor(box, 0d);
		AnchorPane.setRightAnchor(box, 0d);
		AnchorPane.setTopAnchor(box, 0d);
	}
	
	private void drawFeature(FeatureSet instance, Pane pane, VBox parent, Feature feature, List<String> enabled, List<String> disabled, TextField search, Map<String, Feature> features) {
		HBox box = new HBox();
		
		if (search != null) {
			search.textProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
					boolean matches = arg2 == null || arg2.trim().isEmpty()
							|| (feature.getName() != null && feature.getName().matches("(?s)(?i).*" + arg2.replace("*", ".*") + ".*"))
							|| (feature.getDescription() != null && feature.getDescription().matches("(?s)(?i).*" + arg2.replace("*", ".*") + ".*"));
					box.setVisible(matches);
					box.setManaged(matches);
				}
			});
		}
		
		CheckBox check = new CheckBox();
		check.setSelected(enabled.contains(feature.getName()));
		check.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean selected) {
				if (selected != null && selected) {
					if (!enabled.contains(feature.getName())) {
						enabled.add(feature.getName());
						disabled.remove(feature.getName());
						MainController.getInstance().setChanged();
						display(instance, pane, features);
					}
				}
				else {
					enabled.remove(feature.getName());
					disabled.add(feature.getName());
					MainController.getInstance().setChanged();
					display(instance, pane, features);
				}
			}
		});
		
		Label name = new Label(feature.getName());
		name.getStyleClass().add("feature-name");
		
		Label description = new Label(feature.getDescription());
		description.getStyleClass().add("feature-description");
		
		name.setPadding(new Insets(0, 0, 0, 10));
		description.setPadding(new Insets(0, 0, 0, 10));
		
		box.getChildren().addAll(check, name, description);
		box.setAlignment(Pos.CENTER_LEFT);
		parent.getChildren().add(box);
	}
	
	@Override
	public String getCategory() {
		return "Miscellaneous";
	}
}
