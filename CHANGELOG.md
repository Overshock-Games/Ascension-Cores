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
