## 1.4.0

### Added
- **Advancements** — a full ascension tree under its own tab: Ascension Core pickup roots a 5-tier chain (Honed → Empowered → Ascendant → Mythic → Divine), with a Chaos Core branch. Custom dark end-stone background.
- **Per-item kill counter** — leveled weapons quietly track kills landed with them and display the count on the tooltip. Survives anvil upgrades, chaos rerolls, and trait donations.
- **Trait synergies** — three hand-picked trait pairs grant bonus damage when both sit on the same item and the target is in the matching state:
  - **Cryoexecution** (Frostbite + Execution Damage) — +25% damage to slowed targets.
  - **Plague Doctor** (Venom + Heal Suppress) — +25% damage to poisoned targets.
  - **Stormbreaker** (Shock + Wither) — +25% damage to weakened targets.
  - Tooltip shows every trait's synergy partners as a dim "pairs with" hint for discovery; promotes to a soft amber active line under both contributing traits when the pair lights up.
- **Curse traits** — leveling an item to L1 has a chance to permanently mark it cursed: its top trait is amplified (×1.5 by default) in exchange for a permanent attribute downside while held/worn. Four starting curses: Frail (−10% Max HP), Sluggish (−8% Movement Speed), Brittle (−2 Armor), Weak (−10% Attack Damage). On by default; configurable via `enableCurseTraits`, `curseChance`, `curseTraitBoost`.
- New config `lootLevelBumpChance` (default 0.35) and `treasureLootLevelBumpChance` (default 0.50) — single knob per table controlling how rare the higher tiers are.

### Changed
- Reworked the naturally-leveled loot tier formula to a truncated geometric distribution. Consecutive tiers now have a clean constant ratio — fixes the old catch-all where Divine piled up against Mythic and never felt meaningfully rarer.
- Default per-loot-drop distributions (including the chance an item rolls un-leveled at all): normal loot L0 60% / L1 26.1% / L2 9.2% / L3 3.2% / L4 1.1% / L5 0.4%; treasure loot L0 40% / L1 31.0% / L2 15.5% / L3 7.7% / L4 3.9% / L5 1.9%.
- **Removed Deflection trait.** Its projectile-reflection niche was too narrow and overlapped with Evasion (which already negates any damage source including projectiles). Existing items with Deflection are auto-repaired by the new trait-gap repair (see below) when the player's inventory is loaded. Evasion's wording in the wiki is clarified to make its any-source coverage explicit.
- **Auto-repair for trait gaps.** When a player joins, carried leveled gear is checked once: trait IDs that no longer exist (because a trait was removed in an update) are dropped, and any resulting empty slots are refilled with fresh rolls from the appropriate pool. Players never silently lose value when a mod update prunes the trait list.
- Removed the per-trait next-level preview and the "Cost to Level" footer from item tooltips entirely. The anvil's result slot already shows the exact post-upgrade item when you put an Ascension Core in the right slot, so the inventory-tooltip preview was redundant noise. Much cleaner tooltips.

## 1.3.0

### Added
- Added native Warband compatibility for mob core drops.
- **Warband** support (upcoming mod, might be out by the time you read this)
  - Warband-stamped mobs now add Ascension Core drop chance based on Warband difficulty.
  - Warband squad members receive a small additional Ascension Core drop bonus.
  - Warband leaders can add a Chaos Core drop bonus based on Warband difficulty.
  - Farm-suppressed Warband mobs are excluded from Warband-based core drop bonuses.
  - Added config controls for Warband integration:
    - `enableWarbandIntegration`
    - `warbandMinimumDifficulty`
    - `warbandAscensionCoreBaseChance`
    - `warbandAscensionCoreDifficultyChance`
    - `warbandSquadRoleAscensionCoreBonus`
    - `warbandChaosCoreDifficultyChance`

### Fixed
- Fixed Hostile Mobs Improve Over Time compatibility so drop scaling only applies to mobs actually tagged as improved by the datapack.
- Fixed Consuming Speed applying far more strongly than its tooltip percentage implied.
- Fixed Experience Bonus, Natural Regeneration, and Repair Discount using flat dynamic modifiers instead of true percentage modifiers.
- Existing ascended gear now periodically rebuilds outdated Ascension attribute modifiers when held, carried, or equipped by a player.

## 1.2.0

### Added
- **Hostile Mobs Improve Over Time** support — Ascension and Chaos Core drop rates now scale with that datapack's per-player difficulty score. Soft integration; no effect if the datapack isn't installed.
- New config: `enableHostileMobsImproveIntegration`, plus per-level drop-chance tuning.

## 1.1.0

### Added
- **Gear Salvage** — place leveled gear alone in an anvil (no second item, no rename) to break it down and recover a portion of the Ascension Cores invested in it. Move your investment to a better base item instead of stranding cores in old gear.
- **Chaos Gamble Mode** — optional config flag. Makes Chaos Core rerolls swingier: traits can roll above their normal maximum, or bust to the minimum.
- New config: `enableSalvage`, `salvageRefundPercent`, `chaosGambleMode`, `enableEnchantmentSlots` (toggle the whole enchantment-slot system).

### Fixed
- Enchantment-slot cap was never enforced on un-ascended gear — enchanted books could be slapped onto un-leveled gear with no limit. The cap now applies from level 0 up, and only affects enchanted books (anvil repairs and renames are untouched).

## 1.0.1

### Changed
- Buffed Stealth trait.
- Removed Consuming Speed trait from the tool pool.

### Fixed
- Fixed tools using the weapon trait pool.

## 1.0.0

Initial release.
