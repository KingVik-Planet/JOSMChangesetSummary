# JOSM Changeset Summary (#CUS_TT)

A JOSM plugin that fills the upload comment with a short summary of what the
mapper changed, so changesets are properly described.

## What it does (v0.1)
- When you click **Upload**, it counts added / modified / deleted buildings,
  highways, waterways, POIs and other tagged features.
- It puts the result in "Provide a brief comment for the changes you are uploading".
  The mapper can read and edit it before uploading.
- If the existing comment is already descriptive, it is kept as is.
- It always makes sure `#CUS_TT` are present for usage tracking(never duplicated).
- It stays within the 255-character limit by shortening the summary, never the hashtags.

## Build
Needs JDK 17+ and Gradle (IntelliJ IDEA Community works well).

    gradle build

The plugin jar is created in `build/dist/`.

## Not yet done
- Tag-level detail (added / modified / deleted tags)
- Squared buildings and spiky-building fixes
- GitHub plugin-site file so JOSM can install from a link
