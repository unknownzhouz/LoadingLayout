package com.nick.loading

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.LayoutRes

abstract class StateView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs)


class LoadingView(
    context: Context,
    @LayoutRes resource: Int? = null
) : StateView(context) {
    init {
        LayoutInflater.from(context).inflate(resource ?: R.layout.view_load_state_start, this, true)
    }
}

class EmptyDataView(
    context: Context,
    @LayoutRes resource: Int? = null,
    model: StateModel? = null
) : StateView(context) {
    init {
        LayoutInflater.from(context).inflate(resource ?: R.layout.view_load_state_end_empty, this, true)

        model?.let {
            val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
            tvEmpty.text = it.title
            tvEmpty.setCompoundDrawablesWithIntrinsicBounds(0, it.iconId, 0, 0)
        }
    }
}

class ErrorDataView(
    context: Context,
    @LayoutRes resource: Int? = null,
    model: StateModel? = null,
    reload: (() -> Unit)? = null
) : StateView(context) {
    init {
        LayoutInflater.from(context).inflate(resource ?: R.layout.view_load_state_end_failure, this, true)

        model?.let {
            val tvFailure = findViewById<TextView>(R.id.tvFailure)
            tvFailure.text = it.title
            tvFailure.setCompoundDrawablesWithIntrinsicBounds(0, it.iconId, 0, 0)

            // 重新加载
            findViewById<View>(R.id.reloadView).setOnClickListener {
                reload?.invoke()
            }
        }

    }
}

class ErrorNetworkView(
    context: Context,
    @LayoutRes resource: Int? = null,
    model: StateModel? = null,
    reload: (() -> Unit)? = null
) : StateView(context) {
    init {
        LayoutInflater.from(context).inflate(resource ?: R.layout.view_load_state_end_network_error, this, true)
        model?.let {
            val tvNetworkError = findViewById<TextView>(R.id.tvNetworkError)
            tvNetworkError.text = it.title
            tvNetworkError.setCompoundDrawablesWithIntrinsicBounds(0, it.iconId, 0, 0)
            // 重新加载
            findViewById<View>(R.id.reloadView).setOnClickListener {
                reload?.invoke()
            }
        }

    }
}