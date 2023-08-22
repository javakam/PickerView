# PickerView

> 🌴自己动手丰衣足食

> 2023年8月22日 17:06:47 更新 dependencies
> 
> 2023年5月17日 11:40:00 更新了`app.assets`下的`province.json`文件

> 【注】在使用`地区选择器`时, 如果后台给了地址接口, 建议参照`app.assets`下的`province.json`统一好格式。如果后台没有接口, 可使用
> Idea打开项目<https://github.com/javakam/GetDistricts>, 执行`GetDistricts.main`方法即可下载最新的`province.json`
> 不过需要先申请高德Key(申请地址`https://lbs.amap.com/api/webservice/guide/api/district/`)

#### 1. 修改自: <https://github.com/Bigkoo/Android-PickerView>

#### 2. 引入

```groovy
repositories {
    mavenCentral()
    maven { url "https://s01.oss.sonatype.org/content/groups/public" }
}
```

```groovy
implementation 'com.github.javakam:widget.wheelview:1.5.0@aar'
implementation 'com.github.javakam:widget.pickerview:1.5.0@aar'
```

#### 3. 修改内容

- 支持 androidx
- setPadding(l,t,r,b) 新增内容视图调整上下左右边距
- setRangDate(startDate, endDate) 现支持"年,月,日,时,分,秒"的限定, 原版仅支持"年月日"
- 关闭了默认的数字文本缩放效果: WheelView.initPaints -> paintCenterText.setTextScaleX(1.1F) -> 1.0F
- 优化返回值显示, 如传入的 type 秒值为 false, 回来结果相应秒值为 00 -> WheelTime.getTime()
- 更新了`province.json`(2023年5月17日 11:45:56), 并为此写了个Java小项目, 项目地址
  👉 <https://github.com/javakam/GetDistricts> , Idea打开, 高德Key需要自己申请 👉 以下部分代码片段

![全国行政区域信息(高德)](https://raw.githubusercontent.com/javakam/PickerView/master/screenshot/全国行政区域信息_高德.png)

#### 4. 获取全国行政区域信息(高德) 👉 <https://lbs.amap.com/api/webservice/guide/api/district/>

#### 5. 使用说明

除了`3`修改内容外, 其它与原版完全一致。

`Bigkoo/Android-PickerView`原版说明文件
👉 [README_ORIGIN.md](https://github.com/javakam/PickerView/blob/master/README_ORIGIN.md)
