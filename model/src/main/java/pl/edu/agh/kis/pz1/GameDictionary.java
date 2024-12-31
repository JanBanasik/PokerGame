package pl.edu.agh.kis.pz1;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * The {@code GameDictionary} class is a utility class that dynamically discovers and creates instances
 * of all subclasses of the {@link Game} class in the specified package.
 * <p>
 * It utilizes the Reflections library to scan the package for subclasses and then stores them in a map,
 * where the key is the name of the subclass in camelCase format and the value is the instance of the subclass.
 * This allows easy access to any {@code Game} subclass by its class name.
 * </p>
 * <p>
 * This class is not meant to be instantiated and has a private constructor to prevent instantiation.
 * </p>
 */
public class GameDictionary {

    /**
     * Private constructor to prevent instantiation of the utility class.
     * Throws an {@code IllegalStateException} when called.
     */
    private GameDictionary() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Scans the package {@code pl.edu.agh.kis.pz1} for all subclasses of the {@code Game} class
     * and creates instances of them. The resulting map contains the class names (in camelCase format) as keys
     * and the instantiated game objects as values.
     *
     * @return a {@code Map} where the keys are the class names of the {@code Game} subclasses (in camelCase format)
     *         and the values are the instances of the corresponding subclasses.
     * @throws NoSuchMethodException if no suitable constructor is found for a subclass.
     * @throws InvocationTargetException if the constructor of a subclass throws an exception.
     * @throws InstantiationException if the subclass cannot be instantiated.
     * @throws IllegalAccessException if the constructor or class is not accessible.
     */
    public static Map<String, Game> getGameDictionary() throws NoSuchMethodException,
            InvocationTargetException,
            InstantiationException,
            IllegalAccessException {
        Map<String, Game> gameMap = new HashMap<>();

        // Use Reflections library to find all subclasses of Game
        Reflections reflections = new Reflections("pl.edu.agh.kis.pz1");
        Set<Class<? extends Game>> gameClasses = reflections.getSubTypesOf(Game.class);

        for (Class<? extends Game> gameClass : gameClasses) {
            // Convert class name to camelCase to use as a dictionary key
            String className = gameClass.getSimpleName();

            // Create an instance of the subclass using reflection
            Game gameInstance = gameClass.getDeclaredConstructor().newInstance();
            gameMap.put(className, gameInstance);
        }
        return gameMap;
    }

    /**
     * Main method that demonstrates how to retrieve the game dictionary and print it.
     *
     * @param args command-line arguments (unused in this implementation).
     * @throws InvocationTargetException if the constructor invocation fails.
     * @throws NoSuchMethodException if no suitable constructor is found.
     * @throws InstantiationException if the subclass cannot be instantiated.
     * @throws IllegalAccessException if the constructor or class is not accessible.
     */
    public static void main(String[] args) throws InvocationTargetException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException {
        // Demonstration of how to retrieve and print the game dictionary
        System.out.println(getGameDictionary());
    }
}
