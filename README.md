# TachiSYA

Personal fork of TachiyomiSY.

## Download
Download [here](https://github.com/Amqx/TachiyomiSYPreview/releases/latest).

I won't be maintaining a stable release, so please use preview. Furthermore, if anything goes wrong,
don't expect me to fix it on this fork unless it personally impacts my usage.

## Stuff I've Added/ Reverted

Below is a small list of the stuff I've modified in this fork. If you don't like them
then go back to upstream. I just personally couldn't stand these changes.

### Defaults (Changed from upstream SY)
- Changed app names from `TachiyomiSY` to `TachiSYA`
- Changed app id from `eu.kanade.tachiyomi.sy` to `org.ryst.tachiyomi.sy`
  - Behaviour expecting upstream SY now points to my forks of SY, including:
    - Release/ Preview update checks
    - GitHub links
- Removed a bunch of the settings menu links to Mihon's socials

### Specific Features Added/ Removed
- Replace the popular page of extensions with a modifiable home page
  - Popular (default behaviour to match upstream)
  - Latest (mirrors latest button)
  - Custom filter option (if source supports filter)
- Get rid of large update harming sources warnings
- Full migration to M3E (or as much as I can as it releases)
  - Currently implemented:
    - Shapes
    - Navigation bars/ rails
    - Animations
    - Buttons and colors
    - Loading spinners
    - Progress indicators


### To-do list/ Stuff I want to change
- M3E:
  - Full reader UI overhaul
  - Filters/ in-reader settings menu overhaul
- Maybe migrating to Gradle v9
- Maybe updating the statistics page with more fun stuff
  - Genre clouds
  - Ghosting statistics (stale/ unread items)
  - Source diversity
  - Physical comparisons (shelf height, scroll distance, trees saved, etc.)
- If I run into anything during use that I dislike I will probably get rid of it.
- If I run into anything during use that I want I will probably add it.
