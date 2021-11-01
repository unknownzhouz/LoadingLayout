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

        fun defaultEmptyData(): StateModel {
            return buildStateModel("暂无数据哦~", R.drawable.loadstate_empty)!!
        }

        fun defaultErrorData(): StateModel {
            return buildStateModel("数据异常，请重试~", R.drawable.loadstate_data_failed)!!;
        }

        fun defaultErrorNetwork(): StateModel {
            return buildStateModel("网络异常，请重试~", R.drawable.loadstate_net_failed)!!;
        }

    }
}