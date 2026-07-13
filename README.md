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
By executing these tasks, the guide's resource pack can be updated during running. don't forget F3+T
### Generate resource pack for guide.
generate to [build/guides](build/guides).
```groovy
gradlew genGuideResources
```
### Copy resource packs to run
copy [build/guides](build/guides) to [run/resourcepacks](run/resourcepacks).  
depends on `genGuideResources`
```groovy
gradlew copyResourcePacks
```

### Make release
1. commit with "update version to vx.y.z"
2. add tag "v1.2x.x-x.y.z"
3. push with tag

## Other

### Disable adding upgrade slots
If you can't open Pattern Provider GUI,
Add the following to the JVM arguments.
```
-Dpccard.disableSlot
```
