package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import adri.suys.un_mutescan.model.User;
import androidx.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class EventListActivityTest {

    @Rule
    public ActivityTestRule<EventListActivity> activityTestRule = new ActivityTestRule<>(EventListActivity.class);

    @Test
    public void initOK(){
        initGoodActivity();
        String eventName = "[Ticked-it] Mon anniversaire";
        onView(withText(eventName)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void initKO(){
        initWrongActivity();
        String error = "Vous n'avez pas d'événements.";
        onView(withText(error)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    private void initGoodActivity() {
        User user = new User(412, "adrien.suys@gmail.com", "$2y$13$fYRZ4t.zUDkm6yVzFKxeR.QXV83liep6m4JjgSh2s.MyG2SJwrYim", "");
        Intent i = new Intent();
        i.putExtra("user", user);
        activityTestRule.launchActivity(i);
    }

    private void initWrongActivity() {
        User user = new User(411, "adrien.suys@gmail.com", "$2y$13$fYRZ4t.zUDkm6yVzFKxeR.QXV83liep6m4JjgSh2s.MyG2SJwrYim", "");
        Intent i = new Intent();
        i.putExtra("user", user);
        activityTestRule.launchActivity(i);
    }

}