package org.loofer;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.loofer.utils.ToastUtils;
import org.loofer.view.FullyGridLayoutManager;
import org.loofer.watermark.R;

import java.io.InputStream;

import static org.loofer.watermark.R.id.recyclerView;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

    }

    private void initData() {
        mRecyclerView.setNestedScrollingEnabled(false);
        FullyGridLayoutManager layoutManager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setAdapter(new HoneAdapter());
        mRecyclerView.setLayoutManager(layoutManager);

    }

    class HoneAdapter extends RecyclerView.Adapter<HoneAdapter.ListHolder> {
        int iconA[] = {R.drawable.ic_camera, R.drawable.ic_album, R.drawable.ic_camera,
                R.drawable.ic_album, R.drawable.ic_camera, R.drawable.ic_album,
                R.drawable.ic_camera, R.drawable.ic_album, R.drawable.ic_camera};

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home, parent, false);
            return new ListHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ListHolder holder, int position) {
            holder.icon.setImageResource(iconA[position]);
        }

        @Override
        public int getItemCount() {
            return iconA.length;
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int position = (int) v.getTag();
//                ToastUtils.showToast(HomeActivity.this, "已被点击位置:" + position);

            }
        };


        class ListHolder extends RecyclerView.ViewHolder {
            ImageView icon;

            ListHolder(View itemView) {
                super(itemView);
//                itemView.setOnClickListener(onClickListener);
                icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            }

            public void setData(int position) {
                icon.setImageResource(iconA[position]);

            }
        }


    }


}
