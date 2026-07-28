---
navigation:
  parent: index.md
  title: Integration with Other Mods
  icon: expatternprovider:ex_pattern_provider
  position: 30
---

# Integration with Other Mods

## Extended AE
* Compatibility with the Extended Pattern Provider.

## Advanced AE
* Compatibility with the Advanced Pattern Provider.

## Expanded AE
* Compatibility with Expanded Pattern Provider.

## Modern AE2 Additions (MAE2)
* Compatibility with Pattern P2P.

## MEGA Cells
* Compatibility MEGA Pattern Provider.

## Applied Flux
* Integration of the upgrade slot.

## GTCEu
* ME Pattern Buffers always provide a blocking toggle and a Normal/Smart/Full mode selector. Normal follows AE2's aggregate-input blocking, Smart permits compatible repeated pushes, and Full waits for every non-circuit buffered input to clear.
* A dedicated one-card slot enables Programmed Circuit Card support. With a card installed, the buffer removes the circuit from decoded pattern inputs and applies that circuit to its shared GTCEu circuit slot.
* A different PCC circuit waits until earlier buffered inputs are consumed or refunded, even when general blocking is disabled. The card can only be inserted or removed while no non-circuit pushed inputs remain.

## JEI/EMI
* Modified behavior to include the programmed circuit when registering recipes.
