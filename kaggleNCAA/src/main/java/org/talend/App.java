package org.talend;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Hello world!
 */
public class App {
    private static final String CSV_SPLIT_BY = ",";

    public static void main(String[] args) {
        Map<String, String> teams = getTeams();
        List<String[]> results = getResults();

        Map<String, Map<String, Integer>> nbVictories = new HashMap<>();
        Map<String, Map<String, Integer>> nbDefeats = new HashMap<>();

        results.forEach(result -> {
            String season = result[0];
            String winTeam = result[2];
            String looseTeam = result[4];

            Map<String, Integer> yearAndVictory = nbVictories.get(winTeam);
            if(yearAndVictory == null) {
                yearAndVictory = new HashMap<>();
            }

            Integer nbVictory = yearAndVictory.get(season);
            if (nbVictory == null) {
                nbVictory = 1;
            } else {
                nbVictory += 1;
            }
            yearAndVictory.put(season, nbVictory);
            nbVictories.put(winTeam, yearAndVictory);

            Map<String, Integer> yearAndDefeat = nbDefeats.get(looseTeam);
            if(yearAndDefeat == null) {
                yearAndDefeat = new HashMap<>();
            }
            Integer nbLoose = yearAndDefeat.get(season);
            if (nbLoose == null) {
                nbLoose = 1;
            } else {
                nbLoose += 1;
            }
            yearAndDefeat.put(season, nbLoose);
            nbDefeats.put(looseTeam, yearAndDefeat);
        });

        teams.forEach((teamId, teamName) -> {

            Map<String, Integer> victories = nbVictories.get(teamId);
            Map<String, Integer> defeats = nbDefeats.get(teamId);

            Map<String, Integer> nbGames = new HashMap<>();
            if (victories != null)
                nbGames.putAll(victories);

            if (defeats != null)
            defeats.forEach((year, nbDefeat) -> {
                if (nbGames.get(year) == null)
                    nbGames.put(year, nbDefeat);
                else
                    nbGames.put(year, nbGames.get(year) + nbDefeat);
            });

            nbGames.forEach((year, nbGame) -> {
                int nbVictory = nbVictories.get(teamId).get(year) == null ? 0 : nbVictories.get(teamId).get(year);
                int nbDefeat = nbDefeats.get(teamId).get(year) == null ? 0 : nbDefeats.get(teamId).get(year);
                float ratio = 0;
                if (nbGame != 0) {
                    ratio = (float) nbVictory / nbGame;
                }

                System.out.println(teamName + " - year : " + year + " - nbGame : " + nbGame + " - nb Victory : " + nbVictory + " - nb Looses : " + nbDefeat + " - ratio : " + ratio);
            });
        });

    }

    private static Map<String, String> getTeams() {
        Map<String, String> teams = new HashMap<>();

        try (Stream<String> stream = Files.lines(Paths.get(ClassLoader.getSystemResource("WTeams.csv").toURI())).skip(1)) {
            stream.forEach(line -> {
                String[] team = line.split(CSV_SPLIT_BY);
                teams.put(team[0], team[1]);
            });
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return teams;
    }

    private static List<String[]> getResults() {
        List<String[]> results = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(ClassLoader.getSystemResource("WRegularSeasonCompactResults.csv").toURI())).skip(1)) {

            stream.forEach(line -> {
                results.add(line.split(CSV_SPLIT_BY));
            });

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return results;
    }
}
