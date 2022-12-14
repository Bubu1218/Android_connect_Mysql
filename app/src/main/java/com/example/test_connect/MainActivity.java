package com.example.test_connect;

import androidx.appcompat.app.AppCompatActivity;
import java.net.URL;
import android.os.Bundle;
import java.net.HttpURLConnection;
import java.io.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView; // 把視圖的元件宣告成全域變數
    Button button;
    String result; // 儲存資料用的字串

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 找到視圖的元件並連接
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        // 宣告按鈕的監聽器監聽按鈕是否被按下
        // 跟上次在 View 設定的方式並不一樣
        // 我只是覺得好像應該也教一下這種寫法
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            // 按鈕事件
            public void onClick(View view) {
                // 按下之後會執行的程式碼
                // 宣告執行緒
                Thread thread = new Thread(mutiThread);
                thread.start(); // 開始執行
            }
        });
    }

    /* ======================================== */

    // 建立一個執行緒執行的事件取得網路資料
    // Android 有規定，連線網際網路的動作都不能再主線程做執行
    // 畢竟如果使用者連上網路結果等太久整個系統流程就卡死了
    private Runnable mutiThread = new Runnable(){
        public void run()
        {
            try {
                URL url = new URL("http://192.168.56.1/GetData.php");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.connect(); // 開始連線

                int responseCode = connection.getResponseCode();// 建立取得回應的物件
                if(responseCode == HttpURLConnection.HTTP_OK){
                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                    InputStream inputStream = connection.getInputStream();
                    // 取得輸入串流
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    // 讀取輸入串流的資料
                    String box = ""; // 宣告存放用字串
                    String line = null; // 宣告讀取用的字串
                    while((line = bufReader.readLine()) != null) {
                                box += line + "\n"; // 每當讀取出一列，就加到存放字串後面
                    }
                    inputStream.close(); // 關閉輸入串流


                    String target="吳留手-明太子鮭魚烤飯糰";
                    int data_index=0;
                    int target_len=target.length();
                    float[] target_data=new float[6];

                    String get_substring =
                            box.substring(box.indexOf(target)+target_len+3,box.indexOf(target)+target_len+56); // 邊界處理很重要!!!

                    String[] target_substring =
                            get_substring.split("\"|\\s+|,|:|熱量|[(]|[)]|kcal|g|食|物|名|稱|蛋白質|脂肪|醣類|\\{|\\}");

                    for (int i=0;i<target_substring.length;i++) {
                        target_data[data_index]=Float.parseFloat(target_substring[i].isEmpty() ? "-1" : target_substring[i]); // 空格轉換問題!!!!!

                        if(target_data[data_index]!=-1)
                            data_index++;
                    }
                    /*
                    for(int i=0;i<4;i++){
                        System.out.println(target_data[i]);
                    }
                    */

                    box=get_substring;
                    result = box; // 把存放用字串放到全域變數
                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
                result = e.toString(); // 如果出事，回傳錯誤訊息
            }

            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
                    textView.setText(result); // 更改顯示文字
                }
            });
        }
    };
}