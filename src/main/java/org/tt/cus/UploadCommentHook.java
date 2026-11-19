package org.tt.cus;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.openstreetmap.josm.actions.upload.UploadHook;
import org.openstreetmap.josm.data.APIDataSet;
import org.openstreetmap.josm.gui.io.UploadDialog;
import org.openstreetmap.josm.gui.io.UploadDialogModel;
import org.openstreetmap.josm.tools.Logging;

/**
 * Runs when the mapper clicks Upload.
 *
 * 1. The summary is calculated right away and remembered.
 * 2. It is written into the comment box only when the upload dialog is actually
 *    shown on screen. By then the dialog has loaded the default comment and
 *    hashtags (Tasking Manager / MapRoulette / history), so we add to them
 *    instead of being overwritten.
 *
 * The mapper can still read, edit or delete the text before uploading.
 */
public class UploadCommentHook implements UploadHook {

    private static String pendingSummary;
    private static boolean listenersAdded;

    @Override
    public boolean checkUpload(APIDataSet apiDataSet) {
        try {
            String summary = ChangeSummary.of(apiDataSet).toSentence();
            Logging.info("ChangesetSummary: hook called, summary = '" + summary + "'");
            synchronized (UploadCommentHook.class) {
                pendingSummary = summary;
            }
            SwingUtilities.invokeLater(UploadCommentHook::ensureListeners);
        } catch (RuntimeException | LinkageError e) {
            Logging.warn("ChangesetSummary: " + e);
        }
        return true; // never block an upload because of this plugin
    }

    /** Adds (once) the listeners that fire when the upload dialog is shown. */
    private static void ensureListeners() {
        try {
            if (listenersAdded) {
                return;
            }
            UploadDialog dialog = UploadDialog.getUploadDialog();
            dialog.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    applyPending();
                }
            });
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    applyPending();
                }
            });
            listenersAdded = true;
            // The dialog may already be showing by the time we get here.
            if (dialog.isShowing()) {
                applyPending();
            }
        } catch (RuntimeException | LinkageError e) {
            Logging.warn("ChangesetSummary: " + e);
        }
    }

    /** Writes the remembered summary into the comment box, once per upload. */
    private static void applyPending() {
        final String summary;
        synchronized (UploadCommentHook.class) {
            summary = pendingSummary;
            pendingSummary = null; // use it only once
        }
        if (summary == null) {
            return;
        }
        try {
            UploadDialogModel model = UploadDialog.getUploadDialog().getModel();
            String existing = model.getValue(UploadDialogModel.COMMENT);
            String comment = CommentBuilder.build(existing == null ? "" : existing, summary);
            Logging.info("ChangesetSummary: existing = '" + existing + "', new = '" + comment + "'");
            if (!comment.equals(existing)) {
                model.put(UploadDialogModel.COMMENT, comment);
            }
        } catch (RuntimeException | LinkageError e) {
            Logging.warn("ChangesetSummary: " + e);
        }
    }
}
