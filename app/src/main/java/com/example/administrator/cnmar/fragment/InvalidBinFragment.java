package com.example.administrator.cnmar.fragment;


import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.administrator.cnmar.R;
import com.example.administrator.cnmar.entity.MyListView;
import com.example.administrator.cnmar.helper.UniversalHelper;
import com.example.administrator.cnmar.helper.UrlHelper;
import com.example.administrator.cnmar.helper.VolleyHelper;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import component.com.model.ComBox;
import component.com.vo.BoxTypeVo;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvalidBinFragment extends Fragment {
    private BillAdapter myAdapter;
    private Handler handler = new Handler();

    int page = 1;    //    page代表显示的是第几页内容，从1开始
    private int total; // 总页数
    private int num = 1; // 第几页
    private int count; // 数据总条数
    //    用来存放从后台取出的数据列表，作为adapter的数据源
    private List<ComBox> data = new ArrayList<>();
    private String url = UniversalHelper.getTokenUrl(UrlHelper.URL_BIN.replace("{type}", BoxTypeVo.failure.getKey() + "").replace("{page}", String.valueOf(page)));

    @BindView(R.id.etSearchInput)
    EditText etSearchInput;
    @BindView(R.id.ivDelete)
    ImageView ivDelete;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.tv4)
    TextView tv4;
    @BindView(R.id.tv5)
    TextView tv5;
    @BindView(R.id.tv6)
    TextView tv6;
    @BindView(R.id.tableLayout)
    TableLayout tableLayout;
    @BindView(R.id.listView)
    MyListView listView;
    @BindView(R.id.refreshLayout)
    TwinklingRefreshLayout refreshLayout;

    public InvalidBinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.refresh_frame, container, false);
        ButterKnife.bind(this, view);

        init();

        UniversalHelper.initRefresh(getActivity(), refreshLayout);
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                      下拉刷新默认显示第一页（10条）内容
                        page = 1;
                        getDataFromNet(url);
                        refreshLayout.finishRefreshing();
                    }
                }, 400);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page++;
//                            当page等于总页数的时候，提示“加载完成”，不能继续上拉加载更多
                        if (page == total) {
                            String url = UniversalHelper.getTokenUrl(UrlHelper.URL_BIN.replace("{type}", BoxTypeVo.failure.getKey() + "").replace("{page}", String.valueOf(page)));
                            Log.d("urlfinish", url);
                            getDataFromNet(url);
                            Toast.makeText(getActivity(), "加载完成", Toast.LENGTH_SHORT).show();
                            // 结束上拉刷新...
                            refreshLayout.finishLoadmore();
                            return;
                        }
                        String url = UniversalHelper.getTokenUrl(UrlHelper.URL_BIN.replace("{type}", BoxTypeVo.failure.getKey() + "").replace("{page}", String.valueOf(page)));
                        getDataFromNet(url);
                        Toast.makeText(getActivity(), "已加载更多", Toast.LENGTH_SHORT).show();
                        // 结束上拉刷新...
                        refreshLayout.finishLoadmore();
                    }
                }, 400);
            }
        });
        etSearchInput.setHint("料框编码查询");
        etSearchInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    String input = etSearchInput.getText().toString().trim();
                    if (input.equals("")) {
                        Toast.makeText(getActivity(), "请输入内容后再查询", Toast.LENGTH_SHORT).show();
                    } else {
                        String urlString = UrlHelper.URL_SEARCH_BIN.replace("{type}", BoxTypeVo.failure.getKey()+"").replace("{code}", input);
                        urlString = UniversalHelper.getTokenUrl(urlString);
                        getDataFromNet(urlString);
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
                if (s.toString().equals("")) {
                    ivDelete.setVisibility(View.GONE);
                    getDataFromNet(url);
                } else {
                    ivDelete.setVisibility(View.VISIBLE);
                }

            }
        });
        getDataFromNet(url);
        return view;
    }

    public void init(){
        tableLayout.setColumnCollapsed(9,false);
        tableLayout.setColumnCollapsed(10,false);
        tableLayout.setColumnCollapsed(11,false);
        tableLayout.setColumnCollapsed(12,false);
        tableLayout.setColumnStretchable(9,true);
        tableLayout.setColumnStretchable(11,true);
        tv1.setText("料框编码");
        tv2.setText("（子）加工单编号");
        tv3.setText("工序");
        tv4.setText("机台工位");
        tv5.setText("现存数量");
        tv6.setText("操作工");
    }

    /*
 * Fragment 从隐藏切换至显示，会调用onHiddenChanged(boolean hidden)方法
 * */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        Fragment重新显示到最前端中
        if (!hidden) {
            page = 1;
            getDataFromNet(url);
        }
    }


    public void getDataFromNet(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestQueue quene = Volley.newRequestQueue(getParentFragment().getActivity());
                StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        String json = VolleyHelper.getJson(s);
                        Log.d("GGGG", json);
                        component.common.model.Response response = JSON.parseObject(json, component.common.model.Response.class);
                        List<ComBox> list = JSON.parseArray(response.getData().toString(), ComBox.class);
                        count = response.getPage().getCount();
                        total = response.getPage().getTotal();
                        num = response.getPage().getNum();

                        //      数据小于10条或者当前页为最后一页就设置不能上拉加载更多
                        if (count <= 10 || num == total)
                            refreshLayout.setEnableLoadmore(false);
                        else
                            refreshLayout.setEnableLoadmore(true);
                        //  当前是第一页的时候，直接显示list内容；当显示更多页的时候，将后面页的list数据加到data中
                        if (num == 1) {
                            data = list;
                            myAdapter = new BillAdapter(data, getActivity());
                            listView.setAdapter(myAdapter);
                        } else {
                            data.addAll(list);
//                            myAdapter.notifyDataSetChanged();
                            myAdapter = new BillAdapter(data, getActivity());
                            listView.setAdapter(myAdapter);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("Tag", volleyError.toString());

                    }
                });
                quene.add(stringRequest);
            }
        }).start();
    }

    @OnClick({R.id.ivDelete, R.id.llSearch})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivDelete:
                etSearchInput.setText("");
                break;
            case R.id.llSearch:
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
                String input = etSearchInput.getText().toString().trim();
                if (input.equals("")) {
                    Toast.makeText(getActivity(), "请输入内容后再查询", Toast.LENGTH_SHORT).show();
                    return;
                }
                String urlString = UrlHelper.URL_SEARCH_BIN.replace("{type}", BoxTypeVo.failure.getKey()+"").replace("{code}", input);
                urlString = UniversalHelper.getTokenUrl(urlString);
                getDataFromNet(urlString);
                break;
        }
    }

    class BillAdapter extends BaseAdapter {
        private Context context;
        private List<ComBox> list = null;

        public BillAdapter(List<ComBox> list, Context context) {
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
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.table_list_item, parent, false);
                TableLayout tableLayout = (TableLayout) convertView.findViewById(R.id.tableLayout);
                tableLayout.setColumnCollapsed(9, false);
                tableLayout.setColumnCollapsed(10, false);
                tableLayout.setColumnCollapsed(11, false);
                tableLayout.setColumnCollapsed(12, false);
                tableLayout.setColumnStretchable(9, true);
                tableLayout.setColumnStretchable(11, true);
                TableRow tableRow = (TableRow) convertView.findViewById(R.id.table_row);
//                偶数行背景设为灰色
                if (position % 2 == 0)
                    tableRow.setBackgroundColor(getResources().getColor(R.color.color_light_grey));
                holder.tvBoxCode = (TextView) convertView.findViewById(R.id.column1);
                holder.tvCode = (TextView) convertView.findViewById(R.id.column2);
                holder.tvProcess = (TextView) convertView.findViewById(R.id.column3);
                holder.tvStation = (TextView) convertView.findViewById(R.id.column4);
                holder.tvNum = (TextView) convertView.findViewById(R.id.column5);
                holder.tvPerson = (TextView) convertView.findViewById(R.id.column6);

                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            holder.tvBoxCode.setText(list.get(position).getCode());
//            判断显示加工单还是子加工单
            if (list.get(position).getReceive().getPlan() != null) {
                holder.tvCode.setText(list.get(position).getReceive().getPlan().getCode());
            } else
                holder.tvCode.setText(list.get(position).getReceive().getBom().getCode());
//            工序的判断
            if (list.get(position).getReceive().getProcessProduct() != null) {
                holder.tvProcess.setText(list.get(position).getReceive().getProcessProduct().getName());
            }else
                holder.tvProcess.setText(list.get(position).getReceive().getProcessHalf().getName());

            holder.tvStation.setText(list.get(position).getStation().getName());
            holder.tvNum.setText(list.get(position).getNum()+"");
            holder.tvPerson.setText(list.get(position).getUser().getName());

            return convertView;
        }

        class ViewHolder {
            public TextView tvBoxCode;
            public TextView tvCode;
            public TextView tvProcess;
            public TextView tvStation;
            public TextView tvNum;
            public TextView tvPerson;
        }

    }
}
