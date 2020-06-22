package Group6;

import Group6.Agent.Behaviour.*;
import Group6.Agent.Intruder.BehaviourBasedIntruder;
import Group9.agent.factories.IAgentFactory;
import Group9.map.parser.Parser;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.Competition.Agent.SittingDuckAgentsFactory;
import Interop.Competition.Competition;
import Interop.Competition.LimitedRoundsGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BehavioursEvaluation {

    static List<String> maps = Arrays.asList(
        "src/main/java/Interop/Competition/Maps/Group6/Simple/simple.map",
        "src/main/java/Interop/Competition/Maps/Group6/Simple/simple2.map",
        "src/main/java/Interop/Competition/Maps/Group6/Simple/simple3.map",
        "src/main/java/Interop/Competition/Maps/Group6/mirror.map",
        "src/main/java/Interop/Competition/Maps/Group6/open.map",
        "src/main/java/Interop/Competition/Maps/Group6/rooms.map",
        "src/main/java/Interop/Competition/Maps/Group6/spiral.map",
        "src/main/java/Interop/Competition/Maps/Group6/temple.map"
    );

    static int maxRounds = 5000;

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {

        Class<?> exploreBehaviour = ExploreBehaviour.class;

        List<Class<?>> additionalIntruderBehaviours = Arrays.asList(
            ToTargetBehaviour.class,
            ToTeleportBehaviour.class,
            ToPassageBehaviour.class,
            AvoidWallsBehaviour.class,
            DisperseBehaviour.class
        );

        List<Class<?>> allIntruderBehaviours = Arrays.asList(
            ToTargetBehaviour.class,
            ToTeleportBehaviour.class,
            ToPassageBehaviour.class,
            AvoidWallsBehaviour.class,
            DisperseBehaviour.class,
            ExploreBehaviour.class
        );

        for (final Class<?> behaviourClass: additionalIntruderBehaviours) {
            LimitedRoundsGame game = new LimitedRoundsGame(
                Parser.parseFile(maps.get(0)),
                new IAgentFactory() {
                    public List<Intruder> createIntruders(int amount) {
                        try {
                            List<Intruder> intruders = new ArrayList<>();
                            for (int i = 0; i < amount; i++) {
                                List<Behaviour> behaviours = Arrays.asList(
                                    (Behaviour)behaviourClass.newInstance(),
                                    (Behaviour)exploreBehaviour.newInstance()
                                );
                                intruders.add(new BehaviourBasedIntruder(
                                    behaviours
                                ));
                            }
                            return intruders;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    public List<Guard> createGuards(int amount) {
                        return SittingDuckAgentsFactory.createGuards(amount);
                    }
                },
                maxRounds
            );
            game.play();
            report(maps.get(0), game, behaviourClass.getClass().getName(), "Guards");
        }
    }

    public static void report(
        String mapPath,
        LimitedRoundsGame game,
        String intruderDesignation,
        String guardDesignation
    ) {
        System.out.println(
            "Map: " + Competition.getMapName(mapPath) + "\t" +
            "Intruders: " + intruderDesignation + "\t" +
            "Guards: " + guardDesignation + "\t" +
            "Winner: " + game.getWinner() + "\t" +
            "Rounds: " + game.getCurrentRoundsCount()
        );
    }

}
