package com.example.takingcontacts;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.virua.project10.R;

import java.util.ArrayList;

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.MyViewHolder> {

//    private final RecyclerViewInterface recyclerViewInterface;
    ArrayList<String> arr_phone = new ArrayList<>();
    ArrayList<String> arr_names = new ArrayList<>();
    ArrayList<Uri> arr_image = new ArrayList<Uri>();
    private final RecyclerViewInterface recyclerViewInterface;

    public MyAdaptor(ArrayList<String> phone, ArrayList<String> names,ArrayList<Uri> image, RecyclerViewInterface recyclerViewInterface) {
        this.arr_phone = phone;
        this.arr_names = names;
        this.arr_image = image;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        View view =  inflater.inflate(R.layout.listview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.phone.setText(arr_phone.get(position));
        holder.name.setText(arr_names.get(position));
        holder.image.setImageURI(arr_image.get(position));

    }

    @Override
    public int getItemCount() {
        return arr_phone.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView phone;
        private TextView name;
        private ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            phone = (TextView) itemView.findViewById(R.id.phone);
            name = (TextView) itemView.findViewById(R.id.name_two);
            image = itemView.findViewById(R.id.person);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface!=null){
                        int pos =  getBindingAdapterPosition();
                        if(pos!= RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}