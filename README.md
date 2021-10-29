## LoadStateLayout

布局状态加载库，主要是为了简化进入页面时，从加载数据到返回数据这个过程的各种状态情况，提供一套通用的显示方案；支持在不同场景下的UI显示效果；

![gif](https://github.com/unknownzhouz/LoadingLayout/blob/main/static/Screenrecorder-2021-10-29-15-25-47-461.gif)



### 一、使用规范

- `LoadStateLayout` 布局中，只能有一个子布局；
- 代码中重写状态视图会直接覆盖 `.xml`布局中的配置；
- 同状态下自定义布局优先级高于自定义`StateModel`

### 二、布局中配置状态视图

```xml
<!-- 当设置状态为EmptyData时，会调用自定义空数据布局 view_empty_new -->
<com.nick.loading.LoadStateLayout
      android:id="@+id/loadStateView"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      app:ls_layoutEmptyData="@layout/view_empty_new">
      <TextView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:layout_gravity="center"
          android:gravity="center"
          android:text="显示正常状态下的文本内容"/>
</com.nick.loading.LoadStateLayout>
```

| 属性参数 | 描述             | 格式 |
| :--------    | :------------------:     | :--------    |
| ls_layoutLoading | 加载时显示布局 | reference（@LayoutRes） |
| ls_layoutEmptyData | 空数据显示布局          | reference（@LayoutRes） |
| ls_layoutErrorData | 数据异常显示布局  | reference（@LayoutRes） |
| ls_layoutErrorNet | 网络异常显示布局  | reference（@LayoutRes） |
| ls_emptyText | 空数据Model文本 | string |
| ls_emptyIcon | 空数据Model图标 | reference（资源ID） |
| ls_errorDataText | 数据异常Model文本 | string |
| ls_errorDataIcon | 数据异常Model图标 | reference（资源ID） |
| ls_errorNetText | 网络异常Model文本 | string |
| ls_errorNetIcon | 网络异常Model图标 | reference（资源ID） |

### 三、通过代码重写状态

加载过程中状态变化的枚举，以及自定义的空数据显示视图（自己工程定义）；

```kotlin
enum class State {
    AttachView,      // 主视图（子布局中第一个视图）
    Loading,         // 加载中
    EmptyData,       // 空数据
    ErrorData,       // 数据错误
    ErrorNetwork,    // 网络错误
}

/**
  * 自定义空数据视图
  */
object EmptyFactory {
    class SearchView(context: Context, goBack: () -> Unit) : StateView(context) {
        init {
            LayoutInflater.from(context).inflate(R.layout.view_empty_search, this, true)
            val tvBack = findViewById<TextView>(R.id.tvBack)
            tvBack.setOnClickListener {
                goBack.invoke()
            }
        }
    }
}
```

- 自定义`StateModel`，当状态设置为数据错误时，替换默认的文本和图标
- 自定义`View`，当状态设置为空数据时，新布局替换默认布局

```kotlin
   // 状态为数据异常时，显示自定义StateModel（其它使用默认）
   loadStateLayout.buildStateModel = {
        if (it == State.ErrorData) {
            val model = StateModel()
            model.title = "数据异常\n替换新文本内容和新图标"
            model.iconId = R.drawable.ic_computer
            model
        } else {
            null
        }
    }

       // 状态为空数据时，显示自定义View视图（其它使用默认）
    loadStateLayout.buildStateView = {
         if (it == State.EmptyData) {
             EmptyFactory.SearchView(this, goBack = {
                 onBackPressed()
              })
          } else {
             null
          }
      }
```



### 其它扩展Block

```kotlin
    /**
     * 自定义视图坐标偏移量（正负方向）
     */
    var offsetStateView: ((state: State) -> Point?)? = null 

    /**
     * 重新加载（数据异常、网络异常使用）
     */
    var reload: (() -> Unit)? = null
```

可以参考Demo案例中使用；



### Gradle集成

在 project `build.gradle` 添加 `maven` 仓库

```kotlin
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

在 module `build.gradle` 添加依赖

```kotlin

```