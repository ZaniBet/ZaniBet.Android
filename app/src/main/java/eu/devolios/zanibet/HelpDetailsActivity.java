package eu.devolios.zanibet;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import eu.devolios.zanibet.model.Help;
import eu.devolios.zanibet.model.QuestionAnswer;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.devolios.zanibet.utils.PicassoImageGetter;
import iammert.com.expandablelib.ExpandCollapseListener;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;


/**
 * Created by Gromat Luidgi on 15/11/2017.
 */

public class HelpDetailsActivity extends AppCompatActivity {

    @BindView(R.id.expandableLayout)
    ExpandableLayout mExpandableLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.subjectTextView)
    TextView mSubjectTextView;

    private Help mHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_details);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        mHelp = (Help) bundle.getSerializable("help");

        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(new IconDrawable(getApplicationContext(), MaterialIcons.md_arrow_back).colorRes(R.color.colorWhite).actionBarSize());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(mToolbar);

        mSubjectTextView.setText(mHelp.getSubject().toUpperCase());

        mExpandableLayout.setRenderer(new ExpandableLayout.Renderer<QuestionAnswer, QuestionAnswer>() {
            @Override
            public void renderParent(View view, QuestionAnswer model, boolean isExpanded, int parentPosition) {
                ((TextView) view.findViewById(R.id.tvParent)).setText(model.getQuestion());
                ((ImageView) view.findViewById(R.id.arrow)).setImageDrawable(new IconDrawable(getApplicationContext(), MaterialIcons.md_expand_more));
            }

            @Override
            public void renderChild(View view, QuestionAnswer model, int parentPosition, int childPosition) {
                TextView textView = ((TextView) view.findViewById(R.id.tvChild));
                PicassoImageGetter imageGetter = new PicassoImageGetter(textView, getApplicationContext());
                textView.setText(Html.fromHtml(model.getAnswer(), imageGetter, null));
            }
        });

        mExpandableLayout.setExpandListener(new ExpandCollapseListener.ExpandListener<QuestionAnswer>() {
            @Override
            public void onExpanded(int parentIndex, QuestionAnswer parent, View view) {
                //Layout expanded
                ((ImageView) view.findViewById(R.id.arrow)).setImageDrawable(new IconDrawable(getApplicationContext(), MaterialIcons.md_expand_less));

            }
        });

        mExpandableLayout.setCollapseListener(new ExpandCollapseListener.CollapseListener<QuestionAnswer>() {
            @Override
            public void onCollapsed(int parentIndex, QuestionAnswer parent, View view) {
                //Layout collapsed
                ((ImageView) view.findViewById(R.id.arrow)).setImageDrawable(new IconDrawable(getApplicationContext(), MaterialIcons.md_expand_more));
            }
        });

        for (int i = 0; i < mHelp.getQuestionAnswers().length; i++){
            Section<QuestionAnswer, QuestionAnswer> section = new Section<>();
            section.parent = mHelp.getQuestionAnswers()[i];
            section.children.add(mHelp.getQuestionAnswers()[i]);
            mExpandableLayout.addSection(section);
        }
    }

}
