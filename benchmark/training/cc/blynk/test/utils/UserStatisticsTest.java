package cc.blynk.test.utils;


import cc.blynk.server.core.dao.FileManager;
import cc.blynk.server.core.dao.UserKey;
import cc.blynk.server.core.model.DashBoard;
import cc.blynk.server.core.model.auth.User;
import cc.blynk.server.core.model.widgets.Widget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;


/**
 * User: ddumanskiy
 * Date: 09.12.13
 * Time: 8:07
 */
@Ignore
public class UserStatisticsTest {
    static FileManager fileManager;

    static Map<UserKey, User> users;

    @Test
    public void read() {
        System.out.println(("Registered users : " + (UserStatisticsTest.users.size())));
    }

    @Test
    public void printWidgetUsage() {
        System.out.println();
        System.out.println("Widget Usage :");
        Map<String, Integer> boards = new HashMap<>();
        for (User user : UserStatisticsTest.users.values()) {
            for (DashBoard dashBoard : user.profile.dashBoards) {
                if ((dashBoard.widgets) != null) {
                    for (Widget widget : dashBoard.widgets) {
                        Integer i = boards.get(widget.getClass().getSimpleName());
                        if (i == null) {
                            i = 0;
                        }
                        boards.put(widget.getClass().getSimpleName(), (++i));
                    }
                }
            }
        }
        for (Map.Entry<String, Integer> entry : boards.entrySet()) {
            System.out.println((((entry.getKey()) + " : ") + (entry.getValue())));
        }
    }

    @Test
    public void printDashFilling() {
        System.out.println();
        System.out.println("Dashboard Space Usage :");
        List<Integer> all = new ArrayList<>();
        for (User user : UserStatisticsTest.users.values()) {
            for (DashBoard dashBoard : user.profile.dashBoards) {
                if ((dashBoard.widgets.length) > 3) {
                    int sum = 0;
                    for (Widget widget : dashBoard.widgets) {
                        sum += (widget.height) * (widget.width);
                    }
                    all.add(sum);
                }
            }
        }
        Collections.sort(all);
        System.out.println(("Mediana of cells used : " + (all.get(((all.size()) / 2)))));
        System.out.println((("Avg. percentage of space filling : " + (((all.get(((all.size()) / 2))) * 100) / 72)) + "%"));
        // System.out.println("Average filled square per dash : " + (sum / dashes));
        // System.out.println("Percentage : " + (int)((sum / dashes) * 100 / 72));
    }

    @Test
    public void printOutdatedProfiles() {
        long now = System.currentTimeMillis();
        int counter = 0;
        long PERIOD = (((1000L * 60) * 60) * 24) * 90;
        for (User user : UserStatisticsTest.users.values()) {
            if ((user.lastModifiedTs) < (now - PERIOD)) {
                counter++;
            }
        }
        System.out.println(("Profiles not updated more then 3 months : " + counter));
    }

    @Test
    public void dashesPerUser() {
        int usersCounter = 0;
        int dashesCounter = 0;
        int maxDashes = 0;
        int widgetCount = 0;
        for (User user : UserStatisticsTest.users.values()) {
            if ((user.profile.dashBoards.length) == 0) {
                continue;
            }
            usersCounter++;
            dashesCounter += user.profile.dashBoards.length;
            maxDashes = Math.max(user.profile.dashBoards.length, maxDashes);
            for (DashBoard dash : user.profile.dashBoards) {
                widgetCount += dash.widgets.length;
            }
        }
        System.out.println(("Dashboards per user : " + (((double) (dashesCounter)) / usersCounter)));
        System.out.println(("Widgets per user : " + (((double) (widgetCount)) / usersCounter)));
        System.out.println(("Max dashes : " + maxDashes));
    }
}

