package com.nick.loadinglayout

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nick.loading.State
import com.nick.loadinglayout.databinding.ActivitySwitchStateBinding


class SwitchStateActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySwitchStateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySwitchStateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.loadStateView.reload = {
            binding.loadStateView.state = State.Loading
            Toast.makeText(this, "正在加载中...", Toast.LENGTH_LONG).show()
        }

        binding.btnSwitchState.setOnClickListener {
            when (binding.loadStateView.state) {
                State.AttachView -> {
                    binding.loadStateView.state = State.Loading
                    Toast.makeText(this, "正在加载中...", Toast.LENGTH_LONG).show()
                }
                State.Loading -> {
                    binding.loadStateView.state = State.EmptyData
                    Toast.makeText(this, "数据为空时的效果", Toast.LENGTH_LONG).show()
                }
                State.EmptyData -> {
                    binding.loadStateView.state = State.ErrorData
                    Toast.makeText(this, "数据读取错误时的效果", Toast.LENGTH_LONG).show()
                }
                State.ErrorData -> {
                    binding.loadStateView.state = State.ErrorNetwork
                    Toast.makeText(this, "网络异常时的效果", Toast.LENGTH_LONG).show()
                }
                State.ErrorNetwork -> {
                    binding.loadStateView.state = State.AttachView
                    Toast.makeText(this, "显示正常视图时的效果", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}