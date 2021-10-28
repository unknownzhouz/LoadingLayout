package com.nick.loading

enum class LoadState {
    AttachView,      // 主视图（子布局中第一个视图）
    Loading,         // 加载中
    EmptyData,       // 空数据
    ErrorData,       // 数据错误
    ErrorNetwork,    // 网络错误
}