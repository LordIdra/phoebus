/*******************************************************************************
 * Copyright (c) 2018-2022 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.phoebus.applications.alarm.ui.tree;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import org.phoebus.applications.alarm.client.AlarmClient;
import org.phoebus.applications.alarm.model.AlarmTreeItem;
import org.phoebus.applications.alarm.ui.Messages;
import org.phoebus.framework.jobs.JobManager;
import org.phoebus.ui.dialog.DialogHelper;
import org.phoebus.ui.dialog.ExceptionDetailsErrorDialog;
import org.phoebus.ui.javafx.ImageCache;


@SuppressWarnings("nls")
class CreateNodeAction extends MenuItem
{

    /** @param node Node to position dialog
     *  @param model Model where new component is added
     *  @param parent Parent item in alarm tree
     */
    public CreateNodeAction(final Node node, final AlarmClient model, final AlarmTreeItem<?> parent)
    {
        super("Create Node", ImageCache.getImageView(ImageCache.class, "/icons/add.png"));
        setOnAction(event ->
        {
            final NameDialog dialog = new NameDialog(model, parent, "", true);
            dialog.setTitle(Messages.createNodeDialogTitle);
            dialog.setHeaderText(Messages.createNodeDialogHeader);
            DialogHelper.positionDialog(dialog, node, -100, -50);
            final String name = dialog.showAndWait().orElse(null);
            if (name == null) {
                return;
            }

            JobManager.schedule(getText(), monitor -> {
                try {
                    model.addComponent(parent.getPathName(), name);
                } catch (final Exception ex) {
                    ExceptionDetailsErrorDialog.openError(Messages.error, Messages.createNodeFailed, ex);
                }
            });
        });
    }
}
