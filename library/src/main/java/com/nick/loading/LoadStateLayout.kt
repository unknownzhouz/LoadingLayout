package com.nick.loading

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Point
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout


class LoadStateLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    /**
     * 自定义视图
     */
    var buildStateView: ((state: State) -> StateView?)? = null

    /**
     * 自定义视图文本、图标
     */
    var buildStateModel: ((state: State) -> StateModel?)? = null

    /**
     * 自定义视图坐标偏移量（正负方向）
     */
    var offsetStateView: ((state: State) -> Point?)? = null

    /**
     * 重新加载（数据异常、网络异常使用）
     */
    var reload: (() -> Unit)? = null

    private val config = StyleConfig()

    /**
     * 状态枚举
     */
    var state: State = State.AttachView
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
            getChildAt(0)?.visibility = if (value == State.AttachView) VISIBLE else GONE
        }

    init {
        attrs?.let {
            val ta: TypedArray = context.obtainStyledAttributes(it, R.styleable.LoadStateLayout)
            config.layoutLoading = ta.getResourceId(R.styleable.LoadStateLayout_ls_layoutLoading, 0)
            config.layoutEmptyData = ta.getResourceId(R.styleable.LoadStateLayout_ls_layoutEmptyData, 0)
            config.layoutErrorData = ta.getResourceId(R.styleable.LoadStateLayout_ls_layoutErrorData, 0)
            config.layoutErrorNet = ta.getResourceId(R.styleable.LoadStateLayout_ls_layoutErrorNet, 0)

            val emptyText = ta.getString(R.styleable.LoadStateLayout_ls_emptyText) ?: ""
            val emptyIcon = ta.getResourceId(R.styleable.LoadStateLayout_ls_emptyIcon, 0)
            config.emptyModel = StyleConfig.buildStateModel(emptyText, emptyIcon)

            val errorDataText = ta.getString(R.styleable.LoadStateLayout_ls_errorDataText) ?: ""
            val errorDataIcon = ta.getResourceId(R.styleable.LoadStateLayout_ls_errorDataIcon, 0)
            config.errorDataModel = StyleConfig.buildStateModel(errorDataText, errorDataIcon)

            val errorNetText = ta.getString(R.styleable.LoadStateLayout_ls_errorNetText) ?: ""
            val errorNetIcon = ta.getResourceId(R.styleable.LoadStateLayout_ls_errorNetIcon, 0)
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
    private fun defaultStateView(state: State): StateView? {
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
            State.AttachView -> {
                view = null
            }
            State.Loading -> {
                view = LoadingView(context)
            }
            State.EmptyData -> {
                view = EmptyDataView(context, model = model)
            }
            State.ErrorData -> {
                view = ErrorDataView(context, model = model, reload = reload)
            }
            State.ErrorNetwork -> {
                view = ErrorNetworkView(context, model = model, reload = reload)
            }
        }

        return view
    }

    private fun defaultStateModel(state: State): StateModel? {
        return when (state) {
            State.EmptyData -> {
                StateModel.defaultEmptyData()
            }
            State.ErrorData -> {
                StateModel.defaultErrorData()
            }
            State.ErrorNetwork -> {
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

        fun getStateModel(state: State): StateModel? {
            return when (state) {
                State.EmptyData -> {
                    emptyModel
                }
                State.ErrorData -> {
                    errorDataModel
                }
                State.ErrorNetwork -> {
                    errorNetModel
                }
                else -> null
            }
        }

        fun buildStateView(context: Context, state: State): StateView? {
            var view: StateView? = null
            when (state) {
                State.AttachView -> {
                    view = null
                }
                State.Loading -> {
                    if (0 != layoutLoading) {
                        view = LoadingView(context, layoutLoading)
                    }
                }
                State.EmptyData -> {
                    if (0 != layoutEmptyData) {
                        view = EmptyDataView(context, layoutEmptyData)
                    }
                }
                State.ErrorData -> {
                    if (0 != layoutErrorData) {
                        view = ErrorDataView(context, layoutErrorData)
                    }
                }
                State.ErrorNetwork -> {
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


