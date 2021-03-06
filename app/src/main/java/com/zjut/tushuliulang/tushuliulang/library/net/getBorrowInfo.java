package com.zjut.tushuliulang.tushuliulang.library.net;

import android.util.Log;

import com.zjut.tushuliulang.tushuliulang.bookshare.getBookShareInfo;
import com.zjut.tushuliulang.tushuliulang.collection.net.COLLECTION_INFO;
import com.zjut.tushuliulang.tushuliulang.net.BOOK_INFO;
import com.zjut.tushuliulang.tushuliulang.net.TSLLURL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ben on 2015/10/6.
 */
public class getBorrowInfo
{
    private String tmp = "";
    private InputStream is;
    private boolean result;
    private String url = TSLLURL.getborrowinfo;

    private String stuid = "";
    private BORROW_INFO[] borrow_infos;

    public getBorrowInfo(String stuid)
    {
        this.stuid = stuid;
    }
    public boolean fetch()
    {
        connect();
        regexp();

        return result;
    }

    private void connect() {
        List<BasicNameValuePair> gets = new LinkedList<>();
        gets.add(new BasicNameValuePair("stuid", stuid));

        String get = URLEncodedUtils.format(gets, "UTF-8");
//        HttpGet getmethod = new HttpGet(url + '?' + get);

        InputStream is = null;

        try {
            //得到HttpClient对象
            HttpClient getClient = new DefaultHttpClient();


            //得到HttpGet对象
            HttpGet request = new HttpGet(url + "?" +
                    get);
            //客户端使用GET方式执行请教，获得服务器端的回应response
            HttpResponse response = getClient.execute(request);
            //判断请求是否成功
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Log.i("tag", "请求服务器端成功");
                //获得输入流
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                //关闭输入流

            } else {
                Log.i("tag", "请求服务器端失败");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("wonrg", "wrong");
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }




            tmp = sb.toString();


            is.close();
        } catch (Exception e) {
//                    return "Fail to convert net stream!";
        }
    }

    private void regexp() {

        Pattern pattern_total = Pattern.compile("<found>([\\s\\S]*)</found>");
        Matcher matcher_total = pattern_total.matcher(tmp);
        if (matcher_total.find())
        {
            int n = Integer.parseInt(matcher_total.group(1));
            borrow_infos = new BORROW_INFO[n];
            n=0;
            result = false;

            Pattern pattern_book = Pattern.compile("<book>([\\s\\S]*?)</book>");
            Matcher matcher_book = pattern_book.matcher(tmp);
            while(matcher_book.find())
            {
                borrow_infos[n] = new BORROW_INFO();

                String str = matcher_book.group(1);

                Pattern pattern_codeincode = Pattern.compile("<codeincode>(.*)</codeincode>");
                Matcher matcher_codeincode = pattern_codeincode.matcher(str);
                matcher_codeincode.find();
                borrow_infos[n].codeincode = matcher_codeincode.group(1);

                Pattern pattern_code = Pattern.compile("<code>(.*)</code>");
                Matcher matcher_code = pattern_code.matcher(str);
                matcher_code.find();
                borrow_infos[n].code = matcher_code.group(1);

                getshareid getshareid = new getshareid(borrow_infos[n].code,borrow_infos[n].codeincode);
                if(getshareid.fetch())
                {
                    borrow_infos[n].share = true;
                    borrow_infos[n].shareid = getshareid.getbookshareid();
                }
                else
                {
                    borrow_infos[n].share = false;
                }

                result = true;
                n++;
            }

        }
    }
    public BORROW_INFO[] getCollections()
    {
        return borrow_infos;
    }
}
