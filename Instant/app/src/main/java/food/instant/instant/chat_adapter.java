package food.instant.instant;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mpauk on 3/20/2018.
 */

public class chat_adapter extends BaseAdapter {
    private Context context;
    private List<Message> messages;
    public chat_adapter(Context context,List<Message> messages){
        this.context=context;
        this.messages=messages;
    }
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Message message = messages.get(i);
        TextView content;
        System.out.println(SaveSharedPreference.getType(context).substring(0,5)+SaveSharedPreference.getId(context));
        System.out.println(message.getRecieverType()+message.getRecieverID());
        if(message.getRecieverType().equals(SaveSharedPreference.getType(context).substring(0,5)) && message.getRecieverID()==Integer.parseInt(SaveSharedPreference.getId(context))){
            view = LayoutInflater.from(context).inflate(R.layout.sent_message, null);
            content = view.findViewById(R.id.message_sent);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.recieved_message, null);
            content = view.findViewById(R.id.message_recieved);
        }
        content.setText(message.getMessage());
        return view;
    }
}
