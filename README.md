# TachiSYA

Amqx's personal fork of TachiyomiSY.

## Download
Download [here](https://github.com/Amqx/TachiyomiSYPreview/releases/latest).

I won't be maintaining a stable release. After all, what's life on SY without only using the Preview
builds 🥀. Furthermore, if anything goes wrong, don't expect me to fix it on this fork unless it
personally impacts my usage.

## Stuff I've Added/ Reverted

Below is a small list of the stuff I've modified in this fork. If you don't like them
then go back to upstream. I just personally couldn't stand these changes.

### Defaults (Changed from SY)
 - Changed app id from `eu.kanade.tachiyomi.sy` to `org.ryst.tachiyomi.sy`
   - Behaviour expecting upstream SY now point to my forks of SY, including:
     - Release/ Preview update checks
     - GitHub links
 - Removed a bunch of the settings menu links to Mihon's socials

### Specific Features Added/ Removed
 - Replace the popular page of extensions with a modifiable home page
   - Popular (default behaviour to match upstream)
   - Latest (clones latest button)
   - Custom filter option (if source supports filter)
 - Get rid of large update harming sources warnings


### To-do list/ Stuff I want to change
 - Either fully convert to Material 3 Expressive or fully revert to Material 3. Right now, SY is an
   mix of both versions, causing it to look disjointed/ wrong in all the small nooks/ crannys.
 - If I run into anything during use that I dislike I will probably get rid of it.
 - If I run into anything during use that I want I will probably add it.
