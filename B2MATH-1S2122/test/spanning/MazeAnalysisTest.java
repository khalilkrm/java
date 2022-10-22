package spanning;

import static org.junit.jupiter.api.Assertions.*;

import graphics.Image;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class MazeAnalysisTest {
	private static final MazeAnalysis perfect = new MazeAnalysis(Image.loadImage("img/mazes/maze_perfect.png"));
	private static final MazeAnalysis separate_zones_exit_reachable = new MazeAnalysis(Image.loadImage("img/mazes/maze_separate_zones_exit_reachable.png"));
	private static final MazeAnalysis separate_zones_no_exit = new MazeAnalysis(Image.loadImage("img/mazes/maze_separate_zones_no_exit.png"));
	private static final MazeAnalysis with_cycles = new MazeAnalysis(Image.loadImage("img/mazes/maze_with_cycles.png"));

	@ParameterizedTest
	@MethodSource("mazeProviderIsPerfect")
	void testIsPerfectMaze1(MazeAnalysis maze, boolean expected) {
		assertEquals(expected, maze.isPerfect());
	}

	@ParameterizedTest
	@MethodSource("mazeProviderIsConnected")
	void testIsConnectedMaze1(MazeAnalysis maze, boolean expected) {
		assertEquals(expected, maze.isConnected());
	}

	@ParameterizedTest
	@MethodSource("mazeProviderWithCycles")
	void testIsConnectedWithCyclesMaze1(MazeAnalysis maze, boolean expected) {
		assertEquals(expected, maze.isConnectedWithCycles());
	}

	@ParameterizedTest
	@MethodSource("mazeProviderExitReachable")
	void testIsExitReachableMaze1(MazeAnalysis maze, boolean expected) {
		assertEquals(expected, maze.isExitReachable());
	}

	public static Stream<Arguments> mazeProviderExitReachable() {
		return Stream.of(
				Arguments.of(perfect, true),
				Arguments.of(separate_zones_exit_reachable, true),
				Arguments.of(separate_zones_no_exit, false),
				Arguments.of(with_cycles, true)
		);
	}

	public static Stream<Arguments> mazeProviderWithCycles() {
		return Stream.of(
				Arguments.of(perfect, false),
				Arguments.of(separate_zones_exit_reachable, false),
				Arguments.of(separate_zones_no_exit, false),
				Arguments.of(with_cycles, true)
		);
	}

	public static Stream<Arguments> mazeProviderIsConnected() {
		return Stream.of(
				Arguments.of(perfect, true),
				Arguments.of(separate_zones_exit_reachable, false),
				Arguments.of(separate_zones_no_exit, false),
				Arguments.of(with_cycles, true)
		);
	}

	public static Stream<Arguments> mazeProviderIsPerfect() {
		return Stream.of(
				Arguments.of(perfect, true),
				Arguments.of(separate_zones_exit_reachable, false),
				Arguments.of(separate_zones_no_exit, false),
				Arguments.of(with_cycles, false)
		);
	}
}