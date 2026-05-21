# Ascendant Armory

**ARPG gear progression for vanilla items.** Vanilla progression ends at netherite + max enchants. Every diamond sword you find is identical to the last, no god rolls, no reason to keep hunting. Most "RPG loot" mods solve this by adding hundreds of custom items, tiers, and rarities that fight with vanilla and every other mod you have installed.

Ascendant Armory does the opposite: your diamond sword stays a diamond sword. But now it can be *Honed*, *Empowered*, *Ascendant*, *Mythic*, or *Divine*, each tier earned at the anvil, each tier adding a randomly-rolled trait. Deep late-game progression and build crafting without breaking anything else.

- 5 ascension tiers (*Honed* → *Divine*), each adds a trait slot + enchantment slot
- 30+ traits across weapon, ranged, armor, tool pools
- Trait donation, gear salvage, deterministic anvil rerolls, smithing-aware trait fill
- Vanilla-friendly: doesn't add gear, doesn't replace enchants, doesn't fight other mods

---

## How it works

### Cores
- **Ascension Core**: Use in an anvil to level up gear. Each level adds a trait + enchantment slot and powers up existing traits.
- **Chaos Core**: Use in an anvil to reroll the weakest traits on leveled gear. Stack more cores to reroll more at once. Optional *Gamble Mode* (config) makes rerolls swingier — traits can roll above their normal max, or bust to the minimum.

![Ascension and Chaos cores](https://cdn.modrinth.com/data/cached_images/b774ebc3efc40c416fc25d93889a49b83b4335d4.png)

### Tiers
| Level | Name | Trait slots | Enchant slots | Ascension Core Cost |
|-------|------|-------------|---------------|---------------------|
| 1 | Honed | 1 | 1 | 1 |
| 2 | Empowered | 2 | 2 | 4 |
| 3 | Ascendant | 3 | 3 | 16 |
| 4 | Mythic | 4 | 4 | 32 |
| 5 | Divine | 5 | 5 | 64 |

Trait slot count is also capped by material (wood = 1, iron = 3, diamond = 4, netherite = 5). Smithing-upgrade your gear and the new slot auto-fills with a fresh trait.

![Tiered Diamond Weapons](https://cdn.modrinth.com/data/cached_images/3be44c96fc36844c08b333f75c4043f10bddc712.png)
![Anvil Upgrade Preview](https://cdn.modrinth.com/data/cached_images/b8aa01b81c3f673ed8d8eb268d1a7d10dee9eb96.png)

### Trait ranks
Traits are ordered by power: first trait scales 5x at L5, second 4x, etc. A god-roll = chasing the right primary trait. Use Chaos Cores to reroll the weak ones at the bottom.

### Trait donation
Combine two of the same item in an anvil to transfer a trait. Surgical alternative to chaos rerolling.

### Salvage
Place leveled gear alone in an anvil — no second item, no rename — to break it down and recover a portion of the Ascension Cores invested in it. Costs XP. Lets you move your investment to a better base item instead of stranding cores in old gear.

### Visual feedback
Tier-colored inventory badges, equipped item particle auras, detailed tooltips with trait values + next-level preview + cost-to-level.

---

## How to get cores

- **Crafting**: Both Ascension and Chaos Cores have crafting recipes (see in-game JEI / wiki).
- **Mob drops**: Hostile mobs can drop cores based on carried equipment, with optional scaling from supported difficulty mods/datapacks.
- **Chest loot**: Generates in dungeons, mineshafts, strongholds, temples, shipwrecks, villages, trial chambers, end cities, bastion treasure, and ancient cities.
- **Pre-ascended loot** (optional): Looted gear can spawn already leveled, ready to use.

All drop rates and weights configurable in `config/ascensioncores.properties`.

---

## Traits

Every proc-based trait has its own particle effect and sound.

<details>
<summary><b>Full trait list (click to expand)</b></summary>

**Damage shapers**
- **Critical Damage**: extra damage on vanilla crits.
- **Execution Damage**: extra damage below 35% target HP.
- **Ambush Damage**: extra damage from behind.
- **Opening Damage**: extra damage on near-full HP target.
- **Chain Damage** (melee): bonus per consecutive hit, up to 5 stacks.
- **Overcharge Damage** (ranged): extra damage on full-draw shots.

**On-hit procs**
- **Frostbite** (melee): chance for Slowness II, 3s.
- **Venom**: chance for Poison II, 5s.
- **Shock**: chance for Weakness, 3s.
- **Wither**: chance for Wither, 4s.
- **Pinning** (ranged, full-draw): chance to root with Slowness VII, 2s.
- **Heal Suppress**: chance to halve target healing, 4s.

**Offensive utility**
- **Life Steal**: % of damage returns as healing.
- **Reach** (melee): extended attack range.
- **Attack Speed** (melee): faster swing recovery.
- **Armor Shred** / **Toughness Shred**: temporarily reduce target armor.

**Movement & economy**
- **Sprint Speed**, **Jump Height**, **Stealth**, **Repair Discount**, **Experience Bonus**, **Consuming Speed**, **Stamina**.

**Defensive (armor)**
- **Evasion**: dodge chance.
- **Deflection**: reflect projectiles.
- **Effect Resist**: shorter debuff duration.
- **Melee Resistance**: % damage reduction on melee hits (capped 50%).
- **Natural Regeneration**: faster regen while saturated.
- **Max Health**: more HP.
- **Low Health Guard**: damage reduction below 35% HP.
- **Sneak Guard**: damage reduction while sneaking.
- **Standstill Guard**: damage reduction while still.
- **Emergency Healing**: heal % max HP on near-death (30s cooldown).
- **Tamed Resistance**: reduces damage from your own pets.

</details>

---

## Compatibility

**Required**: Fabric API + [Pufferfish's Attributes](https://modrinth.com/mod/attributes).

<details>
<summary><b>Optional integrations + plays-nice list (click to expand)</b></summary>

**Auto-integrates when present:**
- [JEI](https://modrinth.com/mod/jei) — recipe lookup.
- [Progression Reborn](https://modrinth.com/mod/progression-reborn) — tier-aware material capacity.
- [Artifacts](https://modrinth.com/mod/artifacts) — artifacts roll traits and contribute to procs.
- [Better Vanilla Mobs](https://modrinth.com/mod/better-vanilla-mobs) — mob-tier-aware core drop rates.
- [Hostile Mobs Improve Over Time](https://modrinth.com/datapack/hostile-mobs-improve-over-time) — core drop rates scale for mobs actually improved by the datapack.
- Warband — Warband-stamped mobs add core drop chance based on mob difficulty, squad membership, and leader status. Farm-suppressed Warband mobs are excluded.
- [Farmer's Delight Refabricated](https://modrinth.com/mod/farmers-delight-refabricated) — knives recognized as tools.
- [More Delight](https://modrinth.com/mod/more-delight) — tools recognized.

**Plays nicely with:**
- Enchantments (independent slot system, anvil enforces tier cap).
- Other gear mods (auto-detects modded gear via durability + attribute heuristics).
- Recipe overhauls, biome mods, etc., no known conflicts.

</details>

---

## Commands & config

- `/ascensioncores reload` — hot-reload config.
- `/ascensioncores level get` — show held item's ascension level.
- `/ascensioncores level set <level>` — set held item's ascension level.
- `/ascensioncores info` — list valid traits for the held item pool.
- `/ascensioncores reroll` — reroll all traits on the held item.
- `/ascensioncores trait set <id> [amount]` — force a trait onto the held item.
- `/ascensioncores givecore upgrade <count>` / `/ascensioncores givecore chaos <count>` — give cores.

<details>
<summary><b>Config options (click to expand)</b></summary>

`config/ascensioncores.properties`:

- Max item level (default 5)
- XP + core costs per tier
- Mob/chest drop rates per tier
- Per-integration drop scaling (Better Vanilla Mobs, Hostile Mobs Improve Over Time, Warband)
- Per-pool trait blacklist
- Inventory marker + anvil sound toggles
- Enchantment slot system toggle
- Gear salvage toggle + refund percentage
- Chaos gamble mode toggle

</details>

---

## License

MIT.
