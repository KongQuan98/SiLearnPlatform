package com.example.imagepro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{

    private List<DataModel> modelList;
    private List<String> list = new ArrayList<>();
    private List<String> videolist = new ArrayList<>();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public NestedAdapter getNestedAdapter() {
        return nestedAdapter;
    }

    public void setNestedAdapter(NestedAdapter nestedAdapter) {
        this.nestedAdapter = nestedAdapter;
    }

    public NestedAdapter nestedAdapter;

    public ItemAdapter(List<DataModel> modelList){
        this.modelList = modelList;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        DataModel model = modelList.get(position);
        holder.textView.setText(model.getItemText());

        if (model.getItemText().equals("Alphabet")){
            holder.textCount.setText((int) model.getCount() + " / 26");
        }else if (model.getItemText().equals("Numbers")){
            holder.textCount.setText((int) model.getCount() + " / 10");
        }else{
            holder.textCount.setText((int) model.getCount() + " / 5");
        }

        boolean isExpandable = model.isExpandable();
        holder.relativeLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

        if (isExpandable){
            holder.imageView.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
        }else{
            holder.imageView.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
        }

        nestedAdapter = new NestedAdapter(list, videolist, model.getItemText(), model.getCheckAlph());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setAdapter(nestedAdapter);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setExpandable(!model.isExpandable());
                list = model.getNestedList();
                videolist = model.getNestedVideoList();
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout linearLayout;
        private RelativeLayout relativeLayout;
        private TextView textView, textCount;
        private ImageView imageView;
        private RecyclerView recyclerView;

        public ItemViewHolder(@NonNull View itemView){
            super(itemView);

            linearLayout = itemView.findViewById(R.id.Linear_layout);
            relativeLayout = itemView.findViewById(R.id.expandable_layout);
            textView = itemView.findViewById(R.id.itemText);
            textCount = itemView.findViewById(R.id.itemCount);
            imageView = itemView.findViewById(R.id.itemArrow);
            recyclerView = itemView.findViewById(R.id.recycleView);

        }
    }

    //Search function
    public void filterList(List<DataModel> filteredList){
        modelList = filteredList;
        notifyDataSetChanged();
    }
}
