package pt.uminho.haslab.smpc.sharmind.spdz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pt.uminho.haslab.smpc.helpers.MemSpdzPlayer;

import java.util.List;

@RunWith(Parameterized.class)
public abstract class SPDZProtocolTest {

    public MemSpdzPlayer[] setupNetwork() {
        MemSpdzPlayer[] players = new MemSpdzPlayer[3];
        for(int i = 0 ; i < 3; i++){
            players[i] = new MemSpdzPlayer();
        }
        return players;
    }
    /*
     * Has to return a list with three DbTest.
     */
    public abstract List<SpdzDB> prepareDatabases(MemSpdzPlayer[] players);

    /*
     * Must contain the assert statement.
     */
    public abstract void condition(SpdzDB db1, SpdzDB db2,
                                   SpdzDB db3);

    @Test
    public void protocol() throws InterruptedException {
        System.out.println("Running protocol");
        MemSpdzPlayer[] players = setupNetwork();
        List<SpdzDB> workers = prepareDatabases(players);

        SpdzDB rdb0 = workers.get(0);
        SpdzDB rdb1 = workers.get(1);
        SpdzDB rdb2 = workers.get(2);

        Long startTime = System.nanoTime();
        rdb0.start();
        rdb1.start();
        rdb2.start();

        rdb0.join();
        rdb1.join();
        rdb2.join();
        Long stopTime = System.nanoTime();
        System.out.println("Execution time was " + (stopTime - startTime));
        condition(rdb0, rdb1, rdb2);

    }
}
