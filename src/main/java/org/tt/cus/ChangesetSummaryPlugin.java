package org.tt.cus;

import org.openstreetmap.josm.actions.UploadAction;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.Logging;

/**
 * Entry point. JOSM creates this class when the plugin loads.
 * It registers one upload hook that fills the changeset comment with a summary.
 */
public class ChangesetSummaryPlugin extends Plugin {

    public ChangesetSummaryPlugin(PluginInformation info) {
        super(info);
        try {
            // false = run as a normal (early) hook, before the upload dialog is shown
            UploadAction.registerUploadHook(new UploadCommentHook(), false);
        } catch (RuntimeException | LinkageError e) {
            // A future JOSM may change this API. Do not crash JOSM; just log it.
            Logging.warn("ChangesetSummary: could not register upload hook: " + e);
        }
    }
}
