---
navigation:
  parent: index.md
  title: Programmed Circuit Card
  icon: pccard:card_programmed_circuit
  position: 10
categories:
  - tools
item_ids:
  - pccard:card_programmed_circuit
---

# Programmed Circuit Card

A Pattern Provider with this card inserted will change the circuit number instead of pushing a programmed circuit into
GregTech machines.

## Recipe

<RecipeFor id="pccard:card_programmed_circuit" />

## Pattern Registration

Include the programmed circuit in the pattern. It will work even if there is no programmed circuit in the network.  
Manually placing it every time is inconvenient, so the behavior has been modified to include the programmed circuit when
registering recipes from JEI or EMI.  
If the programmed circuit is not being placed, or if you want to disable this feature, check the `jei_integration`
setting in the config.  
![Pattern Registration](../pic/encode_pattern.png)

## Pattern Provider

Place the Programmed Circuit Card in the upgrade slot of the Pattern Provider.  
**Don't forget to set the Pattern Provider to blocking mode.**  
![Pattern Provider](../pic/pattern_provider.png)

### NOTE

> - Patterns without a programmed circuit will be treated as circuit number 0.
> - Blocking modes may not work well if multiple pattern providers are adjacent to one machine.

## Crafting Order

You can order it just like a normal item. The programmed circuit will not appear in the crafting plan.  
If it does appear, check whether the Programmed Circuit Card is installed in the Pattern Provider.
