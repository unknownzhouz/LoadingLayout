package com.nick.loading

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.LayoutRes

abstract class StateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    abstract fun bindData(model: StateModel? = null, reload: (() -> Unit)? = null)

}


class LoadingView(context: Context, @LayoutRes resource: Int) : StateView(context) {
    init {
        LayoutInflater.from(context).inflate(resource, this, true)
    }

    override fun bindData(model: StateModel?, reload: (() -> Unit)?) {

    }
}

class EmptyDataView(context: Context, @LayoutRes resource: Int) : StateView(context) {
    init {
        LayoutInflater.from(context).inflate(resource, this, true)
    }

    override fun bindData(model: StateModel?, reload: (() -> Unit)?) {
        model?.let {
            val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
            tvEmpty?.text = it.title
            tvEmpty?.setCompoundDrawablesWithIntrinsicBounds(0, it.iconId, 0, 0)
        }
    }
}

class ErrorDataView(context: Context, @LayoutRes resource: Int) : StateView(context) {
    init {
        LayoutInflater.from(context).inflate(resource, this, true)
    }

    override fun bindData(model: StateModel?, reload: (() -> Unit)?) {
        model?.let {
            val tvFailure = findViewById<TextView>(R.id.tvFailure)
            tvFailure?.text = it.title
            tvFailure?.setCompoundDrawablesWithIntrinsicBounds(0, it.iconId, 0, 0)

            // 重新加载
            val reloadView = findViewById<TextView>(R.id.reloadView)
            reloadView?.text = it.reloadText
            reloadView?.setOnClickListener {
                reload?.invoke()
            }
        }
    }
}

class ErrorNetworkView(context: Context, @LayoutRes resource: Int) : StateView(context) {
    init {
        LayoutInflater.from(context).inflate(resource, this, true)
    }

    override fun bindData(model: StateModel?, reload: (() -> Unit)?) {
        model?.let {
            val tvNetworkError = findViewById<TextView>(R.id.tvNetworkError)
            tvNetworkError?.text = it.title
            tvNetworkError?.setCompoundDrawablesWithIntrinsicBounds(0, it.iconId, 0, 0)

            // 重新加载
            val reloadView = findViewById<TextView>(R.id.reloadView)
            reloadView?.text = it.reloadText
            reloadView?.setOnClickListener {
                reload?.invoke()
            }

        }
    }
}