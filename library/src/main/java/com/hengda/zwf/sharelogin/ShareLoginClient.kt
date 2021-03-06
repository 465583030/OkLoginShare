package com.hengda.zwf.sharelogin

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.hengda.zwf.sharelogin.content.ShareContent
import com.hengda.zwf.sharelogin.qq.QQHandlerActivity
import com.hengda.zwf.sharelogin.sina.SinaHandlerActivity
import com.hengda.zwf.sharelogin.type.LoginPlatform
import com.hengda.zwf.sharelogin.type.LoginPlatform.*
import com.hengda.zwf.sharelogin.type.SharePlatform
import com.hengda.zwf.sharelogin.type.SharePlatform.*
import com.hengda.zwf.sharelogin.wechat.WechatHandlerActivity
import java.util.*
import com.sina.weibo.sdk.utils.LogUtil


/**
 * 作者：祝文飞（Tailyou）
 * 邮箱：tailyou@163.com
 * 时间：2017/6/6 11:02
 * 描述：
 */
object ShareLoginClient {

    const val SHARE_CONTENT = "SHARE_CONTENT"
    const val SHARE_PLATFORM = "SHARE_PLATFORM"
    const val ACTION_LOGIN = "ACTION_LOGIN"
    const val ACTION_SHARE = "ACTION_SHARE"

    var sLoginListener: ILoginListener? = null
    var sShareListener: IShareListener? = null

    fun init(slc: ShareLoginConfig) {
        if (slc.isDebug) {
            LogUtil.enableLog()
        } else {
            LogUtil.disableLog()
        }
    }

    fun isQQInstalled(context: Context): Boolean {
        return isInstalled(context, "com.tencent.mobileqq")
    }

    fun isWeiBoInstalled(context: Context): Boolean {
        return isInstalled(context, "com.sina.weibo")
    }

    fun isWeiXinInstalled(context: Context): Boolean {
        return isInstalled(context, "com.tencent.mm")
    }

    /**
     * 根据包名判断是否安装

     * @author 祝文飞（Tailyou）
     * *
     * @time 2017/6/6 11:09
     */
    private fun isInstalled(context: Context, pkgName: String): Boolean {
        val pm = context.applicationContext.packageManager ?: return false
        val packages = pm.getInstalledPackages(0)
        return packages
                .map { it.packageName.toLowerCase(Locale.ENGLISH) }
                .contains(pkgName)
    }

    /**
     * 第三方登录

     * @author 祝文飞（Tailyou）
     * *
     * @time 2017/6/6 13:52
     */
    fun login(activity: Activity, @LoginPlatform type: String, loginListener: ILoginListener?) {
        ShareLoginClient.sLoginListener = loginListener
        when (type) {
            WEIBO//微博
            -> if (isWeiBoInstalled(activity)) {
                toLogin(activity, SinaHandlerActivity::class.java)
            } else {
                loginListener?.onError("未安装微博")
            }
            QQ//腾讯QQ
            -> if (isQQInstalled(activity)) {
                toLogin(activity, QQHandlerActivity::class.java)
            } else {
                loginListener?.onError("未安装QQ")
            }
            WEIXIN//微信
            -> if (isWeiXinInstalled(activity)) {
                WechatHandlerActivity.doLogin(activity)
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } else {
                loginListener?.onError("未安装微信")
            }
        }
    }

    /**
     * 跳转->第三方登录

     * @author 祝文飞（Tailyou）
     * *
     * @time 2017/6/7 9:50
     */
    private fun toLogin(activity: Activity, cls: Class<out Activity>) {
        val intent = Intent(activity, cls)
        intent.action = ACTION_LOGIN
        activity.startActivity(intent)
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     * 第三方分享

     * @author 祝文飞（Tailyou）
     * *
     * @time 2017/6/7 9:51
     */
    fun share(activity: Activity, @SharePlatform sharePlatform: String, shareContent: ShareContent, shareListener: IShareListener?) {
        ShareLoginClient.sShareListener = shareListener
        when (sharePlatform) {
        //微博
            WEIBO_TIME_LINE -> if (isWeiBoInstalled(activity)) {
                toShare(activity, sharePlatform, shareContent, SinaHandlerActivity::class.java)
            } else {
                shareListener?.onError("未安装微博")
            }
        //QQ
            QQ_FRIEND, QQ_ZONE -> if (isQQInstalled(activity)) {
                toShare(activity, sharePlatform, shareContent, QQHandlerActivity::class.java)
            } else {
                shareListener?.onError("未安装QQ")
            }
        //微信
            WEIXIN_FRIEND, WEIXIN_FRIEND_ZONE, WEIXIN_FAVORITE -> if (isWeiXinInstalled(activity)) {
                WechatHandlerActivity().doShare(activity, shareContent, sharePlatform)
            } else {
                shareListener?.onError("未安装微信")
            }
        }
    }

    /**
     * 跳转->第三方分享

     * @author 祝文飞（Tailyou）
     * *
     * @time 2017/6/7 9:57
     */
    private fun toShare(activity: Activity, sharePlatform: String, shareContent: ShareContent, cls: Class<out Activity>) {
        val intent = Intent(activity, cls)
        intent.action = ACTION_SHARE
        intent.putExtra(SHARE_PLATFORM, sharePlatform)
        intent.putExtra(SHARE_CONTENT, shareContent)
        activity.startActivity(intent)
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}
