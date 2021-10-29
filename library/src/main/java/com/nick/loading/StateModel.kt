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
            val model = StateModel()
            model.title = "暂无数据哦~"
            model.iconId = R.drawable.loadstate_empty
            return model;
        }

        fun defaultErrorData(): StateModel {
            val model = StateModel()
            model.title = "数据异常，请重试~"
            model.iconId = R.drawable.loadstate_data_failed
            return model;
        }

        fun defaultErrorNetwork(): StateModel {
            val model = StateModel()
            model.title = "网络异常，请重试~"
            model.iconId = R.drawable.loadstate_net_failed
            return model;
        }

    }
}