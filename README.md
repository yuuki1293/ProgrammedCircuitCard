![](https://cf.way2muchnoise.eu/1150540.svg)
# Programmed Circuit Card
This mod is an add-on for GregTech and AE2.  
Port Neeve's AE2 Programmed Circuit Card to 1.20.1.

## Description
Programmed Circuit Card can be inserted into Pattern Provider. When pushing a recipe that requires a Programmed Circuit, set the GT machine number.  

## Images
![](https://github.com/user-attachments/assets/c16816e1-2d03-453a-abd0-14a539bded3d)
![](https://github.com/user-attachments/assets/a6e7a248-fc94-4c8b-a595-5bde17a148e7)
![](https://github.com/user-attachments/assets/fa83040f-3244-481b-a941-c63aedd7f713)

## Credits
- [GregTechCEu Modern](https://www.curseforge.com/minecraft/mc-mods/gregtechceu-modern) (by KilaBash)
- [Applied-Energistics-2](https://www.curseforge.com/minecraft/mc-mods/applied-energistics-2) (by thetechnici4n)
- [Neeve's AE2: Extended Life Additions](https://www.curseforge.com/minecraft/mc-mods/nae2) (by notmywing)

## Supported Addons
- [ExtendedAE](https://www.curseforge.com/minecraft/mc-mods/ex-pattern-provider) (by GlodBlock)
- [AdvancedAE](https://www.curseforge.com/minecraft/mc-mods/advancedae) (by pedroksl)
- [MAE2](https://www.curseforge.com/minecraft/mc-mods/modern-ae2-additions) (by AE2Enthusiast)
- [Expanded AE](https://www.curseforge.com/minecraft/mc-mods/expanded-ae) (by Kolja)
- [MEGA Cells](https://www.curseforge.com/minecraft/mc-mods/mega-cells) (by ninety)

## Develop
### Brief
1. Run minecraft with `Client`.

### Test with Monilabs
Comment out this line.
```
modImplementation (libs.ae2) // AE2
```

Uncomment those lines.
```
modCompileOnly (libs.ae2cosmo) // AE2 cosmolite
modImplementation (libs.monilabs) // AE2 monilabs
modRuntimeOnly (libs.kubejs) // depended by monilabs
modRuntimeOnly (libs.rhino) // depended by KubeJS
modRuntimeOnly (libs.melody) // depended by FancyMenu
modRuntimeOnly (libs.konkrete) // depended by FancyMenu

modRuntimeOnly (libs.chloride) // depended by monilabs
modRuntimeOnly (libs.embeddium) // depended by monilabs
modRuntimeOnly (libs.fancymenu) // depended by monilabs
modRuntimeOnly (libs.oculus) // depended by monilabs
```

### Make release
1. Update version in [Constants.kt](buildSrc/src/main/kotlin/Constants.kt).
2. Update [Changelog](CHANGELOG.md).
3. Run `Deploy` workflow.

## Other

### Disable adding upgrade slots
If you can't open Pattern Provider GUI,
Add the following to the JVM arguments.
```
-Dpccard.disableSlot
```
