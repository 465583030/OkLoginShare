package com.hengda.zwf.sharelogin

/**
 * 作者：祝文飞（Tailyou）
 * 邮箱：tailyou@163.com
 * 时间：2017/6/6 14:07
 * 描述：第三方分享回调
 */
interface IShareListener {

    fun onSuccess()

    fun onCancel()

    fun onError(msg: String)

}
