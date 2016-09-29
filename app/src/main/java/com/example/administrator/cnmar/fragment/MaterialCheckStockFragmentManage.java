package com.example.administrator.cnmar.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.example.administrator.cnmar.MaterialCheckStockManageActivity;
import com.example.administrator.cnmar.R;
import com.example.administrator.cnmar.entity.MyListView;
import com.example.administrator.cnmar.helper.UniversalHelper;
import com.example.administrator.cnmar.helper.UrlHelper;
import com.example.administrator.cnmar.http.VolleyHelper;

import java.util.List;

import component.basic.vo.StockTypeVo;
import component.material.model.MaterialStock;

/**
 * A simple {@link Fragment} subclass.
 */
public class MaterialCheckStockFragmentManage extends Fragment {
    private MyListView lvCheckManage;
    private LinearLayout llSearch;
    private EditText etSearchInput;
    private PtrClassicFrameLayout ptrFrame;
    private Handler handler = new Handler();
    int page=0;
    private TextView tvField1,tvField2,tvField3,tvField4;
    private String url= UniversalHelper.getTokenUrl(UrlHelper.URL_CHECK_MANAGE);

    public MaterialCheckStockFragmentManage() {
//        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_check_stock_fragment_manage, container, false);
        lvCheckManage= (MyListView) view.findViewById(R.id.lvTable);
//        lvCheckManage.addFooterView(new ViewStub(getParentFragment().getActivity()));

        llSearch= (LinearLayout) view.findViewById(R.id.llSearch);
        tvField1= (TextView) view.findViewById(R.id.column1);
        tvField2= (TextView) view.findViewById(R.id.column2);
        tvField3= (TextView) view.findViewById(R.id.column3);
        tvField4= (TextView) view.findViewById(R.id.column4);
        tvField1.setText("原料编码");
        tvField2.setText("原料名称");
        tvField3.setText("库存总量");
        tvField4.setText("操作");

        ptrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.ptrFrame);
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh(true);
            }
        }, 150);
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page = 0;
//                        mData.clear();
//                        for (int i = 0; i < 17; i++) {
//                            mData.add(new String("  ListView item  -" + i));
//                        }
//                        mAdapter.notifyDataSetChanged();
                        getCheckStockManageListFromNet(url);
                        ptrFrame.refreshComplete();

//                        if (!ptrFrame.isLoadMoreEnable()) {
//                        ptrFrame.setLoadMoreEnable(true);
//                            }

                    }
                }, 1500);
            }
        });
        ptrFrame.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
//                        mData.add(new String("  ListView item  - add " + page));
//                        mAdapter.notifyDataSetChanged();
                        ptrFrame.loadMoreComplete(true);
                        page++;
                        Toast.makeText(getActivity(), "加载完成", Toast.LENGTH_SHORT)
                                .show();

                        if (page == 1) {
                            //set load more disable
//                            ptrClassicFrameLayout.setLoadMoreEnable(false);
                        }
                    }
                }, 1000);
            }
        });
        etSearchInput= (EditText) view.findViewById(R.id.etSearchInput);
        etSearchInput.setHint("原料编码查询");
        etSearchInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode==KeyEvent.KEYCODE_ENTER){
                    String input=etSearchInput.getText().toString().trim();
                    if(input.equals("")){
                        Toast.makeText(getActivity(),"请输入内容后再查询",Toast.LENGTH_SHORT).show();
                    }else{
                        String urlString=UrlHelper.URL_SEARCH_CHECK_MANAGE.replace("{query.code}",input);
                        urlString=UniversalHelper.getTokenUrl(urlString);
                        getCheckStockManageListFromNet(urlString);
                    }
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }

        });
        etSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(""))
                    getCheckStockManageListFromNet(url);
            }
        });
        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input=etSearchInput.getText().toString().trim();
                String urlString=UrlHelper.URL_SEARCH_CHECK_MANAGE.replace("{query.code}",input);
                urlString=UniversalHelper.getTokenUrl(urlString);

                getCheckStockManageListFromNet(urlString);
            }
        });
        getCheckStockManageListFromNet(url);
        return view;
    }

    public void getCheckStockManageListFromNet(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue quene= Volley.newRequestQueue(getParentFragment().getActivity());
                Log.d("Tag","开始执行");
                StringRequest stringRequest=new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String json= VolleyHelper.getJson(s);
                        Log.d("GGGG",json);
                        component.common.model.Response response= JSON.parseObject(json, component.common.model.Response.class);
                        List<MaterialStock> list= JSON.parseArray(response.getData().toString(),MaterialStock.class );
                        BillAdapter myAdapter=new BillAdapter(list,getParentFragment().getActivity());
                        lvCheckManage.setAdapter(myAdapter);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("Tag",volleyError.toString());

                    }
                });
                quene.add(stringRequest);
            }
        }).start();
    }

    class BillAdapter extends BaseAdapter {
        private Context context;
        private List<MaterialStock> list=null;

        public BillAdapter(List<MaterialStock> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if(convertView==null){
                holder=new ViewHolder();
                convertView= LayoutInflater.from(context).inflate(R.layout.table_list_item,parent,false);
                holder.tvMaterialCode= (TextView) convertView.findViewById(R.id.column1);
                holder.tvMaterialName= (TextView) convertView.findViewById(R.id.column2);
                holder.tvStockNum= (TextView) convertView.findViewById(R.id.column3);
                holder.tvCheck= (TextView) convertView.findViewById(R.id.column4);
                convertView.setTag(holder);
            }else
                holder= (ViewHolder) convertView.getTag();
//            Log.d("GGGG", DateFormat.getDateInstance().format(list.get(position).getArrivalDate()));
            holder.tvMaterialCode.setText(list.get(position).getMaterial().getCode());
            holder.tvMaterialName.setText(list.get(position).getMaterial().getName());
            holder.tvStockNum.setText(String.valueOf(list.get(position).getStock()));
            if(list.get(position).getMaterial().getStockType()== StockTypeVo.SCAN.getKey()){
                holder.tvCheck.setText("");
            }else{
                holder.tvCheck.setText("盘点");
                holder.tvCheck.setTextColor(getResources().getColor(R.color.colorBase));
            }
            holder.tvCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MaterialCheckStockManageActivity.class);
                    intent.putExtra("ID", list.get(position).getId());
//                    intent.putExtra("flag", 0);
                    context.startActivity(intent);
                }
            });
            return convertView;
        }

        class ViewHolder{
            public  TextView tvMaterialCode;
            public  TextView tvMaterialName;
            public  TextView tvStockNum;
            public  TextView tvCheck;
        }

    }
}
