package org.loofer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.loofer.photo.SelectPicActivity;
import org.loofer.utils.ToastUtils;
import org.loofer.view.FullyGridLayoutManager;
import org.loofer.watermark.R;

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
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
        public void onBindViewHolder(final ListHolder holder, final int position) {
            holder.icon.setImageResource(iconA[position]);
            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            startActivity(new Intent(HomeActivity.this, SelectPicActivity.class));
                            break;
                        case 1:
                            break;

                    }
                    ToastUtils.showToast(holder.mItemView.getContext(), "点击事件：" + position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return iconA.length;
        }


        class ListHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            View mItemView;

            ListHolder(View itemView) {
                super(itemView);
                mItemView = itemView;
                icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            }
        }


    }


}
