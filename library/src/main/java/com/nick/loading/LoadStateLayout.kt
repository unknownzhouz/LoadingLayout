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

    /**
     * xml配置信息
     */
    private var xmlConfig: StyleConfig? = null


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

            // 自定义布局
            var view = buildStateView?.invoke(value)

            // xml配置布局
            if (null == view) {
                view = xmlConfig?.buildViewByState(context, value)
            }
            // 全局配置布局
            if (null == view) {
                view = StyleConfig.global.buildViewByState(context, value)
            }

            // 自定义Model
            var model = buildStateModel?.invoke(value)

            // xml配置Model
            if (null == model) {
                model = xmlConfig?.getStateModel(value)
            }
            // 全局配置Model
            if (null == model) {
                model = StyleConfig.global.getStateModel(value)
            }

            // View绑定Model数据
            view?.bindData(model, reload)


            // 添加状态视图
            if (null != view) {
                addView(view, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            }

            // 主视图显示与隐藏
            getChildAt(0)?.visibility = if (value == State.AttachView) VISIBLE else GONE
        }

    init {
        attrs?.let {
            xmlConfig = StyleConfig.loadXmlConfig(context, it)
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
     * 配置信息
     */
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


        fun buildViewByState(context: Context, state: State): StateView? {
            var view: StateView? = null
            when (state) {
                State.AttachView -> {
                    view = null
                }

                State.Loading -> {
                    if (layoutLoading != 0) {
                        view = LoadingView(context, layoutLoading)
                    }
                }

                State.EmptyData -> {
                    if (layoutEmptyData != 0) {
                        view = EmptyDataView(context, layoutEmptyData)
                    }
                }

                State.ErrorData -> {
                    if (layoutErrorData != 0){
                        view = ErrorDataView(context, layoutErrorData)
                    }
                }

                State.ErrorNetwork -> {
                    if (layoutErrorNet != 0){
                        view = ErrorNetworkView(context, layoutErrorNet)
                    }
                }
            }
            return view
        }


        companion object {
            /**
             * 全局配置信息
             */
            var global = defaultConfig()

            /**
             * 默认布局配置信息
             */
            private fun defaultConfig(): StyleConfig {
                val sc = StyleConfig()
                sc.layoutLoading = R.layout.view_load_state_start
                sc.layoutEmptyData = R.layout.view_load_state_end_empty
                sc.layoutErrorData = R.layout.view_load_state_end_failure
                sc.layoutErrorNet = R.layout.view_load_state_end_network_error
                sc.emptyModel = StateModel.defaultEmptyData()
                sc.errorDataModel = StateModel.defaultErrorData()
                sc.errorNetModel = StateModel.defaultErrorNetwork()
                return sc
            }

            /**
             * XML布局配置信息
             */
            fun loadXmlConfig(context: Context, attrs: AttributeSet): StyleConfig {
                val sc = StyleConfig()
                val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadStateLayout)
                sc.layoutLoading = ta.getResourceId(R.styleable.LoadStateLayout_ls_layoutLoading, 0)
                sc.layoutEmptyData = ta.getResourceId(R.styleable.LoadStateLayout_ls_layoutEmptyData, 0)
                sc.layoutErrorData = ta.getResourceId(R.styleable.LoadStateLayout_ls_layoutErrorData, 0)
                sc.layoutErrorNet = ta.getResourceId(R.styleable.LoadStateLayout_ls_layoutErrorNet, 0)

                val emptyText = ta.getString(R.styleable.LoadStateLayout_ls_emptyText) ?: ""
                val emptyIcon = ta.getResourceId(R.styleable.LoadStateLayout_ls_emptyIcon, 0)
                sc.emptyModel = StateModel.buildStateModel(emptyText, emptyIcon, "")

                val errorDataText = ta.getString(R.styleable.LoadStateLayout_ls_errorDataText) ?: ""
                val errorDataIcon = ta.getResourceId(R.styleable.LoadStateLayout_ls_errorDataIcon, 0)
                val errorDataReload = ta.getString(R.styleable.LoadStateLayout_ls_errorDataReloadText) ?: ""
                sc.errorDataModel = StateModel.buildStateModel(errorDataText, errorDataIcon, errorDataReload)

                val errorNetText = ta.getString(R.styleable.LoadStateLayout_ls_errorNetText) ?: ""
                val errorNetIcon = ta.getResourceId(R.styleable.LoadStateLayout_ls_errorNetIcon, 0)
                val errorNetReload = ta.getString(R.styleable.LoadStateLayout_ls_errorNetReloadText) ?: ""
                sc.errorNetModel = StateModel.buildStateModel(errorNetText, errorNetIcon, errorNetReload)

                ta.recycle()
                return sc
            }

            fun initGlobalConfig(config: StyleConfig) {
                global = config
            }
        }

    }
}


