package com.fenghks.business.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fenghks.business.AppConstants;
import com.fenghks.business.R;
import com.fenghks.business.bean.Printer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fenghuo on 2017/9/11.
 */

public class PrintersListAdapter extends RecyclerView.Adapter<PrintersListAdapter.PrinterViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private OnPrintersItemClickListener onPrintersItemClickListener;
    private OnSettingClickListener onSettingClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private OnPauseClickListener onPauseClickListener;
    private List<Printer> printerList = new ArrayList<>();
    private Printer printer;

    public PrintersListAdapter(Context context, List<Printer> printerList) {
        this.mContext = context;
        this.printerList = printerList;
        if (null != mContext) {
            inflater = LayoutInflater.from(mContext);
        }
    }

    @Override
    public PrinterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_printers, parent, false);
        return new PrinterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PrinterViewHolder holder, final int position) {
        // 绑定数据
        printer = printerList.get(position);
        holder.printerName.setText(printer.getName());
        if (printer.getPrinterType() == 1) {
            holder.printerType.setText(R.string.network_printer);
            holder.printerImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_network_printer));
        } else {
            holder.printerType.setText(R.string.usb_printer);
            holder.deletePrinter.setVisibility(View.INVISIBLE);
            holder.printerImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_usb_printer));
        }
        if (printer.getStatus() == AppConstants.STATUS_NORMAL) {
            holder.status.setText(R.string.status_nomal);
            holder.status.setTextColor(mContext.getResources().getColor(R.color.gray));
        } else if (printer.getStatus() == AppConstants.STATUS_SETTING) {
            holder.status.setText(R.string.modifying);
            holder.settingText.setText(R.string.modifying);
            holder.status.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else if (printer.getStatus() == AppConstants.STATUS_PAUSEING) {
            holder.status.setText(R.string.printer_pauseing);
            holder.pauseText.setText(R.string.printer_pauseing);
            holder.status.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else if(printer.getStatus() == AppConstants.STATUS_DELETING){
            holder.status.setText(R.string.deleting);
            holder.deleteText.setText(R.string.deleting);
            holder.status.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }else if (printer.getStatus() == AppConstants.STATUS_SET_FAILURE) {
            holder.status.setText(R.string.printer_setting_failure);
            holder.settingText.setText(R.string.printer_setting);
            holder.status.setTextColor(mContext.getResources().getColor(R.color.red));
        } else if (printer.getStatus() == AppConstants.STATUS_PAUSE_SUCCEED) {
            holder.status.setText(R.string.printer_pause_succeed);
            holder.pauseText.setText(R.string.printer_restart);
            holder.status.setTextColor(mContext.getResources().getColor(R.color.gray));
        } else if(printer.getStatus() == AppConstants.STATUS_PAUSE_FAILURE){
            holder.status.setText(R.string.printer_pause_failure);
            holder.pauseText.setText(R.string.printer_pause);
            holder.status.setTextColor(mContext.getResources().getColor(R.color.red));
        }else if(printer.getStatus() == AppConstants.STATUS_DELETE_FAILURE){
            holder.status.setText(R.string.delete_failure);
            holder.deleteText.setText(R.string.printer_delete);
            holder.status.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        if (null != onSettingClickListener) {
            holder.setPrinter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSettingClickListener.onClick(position);
                }
            });
        }
        if (null != onPauseClickListener) {
            holder.pausePrinter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPauseClickListener.onClick(position);
                }
            });
        }
        if (null != onDeleteClickListener) {
            holder.deletePrinter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClickListener.onClick(position);
                }
            });
        }
        if (onPrintersItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPrintersItemClickListener.onClick(position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onPrintersItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(PrinterViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            int status = (int) payloads.get(0);
            switch (status) {
                case AppConstants.STATUS_NORMAL:    //正常使用
                    holder.status.setText(R.string.status_nomal);
                    holder.settingText.setText(R.string.printer_setting);
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.gray));
                    break;
                case AppConstants.STATUS_SETTING:   //设置中
                    holder.status.setText(R.string.modifying);
                    holder.settingText.setText(R.string.modifying);
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                    break;
                case AppConstants.STATUS_PAUSEING:   //停用中
                    holder.status.setText(R.string.printer_pauseing);
                    holder.pauseText.setText(R.string.printer_pauseing);
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                    break;
                case AppConstants.STATUS_DELETING:   //删除中
                    holder.status.setText(R.string.deleting);
                    holder.deleteText.setText(R.string.deleting);
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                    break;
                case AppConstants.STATUS_SET_FAILURE:   //设置失败
                    holder.status.setText(R.string.printer_setting_failure);
                    holder.settingText.setText(R.string.printer_setting);
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.red));
                    break;
                case AppConstants.STATUS_PAUSE_SUCCEED:   //停用成功
                    holder.status.setText(R.string.printer_pause_succeed);
                    holder.pauseText.setText(R.string.printer_restart);
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.gray));
                    break;
                case AppConstants.STATUS_PAUSE_FAILURE:   //停用失败
                    holder.status.setText(R.string.printer_pause_failure);
                    holder.pauseText.setText(R.string.printer_pause);
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.red));
                    break;
                case AppConstants.STATUS_DELETE_FAILURE:   //删除失败
                    holder.status.setText(R.string.delete_failure);
                    holder.deleteText.setText(R.string.printer_delete);
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.red));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.d("PrintersListAdapter", "打印机列表长度为：" + printerList.size());
        return printerList.size();
    }


    public interface OnPrintersItemClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }

    public interface OnSettingClickListener {
        void onClick(int position);
    }

    public interface OnPauseClickListener {
        void onClick(int position);
    }

    public interface OnDeleteClickListener {
        void onClick(int position);
    }

    public void setOnPrintersItemClickListener(OnPrintersItemClickListener onPrintersItemClickListener) {
        this.onPrintersItemClickListener = onPrintersItemClickListener;
    }

    public void setOnPauseClickListener(OnPauseClickListener onPauseClickListener) {
        this.onPauseClickListener = onPauseClickListener;
    }

    public void setOnSettingClickListener(OnSettingClickListener onSettingClickListener) {
        this.onSettingClickListener = onSettingClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }


    class PrinterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.printer_image)
        ImageView printerImage;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.printer_name)
        TextView printerName;
        @BindView(R.id.printer_type)
        TextView printerType;
        @BindView(R.id.set_printer)
        LinearLayout setPrinter;
        @BindView(R.id.pause_printer)
        LinearLayout pausePrinter;
        @BindView(R.id.delete_printer)
        LinearLayout deletePrinter;
        @BindView(R.id.setting_text)
        TextView settingText;
        @BindView(R.id.pause_text)
        TextView pauseText;
        @BindView(R.id.delete_text)
        TextView deleteText;

        public PrinterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
