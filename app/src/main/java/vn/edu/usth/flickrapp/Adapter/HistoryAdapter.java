package vn.edu.usth.flickrapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.usth.flickrapp.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<String> dataList; // Dữ liệu hiển thị trong RecyclerView

    // Constructor để truyền dữ liệu vào Adapter
    public HistoryAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    // Được gọi khi RecyclerView cần tạo ViewHolder mới
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history, parent, false);
        return new ViewHolder(view);
    }

    // Được gọi khi RecyclerView cần cập nhật nội dung của ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = dataList.get(position);
        holder.textView.setText(item);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Lớp ViewHolder để tham chiếu đến các phần tử giao diện trong mỗi mục
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txt_history);
        }
    }
}
