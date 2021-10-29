package com.nick.loadinglayout.util

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.nick.loading.StateModel
import com.nick.loading.StateView
import com.nick.loadinglayout.R

object EmptyFactory {

    /**
     * 自定义空数据视图
     */
    class SearchView(context: Context, goBack: () -> Unit) : StateView(context) {
        init {
            LayoutInflater.from(context).inflate(R.layout.view_empty_search, this, true)
            val tvBack = findViewById<TextView>(R.id.tvBack)
            tvBack.setOnClickListener {
                goBack.invoke()
            }
        }

        override fun bindData(model: StateModel?, reload: (() -> Unit)?) {

        }
    }
}