# 1.20.1 Changelog
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)

## [Unreleased]

## [1.2.11]
### Added
 - Expanded AE's Smart Blocking takes into account differences in Programmed Circuits. (by @Zatone0) [#56](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/56)

## [1.2.10]
### Added
 - Supports Giga Pattern Provider (Expanded AE).

### Fixed
 - Fixed an issue where the card would behave as though it was not inserted when the pccard.disableSlot flag was enabled.

## [1.2.9]
### Fixed
 - card_programmed_circuit causes the GUI interface of adv_pattern_provider to not open [#48](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/48)

### Changed
 - Specify the version range of mae2.

## [1.2.8] 2025-02-16
### Added
 - Add 1.21 style texture. Can be applied from resource packs. (by @xiaoleng5261).
 - New lang zn_cn (by @xiaoleng5261).

## [1.2.7] - 2025-02-02
### Fixed
 - Explicit Forge version range.
 - PC is not set when Storage Bus face non GT machine.

## [1.2.6] - 2025-12-24
### Fixed
 - Changed some code handling. Maybe the bug that didn't work properly has been fixed?

## [1.2.5] - 2025-11-06
### Fixed
 - Expanded AE makes optional.

## [1.2.4] - 2025-10-28
### Changed
 - Compatible with Expanded AE 1.2.5 and later. (by @ko-lja)

## [1.2.3] - 2025-10-26
### Added
 - Support AE2 15.4.10 & EMI

### Fixed
 - Fixed trying to read unloaded configs (by @pedroksl)

## [1.2.2] - 2025-10-16
### Fixed
 - Fixed a bug where circuit numbers were not set in certain environments.

## [1.2.1] - 2025-10-11
### Fixed
 - [Bug] EMI Recipe assign doesnt add circuit anymore [#38](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/38)

## [1.2.0] - 2025-10-06
### Added
 - Add a JVM argument to control the addition of upgrade slots. Check the README.

### Fixed
 - Resolve the conflict with Expanded AE. (Thanks, ko-lja.)

## [1.1.1] - 2025-09-22
### Fixed
 - It may not function correctly on the deep subnet.

## [1.1.0] - 2025-09-21
### Added
 - It works with deeper recursive networks [#34](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/34)
 - new config file (pccard-common.toml)
 - new config entry (search_depth)

### Known Issues
 - Currently incompatible with Expanded AE. This will be resolved in a future release.

## [1.0.18] - 2025-08-29
### Fixed
 - 1.0.17 is not working.

## [1.0.17] - 2025-08-15
### Fixed
 - MAE2 pattern p2p tunnel is working again. [#30](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/30)

## [1.0.16] - 2025-07-23
### Added
 - MEGA Pattern Provider (MEGA Cells) support.

## [1.0.15] - 2025-05-06
### Fixed
- Mod causing crash [#21](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/21)
- ワールドリログ時に進行していた自動クラフトが正常に動作しない [#22](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/22)

## [1.0.14] - 2025-04-09
### Added
 - Add note about multiple pattern providers on blocking mode to the guide. [#18](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/18)
 - Support Expanded AE [#20](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/20)

### Fixed
 - Wrong GTM version range (1.6.0 and above now) [#17](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/17)
 - Tool Box not showing [#19](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/19)

## [1.0.13] - 2025-03-25
### Added
 - Localize tooltip.

### Fixed
 - No guide included in the release [#14](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/14)

## [1.0.12] - 2025-03-16
### Added
 - Add guide. [#12](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/12)
 - it doesn't work when combine it with AE2 subnet [#10](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/10)

## [1.0.11] - 2025-02-23
### Fixed
 - Does not work properly across multiple pattern providers using crafting co-processing units [#11](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/11)

### Removed
 - Support for older GregTech versions.

## [1.0.10] - 2025-02-16
### Added
 - JEI integration config. [#8](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/8)

## [1.0.9] - 2025-02-08
### Fixed
 - Circuit is not placed with EMI. [#9](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/9)

## [1.0.8] - 2025-01-27
### Added
 - Advanced AE pattern provider support [#6](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/6)

### Fixed
 - Not working in the input hatch. #7 [#7](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/7)

## [1.0.7] - 2025-01-18
### Fixed
 - Not working with AdvancedAE CPU [#5](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/5)

## [1.0.6] - 2024-12-28
### Fixed
 - ExtendedAE pattern provider part [#3](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/3)
 - Crash with GregTech 1.6.1 [#4](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/4)

## [1.0.5] - 2024-12-27
### Fixed
 - Not working properly [#2](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/2)

## [1.0.4] - 2024-12-24
### Fixed
 - Broken cable pattern provider [#1](https://github.com/yuuki1293/ProgrammedCircuitCard/issues/1)

## [1.0.3] - 2024-12-13
### Fixed
 - Ex pattern provider has no upgrade slots.
 
## [1.0.2] - 2024-12-11
### Added
 - Support GregTech 1.4+
 - JEI/EMI places the circuit.

## [1.0.1] - 2024-12-03
### Fixed
 - Not working for MultiBlock Item Bus.

## [1.0.0] - 2024-12-02
 - First release!
