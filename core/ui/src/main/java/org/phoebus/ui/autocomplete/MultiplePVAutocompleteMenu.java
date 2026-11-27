package org.phoebus.ui.autocomplete;

import javafx.scene.control.TextInputControl;
import org.phoebus.framework.autocomplete.PVProposalService;
import org.phoebus.framework.autocomplete.Proposal;

import java.util.regex.Pattern;


/** Auto-completion menu for multiple PV names
 *
 * Allows auto-completion of multiple PVs within a single text
 * field, separated by delimiters specified by DELIMITER_PATTERN
 *
 *  @author Percy Baker
 */
public class MultiplePVAutocompleteMenu extends AutocompleteMenu
{
    public static final MultiplePVAutocompleteMenu INSTANCE = new MultiplePVAutocompleteMenu();

    public static final Pattern DELIMITER_PATTERN = Pattern.compile("[\\s;,]+");

    private MultiplePVAutocompleteMenu()
    {
        super(PVProposalService.INSTANCE);
    }

    @Override
    protected String getCompletionText(final TextInputControl field) {
        final String[] split = DELIMITER_PATTERN.split(field.getText());
        return split.length == 0 ? "" : split[split.length - 1];
    }

    @Override
    protected void autocompleteAction(final TextInputControl field, final String text, final Proposal proposal) {
        final String value = field.getText(0, field.getText().length() - text.length()) + proposal.apply(text);
        field.setText(value);
        field.positionCaret(value.length());
        invokeAction(field);
    }
}
