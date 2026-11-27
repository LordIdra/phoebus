package org.phoebus.applications.alarm.ui.tree;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.phoebus.applications.alarm.client.AlarmClient;
import org.phoebus.applications.alarm.ui.Messages;
import org.phoebus.ui.autocomplete.MultiplePVAutocompleteMenu;
import org.phoebus.ui.autocomplete.PVAutocompleteMenu;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;


public class CreateAlarmsDialog extends Dialog<List<String>> {

    public CreateAlarmsDialog(final AlarmClient model) {
        super();

        setTitle(Messages.createAlarmsDialogTitle);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final Label hintText = new Label(Messages.createAlarmsDialogHint);

        final TextArea textArea = new TextArea();
        textArea.setPrefWidth(800);

        final Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        final Consumer<String> updateError = newError -> {
            errorLabel.setText(newError);
            getDialogPane().lookupButton(ButtonType.OK).setDisable(newError != null);
        };

        final Label pvCountLabel = new Label();
        pvCountLabel.setText(MessageFormat.format(Messages.creatingAlarms, 0));

        final Set<String> allPvs = AlarmTreeHelper.getLeafNames(model.getRoot());
        textArea.textProperty().addListener(_ -> {
            final List<String> pvs = splitNames(textArea.getText());

            pvCountLabel.setText(MessageFormat.format(Messages.creatingAlarms, pvs.size()));

            // Disable OK if no alarms being added
            if (pvs.isEmpty()) {
                updateError.accept("");
                return;
            }

            // Show error and disable OK if any PV already exists in the config
            boolean ok = true;
            for (final String pv : pvs) {
                if (allPvs.contains(pv)) {
                    ok = false;
                    updateError.accept(MessageFormat.format(Messages.specificPvAlreadyExists, pv));
                    break;
                }
                if (pvs.stream().filter(x -> x.equals(pv)).count() > 1) {
                    ok = false;
                    updateError.accept(MessageFormat.format(Messages.pvEnteredMoreThanOnce, pv));
                    break;
                }
            }
            if (ok) {
                updateError.accept(null);
            }
        });

        getDialogPane().setContent(new VBox(hintText, textArea, pvCountLabel, errorLabel));
        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return splitNames(textArea.getText());
            }
            return null;
        });

        MultiplePVAutocompleteMenu.INSTANCE.attachField(textArea);

        updateError.accept(""); // disable OK by default as text field is empty

        textArea.requestFocus();
    }

    private static List<String> splitNames(final String names) {
        return names.isEmpty()
                ? List.of()
                : List.of(MultiplePVAutocompleteMenu.DELIMITER_PATTERN.split(names));
    }
}
