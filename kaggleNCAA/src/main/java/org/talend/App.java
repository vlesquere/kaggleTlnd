package org.talend;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 */
public class App {
    private static final String CSV_SPLIT_BY = ",";

    public static void main(String[] args) {

        // read CSV Data Files
        Map<String, String> teams = getTeams();
        List<String[]> results = getResults();

        // Init
        Map<String, Map<String, Integer>> nbVictories = new HashMap<>();
        Map<String, Map<String, Integer>> nbDefeats = new HashMap<>();

        // Count Match type
        results.forEach(result -> {
            String season = result[0];
            String winTeam = result[2];
            String looseTeam = result[4];

            countResultTypeMatch(nbVictories, season, winTeam);
            countResultTypeMatch(nbDefeats, season, looseTeam);
        });

        teams.forEach((teamId, teamName) -> {

            Map<String, Integer> nbGames = new HashMap<>();

            if (nbVictories.containsKey(teamId))
                nbGames.putAll(nbVictories.get(teamId));

            if (nbDefeats.containsKey(teamId))
                nbDefeats.get(teamId).forEach((year, nbDefeat) -> {
                nbGames.computeIfPresent(year, (key, nbMatch) -> nbMatch + nbDefeat);
                nbGames.putIfAbsent(year, nbDefeat);
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


    private static void countResultTypeMatch(Map<String, Map<String, Integer>> mapResultsType, String season, String team) {
        Map<String, Integer> yearAndResult = mapResultsType.getOrDefault(team, new HashMap<>());

        Integer nbResultType = yearAndResult.get(season);
        nbResultType = nbResultType == null ? 1 : nbResultType + 1;

        yearAndResult.put(season, nbResultType);
        mapResultsType.put(team, yearAndResult);
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
