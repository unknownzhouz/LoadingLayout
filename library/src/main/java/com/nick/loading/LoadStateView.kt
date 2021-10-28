package com.nick.loading

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Point
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout


class LoadStateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    /**
     * 自定义视图
     */
    var buildStateView: ((state: LoadState) -> StateView?)? = null

    /**
     * 自定义视图文本、图标
     */
    var buildStateModel: ((state: LoadState) -> StateModel?)? = null

    /**
     * 自定义视图坐标偏移量（正负方向）
     */
    var offsetStateView: ((state: LoadState) -> Point?)? = null

    /**
     * 重新加载（数据异常、网络异常使用）
     */
    var reload: (() -> Unit)? = null

    private val config = StyleConfig()

    /**
     * 状态枚举
     */
    var state: LoadState = LoadState.AttachView
        set(value) {
            if (field == value) {
                return
            }
            // 更新状态值
            field = value

            // 移除最后一次状态视图
            removeStateView()

            // 使用自定义状态视图
            var view = buildStateView?.invoke(value)

            // 使用xml配置视图
            if (null == view) {
                view = config.buildStateView(context, value)
            }

            // 使用默认视图
            if (null == view) {
                view = defaultStateView(value)
            }

            // 添加状态视图
            if (null != view) {
                addView(view, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            }

            // 主视图显示与隐藏
            getChildAt(0)?.visibility = if (value == LoadState.AttachView) VISIBLE else GONE
        }

    init {
        attrs?.let {
            val ta: TypedArray = context.obtainStyledAttributes(it, R.styleable.LoadStateView)
            config.layoutLoading = ta.getResourceId(R.styleable.LoadStateView_ls_layoutLoading, 0)
            config.layoutEmptyData = ta.getResourceId(R.styleable.LoadStateView_ls_layoutEmptyData, 0)
            config.layoutErrorData = ta.getResourceId(R.styleable.LoadStateView_ls_layoutErrorData, 0)
            config.layoutErrorNet = ta.getResourceId(R.styleable.LoadStateView_ls_layoutErrorNet, 0)

            val emptyText = ta.getString(R.styleable.LoadStateView_ls_emptyText) ?: ""
            val emptyIcon = ta.getResourceId(R.styleable.LoadStateView_ls_emptyIcon, 0)
            config.emptyModel = StyleConfig.buildStateModel(emptyText, emptyIcon)

            val errorDataText = ta.getString(R.styleable.LoadStateView_ls_errorDataText) ?: ""
            val errorDataIcon = ta.getResourceId(R.styleable.LoadStateView_ls_errorDataIcon, 0)
            config.errorDataModel = StyleConfig.buildStateModel(errorDataText, errorDataIcon)

            val errorNetText = ta.getString(R.styleable.LoadStateView_ls_errorNetText) ?: ""
            val errorNetIcon = ta.getResourceId(R.styleable.LoadStateView_ls_errorNetIcon, 0)
            config.errorNetModel = StyleConfig.buildStateModel(errorNetText, errorNetIcon)

            ta.recycle()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        adjustChildView()
    }

    private fun adjustChildView() {
        offsetStateView ?: return
        val point = offsetStateView?.invoke(state)
        if (childCount > 1) {
            val targetView = getChildAt(1)
            val params = targetView.layoutParams as LayoutParams
            params.leftMargin = point?.x ?: 0
            params.topMargin = point?.y ?: 0
        }
    }


    /**
     * 移除状态视图
     */
    private fun removeStateView() {
        val count = childCount
        if (count > 1) {
            removeViews(1, count - 1)
        }
    }

    /**
     * 默认StateView
     */
    private fun defaultStateView(state: LoadState): StateView? {
        // 使用自定义Model
        var model = buildStateModel?.invoke(state)

        // 使用xml配置视图
        if (null == model) {
            model = config.getStateModel(state)
        }

        // 使用默认Model
        if (null == model) {
            model = defaultStateModel(state)
        }
        var view: StateView? = null
        when (state) {
            LoadState.AttachView -> {
                view = null
            }
            LoadState.Loading -> {
                view = LoadingView(context)
            }
            LoadState.EmptyData -> {
                view = EmptyDataView(context, model = model)
            }
            LoadState.ErrorData -> {
                view = ErrorDataView(context, model = model, reload = reload)
            }
            LoadState.ErrorNetwork -> {
                view = ErrorNetworkView(context, model = model, reload = reload)
            }
        }

        return view
    }

    private fun defaultStateModel(state: LoadState): StateModel? {
        return when (state) {
            LoadState.EmptyData -> {
                StateModel.defaultEmptyData()
            }
            LoadState.ErrorData -> {
                StateModel.defaultErrorData()
            }
            LoadState.ErrorNetwork -> {
                StateModel.defaultErrorNetwork()
            }
            else -> {
                null
            }
        }
    }


    class StyleConfig {
        var layoutLoading = 0
        var layoutEmptyData = 0
        var layoutErrorData = 0
        var layoutErrorNet = 0
        var emptyModel: StateModel? = null
        var errorDataModel: StateModel? = null
        var errorNetModel: StateModel? = null

        fun getStateModel(state: LoadState): StateModel? {
            return when (state) {
                LoadState.EmptyData -> {
                    emptyModel
                }
                LoadState.ErrorData -> {
                    errorDataModel
                }
                LoadState.ErrorNetwork -> {
                    errorNetModel
                }
                else -> null
            }
        }

        fun buildStateView(context: Context, state: LoadState): StateView? {
            var view: StateView? = null
            when (state) {
                LoadState.AttachView -> {
                    view = null
                }
                LoadState.Loading -> {
                    if (0 != layoutLoading) {
                        view = LoadingView(context, layoutLoading)
                    }
                }
                LoadState.EmptyData -> {
                    if (0 != layoutEmptyData) {
                        view = EmptyDataView(context, layoutEmptyData)
                    }
                }
                LoadState.ErrorData -> {
                    if (0 != layoutErrorData) {
                        view = ErrorDataView(context, layoutErrorData)
                    }
                }
                LoadState.ErrorNetwork -> {
                    if (0 != layoutErrorNet) {
                        view = ErrorNetworkView(context, layoutErrorNet)
                    }
                }
            }
            return view
        }

        companion object {
            fun buildStateModel(title: String, iconId: Int): StateModel? {
                if (title.isNotEmpty() || iconId != 0) {
                    val model = StateModel()
                    model.title = title
                    model.iconId = iconId
                    return model
                }
                return null
            }
        }

    }
}


