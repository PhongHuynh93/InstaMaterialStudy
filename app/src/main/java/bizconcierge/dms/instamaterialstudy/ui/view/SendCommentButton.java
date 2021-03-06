package bizconcierge.dms.instamaterialstudy.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewAnimator;

import bizconcierge.dms.instamaterialstudy.R;


/**
 * Created by froger_mcs on 01.12.14.
 * info - viewanimator is the framelayout, used to animate switching between view
 * <a href="http://abhiandroid.com/ui/viewanimator"></a>
 *
 * ViewAnimator as a base class for our button.
 * It has one functionality which is the most interesting for us -
 * it can perform enter and exit animations when switching between its child views.
 */
public class SendCommentButton extends ViewAnimator implements View.OnClickListener {
    public static final int STATE_SEND = 0;
    public static final int STATE_DONE = 1;

    // time between 2 change state
    private static final long RESET_STATE_DELAY_MILLIS = 2000;

    private int currentState;

    private OnSendClickListener onSendClickListener;

    private Runnable revertStateRunnable = new Runnable() {
        @Override
        public void run() {
            setCurrentState(STATE_SEND);
        }
    };

    public SendCommentButton(Context context) {
        super(context);
        init();
    }

    public SendCommentButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_send_comment_button, this, true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        currentState = STATE_SEND;
        super.setOnClickListener(this);
    }

    /**
     * info - nhớ này, mấy cái runnable phải remove khi detect window
     * onDetachedFromWindow(): removes revertStateRunnable callback in case you scheduled one, but haven’t finished yet
     * (i.e. you destroy the Activity before button state switches back).
     */
    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(revertStateRunnable);
        super.onDetachedFromWindow();
    }

    public void setCurrentState(int state) {
        if (state == currentState) {
            return;
        }

        currentState = state;
        if (state == STATE_DONE) {
            setEnabled(false);
            postDelayed(revertStateRunnable, RESET_STATE_DELAY_MILLIS);
            setInAnimation(getContext(), R.anim.slide_in_done);
            setOutAnimation(getContext(), R.anim.slide_out_send);
        } else if (state == STATE_SEND) {
            setEnabled(true);
            setInAnimation(getContext(), R.anim.slide_in_send);
            setOutAnimation(getContext(), R.anim.slide_out_done);
        }
        showNext();
    }

    @Override
    public void onClick(View v) {
        if (onSendClickListener != null) {
            onSendClickListener.onSendClickListener(this);
        }
    }

    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        this.onSendClickListener = onSendClickListener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        //Do nothing, you have you own onClickListener implementation (OnSendClickListener)
    }

    public interface OnSendClickListener {
        public void onSendClickListener(View v);
    }
}