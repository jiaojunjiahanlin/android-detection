package com.qiniu.liuhanlin.android_detection;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.network.QNetDiagnoService.LDNetDiagnoListener;
import com.network.QNetDiagnoService.LDNetDiagnoService;
import com.network.QNetDiagnoUtils.Client;
import com.network.QNetDiagnoUtils.TAG;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment implements View.OnClickListener,
        LDNetDiagnoListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button btn;
    private String probe;
    private ProgressBar progress;
    private TextView text;
    private EditText edit;
    private String showInfo = "";
    private boolean isRunning = false;
    private LDNetDiagnoService _netDiagnoService;
    private Handler handler = new Handler();
    private View rootView;

    private OnFragmentInteractionListener mListener;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btn = (Button) rootView.findViewById(R.id.btn);
        btn.setOnClickListener(this);
        progress = (ProgressBar) rootView.findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);
        text = (TextView) rootView.findViewById(R.id.text);
        edit = (EditText) rootView.findViewById(R.id.domainName);
        edit.clearFocus();

    }

    @Override
    public void onClick(View v) {
        getActivity().getApplication();
        TAG.tag="id";
        if (v == btn) {
            if (!isRunning) {
                showInfo = "";
                String domainName = edit.getText().toString().trim();
                _netDiagnoService = new LDNetDiagnoService(getActivity().getApplication().getBaseContext(),
                        "rover", "网络诊断应用", "1.1.0", "hellowrold@qq.com",
                        "未知", domainName, "explorer", "ISOCountyCode",
                        "MobilCountryCode", "MobileNetCode", this);
                // 设置是否使用JNIC 完成traceroute
                _netDiagnoService.setIfUseJNICTrace(false);
//              _netDiagnoService.setIfUseJNICConn(true);
                //在UI线程更新UI
                _netDiagnoService.execute(probe);
                progress.setVisibility(View.VISIBLE);
                btn.setText("停止诊断");
                btn.setEnabled(true);
                edit.setInputType(InputType.TYPE_NULL);

            } else {
                progress.setVisibility(View.GONE);
                btn.setText("开始诊断");
                _netDiagnoService.cancel(true);
                _netDiagnoService = null;
                btn.setEnabled(true);
                edit.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            isRunning = !isRunning;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return rootView=inflater.inflate(R.layout.fragment_message, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void OnNetDiagnoFinished(String log) {
        String domainName = edit.getText().toString().trim();
        text.setText(log);
        System.out.println("");
        progress.setVisibility(View.GONE);
        btn.setText("开始诊断");
        btn.setEnabled(true);
        edit.setInputType(InputType.TYPE_CLASS_TEXT);
        isRunning = false;
//        SimpleMailSender sse = new SimpleMailSender();
//        sse.email(log,domainName);
        Log.i("client-----------", Client.client.toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PostJson();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void OnNetDiagnoUpdated(String log) {
        showInfo += log;
        text.setText(showInfo);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (_netDiagnoService != null) {
            _netDiagnoService.stopNetDialogsis();
        }
    }

    public String PostJson() throws IOException {
        String request = "http://3113abe4.ngrok.io/api/report/create";
        String msg = "";
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL(request).openConnection();
            //设置请求方式,请求超时信息
            conn.setRequestMethod("POST");
            conn.setReadTimeout(50000);
            conn.setConnectTimeout(50000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //设置运行输入,输出:
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //Post方式不能缓存,需手动设置为false
            conn.setUseCaches(false);
            //我们请求的数据:
            String data = Client.client.toString();
            //这里可以写一些请求头的东东...
            //获取输出流
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            if (conn.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                msg = new String(message.toByteArray());
                return msg;
            }
        }catch(Exception e){e.printStackTrace();}
        return msg;
    }

}
