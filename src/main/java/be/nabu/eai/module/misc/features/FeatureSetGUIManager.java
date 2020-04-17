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
import be.nabu.eai.repository.api.Feature;
import be.nabu.eai.repository.api.FeaturedArtifact;
import be.nabu.eai.repository.resources.RepositoryEntry;
import be.nabu.libs.property.api.Property;
import be.nabu.libs.property.api.Value;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
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
			VBox.setMargin(labelEnabled, new Insets(10, 0, 10, 0));
			box.getChildren().add(labelEnabled);
		}
		
		Iterator<String> iterator = instance.getConfig().getFeatures().iterator();
		// first we draw the already enabled features
		while (iterator.hasNext()) {
			String enabled = iterator.next();
			if (features.containsKey(enabled)) {
				drawFeature(instance, pane, box, features.get(enabled), instance.getConfig().getFeatures(), null, features);
				// don't list it below
				list.remove(enabled);
			}
			else {
				iterator.remove();
				MainController.getInstance().setChanged();
			}
		}
		
		if (!list.isEmpty()) {
			Label labelAvailable = new Label("Available features");
			VBox.setMargin(labelAvailable, new Insets(10, 0, 10, 0));
			box.getChildren().add(labelAvailable);
			
			TextField search = new TextField();
			search.setPromptText("Search");
			VBox.setMargin(search, new Insets(10, 0, 10, 0));
			box.getChildren().add(search);
			
			// then we draw the currently disabled features
			for (String disabled : list) {
				drawFeature(instance, pane, box, features.get(disabled), instance.getConfig().getFeatures(), search, features);
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
	
	private void drawFeature(FeatureSet instance, Pane pane, VBox parent, Feature feature, List<String> enabled, TextField search, Map<String, Feature> features) {
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
						MainController.getInstance().setChanged();
						display(instance, pane, features);
					}
				}
				else {
					enabled.remove(feature.getName());
					MainController.getInstance().setChanged();
					display(instance, pane, features);
				}
			}
		});
		Label name = new Label(feature.getName());
		name.getStyleClass().add("feature-name");
		
		Label description = new Label(feature.getDescription());
		description.getStyleClass().add("feature-description");
		
		box.getChildren().addAll(check, name, description);
		parent.getChildren().add(box);
	}
	
	@Override
	public String getCategory() {
		return "Miscellaneous";
	}
}
