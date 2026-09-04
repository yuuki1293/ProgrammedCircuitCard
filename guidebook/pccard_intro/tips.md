---
navigation:
  parent: index.md
  title: Tips
  icon: minecraft:writable_book
  position: 20
---

# Tips

## Crafting Recipes with Both Items and Fluids in a Multiblock

Yu can use a subnet to handle these crafting recipes more efficiently.  
This mod also changes the circuit number of machines within the subnet.  
Take a look at the following diagram.

<GameScene zoom="4" background="transparent" interactive={true}>
<ImportStructure src="../structure/provider_interface_storage.snbt" />

<BoxAnnotation color="#dddddd" min="2.7 0 0" max="3 1 1">
        Interface
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 2 1">
        Storage Bus
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 0 0" max="1 2 1">
        Bus & Hatch
  </BoxAnnotation>

<IsometricCamera yaw="200" pitch="30" />
</GameScene>

Set "Report inaccessible items" to "Yes" for all storage buses.  
If left disabled, the blocking mode may not function properly.  
![](../pic/storage_bus_setting.png)

The circuit numbers for the ME Stocking Bus and Hatch on the subnet will also be changed.
