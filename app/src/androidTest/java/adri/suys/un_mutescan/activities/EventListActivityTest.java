package adri.suys.un_mutescan.activities;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.dataholder.UnMuteDataHolder;
import adri.suys.un_mutescan.model.User;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class EventListActivityTest {

    @Rule
    public ActivityTestRule<EventListActivity> activityTestRule = new ActivityTestRule<>(EventListActivity.class);

    @Test
    public void initOK() {
        String eventName = "MES EVENEMENTS";
        onView(withText(eventName)).check(matches(anything()));
    }

    @Test
    public void initEvents(){
        //initEvent(16, "Barema en concert");
        initEvent(1, "Fred & The Healers au Spacium");
        initEvent(4, "Christian Olivier (Têtes Raides) & Piwi Leman au Spacium");
        //initEvent(9, "Mon event Fidelity");
    }

    @BeforeClass
    public static void initGoodActivity() {
        User user = new User(412, "adrien.suys@gmail.com", "$2y$13$fYRZ4t.zUDkm6yVzFKxeR.QXV83liep6m4JjgSh2s.MyG2SJwrYim", "");
        UnMuteDataHolder.setUser(user);
    }

    private void initEvent(int pos, String eventName){
        android.support.test.espresso.idling.CountingIdlingResource resource = activityTestRule.getActivity().getPresenter().getCountingIdlingResource();
        IdlingRegistry.getInstance().register(resource);
        onView(withId(R.id.event_recycler_view))
                .perform(RecyclerViewActions.
                        actionOnItemAtPosition(pos, MyViewAction.clickChildViewWithId(R.id.event_info_stat)
                        )
                );
        onData(withText(eventName)).check(matches(anything()));
    }

    public static class MyViewAction{
        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }
    }


}