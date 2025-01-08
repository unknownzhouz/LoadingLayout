package com.nick.loading

class StateModel {
    /**
     * 显示文本
     */
    var title: String = ""

    /**
     * 显示资源ID
     */
    var iconId: Int = 0


    /**
     * 错误-重新加载文本
     */
    var reloadText: String = ""


    companion object {

        fun buildStateModel(title: String, iconId: Int, reloadText: String): StateModel? {
            if (title.isNotEmpty() || iconId != 0 || reloadText.isNotEmpty()) {
                val model = StateModel()
                model.title = title
                model.iconId = iconId
                model.reloadText = reloadText
                return model
            }
            return null
        }

        fun defaultEmptyData(): StateModel {
            return buildStateModel("暂无数据哦~", R.drawable.loadstate_empty, "")!!
        }

        fun defaultErrorData(): StateModel {
            return buildStateModel("数据异常，请重试~", R.drawable.loadstate_data_failed, "重新加载")!!
        }

        fun defaultErrorNetwork(): StateModel {
            return buildStateModel("网络异常，请重试~", R.drawable.loadstate_net_failed, "重新加载")!!
        }

    }
}