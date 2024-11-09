import com.cjwebb.javaaverages.server.Stats;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatsTest {

    @Test
    public void testBasicStats() {
        var s = new Stats();
        s.addValue(100);
        s.addValue(200);

        assertEquals(s.getMax(), 200);
        assertEquals(s.getMin(), 100);
        assertEquals(s.getMean(), 150);
    }

    @Test
    public void handlesDivideByZero() {
        var s = new Stats();
        assertEquals(s.getMean(), 0);
    }
}
