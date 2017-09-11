package com.example.webviewdemo02_javascript;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThreeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0001;
    private WebView webView;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);
        webView = (WebView) findViewById(R.id.web_view03);
        //设置WebView的相关设置
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSavePassword(false);
        settings.setSupportZoom(false);
        settings.setDefaultTextEncodingName("UTF-8");

        webView.addJavascriptInterface(new SharpJs(), "sharp");

        webView.loadUrl("file:///android_asset/demo03.html");


    }

    /**
     * 自定义一个Js的业务类,传递给JS的对象就是这个,调用时直接javascript:sharp.contactlist()
     */
    private class SharpJs {
        //从Android4.2开始。 只有添加 @JavascriptInterface 声明的Java方法才可以被JavaScript调用
        @JavascriptInterface
        public void contactlist() {
            //所有的WebView方法都应该在同一个线程程中调用，这里的contactlist方法却在JavaBridge线程中被调用了
            //所以我们要把contactlist里的代码写到同一个线程中
            webView.post(new Runnable() {
                @Override
                public void run() {
                    //检测程序是否开启权限
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) !=
                            PackageManager.PERMISSION_GRANTED) {//没有权限需要动态获取
                        //动态请求权限
                        ActivityCompat.requestPermissions(ThreeActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE);

                    } else {
                        //获取到联系人集合
                        contactList = getContacts();
                        //将获取到的联系人集合写入到JsonObject对象中,再添加到JsonArray数组中
                        String json = buildJson(contactList);
                        webView.loadUrl("javascript:show('" + json + "')");
                    }
                }
            });

        }

        @JavascriptInterface
        public void call(String phone) {
            //跳转到拨号界面
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    /**
     * 定义一个获取联系人的方法,返回的是List<Contact>的数据
     *
     * @return
     */
    public List<Contact> getContacts() {

        List<Contact> contactList = new ArrayList<Contact>();

        //查询raw_contacts表获得联系人的id
        ContentResolver resolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //查询联系人数据
        Cursor cursor = resolver.query(uri, null, null, null, null);
        while (cursor.moveToNext()) {
            Contact contact = new Contact();
            //获取联系人姓名,手机号码
            contact.setId(cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts._ID)));
            contact.setName(
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            contact.setPhone(
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            contactList.add(contact);
        }

        cursor.close();

        return contactList;
    }


    /**
     * 将获取到的联系人集合写入到JsonObject对象中,再添加到JsonArray数组中
     */
    public String buildJson(List<Contact> contactList) {

        JSONArray array = new JSONArray();
        for (Contact contact : contactList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", contact.getId());
                jsonObject.put("name", contact.getName());
                jsonObject.put("phone", contact.getPhone());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(jsonObject);
        }

        return array.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//判断是否给于权限
                contactList = getContacts();
                //将获取到的联系人集合写入到JsonObject对象中,再添加到JsonArray数组中
                String json = buildJson(contactList);
                webView.loadUrl("javascript:show('" + json + "')");
            } else {
                Toast.makeText(this, "请开启权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
