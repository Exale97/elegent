package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class BubbleAdapter extends RecyclerView.Adapter<BubbleAdapter.BubbleViewHolder> {
    private final List<String> items;
    private final Set<Integer> selectedPositions = new HashSet<>();
    private final int[] colors = {
            R.color.bubble_color_1, R.color.bubble_color_2,
            R.color.bubble_color_3, R.color.bubble_color_4, R.color.bubble_color_5
    };

    public BubbleAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public BubbleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bubble_choice, parent, false);
        return new BubbleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BubbleViewHolder holder, int position) {
        String item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class BubbleViewHolder extends RecyclerView.ViewHolder {
        private final View bubbleView;
        private final TextView textView;

        public BubbleViewHolder(@NonNull View itemView) {
            super(itemView);
            bubbleView = itemView.findViewById(R.id.bubble_background);
            textView = itemView.findViewById(R.id.bubble_text);
        }

        void bind(String text, int position) {
            // 设置随机背景色
            int color = colors[new Random().nextInt(colors.length)];
            bubbleView.setBackgroundColor(itemView.getContext().getColor(color));

            // 设置选中状态
            if (selectedPositions.contains(position)) {
                bubbleView.setAlpha(0.5f); // 选中时半透明
            } else {
                bubbleView.setAlpha(1.0f);
            }

            // 点击事件
            itemView.setOnClickListener(v -> {
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove(position);
                } else {
                    if (selectedPositions.size() < 2) { // 限制最多选2项
                        selectedPositions.add(position);
                    }
                }
                notifyItemChanged(position); // 刷新当前项
            });

            textView.setText(text);
        }
    }

    // 获取选中项
    public List<String> getSelectedItems() {
        return selectedPositions.stream()
                .map(items::get)
                .collect(java.util.stream.Collectors.toList());
    }
}