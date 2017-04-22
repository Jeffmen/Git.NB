package com.example.gitnb.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.example.gitnb.app.Application;
import com.example.gitnb.utils.MessageUtils;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WeiXin {
	public static String AppID = "wx69e947c44c461437";
	public static String AppSecret = "898004714678ab504fe12fc6316359e9";
	private IWXAPI api;

	private static class Holder{
		private static WeiXin instance = new WeiXin();
	}

	private WeiXin(){
		api = WXAPIFactory.createWXAPI(Application.mContext, WeiXin.AppID, true);
		api.registerApp(WeiXin.AppID);
	}

	public static WeiXin getInstance(){
		return Holder.instance;
	}

	public void share2WeiXin(Context context, int flag, String title, String description,
							 String webPageUrl, View imageView) {
		if (!api.isWXAppInstalled()) {
			MessageUtils.getToast(context, "您还未安装微信客户端").show();
			return;
		}

		WXWebpageObject webPage = new WXWebpageObject();
		webPage.webpageUrl = webPageUrl;
		WXMediaMessage msg = new WXMediaMessage(webPage);

		msg.title = title;
		msg.description = description;
		msg.setThumbImage(convertViewToBitmap(imageView));
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}

	public static Bitmap convertViewToBitmap(View view){
//		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
}
