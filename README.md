# android_game_match3

## 这个项目的意义

1. 自己无聊的时候可以玩
2. 练习Android控件、动画、项目结构等等

## 游戏包含什么功能？
2023.2.23更新，昨晚试玩，发现存在一些问题：
1. 图标可以设置成只能相邻两个图标转换，即横向和纵向，最好支持滑动转换.
2. 【完成】图标第一次点击后要有选中效果。
3. 【完成】三消动画速度快一些，玩起来更爽。
4. 【完成】快速点击的时候判断可能出错，偶现。
5. 补充新图标时，从上到下掉落体验更好。

需求：
1. 【完成】主体是一个8*8的三消游戏，点击一个图标和另一个图标可以切换位置；
2. 【完成】切换后横向或者竖向累计三个相同图标，则三个图标消失。三消后，从底部补充新图标。
3. 因为可以无限切换，所以不会出现操作一次切换永远无法三消的死局情况。但是算法也需要在生成新图标的时候，注意保证场上同样图标至少有3个。
4. 【完成】如果三消后补充的新图标达到三消条件，则自动三消，直到没有符合条件为止。
5. 【完成】图标替换成狂飙角色头像，增加趣味性。
6. 【完成】三消一次积分从0开始+1，累计到顶部积分栏显示。
7. 【完成】首页增加模式选择，"计时模式"为2分钟内分数比拼，"无限模式"为没有时间限制一直玩。
8. 计时模式有排行榜，可以截图并分享到微信。
9. 微信登陆模式，登陆后顶部状态栏显示微信昵称和头像等信息。
10. 【完成】增加三消后的动画效果。
11. 增加角色音效。
12. 增加连续三消的连击效果和积分增加。
13. 尝试下Taptap的开发者包接入。

