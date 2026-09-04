---
navigation:
  parent: index.md
  title: ヒント
  icon: minecraft:writable_book
  position: 20
---

# ヒント
## マルチブロックでアイテムと液体を含むレシピをクラフトしたい
サブネットワークを使うことでこれらのクラフトをうまく行えます。このmodはサブネットワーク先の機械の回路番号も変更します。  
次の図を見てください。

<GameScene zoom="4" background="transparent" interactive={true}>
<ImportStructure src="../structure/provider_interface_storage.snbt" />

<BoxAnnotation color="#dddddd" min="2.7 0 0" max="3 1 1">
        インターフェース
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="1 0 0" max="1.3 2 1">
        ストレージバス
  </BoxAnnotation>

<BoxAnnotation color="#dddddd" min="0 0 0" max="1 2 1">
        バス・ハッチ
  </BoxAnnotation>

<IsometricCamera yaw="200" pitch="30" />
</GameScene>

全てのストレージバスの「アクセスできないアイテムの報告」を「はい」にしておきます。  
無効のままだとブロッキングモードがうまく働かないことがあります。  
![](../pic/storage_bus_setting.png)

サブネット上のMEストッキングバスとハッチの回路番号も変更されます。
