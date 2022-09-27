# PickerView

> ✨ 自己动手丰衣足食

1. 修改自: <https://github.com/Bigkoo/Android-PickerView>

2. 引入

```groovy
repositories {
    mavenCentral()
    maven { url "https://s01.oss.sonatype.org/content/groups/public" }
}
```

```groovy
implementation 'com.github.javakam:widget.wheelview:1.3.0@aar'
implementation 'com.github.javakam:widget.pickerview:1.3.0@aar'
```

3. 修改内容

- 支持 androidx
- setPadding(l,t,r,b) 新增内容视图调整上下左右边距
- setRangDate(startDate, endDate) 现支持"年,月,日,时,分,秒"的限定, 原版仅支持"年月日"
- 关闭了默认的数字文本缩放效果: WheelView.initPaints -> paintCenterText.setTextScaleX(1.1F) -> 1.0F
- 优化返回值显示, 如传入的 type 秒值为 false, 回来结果相应秒值为 00 -> WheelTime.getTime()

## 使用说明(基本没做改变)

`Bigkoo/Android-PickerView`原版说明文件
👉 [README_ORIGIN.md](https://github.com/javakam/PickerView/blob/master/README_ORIGIN.md)

阿里云中国省市县数据接口 👉 <https://blog.csdn.net/jimolangyaleng/article/details/85596748>