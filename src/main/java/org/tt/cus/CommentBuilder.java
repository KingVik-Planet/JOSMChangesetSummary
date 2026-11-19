package org.tt.cus;

import java.util.regex.Pattern;

/**
 * Decides what the final comment looks like.
 * - keeps whatever text is already there
 * - appends the summary only when the existing text is just hashtags / very short
 * - makes sure #tt_event and #CUS_TT are present (never duplicated)
 */
public final class CommentBuilder {

    static final String EVENT_TAG = "#tt_event";
    static final String USAGE_TAG = "#CUS_TT";
    static final int MAX_LENGTH = 255; // OSM changeset comment limit

    private static final Pattern HASHTAG = Pattern.compile("#\\S+");

    private CommentBuilder() {
    }

    public static String build(String existing, String summary) {
        String base = existing == null ? "" : existing.trim();

        // "Enough" = has at least a few real words besides hashtags
        String withoutTags = HASHTAG.matcher(base).replaceAll("").trim();
        boolean descriptive = withoutTags.split("\\s+").length >= 4 && !withoutTags.isEmpty();

        StringBuilder tags = new StringBuilder();
        appendIfMissing(tags, base, EVENT_TAG);
        appendIfMissing(tags, base, USAGE_TAG);
        String tagPart = tags.toString().trim();

        String text = base;
        if (!descriptive && summary != null && !summary.isEmpty()) {
            text = (base.isEmpty() ? "" : base + " ") + summary + ".";
        }
        String result = (text + " " + tagPart).trim();

        // Respect the 255-char limit: shorten the summary, never the hashtags.
        if (result.length() > MAX_LENGTH) {
            int room = MAX_LENGTH - tagPart.length() - base.length() - 3;
            String shortSummary = room > 10 && summary != null
                    ? summary.substring(0, Math.min(summary.length(), room)).trim() + "..."
                    : "";
            text = (base.isEmpty() ? "" : base + " ") + shortSummary;
            result = (text + " " + tagPart).trim();
        }
        return result.length() > MAX_LENGTH ? result.substring(0, MAX_LENGTH) : result;
    }

    private static void appendIfMissing(StringBuilder sb, String text, String tag) {
        if (!text.toLowerCase().contains(tag.toLowerCase())) {
            sb.append(' ').append(tag);
        }
    }
}
