package pl.edu.agh.kis.pz1;

/**
 * The {@code IntAndString} record is a simple data structure that holds an integer value
 * and a string. This record is typically used to store and pass around pairs of a number
 * and its associated text description.
 * <p>
 * The record provides a compact and immutable way to represent a pair of values:
 * an {@code int} and a {@code String}. The fields {@code number} and {@code text} are
 * automatically provided by the record constructor, along with standard {@code toString()},
 * {@code equals()}, and {@code hashCode()} implementations.
 * </p>
 *
 * @param number the integer value in the pair
 * @param text the string associated with the integer value
 */
public record IntAndString(int number, String text) {}
