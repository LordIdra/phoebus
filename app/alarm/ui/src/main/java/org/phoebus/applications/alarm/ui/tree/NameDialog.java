package org.phoebus.applications.alarm.ui.tree;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.phoebus.applications.alarm.client.AlarmClient;
import org.phoebus.applications.alarm.client.AlarmClientNode;
import org.phoebus.applications.alarm.model.AlarmTreeItem;
import org.phoebus.applications.alarm.ui.Messages;

import java.util.Set;
import java.util.function.Consumer;


public class NameDialog extends Dialog<String> {

    public NameDialog(final AlarmClient model, final AlarmTreeItem<?> parent, final AlarmTreeItem<?> item) {
        this(model, parent, item.getName(), item instanceof AlarmClientNode);
    }

    public NameDialog(final AlarmClient model, final AlarmTreeItem<?> parent, final String initialName, final boolean isNode) {
        super();

        setTitle(Messages.renameDialogTitle);

        final TextField input = new TextField();
        input.setPrefWidth(600.0);
        HBox.setHgrow(input, Priority.ALWAYS);

        final Label errorLabel = new Label(Messages.nodeAlreadyExists);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setMaxWidth(Double.MAX_VALUE);

        final Consumer<String> updateError = newError -> {
            errorLabel.setText(newError);
            getDialogPane().lookupButton(ButtonType.OK).setDisable(newError != null);
        };

        final Set<String> allPvs = AlarmTreeHelper.getLeafNames(model.getRoot());
        input.textProperty().addListener(_ -> {
            // Show error and disable OK if text is empty
            if (input.getText().isEmpty()) {
                updateError.accept(Messages.nameCannotBeEmpty);
                return;
            }

            if (isNode) {
                // Show error and disable OK if the new name matches any child
                final boolean childExists = parent.getChildren().stream().anyMatch(child -> input.getText().equals(child.getName()));
                updateError.accept(childExists ? Messages.nodeAlreadyExists : null);
            } else {
                // Show error and disable OK if the PV already exists in the config
                final boolean pvExists = allPvs.contains(input.getText());
                updateError.accept(pvExists ? Messages.pvAlreadyExists : null);
            }
        });


        getDialogPane().setContent(new VBox(input, errorLabel));
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // " +" matches one or more spaces
                return input.getText().replaceAll(" +", " ").trim();
            }
            return null;
        });

        input.setText(initialName);
        errorLabel.setText(""); // It can be confusing if an error is shown immediately when the UI loads

        input.requestFocus();
    }
}
