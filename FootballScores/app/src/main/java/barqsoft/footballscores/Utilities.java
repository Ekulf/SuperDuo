package barqsoft.footballscores;

import android.content.Context;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;

    public static String getLeague(Context context, int league_num) {
        switch (league_num) {
            case SERIE_A:
                return context.getString(R.string.league_serie_a);
            case PREMIER_LEGAUE:
                return context.getString(R.string.league_premier_league);
            case CHAMPIONS_LEAGUE:
                return context.getString(R.string.league_champions_league);
            case PRIMERA_DIVISION:
                return context.getString(R.string.league_primera_division);
            case BUNDESLIGA:
                return context.getString(R.string.league_bundesliga);
            default:
                return context.getString(R.string.league_unknown);
        }
    }

    public static String getMatchDay(Context context, int match_day, int league_num) {
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                return context.getString(R.string.match_day_group_stages);
            } else if (match_day == 7 || match_day == 8) {
                return context.getString(R.string.match_day_first_knockout_round);
            } else if (match_day == 9 || match_day == 10) {
                return context.getString(R.string.match_day_quarter_final);
            } else if (match_day == 11 || match_day == 12) {
                return context.getString(R.string.match_day_semi_final);
            } else {
                return context.getString(R.string.match_day_final);
            }
        } else {
            return context.getString(R.string.match_day_default, match_day);
        }
    }

    public static String getScores(Context context, int home_goals, int away_goals) {
        if (home_goals < 0 || away_goals < 0) {
            return context.getString(R.string.score_string, "", "");
        } else {
            return context.getString(R.string.score_string, home_goals, away_goals);
        }
    }

    public static int getTeamCrestByTeamName(String teamName) {
        if (teamName == null) {
            return R.drawable.no_icon;
        }

        switch (teamName) { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            default:
                return R.drawable.no_icon;
        }
    }
}
