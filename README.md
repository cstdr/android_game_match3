# android_game_match3

## 体验地址

[小游戏下载体验地址](https://github.com/cstdr/android_game_match3/blob/3ac91d6dadb92cdd011de09a618ef2c0a7f20243/app/release/app-release.apk)

## 项目截图
![](https://github.com/cstdr/android_game_match3/blob/main/%E7%B4%A0%E6%9D%90/pic_cover.png?raw=true)
![](https://github.com/cstdr/android_game_match3/blob/main/%E7%B4%A0%E6%9D%90/pic_main.png?raw=true)
![](https://github.com/cstdr/android_game_match3/blob/main/%E7%B4%A0%E6%9D%90/pic_result.png?raw=true)

## 这个项目的意义

1. 自己无聊的时候可以玩
2. 练习Android控件、动画、项目结构等等，后面有新的技术再持续更新这个项目

## 游戏包含什么功能？
2023.2.23更新，昨晚试玩，发现存在一些问题：
1. 图标可以设置成只能相邻两个图标转换，即横向和纵向，最好支持滑动转换.（此功能要保证随机生成的至少有一个可以达到三消条件，每次随机前要加个检查判定）
2. 【完成】补充新图标时，从上到下掉落体验更好。整体结构调整，事先把游戏画布画好，调整内部元素。
3. 【完成】图标第一次点击后要有选中效果。
4. 【完成】三消动画速度快一些，玩起来更爽。（增加体验感的重点）
5. 【完成】快速点击的时候判断可能出错，偶现。

需求：
1. 【完成】主体是一个8*8的三消游戏，点击一个图标和另一个图标可以切换位置；
2. 【完成】切换后横向或者竖向累计三个相同图标，则三个图标消失。三消后，从顶部补充新图标。
3. 因为可以无限切换，所以不会出现操作一次切换永远无法三消的死局情况。但是算法也需要在生成新图标的时候，注意保证场上同样图标至少有3个。
4. 【完成】如果三消后补充的新图标达到三消条件，则自动三消，直到没有符合条件为止，即连击功能。
5. 【完成】图标替换成狂飙角色头像，增加趣味性。（后续可以根据最新热点切换人物头像）
6. 【完成】三消一次积分从0开始+1，累计到顶部积分栏显示。
7. 【完成】首页增加模式选择，"计时模式"为2分钟内分数比拼，"无限模式"为没有时间限制一直玩。（实际玩家测试来看，计时模式更受欢迎，有挑战）
8. 计时模式有排行榜，可以截图并分享到微信。（因为不是好友竞技，该功能比较软肋）
9. 微信登陆模式，登陆后顶部状态栏显示微信昵称和头像等信息。（同上）
10. 【完成】增加三消后的动画效果。（增加体验感的重点）
11. 【完成】增加角色音效。（增加体验感的重点）
12. 增加连续三消的连击效果和积分增加。（增加体验感的重点）
13. 尝试下Taptap的开发者包接入。

