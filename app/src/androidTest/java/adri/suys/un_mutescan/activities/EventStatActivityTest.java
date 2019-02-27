package adri.suys.un_mutescan.activities;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import adri.suys.un_mutescan.R;
import adri.suys.un_mutescan.model.Event;
import adri.suys.un_mutescan.model.User;
import androidx.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class EventStatActivityTest {

    @Rule
    ActivityTestRule<EventStatActivity> activityTestRule = new ActivityTestRule<>(EventStatActivity.class);

    @Test
    public void initBasic() throws ParseException {
        User user = new User(412, "adrien.suys@gmail.com", "$2y$13$fYRZ4t.zUDkm6yVzFKxeR.QXV83liep6m4JjgSh2s.MyG2SJwrYim", "");
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE).parse("2019-02-18 16:00:00");
        Event event = new Event(749997, "[Ticked-it] Mon anniversaire", 500, 0, 0, 0, date);
        Intent i = new Intent();
        i.putExtra("user", user);
        i.putExtra("event", event);
        activityTestRule.launchActivity(i);
        String s = "[Ticked-it] Mon anniversaire";
        onView(withText(s)).inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test
    public void initNotToday() throws ParseException {
        User user = new User(412, "adrien.suys@gmail.com", "$2y$13$fYRZ4t.zUDkm6yVzFKxeR.QXV83liep6m4JjgSh2s.MyG2SJwrYim", "");
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE).parse("2019-02-18 16:00:00");
        Event event = new Event(749997, "[Ticked-it] Mon anniversaire", 500, 0, 0, 0, date);
        Intent i = new Intent();
        i.putExtra("user", user);
        i.putExtra("event", event);
        activityTestRule.launchActivity(i);
        onView(withId(R.id.stat_event_btn_scan)).check(doesNotExist());
    }

    @Test
    public void initToday(){
        User user = new User(412, "adrien.suys@gmail.com", "$2y$13$fYRZ4t.zUDkm6yVzFKxeR.QXV83liep6m4JjgSh2s.MyG2SJwrYim", "");
        Event event = new Event(749997, "[Ticked-it] Mon anniversaire", 500, 0, 0, 0, new Date());
        Intent i = new Intent();
        i.putExtra("user", user);
        i.putExtra("event", event);
        activityTestRule.launchActivity(i);
        onView(withId(R.id.stat_event_btn_scan)).check(matches(isDisplayed()));
    }


}