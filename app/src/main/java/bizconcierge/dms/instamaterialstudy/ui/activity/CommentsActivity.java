package bizconcierge.dms.instamaterialstudy.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import bizconcierge.dms.instamaterialstudy.R;
import bizconcierge.dms.instamaterialstudy.ui.adapter.CommentsAdapter;
import bizconcierge.dms.instamaterialstudy.ui.utils.Utils;
import butterknife.BindView;

/**
 * Created by USER on 7/26/2017.
 */

public class CommentsActivity extends BaseDrawerActivity{
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.contentRoot)
    LinearLayout contentRoot;
    @BindView(R.id.rvComments)
    RecyclerView rvComments;
    @BindView(R.id.llAddComment)
    LinearLayout llAddComment;

    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

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
