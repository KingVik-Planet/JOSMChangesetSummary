package org.tt.cus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.APIDataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

/** Counts what was added, modified and deleted, grouped by feature type. */
public final class ChangeSummary {

    enum Kind {
        BUILDING("building", "buildings"),
        HIGHWAY("highway", "highways"),
        WATERWAY("waterway", "waterways"),
        POI("POI", "POIs"),
        OTHER("feature", "features");

        final String singular;
        final String plural;

        Kind(String singular, String plural) {
            this.singular = singular;
            this.plural = plural;
        }
    }

    private final Map<Kind, Integer> added = new EnumMap<>(Kind.class);
    private final Map<Kind, Integer> modified = new EnumMap<>(Kind.class);
    private final Map<Kind, Integer> deleted = new EnumMap<>(Kind.class);

    public static ChangeSummary of(APIDataSet ds) {
        ChangeSummary s = new ChangeSummary();
        for (OsmPrimitive p : ds.getPrimitivesToAdd()) {
            s.count(s.added, p);
        }
        for (OsmPrimitive p : ds.getPrimitivesToUpdate()) {
            s.count(s.modified, p);
        }
        for (OsmPrimitive p : ds.getPrimitivesToDelete()) {
            s.count(s.deleted, p);
        }
        return s;
    }

    private void count(Map<Kind, Integer> target, OsmPrimitive p) {
        Kind k = classify(p);
        if (k == null) {
            return; // plain nodes that only belong to ways are not worth mentioning
        }
        target.merge(k, 1, Integer::sum);
    }

    /** Returns null for untagged nodes (way vertices). */
    static Kind classify(OsmPrimitive p) {
        if (p.hasKey("building")) {
            return Kind.BUILDING;
        }
        if (p.hasKey("highway") && !p.getType().toString().equals("NODE")) {
            return Kind.HIGHWAY;
        }
        if (p.hasKey("waterway")) {
            return Kind.WATERWAY;
        }
        if (p.getType().toString().equals("NODE") && p.isTagged()) {
            return Kind.POI;
        }
        if (p.isTagged()) {
            return Kind.OTHER;
        }
        return null;
    }

    public boolean isEmpty() {
        return added.isEmpty() && modified.isEmpty() && deleted.isEmpty();
    }

    /** Example: "Added 12 buildings, modified 3 highways, deleted 1 POI". */
    public String toSentence() {
        List<String> parts = new ArrayList<>();
        addPart(parts, "Added", added);
        addPart(parts, "modified", modified);
        addPart(parts, "deleted", deleted);
        return String.join(", ", parts);
    }

    private static void addPart(List<String> parts, String verb, Map<Kind, Integer> counts) {
        List<String> items = new ArrayList<>();
        for (Map.Entry<Kind, Integer> e : counts.entrySet()) {
            int n = e.getValue();
            items.add(n + " " + (n == 1 ? e.getKey().singular : e.getKey().plural));
        }
        if (!items.isEmpty()) {
            parts.add(verb + " " + String.join(" and ", items));
        }
    }
}
