# project-0016-真忠诚-Tweakeroo补货桥接

Tweakeroo 补货桥接模组（Fabric 1.21.11，客户端）。

- 真·忠诚三叉戟碎裂触发不死图腾动画时，检测手中带
  `bloodsoil:true_loyalty` 的三叉戟，调用 Tweakeroo 自带的
  `PlacementTweaks.cacheStackInHand` + `onProcessRightClickPost`
  走 `tweakHandRestock` 补货。
- 补货逻辑完全留在 Tweakeroo 内：只有安装了 Tweakeroo 且开启
  `tweakHandRestock` 才会补货，桥接模组不实现自己的补货逻辑。
- 不死图腾与真·忠诚互不串货：Tweakeroo 按物品+组件精确匹配，
  桥接只处理三叉戟。

构建：

```powershell
.\gradlew.bat build
```

产物：`build/libs/TrueLoyalty-Tweakeroo-Bridge-1.0.0.jar`，
放入客户端 mods 目录。
