<p align="center">
  <img docs="docs/icon.png" alt="ChangesetSummary icon" width="128">
</p>

# ChangesetSummary — a JOSM plugin

**Every changeset explains itself.** ChangesetSummary writes a short, plain-language
summary of what you changed into the JOSM upload comment, so reviewers, validators and
other mappers can see what was done without opening the edit.

> Status: early version (0.1.0). It works, and it is being extended. See the [roadmap](#roadmap).

---

## The problem

When a mapper uploads from JOSM, the changeset comment is often missing or says very
little. Many changesets carry only the default text added by a tool, such as a
Tasking Manager or MapRoulette hashtag like `#hotosm-project-1234`.

That hashtag tells you **which project** the edit belongs to. It does not tell you
**what was actually done**.

For the people who check the work, this causes real problems:

- **Reviewers** open each changeset and compare the data to find out what changed.
- **Validators** cannot quickly tell whether an edit added buildings, changed roads or only fixed tags.
- **Other mappers** who find a changeset later in OSM history cannot tell why the data changed.
- **Mapping managers** have no simple way to see the kind of work each mapper does.

Asking every mapper to write a good description every time rarely works. People are busy,
the upload dialog is easy to skip, and the default hashtag looks "good enough".

## The solution

ChangesetSummary does the writing for you, at the moment it matters: when you click **Upload**.

1. It looks at the edits you are about to upload.
2. It counts what you added, modified and deleted: **buildings, highways, waterways, POIs** and other tagged features.
3. It puts a short sentence into the *"Provide a brief comment for the changes you are uploading"* box, for example:

   ```
   Added 12 buildings, modified 3 highways and 2 waterways, deleted 1 POI. #tt_event #CUS_TT
   ```

4. **You stay in control.** The text is a suggestion in the normal comment box. Read it, edit it, add your own words, or delete it before you upload.

### How it treats the existing comment

| Situation | What the plugin does |
|---|---|
| Comment is empty | Fills in the summary and the hashtags |
| Comment has only hashtags, such as a Tasking Manager default | Keeps the hashtags and **adds** the summary |
| Comment is already descriptive (a few real words) | Leaves your text alone and only makes sure the hashtags are present |
| Comment would exceed OSM's 255-character limit | Shortens the summary, never the hashtags |

### Hashtags

- `#CUS_TT` is added if it is missing (For usage Mornitoring). It stands for *Changeset Upload Summary* and lets the project count where and how often the plugin is used, for example with [OSMCha](https://osmcha.org/) or the OSM changeset API.
- Neither hashtag is ever duplicated, and you can remove them in the comment box before you upload.

### Privacy and safety

- Everything runs **locally inside JOSM**. The plugin does not contact any server and sends no data anywhere.
- It never blocks an upload. If something goes wrong, the upload carries on as normal and a `ChangesetSummary:` line is written to the JOSM log.

---

## What reviewers, validators and mappers gain

- A readable description on **every** changeset, not only on careful ones.
- Faster review: the kind and size of an edit is visible straight from the changeset list.
- A more useful edit history for everyone who looks at the data later.
- Consistent hashtags for tracking.

---

## Install

### From the JOSM plugin list (once listed)

1. In JOSM open **Preferences → Plugins**.
2. Click **Download list**, search for **ChangesetSummary**, tick it and click **OK**.
3. Restart JOSM.

### Manual install

1. Download `ChangesetSummary.jar` from the [latest release](https://github.com/KingVik-Planet/JOSMChangesetSummary/releases/latest).
2. Copy it into your JOSM `plugins` folder (on Windows: `%APPDATA%\JOSM\plugins\`).
3. Restart JOSM, open **Preferences → Plugins**, tick **ChangesetSummary** and restart again.

Requires a JOSM version that supports plugin API 19481 or newer, and Java 17 or newer.

## How to use it

Just map as usual. When you click **Upload**, check the comment box, edit it if you like, and upload.

---

## Roadmap

Done in 0.1.0:

- [x] Summary of added / modified / deleted buildings, highways, waterways, POIs and other features
- [x] Preview in the upload comment box, fully editable
- [x] #CUS_TT` hashtags, with no duplicates
- [x] 255-character limit handling

Planned:

- [ ] Tag-level detail, such as "added 5 name tags", "fixed 2 highway tags", "deleted wrong tags"
- [ ] Detection of squared (orthogonalised) buildings
- [ ] Detection of fixed spiky buildings
- [ ] A settings page (switch on and off, choose hashtags, summary length)
- [ ] Automated tests

Ideas and bug reports are welcome in the [Issues](https://github.com/KingVik-Planet/JOSMChangesetSummary/issues) tab.

---

## Build from source

Requires JDK 17 and Gradle (the wrapper is included).

1. Download <https://josm.openstreetmap.de/josm-tested.jar> and save it as `libs/josm-tested.jar`.
2. Run `./gradlew build` (Windows: `gradlew.bat build`).
3. The plugin is created at `build/libs/ChangesetSummary.jar`.

## Maintainer

Kingsley Chika Chukwu — [KingVik-Planet](https://github.com/KingVik-Planet)
