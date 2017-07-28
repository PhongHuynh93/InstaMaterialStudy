package bizconcierge.dms.instamaterialstudy.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;

import bizconcierge.dms.instamaterialstudy.R;
import bizconcierge.dms.instamaterialstudy.ui.adapter.CommentsAdapter;
import bizconcierge.dms.instamaterialstudy.ui.utils.Utils;
import bizconcierge.dms.instamaterialstudy.ui.view.SendCommentButton;
import butterknife.BindView;

/**
 * Created by USER on 7/26/2017.
 */

public class CommentsActivity extends BaseDrawerActivity {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.contentRoot)
    LinearLayout contentRoot;
    @BindView(R.id.rvComments)
    RecyclerView rvComments;
    @BindView(R.id.llAddComment)
    LinearLayout llAddComment;
    @BindView(R.id.btnSendComment)
    SendCommentButton btnSendComment;
    @BindView(R.id.etComment)
    EditText etComment;

    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        setupComments();
        setupSendCommentButton();
        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);

        // animate only the first tiem
        if (savedInstanceState == null) {
//            info - Thanks onPreDrawListener animation will be performed at the moment when all views in the tree have been measured and given a frame,
// // but drawing operation hasn’t started yet.
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }
    }

    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(new SendCommentButton.OnSendClickListener() {
            @Override
            public void onSendClickListener(View v) {
                if (validateComment()) {
                    commentsAdapter.addItem();
                    // info - when add the new item, make an animation
                    commentsAdapter.setAnimationsLocked(false);
                    commentsAdapter.setDelayEnterAnimation(false);
                    // move to the last item
                    rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());

                    etComment.setText(null);
                    btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
                }
            }
        });
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(etComment.getText())) {
            // info - shake animation
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }

    // info - animation when exit this UI
    @Override
    public void onBackPressed() {
        // info - move the content out of screen
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        // not used the animation when exit the app
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }


    private void setupComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(this);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    private void startIntroAnimation() {
//         info how set ele = 0 here
        // basic animation, move out of box and navigate back to the original location.
        ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        // info - after expend animation, it will show the datas.
        commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();
    }
}
