package ando.widget.wheelview.timer;

import android.os.Handler;
import android.os.Message;

import ando.widget.wheelview.WheelView;

import androidx.annotation.NonNull;

/**
 * Handler 消息类
 *
 * @author 小嵩
 * date: 2017-12-23 23:20:44
 */
public final class MessageHandler extends Handler {
    public static final int WHAT_INVALIDATE_LOOP_VIEW = 1000;
    public static final int WHAT_SMOOTH_SCROLL = 2000;
    public static final int WHAT_ITEM_SELECTED = 3000;

    private final WheelView wheelView;

    public MessageHandler(@NonNull WheelView wheelView) {
        super(wheelView.getContext().getMainLooper());
        this.wheelView = wheelView;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_INVALIDATE_LOOP_VIEW:
                wheelView.invalidate();
                break;
            case WHAT_SMOOTH_SCROLL:
                wheelView.smoothScroll(WheelView.ACTION.FLING);
                break;
            case WHAT_ITEM_SELECTED:
                wheelView.onItemSelected();
                break;
            default:
        }
    }
}